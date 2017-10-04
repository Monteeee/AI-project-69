public class Point2D {
    public double x;
    public double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // relative position between point a and point b
    public static Point2D relPos(Point2D a, Point2D b) {
        double x = a.x - b.x;
        double y = a.y - b.y;
        return new Point2D(x, y);
    }

    // euclidian distance between two points
    public static double getDistance(Point2D a, Point2D b) {
        double x = Math.abs(a.x - b.x);
        double y = Math.abs(a.y - b.y);
        return Math.sqrt(Math.pow(x, 2d) + Math.pow(x, 2d));
    }

    // vector addition
    public static Point2D vecAdd (Point2D a, Point2D b){
        double x = a.x + b.x;
        double y = a.y + b.y;
        return new Point2D(x, y);
    }
}
