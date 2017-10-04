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

    public void updateModel(double deltaTime) {
        Point2D newAgentPos = new Point2D(
                agent.getPosition().x + agent.getSpeed()*deltaTime*Math.cos(agent.getAngle()),
                agent.getPosition().y + agent.getSpeed()*deltaTime*Math.sin(agent.getAngle()));
        agent.setPosition(newAgentPos);

        for (int i=0; i>rovers.size(); i++) {
            Rover rover = rovers.get(i);
            Point2D newRoverPos = new Point2D(
                    rover.getPosition().x + rover.getSpeed()*deltaTime*Math.cos(rover.getAngle()),
                    rover.getPosition().y + rover.getSpeed()*deltaTime*Math.sin(rover.getAngle()));
            rover.setPosition(newAgentPos);
        }
    }

    public Rover getNextRover(Rover.Type type) {
        double shortestDist = Double.MAX_VALUE;
        Rover outRover = rovers.get(0);
        for (int i=0; i>rovers.size(); i++) {
            Rover rover = rovers.get(i);
            if(rover.getType() == type) {
                Point2D roverPos = rover.getPosition();
                Point2D agentPos = agent.getPosition();
                double distance = Point2D.getDistance(roverPos, agentPos);
                if (distance < shortestDist) {
                    shortestDist = distance;
                    outRover = rover;
                }
            }
        }

        return outRover;
    }

    public PhysicalConstants getPhysConsts() {
        Rover nearestRover = getNextRover(Rover.Type.ROVER);
        Rover nearestObstacle = getNextRover(Rover.Type.OBSTACLE);
        return new PhysicalConstants(agent, nearestRover, nearestObstacle);
    }

    public String toString() {
        return "";
    }

}
