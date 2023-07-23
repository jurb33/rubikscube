import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The CubeLogger class is a custom implementation of LinkedList to log moves and
 * display the cube's solution.
 */
public class CubeLogger extends LinkedList<Cube> {

    // Fields

    /** The list to store the cube states for logging moves. */
    private LinkedList<Cube> list;

    /** The list to store the pointer positions for logging moves. */
    private LinkedList<int[]> pointerLog;

    /** The timer to control the solution display. */
    private Timer timer;

    /** The iterator for the cube list. */
    private Iterator<Cube> iterator;

    /** The iterator for the pointer list. */
    private Iterator<int[]> iterator2;

    /** The count of moves during the solution display. */
    private int count;

    /**
     * Constructor for the CubeLogger class.
     * Initializes the list and pointerLog.
     */
    public CubeLogger() {
        list = new LinkedList<Cube>();
        pointerLog = new LinkedList<int[]>();
    }

    /**
     * Logs the current cube state and pointer position into the list.
     *
     * @param cube The current cube state to be logged.
     */
    public void log(Cube cube) {
        list.add(cube.copy());
        pointerLog.add(CubeInteraction.pointer.clone());
    }

    /**
     * Reverses the cube log and displays the solution.
     * This method animates the solution by displaying each move at a delay of 2 seconds.
     * It updates the GUI with each move from the cube log.
     */
    public void reverseLog() {
        iterator = (Iterator<Cube>) list.descendingIterator();
        iterator2 = (Iterator<int[]>) pointerLog.descendingIterator();
        count = 0;

        // Start the timer with a delay of 2 seconds
        timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (iterator.hasNext()) {
                    Cube display = iterator.next();
                    int[] display2 = iterator2.next();
                    setGUI(display, display2, count);
                    count++;
                } else {
                    // If there are no more elements in the list, stop the timer
                    timer.stop();
                }
            }
        });

        // Start the timer
        timer.start();
    }

    /**
     * Updates the GUI with the current cube state, pointer position, and move count.
     *
     * @param cube The cube state to be displayed in the GUI.
     * @param pointer The pointer position to be displayed in the GUI.
     * @param count The move count to be displayed in the GUI.
     */
    public void setGUI(Cube cube, int[] pointer, int count) {
        Main.output.setText("Solution move: " + count + "\n" + cube.toString() + "Pointer: [" + pointer[0] + ", " + pointer[1] + "]");
    }

    /**
     * Clears the cube log and updates the GUI with the current cube state.
     */
    @Override
    public void clear() {
        this.list = new LinkedList<Cube>();
        Main.output.setText(CubeInteraction.cube.toString());
    }
}
