public class PhysicalConstants {

    // position of target
    public Point2D pTar;

    // velocity of target
    public Point2D vTar;

    // position of robot
    public Point2D p;

    // velocity of robot
    public Point2D v;

    // position of the obstacle
    public double pObs;

    // radius of the obstacle
    public double rObs;

    // relative position from robot to target
    public Point2D pRt;

    // relative position from robot to obstacle
    public double pRo;

    // relative position from obstacle to target
    public double pOt;

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


    public PhysicalConstants(Agent agent, Rover rover){

        this.pTar = rover.getPosition();

        this.vTar = new Point2D(rover.getSpeed() * Math.cos(rover.getAngle()), rover.getSpeed() * Math.sin(rover.getAngle()));

        this.p = agent.getPosition();

        this.v = new Point2D(agent.getSpeed() * Math.cos(agent.getAngle()),agent.getSpeed() * Math.sin(agent.getAngle()));

        this.pRt = (Point2D.relPos(pTar, p));

        this.pSi = Math.atan(pRt.y / pRt.x);

        this.theta = agent.getAngle();

    }
}
