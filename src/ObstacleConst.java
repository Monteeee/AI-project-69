public class ObstacleConst {

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

    // distance from robot to outer radius of the obstacle
    public double ro;

    // relative position from obstacle to target
    public Point2D pOt;

    // angle of vObs
    public double thetaObs;

    // angle of pRo
    public double thetaRo;

    // angle of pOt
    public double thetaOt;


    public ObstacleConst(Rover obstacle, Point2D robotPos, Point2D targetPos) {

        // position of the obstacle
        this.pObs = obstacle.getPosition();

        // velocity of the obstacle
        this.vObs = new Point2D(obstacle.getSpeed() * Math.cos(obstacle.getAngle()),obstacle.getSpeed() * Math.sin(obstacle.getAngle()));


        // raduis of the obstacle
        this.rObs = obstacle.getRadius();

        // relative position from robot to target
        this.pRt = Point2D.relPos(targetPos, obstacle.getPosition());

        // relative position from robot to obstacle
        this.pRo = Point2D.relPos(robotPos, obstacle.getPosition());

        // distance from robot to outer radius of the obstacle
        this.ro = Point2D.getDistance( this.pObs, this.pRo ) - this.rObs;

        // relative position from obstacle to target
        this.pOt = Point2D.relPos(obstacle.getPosition(), targetPos);

        // angle of vObs
        this.thetaObs = obstacle.getAngle();

        // angle of pRo
        this.thetaRo = Math.atan2(pRo.x, pRo.y);

        // angle of pOt
        this.thetaOt = Math.atan2(pOt.x, pOt.y);
    }

    @Override
    public String toString() {
        return "ObstacleConst{" +
                "pObs=" + pObs +
                ", vObs=" + vObs +
                ", rObs=" + rObs +
                ", pRt=" + pRt +
                ", pRo=" + pRo +
                ", pOt=" + pOt +
                ", thetaObs=" + thetaObs +
                ", thetaRo=" + thetaRo +
                ", thetaOt=" + thetaOt +
                '}';
    }
}
