public class ObstacleConst {

    // position of the obstacle
    public Point2D pObs;

    // velocity of the obstacle
    public Point2D vObs;

    // radius of the obstacle
    public double rObs;

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


    public ObstacleConst(Rover obstacle, Point2D p, Point2D pTar) {

        // position of the obstacle
        this.pObs = obstacle.getPosition();

        // velocity of the obstacle
        this.vObs = new Point2D(obstacle.getSpeed() * Math.cos(obstacle.getAngle()),obstacle.getSpeed() * Math.sin(obstacle.getAngle()));

        // raduis of the obstacle
        this.rObs = obstacle.getRadius();

        // relative position from robot (p) to obstacle (pObs)
//        this.pRo = Point2D.relPos( p, pObs );

        // this.pRo = Point2D.relPos(p, obstacle.getPosition());
        this.pRo = Point2D.relPos(pObs, p);

        // distance from robot to outer radius of the obstacle
        this.ro = this.pRo.getLength() - this.rObs;
        // make sure ro never becomes negative
        if (this.ro < 0)
            this.ro = 0;

        // relative position from obstacle to target
        this.pOt = Point2D.relPos(pTar, pObs);

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
                ", pRo=" + pRo +
                ", pOt=" + pOt +
                ", thetaObs=" + thetaObs +
                ", thetaRo=" + thetaRo +
                ", thetaOt=" + thetaOt +
                '}';
    }
}
