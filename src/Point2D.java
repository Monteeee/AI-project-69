import sun.font.PhysicalFont;

public class Point2D {
    public double x;
    public double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Point2D relPos(Point2D a, Point2D b){
        double x = a.x - b.x;
        double y = a.y - b.y;
        return new Point2D(x, y);
    }
}
