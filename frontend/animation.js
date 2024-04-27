/*
Updates the scene and renders the graphics constantly
*/
function animate() {
    requestAnimationFrame(animate);
    sceneAnimation();
    renderer.render(scene, camera);
}
var triggered = false;
var animating = false;
async function solvedAnimation(solved) {
    if (solved) {
        const whitecube =
         {upFace: createFace(CUBE_DIMENSION, 2),
         downFace: createFace(CUBE_DIMENSION, 2),
        rightFace: createFace(CUBE_DIMENSION, 2),
        leftFace: createFace(CUBE_DIMENSION, 2),
        frontFace: createFace(CUBE_DIMENSION, 2),
        backFace: createFace(CUBE_DIMENSION, 2),};
        
        const yellowcube =
         {upFace: createFace(CUBE_DIMENSION, 5),
         downFace: createFace(CUBE_DIMENSION, 5),
        rightFace: createFace(CUBE_DIMENSION, 5),
        leftFace: createFace(CUBE_DIMENSION, 5),
        frontFace: createFace(CUBE_DIMENSION, 5),
        backFace: createFace(CUBE_DIMENSION, 5),};
        //Makes the cube "flash"
        if (!triggered) {
        for (let i = 0; i < 8; i ++) {
            setTimeout(() => updateCubeGraphics(whitecube), i * 200); // Flash every 200ms
            setTimeout(() => updateCubeGraphics(yellowcube), i * 200 + 100); // Offset by 100ms
        }
        setTimeout(() => {
            updateCubeGraphics(fetchCube());
        }, 1600); 
    }
    triggered = true;
    }
    //Loads an array with a specified value
    function createFace(dimension, initialValue) {
        return Array.from({ length: dimension }, () => Array(dimension).fill(initialValue));
    }
}

//Parameters for oscillation
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