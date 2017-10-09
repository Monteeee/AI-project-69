import java.util.ArrayList;

public class DecisonPlanning {
    // not sure where these constant parameter should be placed
    public static final double c1 = 2; // unit cost for d1
    public static final double c2 = 0; // unit cost for d2
    public static final double c3 = 0; // unit cost for n_obs
    public static final double w_d = 8; // width of danger zone
    public static final double kz1 = 1; // scaling parameter in velocity planning
    public static final double kz2 = 1; // scaling parameter in velocity planning
    public static final double v_max = 5.0; // maximum speed of robot/agent
    public static final double rho_z = 8d*5;

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
    public static boolean Decision(Agent robot, ArrayList<Rover> target_list, ArrayList<Rover> obstacle_list)
    {
        if (robot.getCurrent_target() == -1)
        {
            if ( target_list.size() == 0 )
            {
                System.err.println("no target left, game should have ended!");
                return false;
            }
            else if ( target_list.size() == 1 )
            {
                robot.setCurrent_target(0);
                return true;
            }
            else{
                double min_all = 99999999;
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
    // d2: distance from target to closest boarder
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
            if ( P_to_L_distance(a, b, c, obstacle_list.get(i).getPosition() ) <  w_d / 2)
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

    private static void VelocityPlanning(Agent robot, Rover target, ArrayList<Rover> obstacle_list)
    {
        double v_pl = 0;
        double hd_pl = 0;

        int obs_num = obstacle_list.size();
        ArrayList obs_in = new ArrayList<>();
        ArrayList rho_in = new ArrayList<>();
        double rho_temp;
        int obs_in_num = 0;
        for (int i = 0; i < obs_num; i++)
        {
            rho_temp = Point2D.getDistance(obstacle_list.get(i).getPosition(), robot.getPosition()) - obstacle_list.get(i).getRadius();
            if (rho_temp < rho_z)
            {
                obs_in.add(obstacle_list.get(i));
                rho_in.add(rho_temp);
                obs_in_num = obs_in_num + 1;
            }
        }

        double v_temp;
        double hd_temp;
        double v_tar = target.getSpeed();
        Point2D p_rt = Point2D.relPos(robot.getPosition(), target.getPosition());
        double hd_tar = target.getAngle();
        double ang_rt = p_rt.getAngle();

        if (obs_in_num == 0)
        {
            v_temp = Math.pow(v_tar,2d) + 2.0*kz1*p_rt.getLength()*v_tar*Math.cos(hd_tar-ang_rt) + Math.pow(kz1,2d)*p_rt.getLength();
            v_temp = Math.sqrt(v_temp);

            v_pl = Math.min( v_temp , v_max );
            hd_pl = ang_rt + Math.asin( v_tar * Math.sin(hd_tar-ang_rt)/robot.getSpeed() );

            robot.setSpeed(v_pl);
            robot.setAngle(hd_pl);
        }
        else{
            if (obs_in_num == 1)
            {
                Point2D p_ro_1 = Point2D.relPos(robot.getPosition(), obstacle_list.get(0).getPosition());
                if ( p_ro_1.x / p_rt.x - p_ro_1.y / p_rt.y < 0.00001 )
                {
                    if ( Math.abs(p_rt.x) > Math.abs(p_rt.y) )
                    {
                        Point2D new_target = new Point2D(p_rt.x, 0.0);
                        p_rt = new_target;
                        ang_rt = p_rt.getAngle();
                    }
                    else{
                        Point2D new_target = new Point2D(0.0, p_rt.y);
                        p_rt = new_target;
                        ang_rt = p_rt.getAngle();
                    }
                }
            }

            double eta_i;
            double beta_i;
            double item1 = 0;
            double item2 = 0;
            double item3 = 0;
            double hat_ang_rt;
            Point2D p_ro_i = new Point2D(0,0);
            for (int i = 0; i < obs_in_num; i++)
            {
                p_ro_i = Point2D.relPos( robot.getPosition(), obs_in.get(i).getPosition() );
                eta_i = kz2 / ( Math.pow(rho_in.get(i), 2d) * p_ro_i.getLength() ) * ( 1.0/rho_in.get(i) - 1.0/rho_z );
                beta_i = (eta_i * p_ro_i.getLength()) / (kz1 * p_rt.getLength());

                item1 = item1 + beta_i * Math.sin(p_ro_i.getAngle());
                item2 = item2 + beta_i * Math.cos(p_ro_i.getAngle());

                item3 = item3 + beta_i * obstacle_list.get(i).getSpeed() * Math.cos( obstacle_list.get(i).getAngle() - p_ro_i.getAngle() );
            }

            hat_ang_rt = Math.atan2( Math.cos(ang_rt) - item2 , Math.sin(ang_rt) - item1 );

            v_temp = v_tar*Math.cos(hd_tar-ang_rt) - item3 + kz1 * p_rt.getLength();
            v_temp = Math.pow(v_temp, 2d) + Math.pow(v_tar,2d) * Math.pow( Math.sin(hd_tar-hat_ang_rt) , 2d);
            v_temp = Math.sqrt(v_temp);

            v_pl = Math.min( v_temp , v_max );
            hd_pl = hat_ang_rt + Math.asin( v_tar * Math.sin(hd_tar-hat_ang_rt) / robot.getSpeed() );

            robot.setSpeed(v_pl);
            robot.setAngle(hd_pl);
        }
    }

}
