//Three.js object setup
const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);
camera.position.set(4, 4, 4);
camera.lookAt(scene.position);

const group = new THREE.Group(); //Collection of entire cubie container
const groups = {
    front: new Array(), right: new Array(), //ordered cubie lists per face, used to parse .json from server response
    back: new Array(), left: new Array(), up: new Array(), down: new Array()
};
//Used to map colors with respect to backend -> hardfixed, do not touch
let map = {};
    map[0] = 0;
    map[1] = 4;
    map[2] = 5;
    map[3] = 1;
    map[4] = 2;
    map[5] = 3;
      
const pointer = { x: 0, y: 0, horizontal: false };  //Horizontal Indicates z-directional control amongst cubies
const arrow = createArrow();
scene.add(group);
//Orientation specific loop for assigning face colors from server response
const CUBE_DIMENSION = 3;
for (let x = 0; x < CUBE_DIMENSION; x++) {
    for (let y = 0; y < CUBE_DIMENSION; y++) {
        for (let z = CUBE_DIMENSION -1; z >= 0; z--) {
            group.add(createCube(x, y, z));
        }
    }
}
 /*
Updates the scene and renders the graphics constantly
*/
function animate() {
    requestAnimationFrame(animate);
    sceneAnimation();
    renderer.render(scene, camera);
}
// Parameters for oscillation
let t = 0;
const maxDisplacement = 0.001; // Maximum displacement in units
const speed = 0.01; // Speed of oscillation
// Define the hover function
function sceneAnimation() {
// Increment t to slowly move through the oscillation cycle
t += speed;
// Calculate the oscillation effect
const oscillation = Math.sin(t) * maxDisplacement;
// Calculate the vector from the camera towards the origin
const direction = new THREE.Vector3().subVectors(new THREE.Vector3(), camera.position).normalize();
// Set the camera's new position based on the original position plus the oscillation effect
camera.position.add(direction.multiplyScalar(oscillation));
// Reset the direction vector magnitude after modifying the camera position
direction.setLength(1);
const arrowx = pointer.horizontal ? 3: pointer.y - 1;
const arrowy = pointer.x == 1 ? 1 - pointer.y: -1;
const arrowz = pointer.horizontal ? 1 - pointer.y: 3;
const newPosition = new THREE.Vector3(arrowx, arrowy, arrowz);
const dir = pointer.horizontal ? new THREE.Vector3(-1,0,0): new THREE.Vector3(0,0,-1);

arrow.position.copy(newPosition);
arrow.quaternion.setFromUnitVectors(new THREE.Vector3(0,1,0), dir.clone().normalize());
}
function createArrow() {
const direction = new THREE.Vector3(0, 0, -1); // Direction of the arrow
const origin = new THREE.Vector3(-1, -1, 4); // Starting point of the arrow
const length = 1; // Length of the arrow
const color = 0xFBFCFC; // Color of the arrow as a hexadecimal value
    const arrowHelper = new THREE.ArrowHelper(direction.normalize(), origin, length, color);
    arrowHelper.scale.set(8,1,8);
    scene.add(arrowHelper);
    return arrowHelper; 
}
animate();
highlightSlice();
updateCubeGraphics(fetchCube()); 
/*
Creates a cube with initial coordinates and material objects for faces
assigns cube to respective groups
@param x, y, z position in Three.js scene
*/
function createCube(x, y, z) {
const geometry = new THREE.BoxGeometry(0.95, 0.95, 0.95);
const colorMaterials = [null, null, null, null, null, null];

const cube = new THREE.Mesh(geometry, colorMaterials);
cube.userData = { coordinates: { x, y, z }, faces: colorMaterials };
cube.position.set(x-1, y-1, z-1);

// Check coordinates and assign materials and groups accordingly
if (z === 2) {
colorMaterials[4] = new THREE.MeshBasicMaterial({ color: 0xFF0000 }); // Red - front
groups["front"].push(cube);
}
if (x === 0) {
colorMaterials[1] = new THREE.MeshBasicMaterial({ color: 0x0000FF }); // Blue - left
groups["left"].push(cube);
}
if (y === 2) {
colorMaterials[2] = new THREE.MeshBasicMaterial({ color: 0x00FF00 }); // Green - up
groups["up"].push(cube);
}
if (y === 0) {
colorMaterials[3] = new THREE.MeshBasicMaterial({ color: 0xFFFF00 }); // Yellow - down
groups["down"].push(cube);
}
if (x === 2) {
colorMaterials[0] = new THREE.MeshBasicMaterial({ color: 0xFFA500 }); // Orange - right
groups["right"].push(cube);
}
if (z === 0) {
colorMaterials[5] = new THREE.MeshBasicMaterial({ color: 0xFFFFFF }); // White - back
groups["back"].push(cube);
}
return cube;
}
/*
* Makes a request to server to fetch cube info
*/
async function fetchCube() {
    const data =  fetch('/fetch-data')
.then(response => {
    if (!response.ok) {
        throw new Error('Data response was not ok! Status: ' + response.status);
    }
    return response.json();
})
return data;
}
/*
Enum -> Object creator for interpereting int vals from face arrays
Does NOT set a opacity, should be maintained from original material.
*/
function numToColor(number) {
    const colorMaterials = [
        new THREE.MeshBasicMaterial({ color: 0xFF0000 }), // Red - front
        new THREE.MeshBasicMaterial({ color: 0x0000FF }), // Blue - left
        new THREE.MeshBasicMaterial({ color: 0x00FF00 }), // Green - up
        new THREE.MeshBasicMaterial({ color: 0xFFFF00 }), // Yellow - down
        new THREE.MeshBasicMaterial({ color: 0xFFA500 }), // Orange - right
        new THREE.MeshBasicMaterial({ color: 0xFFFFFF })  // White - back
    ];
    return colorMaterials[map[number]];
}
/*
Highlights cubes at the given pointers indexes -> pointer[0] = 1: selects a "slice
horizontally for rotation, horizontal triggered, selects in the perpindicular direction
*/
function highlightSlice() {
    group.children.forEach(cube => {
        const coords = cube.userData.coordinates;
        let sliceIndex;
        //Determines if highlighting slice vertical or horizontal, then selects that index
        if (pointer.horizontal) {
            sliceIndex = pointer.x === 1 ? 2 - coords.y : 2 - coords.z;
        } else if (pointer.x === 0) {
            sliceIndex = coords.x;
        } else if (pointer.x === 1) {
            sliceIndex = 2 - coords.y;
        }
        //Highlight the cubes at the given slice index
        cube.userData.faces.forEach(material => {
            if (material != null) {
            material.opacity = sliceIndex === pointer.y ? 1.2 : 0.5;
            material.transparent = true;
        }
        });
    });
}
/*
Reads from .json file and updates the intended surfaces of cube
Orientation is hardfixed on the index of each face within the material of each cubie
(see comments below)
@param cubeData the .json object containing the faces:
front, back left right, up, down
*/
async function updateCubeGraphics(data) {
try {
     let { frontFace, backFace, leftFace, rightFace, upFace, downFace } = await data;
    if (!frontFace || !backFace || !leftFace || !rightFace || !upFace || !downFace) {
       console.error("One or more face data are undefined:",
        { frontFace, backFace, leftFace, rightFace, upFace, downFace });
        return; 
   }
    //Certain transformations were needed to map the face data correctly from the backend
    //to the surface of the cube groups because of the add order and creation of cubes.
    //These transformations are hardcoded and should not be touched
    let processedBack = (backFace);
    let processedLeft = reflectArray(rotateArray(rotateArray(rotateArray(leftFace))));
    let processedRight = rotateArray(rightFace);
    let processedUp = rotateArray(rotateArray(reflectArray((upFace))));
    let processedDown =((data.downFace));
    let processedFront = reflectArray(rotateArray(rotateArray(frontFace)));
    //updates the individual faces at the index of each cubie in the group
    updateFace(groups.front, processedFront, 4);
    updateFace(groups.back, processedBack, 5);
    updateFace(groups.left, processedLeft, 1);
    updateFace(groups.right, processedRight, 0);
    updateFace(groups.up, processedUp, 2);
    updateFace(groups.down, processedDown, 3);
} catch (Error)  {
   return;
}
}
/*
Updates the given group of cubies with the faceData
@param group the group of cubies of the face
@param faceData the int[][] array from .json file containing cube face data
@param faceIndex the index designating the side; same face of cubie to update
*/
function updateFace(group, faceData, faceIndex) {
    let i = 0, j = 0; //row, col of faceData
    //Add in uniform order, take care of mappings in updateCubeGraphics
    group.forEach(cubie => {
        // Handle material update
        if (cubie.userData.faces[faceIndex] != null) {
        const oldMaterial = cubie.userData.faces[faceIndex];
        cubie.userData.faces[faceIndex] = numToColor(faceData[i][j]);
        cubie.userData.faces.forEach(face => {
            if (face != null) {
            face.opacity = oldMaterial.opacity;
            face.transparent = oldMaterial.transparent;
            }
        });
        //Logic to handle next row/iteration
        if (i === 2) {
            j = (j + 1) % 3;
        }
        i = (i + 1) % 3;
    }
    });
}
/*
* Fetches command from the springboot server at the given endpoint:
* /send-command/${command}
*/
function fetchCommand(command) {
                    updateCubeGraphics(fetch(`/send-command/${command}`, {
                        method: 'POST',
                        headers: {
                     'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({index: pointer.y })
                    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Data response was not ok!');
            }
            updateCubeGraphics(response.json());
        }));       
}
/*
Array manipulation methods used to format .json data for ordered list of cubies
since added order is determined by addition loop, we add each cubie to proper group
and then update each cubie's face with data from .json file
*/
/*
Maps a given array as follows: (plane rotation)
[1,2,3],[4,5,6],[7,8,9] - > [1,4,7][2,8,1][3,6,9]
*/
function rotateArray(array) {
    if (array==null) {
        return;
    }
    const rows = array.length;
    const cols = array[0].length;
    let rotatedArray = [];

    for (let col = 0; col < cols; col++) {
        let newRow = [];
        for (let row = rows - 1; row >= 0; row--) {
            newRow.push(array[row][col]);
        }
        rotatedArray.push(newRow);
    }
    return rotatedArray;
}
/*
reflects this array across the y axis
*/
function reflectArray(array) {
    if (array==null) {return;}
    return array.map(row => row.slice().reverse());
}