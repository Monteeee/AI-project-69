import java.util.ArrayList;

public class DecisonPlanning {
    // not sure where these constant parameter should be placed
    public static final double c1 = 2; // unit cost for d1
    public static final double c2 = 0; // unit cost for d2
    public static final double c3 = 0; // unit cost for n_obs
    public static final double widthOfDangerZone = 1; // width of danger zone
    public static final double scalingParam1 = 0.2; // scaling parameter in velocity planning
    public static final double scalingParam2 = 500000; // scaling parameter in velocity planning
    public static final double Vmax = 6; // maximum speed of robot/agent
    public static final double influenceOfRange = 10d*5; //influence range

    //------------------------

    // Decision part
    // Decision - select the target with least 2nd order heuristic cost
    // Heuristic - compute the heuristic cost based on distances and potential obstacle number
    // P_to_L_distance - helper function to compute the distance from point to line

    // target_list: list of rovers who are labeled as target
    // obstacle_list: list of rovers who are labeled as obstacles
    // min_all: minimum value of all 2nd order heuristic cost
    // ind_all: index of the target with minimum 2nd order heuristic cost
    // h_j: 2nd order heuristic of jth target
    // h_temp: temporary variable to store heuristic
    // min_h: minimum value of the second part of heuristic cost
    // return true -> target already set or target successfully set
    public static boolean Decision(Agent robot, ArrayList<Rover> target_list, ArrayList<Rover> obstacle_list) {

        if (robot.getCurrent_target() == -1) {

            ArrayList targetList = Model.getRoversByType(Rover.Type.TARGET);
            if (targetList.isEmpty()) {

                System.err.println("no target left, game should have ended!");
                return false;
            }
            else if (targetList.size() == 1) {

                robot.setCurrent_target(0);
                return true;

            } else {

                double min_all = Integer.MAX_VALUE;
                int ind_all = 0;
                int target_num = target_list.size();
                double h_j;
                double min_h;
                double h_temp;
                for (int j = 0; j < target_num; j++)
                {
                    // compute the first level of 2nd order heuristic, from robot to first target
                    h_j = Heuristic(robot.getPosition(), target_list.get(j).getPosition(), obstacle_list);
                    min_h = 99999999;
                    for (int i = 0; i < target_num; i++)
                    {
                        if (i == j)
                            continue;
                        // compute the second level of 2nd order heuristic, from first target to second target
                        h_temp = Heuristic(target_list.get(j).getPosition(), target_list.get(i).getPosition(), obstacle_list);
                        if (h_temp < min_h)
                        {
                            min_h = h_temp;
                        }
                    }
                    h_j = h_j + min_h;
                    if (h_j < min_all)
                    {
                        min_all = h_j;
                        ind_all = j;
                    }
                }

                robot.setCurrent_target(ind_all);

                return true;
            }
        }
        return true;
    }

    // p: robot position
    // p_tar: target position
    // heur: heuristic value, the output
    // d1: distance from robot to target
    // d2: distance from target to closest border
    // n_obs: number of obstacles in danger zone
    private static double Heuristic(Point2D p, Point2D p_tar, ArrayList<Rover> obstacle_list)
    {
        double heur = 0.0;
        int n_obs = 0;

        double d1 = Point2D.getDistance(p_tar, p);

        // following lines is to compute the distance of this target to closer boarder
        // here I assume that our game map is built in first quartile,
        // and two boarder are line: x = 0 and line: x =
        double d2;
        if ( p_tar.x < Constants.FIELD_SIZE_X/2.0 )
        {
            d2 = p_tar.x;
        }
        else {
            d2 = Constants.FIELD_SIZE_X - p_tar.x;
        }

        // add items of distance cost into heuristic
        heur = heur + d1 * c1 + d2 * c2;

        // compute potential distance cost represented by obstacles nearby
        double a = p_tar.x - p.x;
        double b = -(p_tar.y - p.y);
        double c = p_tar.y * (p_tar.y - p.y) - p_tar.x * (p_tar.x -  p.x);

        for (int i = 0; i < obstacle_list.size(); i++)
        {
            if ( P_to_L_distance(a, b, c, obstacle_list.get(i).getPosition() ) <  widthOfDangerZone / 2)
            {
                n_obs = n_obs + 1;
            }
        }

        heur = heur + c3 * n_obs;

        return heur;
    }

    private static double P_to_L_distance(double a, double b, double c, Point2D point)
    {
        return ( Math.abs( a*point.x + b*point.y + c ) / Math.sqrt(Math.pow(a, 2d) + Math.pow(b, 2d)) );
    }

    public static void simplePlanning(Rover target) {
        Agent robot = Model.getAgent();
        PhysConst PC = Model.getPhysConsts(target);

        robot.setSpeed(Vmax);
        robot.setAngle(PC.pSi);
    }


    public static void VelocityPlanning(Rover target) {

        // new robot velocity scalar to calculate
        double newV;
        // new robot angle to calculate
        double newTheta;

        Agent robot = Model.getAgent();
        PhysConst PC = Model.getPhysConsts(target);

        // obstacles in range
        ArrayList<Rover> obsInRange = new ArrayList<>();
        int count = 0;
        for (Rover obstacle : Model.getRoversByType(Rover.Type.OBSTACLE)) {
            ObstacleConst OC = PC.getObstacleConst(obstacle);
            count = count + 1;

            // ro = distance from robot to outer radius of the obstacle
            if (OC.ro < influenceOfRange) {
                obsInRange.add(obstacle);
            }
        }
        if (obsInRange.isEmpty()) {
            // pRT = relative position from robot to target
            // thetaTar = angle of target velocity
            // pSi = angle of the relative position from robot to target

            newV = Math.pow(PC.vTar.getLength(), 2d) + 2.0 * scalingParam1 * PC.pRt.getLength() * PC.vTar.getLength() * Math.cos(PC.thetaTar - PC.pSi)
                    + Math.pow(scalingParam1 * PC.pRt.getLength(), 2d);

            newV = Math.sqrt(newV);

            newV = Math.min(newV , Vmax);
            newTheta = PC.pSi + Math.asin( PC.vTar.getLength() * Math.sin(PC.thetaTar - PC.pSi) / newV );

            robot.setSpeed(newV);
            robot.setAngle(newTheta);
        }
        else{
            // for avoiding local minima
            if (obsInRange.size() == 1) {

                ObstacleConst OC = PC.getObstacleConst(obsInRange.get(0));

                // pRo = relative position from robot to obstacle
                // if target and obstacle are on the same vector relative to the robot
                if ( Math.abs( OC.pRo.x / PC.pRt.x - OC.pRo.y / PC.pRt.y ) < 0.01 ) {

                    if ( Math.abs(PC.pRt.x) > Math.abs(PC.pRt.y) )
                    {
                        Point2D newTarget = new Point2D(PC.pRt.x, 0.0);
                        PC.pRt = newTarget;
                        PC.pSi = PC.pRt.getAngle();
                    }
                    else{
                        Point2D newTarget = new Point2D(0.0, PC.pRt.y);
                        PC.pRt = newTarget;
                        PC.pSi = PC.pRt.getAngle();
                    }
                }
            }

            // some intermediate variables
            double etaI;
            double betaI;
            double item1 = 0;
            double item2 = 0;
            double item3 = 0;

            // System.out.println("obstacle number  " + obsInRange.size());
            for (Rover obstacle : obsInRange) {
                ObstacleConst OC = PC.getObstacleConst(obstacle);

                // pRo = relative position from target to obstacle
                etaI = scalingParam2 / ( Math.pow(OC.ro, 2d) * OC.pRo.getLength() ) * ( 1.0/OC.ro - 1.0/ influenceOfRange );

                betaI = (etaI * OC.pRo.getLength()) / (scalingParam1 * PC.pRt.getLength());

                item1 = item1 + betaI * Math.sin(OC.thetaRo);

                item2 = item2 + betaI * Math.cos(OC.thetaRo);

                item3 = item3 + betaI * OC.vObs.getLength() * Math.cos(OC.thetaObs - OC.thetaRo);
            }

            double pSiHat = Math.atan2(Math.sin(PC.pSi) - item1, Math.cos(PC.pSi) - item2 );

            newV = PC.vTar.getLength() * Math.cos( PC.thetaTar - PC.pSi ) - item3 + scalingParam1 * PC.pRt.getLength();

            newV = Math.pow(newV, 2d) + Math.pow( PC.vTar.getLength(), 2d ) * Math.pow( Math.sin(PC.thetaTar-pSiHat), 2d);

            newV = Math.sqrt(newV);

            newV = Math.min(newV , Vmax);

            newTheta = pSiHat + Math.asin( PC.vTar.getLength() * Math.sin(PC.thetaTar-pSiHat) / newV );

            robot.setSpeed(newV);
            robot.setAngle(newTheta);
        }
    }
}
