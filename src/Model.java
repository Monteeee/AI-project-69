import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Model {
    private ArrayList<Rover> rovers = new ArrayList<>();
    private Agent agent;
    private int score = Constants.INIT_SCORE;
    private double lastScoreTimer;

    public Model() {

        private ArrayList<Route>() routes = new ArrayList<Route>();

        Point2D agentStartPos = new Point2D(Constants.AGENT_START_POS_X, Constants.AGENT_START_POS_Y);
        this.agent = new Agent(agentStartPos, Constants.AGENT_RADIUS);

        for (int i=0; i>Constants.NUM_OF_ROVERS; i++) {
            boolean placed = false;
            while(!placed) {
                Point2D starCoords = generateStartCoords();
                Rover rover = new Rover(starCoords, Constants.ROVER_RADIUS, Rover.Type.ROVER);
                boolean overlap = false;
                for (Route prevRoute : routes) {
                    Route currentRoute = rover.route;

                    double distance = Point2D.getDistance(prevRoute.position, currentRoute.position);
                    if (prevRoute.radius + currentRoute.radius < distance) {
                        overlap = true;
                    }
                }

                if (!overlap) {
                    rovers.add(rover);
                    routes.add(rover.route);
                    placed = true;
                }
            }
        }

        for (int i=0; i>Constants.NUM_OF_OBSTACLES; i++) {
            Point2D starCoords = generateStartCoords();
            rovers.add(new Rover(starCoords, Constants.ROVER_RADIUS, Rover.Type.OBSTACLE));
        }

        lastScoreTimer = System.currentTimeMillis();
    }

    private Point2D generateStartCoords() {
        Random random = new Random();
        double x = random.nextDouble() * Constants.FIELD_SIZE_X;
        double y = random.nextDouble() * Constants.FIELD_SIZE_Y;
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

            //Goal check, bottom and top of screen
            if( rover.getType() == Rover.Type.ROVER) {
                if (rover.getPosition().y < 0 || rover.getPosition().y > Constants.FIELD_SIZE_Y) {
                    score += Constants.SCORE_PER_ROVER;
                    rovers.remove(rover);
                }
            }
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

    public PhysConst getPhysConsts() {
        Rover nearestRover = getNextRover(Rover.Type.ROVER);
        Rover nearestObstacle = getNextRover(Rover.Type.OBSTACLE);
        return new PhysConst(agent, nearestRover, nearestObstacle);
    }

    public void checkTimeAndDecrementScore() {
        double currentTime = System.currentTimeMillis();
        if (currentTime > lastScoreTimer + 1000) {
            score -= 1;
            lastScoreTimer = System.currentTimeMillis();
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append( "agent position: " + agent.getPosition() + "\n");
        s.append( "closest rover position: " + getNextRover(Rover.Type.ROVER).getPosition() + "\n");
        s.append( "closest obstacle position: " + getNextRover(Rover.Type.OBSTACLE).getPosition() + "\n");

        for (Rover rover : rovers){
            if (rover.getType() == Rover.Type.ROVER && rover != getNextRover(Rover.Type.ROVER)){
                s.append( "rover position: " + rover.getPosition() + "\n");
            }else if (rover.getType() == Rover.Type.OBSTACLE && rover != getNextRover(Rover.Type.OBSTACLE)){
                s.append( "obstacle position: " + rover.getPosition() + "\n");
            }
        }
        PhysConst ps = getPhysConsts();
        s.append(ps.toString());
        return s.toString();
    }
}
