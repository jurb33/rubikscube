const pointer = { x: 0, y: 0, horizontal: false };  //Horizontal Indicates z-directional control amongst cubies

//Used to map colors with respect to backend -> hardfixed, do not touch
let map = {};
    map[0] = 0;
    map[1] = 4;
    map[2] = 5;
    map[3] = 1;
    map[4] = 2;
    map[5] = 3;
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
Reads from .json file and updates the intended surfaces of cube
Orientation is hardfixed on the index of each face within the material of each cubie
(see comments below)
@param cubeData the .json object containing the faces:
front, back left right, up, down
*/
async function updateCubeGraphics(data) {
    try {
         let { frontFace, backFace, leftFace, rightFace, upFace, downFace, solved } = await data;
        if (!frontFace || !backFace || !leftFace || !rightFace || !upFace || !downFace) {
           console.error("One or more face data are undefined:",
            { frontFace, backFace, leftFace, rightFace, upFace, downFace});
            return; 
       }
        //Certain transformations were needed to map the face data correctly from the backend
        //to the surface of the cube groups because of the add order and creation of cubes.
        //These transformations are hardcoded and should not be touched
        let processedBack = (backFace);
        let processedLeft = reflectArray(rotateArray(rotateArray(rotateArray(leftFace))));
        let processedRight = rotateArray(rightFace);
        let processedUp = rotateArray(rotateArray(reflectArray((upFace))));
        let processedDown =((downFace));
        let processedFront = reflectArray(rotateArray(rotateArray(frontFace)));
        //updates the individual faces at the index of each cubie in the group
        updateFace(groups.front, processedFront, 4);
        updateFace(groups.back, processedBack, 5);
        updateFace(groups.left, processedLeft, 1);
        updateFace(groups.right, processedRight, 0);
        updateFace(groups.up, processedUp, 2);
        updateFace(groups.down, processedDown, 3);
        solvedAnimation(solved);
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