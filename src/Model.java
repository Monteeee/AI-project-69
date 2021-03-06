import java.util.ArrayList;


public class Model {
    private static int numOfPlacedRovers = -1;

    private static int numOfUpdates = 0;


    private static ArrayList<Rover> rovers = new ArrayList<>();
//    private ArrayList<Rover>
    private static Agent agent;
    private static int score = Constants.INIT_SCORE;
    private static double lastScoreTimer;
    private static ArrayList<Route> routes = new ArrayList<Route>();
    public static boolean targetFleeOnCollsion = false;
    private static boolean targetMoveOutwardsOnCollision = true;

    public static boolean findNewTarget = true;
    public static int upOrDown =0;
    public static Rover optimalTarget;

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
//            Point2D starCoords = fixedStartCoords();

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

    private static Point2D fixedStartCoords(){
        double x = 0;
        double y = 0;

        System.out.println(numOfPlacedRovers);
        int min = 300;
        int max = 700;

        switch (numOfPlacedRovers){
            case 0:
                x = min;
                y = min;
                break;
            case 1:
                x = max;
                y = min;
                break;
            case 2:
                x = min;
                y = max;
                break;
            case 3:
                x = max;
                y = max;
                break;
        }
        numOfPlacedRovers++;
        return new Point2D(x, y);

    }

    private static Point2D generateStartCoords() {
/*
        numOfPlacedRovers++;
        Point2D[] roverPositions = new Point2D[] {
            new Point2D(700, 700),
            new Point2D(350, 300),
            new Point2D(750, 300),
            new Point2D(300, 700),
        };
*/
        numOfPlacedRovers++;
        Point2D[] roverPositions = new Point2D[] {
                new Point2D(600, 700),
                new Point2D(450, 400),
                new Point2D(650, 400),
                new Point2D(400, 700),

                new Point2D(500, 800),
                new Point2D(250, 200),
                new Point2D(550, 200),
                new Point2D(800, 500),

        };

        //Random random = new Random();
        //double x = random.nextDouble() * Constants.FIELD_SIZE_X;
        //double y = random.nextDouble() * Constants.FIELD_SIZE_Y;
        return roverPositions[numOfPlacedRovers];
    }


    public static void updateModel(double deltaTime) {
        numOfUpdates++;
        System.out.println("numOfUpdates: " + numOfUpdates);

        if (rovers.size() == 0){
            System.out.println("number of updates: " + numOfUpdates);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

        if (findNewTarget) {
            double[] cost = DecisonPlanning.makeDecision(4);

            ArrayList<Rover> target_rovers = getRoversByType(Rover.Type.TARGET);

            optimalTarget = target_rovers.get( (int) cost[1] );

            upOrDown = (int) cost[2]; // 1 is lower field
            System.out.print("Direction [up is 1]: ");
            System.out.println(upOrDown);
            findNewTarget = false;
        }
        DecisonPlanning.VelocityPlanning(optimalTarget);
//        DecisonPlanning.simplePlanning(optimalTarget);
//        DecisonPlanning.VelocityPlanning(getNextRover(Rover.Type.TARGET));
//        DecisonPlanning.simplePlanning(getNextRover(Rover.Type.TARGET));

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

            //Goal check, bottom and top of screen
            if( rover.getType() == Rover.Type.TARGET) {
                if (rover.getPosition().y < 0 || rover.getPosition().y > Constants.FIELD_SIZE_Y) {
                    score += Constants.SCORE_PER_ROVER;
                    rovers.remove(rover);
                    findNewTarget = true;
                }
                //collision handling with robot
                double targetAgentDist = Point2D.getDistance(agent.getPosition(), rover.getPosition());

                // if target flee on collision and target within proximity
                if (targetFleeOnCollsion && targetAgentDist <= agent.getRadius() + rover.getRadius()+ Constants.FLEE_DISTANCE) {
                    // vector from agent towards target
                    Point2D relPos = Point2D.relPos(rover.getPosition(), agent.getPosition());
                    // set flee angle
                    rover.setAngle(Math.atan2(relPos.x, relPos.y));
                }
                // if target Move Outwards On Collision and target within radius
                else if(targetMoveOutwardsOnCollision && targetAgentDist <= agent.getRadius() + rover.getRadius()){
                    Point2D direction;
                    // vector outwards
                    if (upOrDown == 1){
                        // set direction towards upper bound
                        direction = new Point2D(0, 1);
                    }
                    else{
                        // set direction towards lower bound
                        direction = new Point2D(0, -1);
                    }
                    // set outwards angle
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
