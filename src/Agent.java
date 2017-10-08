public class Agent {

    private final double radius;
    private Point2D position;
    private double speed;
    private double angle;
    private double angularVelocity; // i don't think we really need this, angle is directly set
    // current target set
    private int current_target = -1;

    public double getRadius() { return radius; }
    public Point2D getPosition() { return position; }
    public double getSpeed() { return speed; }
    public double getAngle() { return angle; }
    public double getAngularVelocity() { return angularVelocity; }
    public int getCurrent_target() { return current_target; }

    public void setPosition(Point2D position) {
        this.position = position;
    }
    public void setCurrent_target(int target_number) { this.current_target = target_number; }

    public void setSpeed(double speed) { this.speed = speed; }
    public void setAngle(double angle) { this.angle = angle; }

    public Agent (Point2D position, double radius) {
        this.position = position;
        this.radius = radius;
    }

}
