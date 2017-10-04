public class Route {
    public double radius;
    public Point2D position;

    public Route() {
        this.radius = 0;
        this.position = null;
    }

    public Route(double radius, Point2D position) {
        this.radius = radius;
        this.position = position;
    }
}
