import java.util.Random;

public class Rover {

    private final double radius;
    private Point2D position;
    private double speed;
    private double angle;
    private double angularVelocity;
    public Route route = new Route();
    private final Type type;

    public double getRadius() { return radius; }
    public Point2D getPosition() { return position; }
    public double getSpeed() { return speed; }
    public double getAngle() { return angle; }
    public double getAngularVelocity() { return angularVelocity; }
    public Type getType() { return type; }

    public void setPosition(Point2D position) {
        this.position = position;
        this.speed = Constants.ROVER_INIT_SPEED;
        this.angle = new Random().nextDouble() * 2 * Math.PI;
    }

    public Rover (Point2D position, double radius, Type type) {
        this.position = position;
        this.radius = radius;
        this.type = type;
        findRoute();
    }

    public enum Type {
        ROVER, OBSTACLE
    }

    public void findRoute(){
        this.route.radius = (speed / angularVelocity) + radius;

        double relativeAngle;
        if (angularVelocity >= 0){
            relativeAngle = angle + (Math.PI / 2);
        }
        else{
            relativeAngle = angle - (Math.PI / 2);
        }
        Point2D relativeVector = new Point2D(Math.cos(relativeAngle) * route.radius, Math.sin(relativeAngle) * route.radius);

        this.route.position = Point2D.vecAdd(position, relativeVector);
    }
}
