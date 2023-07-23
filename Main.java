import java.awt.event.KeyListener;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * The Main class serves as the entry point for the JavaCube application.
 * It sets up the GUI, initializes the listener, and handles user input.
 */
public class Main {

    // Fields

    /** The main JFrame that holds the JavaCube application. */
    public static JFrame frame;

    /** The JTextArea used to display the cube state and instructions. */
    public static JTextArea output;

    /** The CubeInteraction object that handles user interactions with the cube. */
    public static CubeInteraction listener;

    /** The KeyListener to capture user keyboard input. */
    public static KeyListener keyListener;

    // Methods

    /**
     * Sets up the JavaCube application.
     * Initializes the GUI components, listener, and keyListener.
     * Also, sets the initial message on the JTextArea.
     */
    public static void setup() {
        // Initialize the listener and keyListener
        listener = new CubeInteraction();
        keyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    listener.keyPressed(e);
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    listener.keyTyped(e);
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    listener.keyReleased(e);
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        };

        // Initialize the JTextArea and set focus
        output = new JTextArea();
        output.setFocusable(false);
        output.requestFocusInWindow();

        // Initialize the JFrame and set properties
        frame = new JFrame();
        frame.setMaximizedBounds(new Rectangle(375, 350));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(output);

        // Make the JFrame visible and add the keyListener to it and the JTextArea
        frame.setVisible(true);
        frame.addKeyListener(keyListener);
        output.addKeyListener(keyListener);

        // Set the initial message on the JTextArea
        output.setText("Welcome to JavaCube!\nPress y to begin or i for instructions.");
    }

    /**
     * The main method serves as the entry point for the JavaCube application.
     * Calls the setup() method to initialize and start the application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String args[]) {
        try {
            setup();
        } catch (Exception e) {
            System.out.println("An unknown exception occurred...");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
