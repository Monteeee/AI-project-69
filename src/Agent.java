public class Agent {

    private final double radius;
    private Point2D position;
    private double speed;
    private double angle;
    private double angularVelocity;

    public double getRadius() { return radius; }
    public Point2D getPosition() { return position; }
    public double getSpeed() { return speed; }
    public double getAngle() { return angle; }
    public double getAngularVelocity() { return angularVelocity; }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public Agent (Point2D position, double radius) {
        this.position = position;
        this.radius = radius;
    }
}
