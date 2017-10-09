import javax.swing.*;
import java.awt.*;

//@SuppressWarnings("serial")
public class SimpleVisualization extends JFrame{
    private final int width = 600;
    private final int height = 600;
    /**
     * SimpleVisualization ()
     *
     * Creates a Model object and View object places them in one Window.
     * Sets the size of the window
     */
    SimpleVisualization() {
        this.setSize(new Dimension((int)Constants.FIELD_SIZE_X, (int)Constants.FIELD_SIZE_Y));
        this.setResizable(false);
        setTitle("Rover hearding");
        setVisible(true);

        this.setContentPane(this.getContentPane());

//        Model theModel = new Model();
        View theView = new View();
        Simulation sim = new Simulation(theView);
        this.add(theView, "East");
        this.add(sim, "West");
        this.pack();
        sim.start();
        //sim.stop();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}