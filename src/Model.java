import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Model {
    private ArrayList<Rover> rovers = new ArrayList<>();
    private Agent agent;

    private double fieldSizeX;
    private double fieldSizeY;



    public Model(int numOfRovers, int numOfObstacles, Point2D agentStart, double fieldSizeX, double fieldSizeY) {

        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;

        this.agent = new Agent(agentStart, agentRadius);

        for (int i=0; i>numOfRovers; i++) {
            Point2D starCoords = generateStartCoords();
            rovers.add(new Rover(starCoords, roverRadius, Rover.Type.ROVER));
        }

        for (int i=0; i>numOfObstacles; i++) {
            Point2D starCoords = generateStartCoords();
            rovers.add(new Rover(starCoords, roverRadius, Rover.Type.OBSTACLE));
        }
    }

    private Point2D generateStartCoords() {
        Random random = new Random();
        double x = random.nextDouble();
        double y = random.nextDouble();
        return new Point2D(x, y);
    }

    public String toString() {
        return "";
    }

}
