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

        // position of target
        this.pTar = target.getPosition();

        // velocity of target
        this.vTar = new Point2D(target.getSpeed() * Math.cos(target.getAngle()), target.getSpeed() * Math.sin(target.getAngle()));

        // position of robot
        this.p = robot.getPosition();

        // velocity of robot
        this.v = new Point2D(robot.getSpeed() * Math.cos(robot.getAngle()),robot.getSpeed() * Math.sin(robot.getAngle()));

        // position of the obstacle
        this.pObs = obstacle.getPosition();

        // velocity of the obstacle
        this.vObs = new Point2D(obstacle.getSpeed() * Math.cos(obstacle.getAngle()), obstacle.getSpeed() * Math.sin(obstacle.getAngle()));

        // radius of the obstacle
        this.rObs = obstacle.getRadius();

        // relative position from robot to target
        this.pRt = (Point2D.relPos(pTar, p));

        // relative position from robot to obstacle
        this.pRo = Point2D.relPos(robot.getPosition(), obstacle.getPosition());

        // relative position from obstacle to target
        this.pOt = Point2D.relPos(obstacle.getPosition(), target.getPosition());

        // angle of pRt
        this.pSi = Math.atan(pRt.y / pRt.x);

        // angle of pRo
        this.thetaRo = Math.atan(pRo.y / pRo.x);

        // angle of pOt
        this.thetaOt = Math.atan(pOt.y / pOt.x);

        // angle of vTar
        this.thetaTar = target.getAngle();

        // angle of vObs
        this.thetaObs = obstacle.getAngle();

        //angle of v
        this.theta = robot.getAngle();
    }
    @Override
    public String toString() {
        return "PhysConst{" +
                "\npTar=" + pTar +
                "\n vTar=" + vTar +
                "\n p=" + p +
                "\n v=" + v +
                "\n pObs=" + pObs +
                "\n vObs=" + vObs +
                "\n rObs=" + rObs +
                "\n pRt=" + pRt +
                "\n pRo=" + pRo +
                "\n pOt=" + pOt +
                "\n pSi=" + pSi +
                "\n thetaRo=" + thetaRo +
                "\n thetaOt=" + thetaOt +
                "\n thetaTar=" + thetaTar +
                "\n thetaObs=" + thetaObs +
                "\n theta=" + theta +
                '}';
    }
}
