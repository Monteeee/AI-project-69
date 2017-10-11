import java.util.ArrayList;
import java.util.Random;

public class Model {
    private static ArrayList<Rover> rovers = new ArrayList<>();
//    private ArrayList<Rover>
    private static Agent agent;
    private static int score = Constants.INIT_SCORE;
    private static double lastScoreTimer;
    private static ArrayList<Route> routes = new ArrayList<Route>();
    public static boolean targetFleeOnCollsion = false;
    private static boolean targetMoveOutwardsOnCollision = false;

    public static ArrayList<Rover> getRovers() { return rovers; }
    public static ArrayList<Route> getRoutes() { return routes; }

    // some function to place agent might be needed.

    public static Agent getAgent() { return agent; }

    public static void initializeModel() {
        Point2D agentStartPos = new Point2D(Constants.AGENT_START_POS_X, Constants.AGENT_START_POS_Y);
        agent = new Agent(agentStartPos, Constants.AGENT_RADIUS);
        agent.setSpeed(Constants.AGENT_INIT_SPEED);
        agent.setAngle(Constants.AGENT_INIT_ANGULAR_VELOCITY);

        for (int i=0; i<Constants.NUM_OF_ROVERS; i++) {
            placeRover(Rover.Type.TARGET);
        }

        for (int i=0; i<Constants.NUM_OF_OBSTACLES; i++) {
            placeRover(Rover.Type.OBSTACLE);
        }

        lastScoreTimer = System.currentTimeMillis();
    }

    private static void placeRover(Rover.Type type) {
        boolean placed = false;
        while(!placed) {

            Point2D starCoords = generateStartCoords();
            Rover rover;
            if (type == Rover.Type.OBSTACLE){
                rover = new Rover(starCoords, Constants.OBSTACLE_RADIUS, type);
            }
            else {
                rover = new Rover(starCoords, Constants.TARGET_RADIUS, type);
            }
            Route currentRoute = rover.route;

            // check if rover's route goes out of bounds
            boolean outOfBounds = false;
            if (Point2D.outOfBounds(currentRoute.position, currentRoute.radius)){
                outOfBounds = true;
            }

            // check if rover's route overlaps another rovers route
            boolean overlap = false;
            for (Route prevRoute : routes) {
                double distance = Point2D.getDistance(prevRoute.position, currentRoute.position);

                if (prevRoute.radius + currentRoute.radius > distance) {
                    overlap = true;
                }
            }
            if (!overlap && !outOfBounds) {
                rovers.add(rover);
                routes.add(rover.route);
                placed = true;
            }
        }
    }

    private static Point2D generateStartCoords() {
        Random random = new Random();
        double x = random.nextDouble() * Constants.FIELD_SIZE_X;
        double y = random.nextDouble() * Constants.FIELD_SIZE_Y;
        return new Point2D(x, y);
    }


    public static void updateModel(double deltaTime) {
        DecisonPlanning.VelocityPlanning(getNextRover(Rover.Type.TARGET));

        // System.out.println(agent.getPosition().toString());

        Point2D newAgentPos = new Point2D(
                agent.getPosition().x + agent.getSpeed()*deltaTime*Math.sin(agent.getAngle()),
                agent.getPosition().y + agent.getSpeed()*deltaTime*Math.cos(agent.getAngle()));
        agent.setPosition(newAgentPos);

        for (int i=0; i<rovers.size(); i++) {
            Rover rover = rovers.get(i);
            Point2D newRoverPos = new Point2D(
                    rover.getPosition().x + rover.getSpeed()*deltaTime*Math.sin(rover.getAngle()),
                    rover.getPosition().y + rover.getSpeed()*deltaTime*Math.cos(rover.getAngle()));
            rover.updatePosition(newRoverPos, deltaTime);

//            rover.setPosition(newRoverPos);

            //Goal check, bottom and top of screen
            if( rover.getType() == Rover.Type.TARGET) {
                if (rover.getPosition().y < 0 || rover.getPosition().y > Constants.FIELD_SIZE_Y) {
                    score += Constants.SCORE_PER_ROVER;
                    rovers.remove(rover);
                }

                //System.out.println("relPos: " + Point2D.getDistance(agent.getPosition(), rover.getPosition()));

                //collision handling with robot
                double targetAgentDist = Point2D.getDistance(agent.getPosition(), rover.getPosition());
                if (targetFleeOnCollsion && targetAgentDist <= agent.getRadius() + rover.getRadius()+ Constants.FLEE_DISTANCE) {
                    // vector from agent towards target
                    Point2D relPos = Point2D.relPos(rover.getPosition(), agent.getPosition());
                    rover.setAngle(Math.atan2(relPos.x, relPos.y));
                }
                else if(targetMoveOutwardsOnCollision && targetAgentDist <= agent.getRadius() + rover.getRadius()){
                    Point2D direction;
                    if (rover.getPosition().isUpperField()){
                        direction = new Point2D(0, 1);
                    }
                    else{
                        direction = new Point2D(0, -1);
                    }
                    rover.setAngle(Math.atan2(direction.x, direction.y));
                }

                else if (!targetFleeOnCollsion && targetAgentDist <= agent.getRadius() + rover.getRadius()) {
                    rovers.remove(rover);
                }
            }
        }
    }

    public static ArrayList<Rover> getRoversByType(Rover.Type type){
        ArrayList<Rover> ret = new ArrayList<>();
        for (Rover rover : rovers){
            if (rover.getType() == type){
                ret.add(rover);
            }
        }
        return ret;
    }

    public static Rover getNextRover(Rover.Type type) {
        double shortestDist = Double.MAX_VALUE;
        Rover outRover = rovers.get(0);
        for (int i=0; i<rovers.size(); i++) {
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

    public static PhysConst getPhysConsts(Rover target) {
        return new PhysConst(agent, target);
    }


    public static void checkTimeAndDecrementScore() {
        double currentTime = System.currentTimeMillis();
        if (currentTime > lastScoreTimer + 1000) {
            score -= 1;
            lastScoreTimer = System.currentTimeMillis();
        }
    }

    public static String toStr() {
        StringBuilder s = new StringBuilder();

        s.append( "agent position: " + agent.getPosition() + "\n");
        s.append( "closest rover position: " + getNextRover(Rover.Type.TARGET).getPosition() + "\n");
        s.append( "closest obstacle position: " + getNextRover(Rover.Type.OBSTACLE).getPosition() + "\n");

        for (Rover rover : rovers){
            if (rover.getType() == Rover.Type.TARGET && rover != getNextRover(Rover.Type.TARGET)){
                s.append( "rover position: " + rover.getPosition() + "\n");
            }else if (rover.getType() == Rover.Type.OBSTACLE && rover != getNextRover(Rover.Type.OBSTACLE)){
                s.append( "obstacle position: " + rover.getPosition() + "\n");
            }
        }
        PhysConst ps = getPhysConsts(getNextRover(Rover.Type.TARGET));
        s.append(ps.toString());
        return s.toString();
    }
}
