public class PhysConst {

    // position of target
    public Point2D pTar;

    // velocity of target
    public Point2D vTar;

    // position of robot
    public Point2D p;

    // velocity of robot
    public Point2D v;

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

        if (Model.targetFleeOnCollsion){
            this.pTar = target.getChasePosition();
            }

        // velocity of target
        this.vTar = new Point2D(target.getSpeed() * Math.cos(target.getAngle()), target.getSpeed() * Math.sin(target.getAngle()));

        // position of robot
        this.p = robot.getPosition();

        // velocity of robot
        this.v = new Point2D(robot.getSpeed() * Math.cos(robot.getAngle()),robot.getSpeed() * Math.sin(robot.getAngle()));

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
        //if (obstacle.getType() == Rover.Type.OBSTACLE) {
            return new ObstacleConst(obstacle, p, pTar);
        //}
        //else throw new Exception("Rover was expected to be an obstacle, but was not.");
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
