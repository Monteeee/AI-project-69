public class Point2D {
    public double x;
    public double y;


    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString() {
        return "Point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    // length of a vector represented by Point2D
    public double getLength(){
        return Math.sqrt(Math.pow(this.x, 2d) + Math.pow(this.y, 2d));
    }

    // angle of a vector represented by Point2D
    public double getAngle(){
        return Math.atan2(this.x, this.y);
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
        return Math.sqrt(Math.pow(x, 2d) + Math.pow(y, 2d));
    }

    // vector addition
    public static Point2D vecAdd (Point2D a, Point2D b){
        double x = a.x + b.x;
        double y = a.y + b.y;
        return new Point2D(x, y);
    }
}
