public class Rover {

    private double radius;
    private Point2D position;
    private double speed;
    private double angle;
    private double angularVelocity;
    private Type type;


    public Rover (Point2D position, double radius, Type type) {
        this.position = position;
        this.radius = radius;
        this.type = type;
    }

    public enum Type {
        ROVER, OBSTACLE
    }
}
