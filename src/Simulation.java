import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Simulation extends JPanel implements ActionListener, Runnable {
    private Model theModel;
    private View theView;
    private JButton startButton;
    private JButton stopButton;
    private JTextField  agentStartPose;
    private Thread thread;
    static long sleepTime = 48;
    static boolean running = false;

    Simulation(View v) {
//        this.theModel = m;
        this.theView = v;
        this.startButton = new JButton("START");
        this.stopButton = new JButton("STOP");
        this.setLayout(new GridBagLayout());
        GridBagConstraints coordinates = new GridBagConstraints();
        coordinates.gridx = 0;
        coordinates.gridy = 0;
        this.add(startButton, coordinates);
        coordinates.gridx = 1;
        coordinates.gridy = 0;
        this.add(stopButton, coordinates);
        coordinates.gridx = 0;
        coordinates.gridy = 1;
        JLabel startPos = new JLabel("Agent's start position");
        this.add(startPos, coordinates);
        coordinates.gridx = 1;
        coordinates.gridy = 1;
        agentStartPose = new JTextField("20,20,0");
        this.add(agentStartPose, coordinates);

        // buttons
        this.startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String pose = agentStartPose.getText();
                //theModel.setAgent(pose); // set pose of Agent
                Simulation.running = true;
            }
        });
        this.stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Simulation.running = false;
            }
        });
    }

    /**
     * Start a new thread
     */
    public synchronized void start() {
        this.thread = new Thread(this);
        thread.start();
    }

    /**
     * Stops the thread
     */
    public synchronized void stop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("thread failed to join..");
            e.printStackTrace();
        }
    }

    /**
     * Infinite loop that updates the model and repaints
     */
    public void run() {
        while (true) {
            if (Simulation.running) {
                Model.updateModel(10); // update model
                this.theView.updateUI(); // update view
            }
            try {
                Thread.sleep(sleepTime );
            } catch (InterruptedException e) {
                System.err.println("Thread intercepted...");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
    }
}