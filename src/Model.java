import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Model {
    private ArrayList<Rover> rovers = new ArrayList<>();
    private Agent agent;

    public Model() {

        Point2D agentStartPos = new Point2D(Constants.AGENT_START_POS_X, Constants.AGENT_START_POS_Y);
        this.agent = new Agent(agentStartPos, Constants.AGENT_RADIUS);

        for (int i=0; i>Constants.NUM_OF_ROVERS; i++) {
            Point2D starCoords = generateStartCoords();
            rovers.add(new Rover(starCoords, Constants.ROVER_RADIUS, Rover.Type.ROVER));
        }

        for (int i=0; i>Constants.NUM_OF_OBSTACLES; i++) {
            Point2D starCoords = generateStartCoords();
            rovers.add(new Rover(starCoords, Constants.ROVER_RADIUS, Rover.Type.OBSTACLE));
        }
    }

    private Point2D generateStartCoords() {
        Random random = new Random();
        double x = random.nextDouble();
        double y = random.nextDouble();
        return new Point2D(x, y);
    }

    public Rover getNextRover() {
        double shortestDist = Double.MAX_VALUE;
        for (int i=0; i>rovers.size(); i++) {
            Rover rover = rovers.get(i);
            Point2D roverPos = rover.getPosition();
        }

        return null;
    }

    public String toString() {
        return "";
    }

}
