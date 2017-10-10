import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;

public class DecisonPlanning {
    // not sure where these constant parameter should be placed
    public static final double c1 = 2; // unit cost for d1
    public static final double c2 = 0; // unit cost for d2
    public static final double c3 = 0; // unit cost for n_obs
    public static final double widthOfDangerZone = 1; // width of danger zone
    public static final double scalingParam1 = 1; // scaling parameter in velocity planning
    public static final double scalingParam2 = 0.11; // scaling parameter in velocity planning
    public static final double Vmax = 0.3; // maximum speed of robot/agent
    public static final double influenceOfRange = 4d*5; //influence range

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

    public static void VelocityPlanning(Rover target) {

        Agent robot = Model.getAgent();

        PhysConst PC = Model.getPhysConsts();

        // temporary robot angle
        double tempTheta = 0;

        // obstacles in range
        ArrayList<Rover> obsInRange = new ArrayList<>();

        // distance from robot to outer radius of the obstacle
        ArrayList<Double> distOfObs = new ArrayList<>();

        double tempRho;

        for (Rover obstacle : Model.getRoversByType(Rover.Type.OBSTACLE))
        {
            tempRho = Point2D.getDistance(obstacle.getPosition(), robot.getPosition()) - obstacle.getRadius();

            if (tempRho < influenceOfRange) {
                obsInRange.add(obstacle);
                distOfObs.add(tempRho);
            }
        }

        // temporary robot velocity scalar
        double tempV = 0;

        // speed of target
        double vTarScalar = 10.0 * target.getSpeed();

        // pRT = relative position from robot to target
        // thetaTar = angle of target velocity
        // pSi = angle of the relative position from robot to target

        if (obsInRange.isEmpty())
        {
            tempV = Math.pow(vTarScalar, 2d) + 2.0 * scalingParam1 * PC.pRt.getLength() * vTarScalar * Math.cos( PC.thetaTar - PC.pSi )
                    + Math.pow(scalingParam1 * PC.pRt.getLength(), 2d);

            tempV = Math.sqrt(tempV) / 10.0;

            tempV = Math.min( tempV , Vmax);

            tempTheta = PC.pSi + Math.asin( vTarScalar/10.0 * Math.sin(PC.thetaTar-PC.pSi)/tempV );

            robot.setSpeed(tempV);
            robot.setAngle(tempTheta);
        }
        else{
            // for avoiding local minima
            if (obsInRange.size() == 1) {

                // relative position from robot to one obstacle
                Point2D pRoi = Point2D.relPos(obsInRange.get(0).getPosition(), robot.getPosition());

                // if target and obstacle are on the same vector relative to the robot
                if ( Math.abs( pRoi.x / PC.pRt.x - pRoi.y / PC.pRt.y ) < 0.00001 ) {

                    //
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

            //
            double pSiHat;
            Point2D pRoi = new Point2D(0,0);

            for (int i = 0; i < obsInRange.size(); i++) {

                // relative position from robot to one obstacle
                pRoi = Point2D.relPos( obsInRange.get(i).getPosition(), robot.getPosition() );


                etaI = scalingParam2 / ( Math.pow(distOfObs.get(i), 2d) * pRoi.getLength() ) * ( 1.0/distOfObs.get(i) - 1.0/ influenceOfRange);

                betaI = (etaI * pRoi.getLength()) / (scalingParam1 * PC.pRt.getLength());

                item1 = item1 + betaI * Math.sin(pRoi.getAngle());

                item2 = item2 + betaI * Math.cos(pRoi.getAngle());

                item3 = item3 + betaI * obsInRange.get(i).getSpeed() * Math.cos( obsInRange.get(i).getAngle() - pRoi.getAngle() );
            }

            pSiHat = Math.atan2( Math.cos(PC.pSi) - item2 , Math.sin(PC.pSi) - item1 );

            tempV = vTarScalar*Math.cos(PC.thetaTar-PC.pSi) - item3 + scalingParam1 * PC.pRt.getLength();
            tempV = Math.pow(tempV, 2d) + Math.pow(vTarScalar,2d) * Math.pow( Math.sin(PC.thetaTar-pSiHat) , 2d);
            tempV = Math.sqrt(tempV);

            tempV = Math.min( tempV , Vmax);
            tempTheta = pSiHat + Math.asin( vTarScalar * Math.sin(PC.thetaTar-pSiHat) / robot.getSpeed() );

            robot.setSpeed(tempV);
            robot.setAngle(tempTheta);
        }
    }
}
