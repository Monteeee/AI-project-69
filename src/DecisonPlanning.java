public class DecisonPlanning {
    // not sure where these constant parameter should be placed
    public static final double c1 = 2;
    public static final double c2 = 0;
    public static final double c3 = 0;
    public static final double w_d = 8;
    public static final double kz1 = 1;
    public static final double kz2 = 1;

    //------------------------

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
                    h_j = Heuristic(robot.getPosition(), target_list.get(j).getPosition(), obstacle_list);
                    min_h = 99999999;
                    for (int i = 0; i < target_num; i++)
                    {
                        if (i == j)
                            continue;
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
