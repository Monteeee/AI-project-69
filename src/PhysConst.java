import com.sun.corba.se.impl.io.TypeMismatchException;

public class PhysConst {

    // position of target
    public Point2D pTar;

    // velocity of target
    public Point2D vTar;

    // speed of target
    public double sTar;

    // position of robot
    public Point2D p;

    // velocity of robot
    public Point2D v;

    // speed of robot
    public double s;

    // relative position from robot to target
    public Point2D pRt;

    // angle of pRt
    public double pSi;

    // angle of vTar
    public double thetaTar;

    //angle of v
    public double theta;


    public PhysConst(Agent robot, Rover target){

        // position of target
        this.pTar = target.getPosition();

        // velocity of target
        this.vTar = new Point2D(target.getSpeed() * Math.cos(target.getAngle()), target.getSpeed() * Math.sin(target.getAngle()));

        // speed of target (scalar)
        this.sTar =  10.0 * target.getSpeed();

        // position of robot
        this.p = robot.getPosition();

        // velocity of robot
        this.v = new Point2D(robot.getSpeed() * Math.cos(robot.getAngle()),robot.getSpeed() * Math.sin(robot.getAngle()));

        this.s = robot.getSpeed();

        // relative position from robot to target
        this.pRt = (Point2D.relPos(pTar, p));

        // angle of pRt
        this.pSi = Math.atan2(pRt.x, pRt.y);

        // angle of vTar
        this.thetaTar = target.getAngle();

        //angle of v
        this.theta = robot.getAngle();
    }

    public ObstacleConst getObstacleConst(Rover obstacle) {
        if (obstacle.getType() == Rover.Type.OBSTACLE) {
            return new ObstacleConst(obstacle, p, pTar);
        }
        else throw new TypeMismatchException("Rover was expected to be an obstacle, but was not.");
    }

    @Override
    public String toString() {
        return "PhysConst{" +
                "\npTar=" + pTar +
                "\n vTar=" + vTar +
                "\n p=" + p +
                "\n v=" + v +
                "\n pRt=" + pRt +
                "\n pSi=" + pSi +
                "\n thetaTar=" + thetaTar +
                "\n theta=" + theta +
                '}';
    }
}
