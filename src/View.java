import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

//@SuppressWarnings("serial")
public class View extends JPanel {

    double endX, endY;
    Agent agent;
    private ArrayList<Line> lines = new ArrayList<Line>();
    private Point2D lastPos;


//    private Model myModel;
    /**
     * Creates Panel with custom background
//     * @param theModel
     */
    View() {
        Model.initializeModel();
        //this.setPreferredSize(theModel.getDimenstion());
        this.setPreferredSize(new Dimension((int)Constants.FIELD_SIZE_X, (int)Constants.FIELD_SIZE_Y));
        this.setBackground(Color.WHITE);
        this.setVisible(true);
    }

    /**
     * Paints every single particle on the screen
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        agent = Model.getAgent();
        if (lastPos == null) {lastPos = agent.getPosition();}
        endY = agent.getPosition().y + 2 * Constants.AGENT_RADIUS * Math.cos(agent.getAngle() * Math.PI / 180);
        endX = agent.getPosition().x + 2 * Constants.AGENT_RADIUS * Math.sin(agent.getAngle() * Math.PI / 180);
        g.setColor(Color.GREEN);
        // g.drawLine((int)agent.getPosition().x-(int)agent.getRadius(), (int)agent.getPosition().y-(int)agent.getRadius(),(int)endX,(int)endY);
        g.fillOval((int) agent.getPosition().x-(int)agent.getRadius(), (int) agent.getPosition().y-(int)agent.getRadius(),
                (int) agent.getRadius()*2, (int) agent.getRadius()*2);

        int count_obs = 0;
        int count_tar = 0;
        for (Rover rover : Model.getRovers()) {

            // check the type
            // draw targets
            if(rover.getType() == Rover.Type.TARGET) {
                g.setColor(Color.RED);
                g.fillOval((int) rover.getPosition().x - (int) rover.getRadius(), (int) rover.getPosition().y -
                                (int) rover.getRadius(), (int) rover.getRadius()*2, (int) rover.getRadius()*2);
                //g.fillOval((int) rover.getPosition().x - 2*(int) rover.getRadius() - (int)agent.getRadius(),
                //        (int) rover.getPosition().y - (int) rover.getRadius(), (int) rover.getRadius()*2,
                //        (int) rover.getRadius()*2);
                g.setColor(Color.BLUE);
                g.drawString(Integer.toString(count_tar), (int) rover.getPosition().x-3, (int) rover.getPosition().y+3);
                count_tar = count_tar + 1;
            }else{
                // draw obstacles
                g.setColor(Color.BLUE);
                g.fillOval((int) rover.getPosition().x - (int) rover.getRadius(), (int) rover.getPosition().y -
                        (int) rover.getRadius(), (int) rover.getRadius()*2, (int) rover.getRadius()*2);
                g.setColor(Color.YELLOW);
                g.drawString(Integer.toString(count_obs), (int) rover.getPosition().x-3, (int) rover.getPosition().y+3 );
                count_obs = count_obs + 1;
            }

        }

        //New line
        Color color = new Color(0,(int) (255*agent.getSpeed()/6),0);

        //Set color to red in case of collision with obstacle
        for(Rover obs: Model.getRoversByType(Rover.Type.OBSTACLE)) {
            if (Point2D.getDistance(agent.getPosition(), obs.getPosition()) < agent.getRadius() + obs.getRadius()) {
                color = Color.RED;
            }
        }

        Line line = new Line(lastPos.x, lastPos.y, agent.getPosition().x, agent.getPosition().y, color);
        lines.add(line);

        //Update lastPost
        lastPos = agent.getPosition();

        //Draw all lines
        for (Line l: lines) {
            g.setColor(l.color);
            ((Graphics2D)g).setStroke(new BasicStroke(3));
            g.drawLine((int) l.x1, (int) l.y1, (int) l.x2, (int) l.y2);
        }
        g.dispose();
    }
}