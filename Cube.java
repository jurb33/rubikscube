package com.cubeservice.rest;




import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a 3x3x3 Rubik's Cube and contains methods for various operations on the cube.
 */
public class Cube {
    public static final int FRONT = 0;
    public static final int RIGHT = 1;
    public static final int BACK = 2;
    public static final int LEFT = 3;
    public static final int UP = 4;
    public static final int DOWN = 5;

    public int[][] upFace, rightFace, leftFace, frontFace, backFace, downFace;
    public ArrayList<int[][]> faces = new ArrayList<>();
    public final int[] faceList = {FRONT, RIGHT, BACK, LEFT, UP, DOWN};

    public boolean isHorizontal = false;
    public static final int CUBE_DIMENSION = 3;
    public static final int MAP_OPERATIONS = 4;

    public final int[] ROT_COLUMN_FACE = {0, 4, 2, 5, 0};
    public final int[] ROT_COLUMN_SIDE = {3, 4, 1, 5, 3};
    public final int[] ROT_ROW = {0, 1, 2, 3, 0};

    /**
     * Constructor to create a solved Rubik's Cube.
     */
    public Cube() {
        rightFace = new int[CUBE_DIMENSION][CUBE_DIMENSION];
        leftFace = new int[CUBE_DIMENSION][CUBE_DIMENSION];
        frontFace = new int[CUBE_DIMENSION][CUBE_DIMENSION];
        backFace = new int[CUBE_DIMENSION][CUBE_DIMENSION];
        downFace = new int[CUBE_DIMENSION][CUBE_DIMENSION];
        upFace = new int[CUBE_DIMENSION][CUBE_DIMENSION];

        faces.add(frontFace);
        faces.add(rightFace);
        faces.add(backFace);
        faces.add(leftFace);
        faces.add(upFace);
        faces.add(downFace);

        setCubeSolved();
    }

    /**
     * Rotates the top layer (UP) of the cube.
     *
     * @param index The index of the column (0-2) to rotate.
     */
    public void U(int index) {
        this.rotateBar(index, true, false);
        System.out.println(this.backFace[1][0]);
        //CubeInteraction.moveCount++;
    }

    /**
     * Rotates the bottom layer (DOWN) of the cube.
     *
     * @param index The index of the column (0-2) to rotate.
     */
    public void D(int index) {
        this.rotateBar(index, false, false);
        
        //CubeInteraction.moveCount++;
    }

    /**
     * Rotates the left layer (LEFT) of the cube.
     *
     * @param index The index of the row (0-2) to rotate.
     */
    public void L(int index) {
        this.rotateSlice(index % 3, true);
       // CubeInteraction.moveCount++;
    }

    /**
     * Rotates the right layer (RIGHT) of the cube.
     *
     * @param index The index of the row (0-2) to rotate.
     */
    public void R(int index) {
        this.rotateSlice((index ) % 3, false);
        //CubeInteraction.moveCount++;
    }

    /**
     * Rotates the middle layer horizontally (UP or DOWN).
     *
     * @param index The index of the column (0-2) to rotate.
     */
    public void HU(int index) {
        this.rotateBar((index) % 3, true, true);
        //CubeInteraction.moveCount++;
    }

    /**
     * Rotates the middle layer horizontally (UP or DOWN).
     *
     * @param index The index of the column (0-2) to rotate.
     */
    public void HD(int index) {
        this.rotateBar((index) % 3, false, true);
        //CubeInteraction.moveCount++;
    }

    /**
     * Sets all the faces to their default solved state.
     */
    public void setCubeSolved() {
        for (int k = 0; k < faces.size(); k++) {
            setMatrix(faces.get(k), faceList[k]);
        }
    }

    /**
     * Scrambles the cube by performing a sequence of random moves.
     *
     * @param level The level of scrambling (number of random moves).
     */
    public void scramble(int level) {
        int moveCount = 0;
        Random random = new Random();
        
        //Log each scramble move
        while (moveCount < level) {
            //CubeInteraction.moves.log(this);
            moveCount++;
            
            //Bounded to possible indexes and moves.
            int randIndex = random.nextInt(3);
            int randMove = random.nextInt(6);
            //Calls a random move based on seeding
            switch (randMove) {
            case 0:
                this.U(randIndex);
                
                break;
        
            case 1:
                this.D(randIndex);
                break;
                
            case 2:
                this.L(randIndex);
                break;
            case 3: 
                this.R(randIndex);
            break;
            case 4:
                this.HU(randIndex);
                break;
            case 5:
                this.HD(randIndex);
                break;
                
            }
        }
        
    }

    /**
     * Checks if the cube is in a solved state.
     *
     * @return True if the cube is solved, False otherwise.
     */
    public boolean isSolved() {
        for (int i = 0; i < this.faces.size(); i++) {
            for (int j = 0; j < CUBE_DIMENSION; j++) {
                for (int k = 0; k < CUBE_DIMENSION; k++) {
                    if (!(this.faces.get(i)[j][k] == i)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Rotates a third of the cube vertically, facing the front perspective.
     *
     * @param pointX     The current pointer, 0 being the left column facing front, 2 being the right column facing front.
     * @param direction  True for up, False for down.
     * @param horizontal True corresponds to horizontal to front, False corresponds to front face.
     */
    public void rotateBar(int pointX, boolean direction, boolean horizontal) {
        this.mapColumn(pointX, direction, horizontal);
        if (horizontal) {
            if (pointX == 0) {
                this.planeRotation(FRONT, direction);
            } else if (pointX == 2) {
                this.planeRotation(BACK, direction);
            }
        } else {
            if (pointX == 0) {
                this.planeRotation(LEFT, !direction);
            } else if (pointX == 2) {
                this.planeRotation(RIGHT, direction);
            }
        }
    }

    /**
     * Rotates a slice of the cube horizontally or vertically.
     *
     * @param pointY   The index of the row (0-2) or column (0-2) to rotate.
     * @param clockwise True for clockwise rotation, False for counterclockwise rotation.
     */
    public void rotateSlice(int pointY, boolean clockwise) {
        this.mapRow(pointY, !clockwise);
        if (pointY == 0) {
            this.planeRotation(UP, clockwise);
        } else if (pointY == 2) {
            this.planeRotation(DOWN, !clockwise);
        }
    }

    /**
     * Rotates a plane (face) of the cube in a given direction (clockwise or counterclockwise).
     *
     * @param face      The index of the face to be rotated (0-5).
     * @param direction True for clockwise rotation, False for counterclockwise rotation.
     */
    public void planeRotation(int face, boolean direction) {
        int[][] faceCopy = new int[CUBE_DIMENSION][CUBE_DIMENSION];
        for (int i = 0; i < CUBE_DIMENSION; i++) {
            for (int j = 0; j < CUBE_DIMENSION; j++) {
                faceCopy[i][j] = this.faces.get(face)[i][j];
            }
        }

        if (direction) {
            for (int i = 0; i < CUBE_DIMENSION; i++) {
                for (int j = 0; j < CUBE_DIMENSION; j++) {
                    this.faces.get(face)[j][CUBE_DIMENSION - 1 - i] = faceCopy[i][j];
                }
            }
        } else {
            for (int i = 0; i < CUBE_DIMENSION; i++) {
                for (int j = 0; j < CUBE_DIMENSION; j++) {
                    this.faces.get(face)[CUBE_DIMENSION - 1 - j][i] = faceCopy[i][j];
                }
            }
        }
    }

    /**
     * Maps a column as if you were to do a rotation, facing the cube, vertically or horizontally.
     *
     * @param index      The index of the column or row to map.
     * @param direction  True for up, False for down.
     * @param horizontal True for horizontal mapping, False for vertical mapping.
     */
    public void mapColumn(int index, boolean direction, boolean horizontal) {
        Cube temp = this.copy();
        if (horizontal) {
            boolean columnLatch = true;
            int[] indexes = index == 0 ? new int[]{2, 2, 0, 0,2} :
             (index == 2 ? new int[]{0, 0, 2, 2,0} : new int[]{1, 1, 1, 1,1});
            
            if (direction) {
                for (int i = 0; i < MAP_OPERATIONS; i++) {
                    for (int j = 0; j < CUBE_DIMENSION; j++) {
                        
                        if (columnLatch) {
                            this.faces.get(ROT_COLUMN_SIDE[i + 1])[indexes[i + 1]][j] = temp.faces.get(ROT_COLUMN_SIDE[i])[2 - j][indexes[i]];
                        } else {
                            this.faces.get(ROT_COLUMN_SIDE[i + 1])[j][indexes[i + 1]] = temp.faces.get(ROT_COLUMN_SIDE[i])[indexes[i]][j];
                        }
                    }
                    columnLatch = !columnLatch;
                }
            } else {
                for (int i = MAP_OPERATIONS; i > 0; i--) {
                    for (int j = 0; j < CUBE_DIMENSION; j++) {
                        if (columnLatch) {
                            this.faces.get(ROT_COLUMN_SIDE[i - 1])[indexes[i - 1]][2-j] = temp.faces.get(ROT_COLUMN_SIDE[i])[2-j][indexes[i]];
                        } else {
                            this.faces.get(ROT_COLUMN_SIDE[i - 1])[2-j][indexes[i-1]] = temp.faces.get(ROT_COLUMN_SIDE[i])[indexes[i]][j];
                        }
                    }
                    columnLatch = !columnLatch;
                }
            }
        } else {
            if (direction) {
                for (int i = 0; i < MAP_OPERATIONS; i++) {
                    for (int j = 0; j < CUBE_DIMENSION; j++) {
                        this.faces.get(ROT_COLUMN_FACE[i + 1])[j][index] = temp.faces.get(ROT_COLUMN_FACE[i])[j][index];
                    }
                }
            } else {
                for (int i = MAP_OPERATIONS; i > 0; i--) {
                    for (int j = 0; j < CUBE_DIMENSION; j++) {
                        this.faces.get(ROT_COLUMN_FACE[i - 1])[j][index] = temp.faces.get(ROT_COLUMN_FACE[i])[j][index];
                    }
                }
            }
        }
    }

    /**
     * Maps a row as if you were to do a rotation, facing the cube, vertically or horizontally.
     *
     * @param row       The index of the row to map.
     * @param direction True for counterclockwise rotation, False for clockwise rotation.
     */
    public void mapRow(int row, boolean direction) {
        Cube temp = this.copy();
        int backreflectIndex = row == 0 ? 2: row == 2 ? 0: 1;
        int [] indexes = new int[] {row, row, backreflectIndex, row, row};
        if (direction) {
            for (int i = 0; i < MAP_OPERATIONS; i++) {
                for (int j = 0; j < CUBE_DIMENSION; j++) {
                    if (i == 1) {  this.faces.get(ROT_ROW[i + 1])[indexes[i + 1]][2-j] = temp.faces.get(ROT_ROW[i])[indexes[i]][j];}
                    this.faces.get(ROT_ROW[i + 1])[indexes[i + 1]][j] = temp.faces.get(ROT_ROW[i])[indexes[i]][j];
                }
            }
        } else {
            for (int i = MAP_OPERATIONS; i > 0; i--) {
                for (int j = 0; j < CUBE_DIMENSION; j++) {
                    if (i == 3) {  this.faces.get(ROT_ROW[i -1])[indexes[i -1]][2-j] = temp.faces.get(ROT_ROW[i])[indexes[i]][j];}
                    this.faces.get(ROT_ROW[i - 1])[indexes[i -1]][j] = temp.faces.get(ROT_ROW[i])[indexes[i]][j];
                }
            }
        }
    }

    /**
     * Sets the values of the given matrix to the specified face value.
     *
     * @param matrix The 3x3 matrix to be set.
     * @param face   The face value to set in the matrix.
     */
    private void setMatrix(int[][] matrix, int face) {
        for (int i = 0; i < CUBE_DIMENSION; i++) {
            for (int j = 0; j < CUBE_DIMENSION; j++) {
                matrix[i][j] = face;
            }
        }
    }

    /**
     * Creates a copy of this cube.
     *
     * @return A new instance of the Cube class with the same state as this cube.
     */
    public Cube copy() {
        Cube returnCube = new Cube();
        for (int i = 0; i < this.faces.size(); i++) {
            for (int j = 0; j < CUBE_DIMENSION; j++) {
                for (int k = 0; k < CUBE_DIMENSION; k++) {
                    returnCube.faces.get(i)[j][k] = this.faces.get(i)[j][k];
                }
            }
        }
        return returnCube;
    }
    /**
     * Converts the cube to a string representation.
     *
     * @return A string representation of the cube.
     */
    @Override
    public String toString() {
        String returnString = "";
        
        int[] printIndex = {3, 0, 1}; //Left, front right (for formatting)

        
        //Formatting for pointer selection
        returnString += matrixToString(this.faces.get(UP), 7) + "\n";
        for (int i = 0; i < CUBE_DIMENSION; i++) {
//if (CubeInteraction.pointer[0] == 1 && CubeInteraction.pointer[1] == i) {
                returnString += "[";
          //  }
            //prints the elements manually (Cannot matrix to string because of JTextArea properties, but could call matrixToString upon different GL implementation.
            for (int j = 0; j < CUBE_DIMENSION; j++) {
                for (int k = 0; k < CUBE_DIMENSION; k++) {
                    returnString += this.faces.get(printIndex[j])[i][k] + " ";
                }
                returnString += " ";
            }
           // if (CubeInteraction.pointer[0] == 1 && CubeInteraction.pointer[1] == i) {
             //   returnString += "]";
            //}
            returnString += "\n";
        }
        returnString += "\n" + matrixToString(this.faces.get(DOWN), 7) + "\n" + matrixToString(this.faces.get(BACK), 7)
                 + "\nFRONT = 0, RIGHT = 1, BACK = 2, LEFT = 3, UP = 4, DOWN = 5\n";

        return returnString;
    }
    

    /**
     * Converts a matrix to a string representation with the specified indentation.
     *
     * @param face   The 3x3 matrix representing a face of the cube.
     * @param indent The indentation level for the matrix string representation.
     * @return A string representation of the matrix with indentation.
     */
    public String matrixToString(int[][] face, int indent) {
        String returnString = "";
        for (int j = 0; j < CUBE_DIMENSION; j++) {
            returnString += " ".repeat(indent);
            for (int k = 0; k < CUBE_DIMENSION; k++) {
                returnString += face[j][k] + " ";
            }
            returnString += "\n";
        }
        return returnString;
    }

    /**
     * Converts a face number to its corresponding name.
     *
     * @param face The face number (0-5) to convert.
     * @return The name of the face as a string.
     */
    public String numToFace(int face) {
        switch (face) {
            case 0:
                return "Front";
            case 1:
                return "Right";
            case 2:
                return "Back";
            case 3:
                return "Left";
            case 4:
                return "Up";
            case 5:
                return "Down";
            default:
                return "Invalid face";
        }
    }

    /**
     * Checks whether this cube is equal to another cube.
     *
     * @param cube The cube to compare with.
     * @return True if the cubes have the same state, False otherwise.
     */
    public boolean equals(Cube cube) {
        for (int k = 0; k < faces.size(); k++) {
            if (!this.equals(k, cube.faces.get(k))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether a specific face of this cube is equal to a given matrix.
     *
     * @param face   The index of the face to compare.
     * @param matrix The matrix to compare with.
     * @return True if the face is equal to the matrix, False otherwise.
     */
    public boolean equals(int face, int[][] matrix) {
        for (int i = 0; i < CUBE_DIMENSION; i++) {
            for (int j = 0; j < CUBE_DIMENSION; j++) {
                if (this.faces.get(face)[i][j] != matrix[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
