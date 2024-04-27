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
