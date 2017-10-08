public class DecisonPlanning {
    // not sure where these constant parameter should be placed
    public static final double c1 = 2; // unit cost for d1
    public static final double c2 = 0; // unit cost for d2
    public static final double c3 = 0; // unit cost for n_obs
    public static final double w_d = 8; // width of danger zone
    public static final double kz1 = 1; // scaling parameter in velocity planning
    public static final double kz2 = 1; // scaling parameter in velocity planning

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
    public static boolean Decision(Agent robot, Arraylist<Rover> target_list, ArrayList<Rover> obstacle_list)
    {
        if (current_target == -1)
        {
            if ( target_list.size() == 0 )
            {
                System.err.println("no target left, game should have ended!");
                return false;
            }
            else if ( target_list.size() == 1 )
            {
                robot.setCurrent_target(0);
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
            }
        }

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
        if ( p_tar.x < Constants.FIELD_SIZE_X/2.0  )
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



}
