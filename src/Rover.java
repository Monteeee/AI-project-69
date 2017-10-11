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

    public Point2D getChasePosition(){
        Point2D chasePos;
        // if on the upper side of the field
        if (getPosition().isUpperField()){
            // chase agent from underneath
            chasePos = new Point2D( getPosition().x, getPosition().y - Constants.FLEE_DISTANCE * 2);
        }
        // if on the lower side of the field
        else{
            // chase agent from above
            chasePos = new Point2D( getPosition().x, getPosition().y + Constants.FLEE_DISTANCE * 2);
        }
        return chasePos;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void updatePosition(Point2D position, double deltatime){
        this.position = position;
        this.speed = Constants.ROVER_INIT_SPEED;
        this.angle += angularVelocity * deltatime;
    }

    public Rover (Point2D position, double radius, Type type) {
        setPosition(position);
        this.radius = radius;
        this.type = type;
        this.speed = Constants.ROVER_INIT_SPEED;
        this.angularVelocity = Constants.ROVER_INIT_ANGULAR_VELOCITY;
        this.angle = new Random().nextDouble() * 2 * Math.PI;
        findRoute();
    }

    public enum Type {
        TARGET, OBSTACLE
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
        Point2D relativeVector = new Point2D(Math.sin(relativeAngle) * (route.radius - radius), Math.cos(relativeAngle) * (route.radius - radius));

        this.route.position = Point2D.vecAdd(position, relativeVector);
    }
}


// angle += angularvelocity * deltatime