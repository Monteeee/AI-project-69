import sun.font.PhysicalFont;

public class Point2D {
    public double x;
    public double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double getDistance(Point2D a, Point2D b) {
        double x = Math.abs(a.x - b.x);
        double y = Math.abs(a.y - b.y);
        return Math.sqrt(Math.pow(x, 2d) + Math.pow(x, 2d));
    }
}
