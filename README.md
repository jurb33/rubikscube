
# RubiksCube — Express.js + Spring Boot + Three.js

This repo is a full-stack Rubik’s Cube toy:

- **Backend (Spring Boot)** holds the authoritative cube state and applies moves.
- **Frontend server (Node/Express)** serves the static Three.js UI and manages a `user_id` cookie.
- **Frontend UI (Three.js + vanilla JS)** renders a 3D cube, highlights the selected slice, and sends move commands.

There’s also a deployed version (may be offline):

- http://ec2-3-131-142-89.us-east-2.compute.amazonaws.com:8080/

## Project layout

- `backend/` — Spring Boot service
	- `Cube.java` — the cube model + rotation logic
	- `CubeController.java` — REST endpoints + in-memory session repository
	- `CubeserviceApplication.java` — Spring Boot entry point
	- `pom.xml` — Maven build
- `frontend/` — Express server + Three.js client
	- `cubecontroller.js` — Express server, cookie handling, proxy to Spring Boot
	- `cubeGUI.html` — UI shell
	- `scene.js`, `cubemanagement.js`, `animation.js` — Three.js rendering + cube face mapping + win animation
	- `eventhandler.js` — keyboard + buttons
	- `datapaths.js` — `fetchCube()` / `fetchCommand()` helpers

## Run it locally

You’ll run **two processes**: Spring Boot backend and Express frontend.

### 1) Start the Spring Boot backend

From `backend/`:

```powershell
mvn spring-boot:run
```

By default this will come up on port **3000** (based on how the frontend is configured).

Note: I haven’t run this repo since 2023, so you may need to adjust ports / CORS / base URLs and project dependencies.

### 2) Start the Express frontend

From `frontend/`:

```powershell
npm install
npm start
```

Then open:

- http://localhost:8080/

## How to play 🎮

The UI uses the same pointer concept as an older Swing prototype (not included on this branch).

- `w` / `s` — apply U/D (or HU/HD in horizontal mode) *or* move the pointer depending on mode
- `a` / `d` — move pointer or apply L/R depending on mode
- `space` — toggle pointer axis (`pointer.x` 0 ↔ 1)
- `h` — toggle horizontal control (`pointer.horizontal`)

Camera:

- Arrow keys rotate the camera.

Toolbar:

- **Clear** resets to solved
- **Shuffle** scrambles (difficulty 1–3)
- **How To** opens help popup

## Backend API

The Express server exposes simple routes used by the browser:

- `GET /fetch-data`
	- creates a `user_id` cookie if missing
	- returns JSON cube state
- `POST /send-command/:commandType`
	- body: `{ "index": <0..2> }`
	- forwards the command to Spring Boot

Spring Boot endpoints (called by Express):

- `GET /cube/fetch-data?user_id=...`
- `POST /cube/command/?user_id=...&move=...&pointer=...`

Supported `move` values:

`U`, `D`, `L`, `R`, `HU`, `HD`, `shuffle1`, `shuffle2`, `shuffle3`, `clear`

## Inner Cube workings

### Cube representation (backend)

`backend/Cube.java` models the cube as 6 faces, each an `int[3][3]`.

- Face ids are fixed:
	- `0 FRONT`, `1 RIGHT`, `2 BACK`, `3 LEFT`, `4 UP`, `5 DOWN`
- `faces` is an `ArrayList<int[][]]` where `faces.get(id)` returns that face.

The backend returns these matrices as JSON fields like `frontFace`, `upFace`, etc.

**Design note / simplification idea:** the current representation is *sticker-based* (6 independent faces). For a more scalable model (4×4+, solvers, cleaner rotations), consider a *cubie-based* model:

- Represent **corner/edge/center cubies** with positions + orientations.
- Moves become permutations + orientation tweaks.
- Frontend mapping becomes simpler because you can render cubies directly.

This would require a rendering rework, but it improves maintainability long-term.
### Move implementation: “strip remap + optional face rotation”

As in the Swing prototype, each move is implemented in two parts:

1) **Remap a strip** (row/column) across 4 adjacent faces.
2) **Rotate an end face** if the strip is on an outer layer.

#### Face rotation (`planeRotation`)

`planeRotation(face, direction)` does a standard in-place 90° matrix rotation using a copied face buffer.

#### Row mapping (`mapRow`)

Row cycles follow:

`ROT_ROW = { FRONT, RIGHT, BACK, LEFT, FRONT }`

Key detail in this branch: when mapping a row across the **BACK** face, the index and/or order must be reflected.
The code computes a `backreflectIndex` and uses `2 - j` in certain steps to keep orientation correct.

#### Column mapping (`mapColumn`)

Column cycles depend on mode:

- **Normal**: `ROT_COLUMN_FACE = { FRONT, UP, BACK, DOWN, FRONT }`
- **Horizontal**: `ROT_COLUMN_SIDE = { LEFT, UP, RIGHT, DOWN, LEFT }`

Key detail in this branch: the horizontal mapping uses a `columnLatch` that alternates between two assignment styles,
plus precomputed `indexes` to correctly mirror the strip when moving between faces with different orientation.

### Scramble

`scramble(level)` performs a random walk, but in this branch the number of random moves scales as:

$$\text{moves} = level^3$$

That means difficulty grows quickly: `shuffle1=1`, `shuffle2=8`, `shuffle3=27` random moves.

### 3D rendering + face orientation fixes (frontend)

The backend face matrices are in a consistent logical orientation, but the Three.js face groups
(`groups.front`, `groups.left`, etc.) are populated based on cubie creation order and material index layout.

To compensate, `frontend/cubemanagement.js` applies hard-coded transforms like `rotateArray()` and `reflectArray()`
before calling `updateFace(...)`.

That’s why you see code like:

- rotate/reflect of `frontFace`, `leftFace`, `upFace`, …

These are *rendering-space* transforms and don’t change the cube logic.

## Retrospect / areas to improve

I created this project after my first Object-Oriented-Programming Course at UW-Madison in 2023.
This was one of my first self lead, full stack applications. Improvements at the architechtural level in design choices:
Switch to Python framework - Better aiding in OOP and overall application centralization.
Containzerize the application and get CI/CD going
Implement the actual algorithm to solve the cube - currently replays the random walk as the solution. Could use this further to
devlop instructional GUI components.
### Backend

- **Fix `isSolved()` logic**: as written it resets `firstNum` every time `j==0`, which makes the check weaker than intended.
	Usually you want to compare every sticker on a face to that face’s first sticker (or the face id). (Probably fine in practice, but worth fixing for consistency.)
- **Introduce a real move representation**: define an enum for moves (`U`, `D`, …) and validate centrally.
- **Thread safety**: `cubeRepository` / `userIdSessions` are plain `HashMap`s. Under concurrent requests, this can race.
	Consider `ConcurrentHashMap` and safer expiry iteration.
- **Session storage**: in-memory maps are fine for a demo, but a restart wipes sessions. A small persistence layer (Redis) would scale.

### Frontend / Express

- **Base URL mismatch / configuration**: `frontend/cubecontroller.js` sets `axios.defaults.baseURL = 'http://localhost:3000'`.
	That’s great locally if Spring runs on 3000, but should be environment-driven (env var) for deployment.
- **Error handling**: `fetchCommand()` calls `updateCubeGraphics(response.json())` without awaiting; make the promise flow explicit.
- **Input model**: `pointer.x` is used as a mode flag (0/1). Naming it `axisMode` (or similar) would improve readability.

### Cube model correctness & notation

- **Standard notation**: consider adding conventional Singmaster turns and inverse moves (`U'`, `R'`, etc.) on the CubeInterface level to simplify the graphical translation.
- **Tests**: add unit tests for invariants (e.g., move repeated 4× returns to original state).

### Rendering pipeline

- The rotate/reflect transforms are currently “magic constants”.
	A longer-term improvement is to define a single canonical coordinate system and derive transforms from it.


