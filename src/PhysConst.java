public class PhysConst {

    // position of target
    public Point2D pTar;

    // velocity of target
    public Point2D vTar;

    // position of robot
    public Point2D p;

    // velocity of robot
    public Point2D v;

    // position of the obstacle
    public Point2D pObs;

    // velocity of the obstacle
    public Point2D vObs;

    // radius of the obstacle
    public double rObs;

    // relative position from robot to target
    public Point2D pRt;

    // relative position from robot to obstacle
    public Point2D pRo;

    // relative position from obstacle to target
    public Point2D pOt;

    // angle of pRt
    public double pSi;

    // angle of pRo
    public double thetaRo;

    // angle of pOt
    public double thetaOt;

    // angle of vTar
    public double thetaTar;

    // angle of vObs
    public double thetaObs;

    //angle of v
    public double theta;


    public PhysConst(Agent robot, Rover target, Rover obstacle){

        this.pTar = target.getPosition();

        this.vTar = new Point2D(target.getSpeed() * Math.cos(target.getAngle()), target.getSpeed() * Math.sin(target.getAngle()));

        this.p = robot.getPosition();

        this.v = new Point2D(robot.getSpeed() * Math.cos(robot.getAngle()),robot.getSpeed() * Math.sin(robot.getAngle()));

        this.pObs = obstacle.getPosition();

        this.vObs = new Point2D(obstacle.getSpeed() * Math.cos(obstacle.getAngle()), obstacle.getSpeed() * Math.sin(obstacle.getAngle()));

        this.rObs = obstacle.getRadius();

        this.pRt = (Point2D.relPos(pTar, p));

        this.pRo = Point2D.relPos(robot.getPosition(), obstacle.getPosition());

        this.pOt = Point2D.relPos(obstacle.getPosition(), target.getPosition());

        this.pSi = Math.atan(pRt.y / pRt.x);

        this.thetaRo = Math.atan(pRo.y / pRo.x);

        this.thetaOt = Math.atan(pOt.y / pOt.x);

        this.thetaTar = target.getAngle();

        this.thetaObs = obstacle.getAngle();

        this.theta = robot.getAngle();
    }
}
