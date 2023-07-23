import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The CubeInteraction class implements KeyListener to handle user keyboard input for Cube interaction.
 */
public class CubeInteraction implements KeyListener {

    // Fields

    /** The cube object representing the Rubik's cube. */
    public static Cube cube;

    /** The pointer array to control rows and columns of the cube. */
    public static int[] pointer = { 0, 0 };

    /** A flag indicating whether horizontal control is ON or OFF. */
    public static boolean horizontal = false;

    /** The CubeLogger object to log moves. */
    public static CubeLogger moves;

    /** The offset for move count. */
    public static int moveCount = -1;

    /** A flag indicating whether the game is running. */
    public static boolean run = false;

    /** The scramble level selected by the user. */
    public static int scrambleVal;

    /**
     * Constructor for the CubeInteraction class. Initializes the cube and moves objects.
     */
    public CubeInteraction() {
        cube = new Cube();
        moves = new CubeLogger();
    }

    /**
     * Resets the cube and other variables to their initial state.
     */
    public void clear() {
        cube = new Cube();
        cube.scramble(scrambleVal);
        horizontal = false;
        pointer[0] = 0;
        pointer[1] = 0;
        moves.clear();
    }

    /**
     * Handles the key pressed event from the user.
     * This method is called when a key is pressed while the GUI window has focus.
     * It handles various actions based on the user input.
     *
     * @param event The KeyEvent representing the key pressed by the user.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyChar()) {
            // If 'r' is pressed, reverse the cube moves log
            case 'r':
                moves.reverseLog();
                return;

            // If 'c' is pressed, clear the cube and reset the game
            case 'c':
                clear();
                return;

            // If 'q' is pressed, display a message and exit the program
            case 'q':
                Main.output.setText("Quitting!");
                System.exit(0);
        }

        // If the game is in "run" mode (controls are active)
        if (run) {
            try {
                // Handle pointer movements and cube rotations based on user input
                // Update the GUI and check if the cube is solved
                handleMoveInput(event);
                setGUI();
                loop();
            } catch (Exception e) {
                System.out.println("Fatal Error!");
            }
        } else {
            // Handle user input before starting the game
            handlePreGameInput(event);
        }
    }

    /**
     * Handles the key typed event from the user.
     * This method is called when a key is typed (pressed and released) while the GUI window has focus.
     * It handles special key input, such as quitting the program.
     *
     * @param event The KeyEvent representing the key typed by the user.
     */
    @Override
    public void keyTyped(KeyEvent event) {
        // Handle special key input (e.g., quitting)
        if (event.getKeyChar() == 'q') {
            System.out.println("Quitting!");
            System.exit(0);
        }
    }

    /**
     * Handles the key released event from the user.
     * This method is called when a key is released while the GUI window has focus.
     * In this context, this method is not used.
     *
     * @param event The KeyEvent representing the key released by the user.
     */
    @Override
    public void keyReleased(KeyEvent event) {
        // This method is not used in this context
    }

    /**
     * Updates the GUI with the current cube state, pointer position, and horizontal control status.
     */
    public void setGUI() {
        Main.output.setText(cube.toString() + "Pointer: [" + pointer[0] + ", " + pointer[1] + "] Horizontal Control: " + (horizontal ? "ON" : "OFF" + "\n".repeat(20)));
        System.out.println(cube.toString());
        System.out.println("Pointer: [" + pointer[0] + ", " + pointer[1] + "] Horizontal Control: " + ((horizontal ? "ON" : "OFF") + "\n".repeat(20))); // Specific format to JFrame
    }

    /**
     * Checks if the cube is solved and displays the appropriate message in the GUI.
     */
    public static void loop() {
        String winPrompt = "Congrats! You solved the cube in " + CubeInteraction.moveCount
                + " moves.\n To play again, restart the applet!";
        Main.output.setText((cube.isSolved() ? winPrompt : Main.output.getText()));

        if (cube.isSolved()) {
            System.out.println(winPrompt);
        }
    }

    /**
     * Handles user input before starting the game (pre-game menu).
     * Displays appropriate messages and initializes the game based on the user input.
     *
     * @param event The KeyEvent representing the key pressed by the user.
     */
    private void handlePreGameInput(KeyEvent event) {
        switch (event.getKeyChar()) {
            // If 'y' is pressed, display scramble level instructions
            case 'y':
                Main.output.setText("Please Select a scramble level between 1 and 3.\n\n I would recommend starting on 1.");
                break;

            // If 'i' is pressed, display game instructions
            case 'i':
                Main.output.setText("JavaCube is just a Rubik's cube displayed on a 2D screen;\nYou have a pointer to control the rows"
                        + " and columns.\n To toggle between row and column select, press space.\n You can only control U/D in horizontal (Other moves are not primitive to the cube)\n Use WSAD to control pointer, c to reset, r to show the solution & q to quit.\n"
                        + "You will need to focus the white window to interact with the game. Good luck!");
                break;

            // If '1', '2', or '3' is pressed, start the game with the corresponding scramble level
            case '1':
                cube.scramble(3);
                scrambleVal = 3;
                run = true;
                Main.output.setText(cube.toString());
                break;
            case '2':
                cube.scramble(6);
                scrambleVal = 6;
                run = true;
                Main.output.setText(cube.toString());
                break;
            case '3':
                cube.scramble(9);
                scrambleVal = 9;
                run = true;
                Main.output.setText(cube.toString());
                break;

            // If any other key is pressed, display the welcome message
            default:
                Main.output.setText("Welcome to JavaCube!\n Press y to begin or i for instructions.");
                break;
        }
    }

    /**
     * Handles the user input during the game (while run is true).
     * Moves the pointer, rotates the cube, and updates the GUI based on the user input.
     *
     * @param event The KeyEvent representing the key pressed by the user.
     */
    private void handleMoveInput(KeyEvent event) {
        char keyChar = event.getKeyChar();
        if (pointer[0] == 0 && !horizontal) {
            handleNormalModeMove(keyChar);
        } else if (pointer[0] == 1 && !horizontal) {
            handleSecondRowMove(keyChar);
        } else if (horizontal) {
            handleHorizontalMove(keyChar);
        }
    }

    /**
     * Handles user input in normal mode (not horizontal, pointer[0] == 0).
     *
     * @param keyChar The character representing the key pressed by the user.
     */
    private void handleNormalModeMove(char keyChar) {
        switch (keyChar) {
            case 'a':
                if (pointer[1] != 0) {
                    pointer[1]--;
                }
                break;
            case 'd':
                if (pointer[1] != 2) {
                    pointer[1]++;
                }
                break;
            case 'w':
                moves.log(cube);
                cube.U(pointer[1]);
                break;
            case 's':
                cube.D(pointer[1]);
                break;
            case ' ':
                pointer[0] = 1;
                break;
            case 'h':
                horizontal = !horizontal;
                break;
            default:
                break;
        }
    }

    /**
     * Handles user input in the second row mode (not horizontal, pointer[0] == 1).
     *
     * @param keyChar The character representing the key pressed by the user.
     */
    private void handleSecondRowMove(char keyChar) {
        switch (keyChar) {
            case 'w':
                if (pointer[1] != 0) {
                    pointer[1]--;
                }
                break;
            case 's':
                if (pointer[1] != 2) {
                    pointer[1]++;
                }
                break;
            case 'a':
                cube.L(pointer[1]);
                break;
            case 'd':
                cube.R(pointer[1]);
                break;
            case ' ':
                pointer[0] = 0;
                break;
            case 'h':
                horizontal = !horizontal;
                break;
            default:
                return;
        }
    }

    /**
     * Handles user input in horizontal mode.
     *
     * @param keyChar The character representing the key pressed by the user.
     */
    private void handleHorizontalMove(char keyChar) {
        switch (keyChar) {
            case 'w':
                if (pointer[0] == 1) {
                    return;
                }
                cube.HU(pointer[1]);
                break;
            case 's':
                if (pointer[0] == 1) {
                    return;
                }
                cube.HD(pointer[1]);
                break;
            case 'a':
                if (pointer[1] != 0) {
                    pointer[1]--;
                }
                break;
            case 'd':
                if (pointer[1] != 2) {
                    pointer[1]++;
                }
                break;
            case 'h':
                horizontal = !horizontal;
                break;
            case ' ':
                pointer[0] = (pointer[0] == 0) ? 1 : 0;
                break;
            default:
                break;
        }
    }

}
