package doorworld;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.visualizer.OOStatePainter;
import burlap.visualizer.ObjectPainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldWall;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

public class DoorWorldVisualizer {
    private static Map<String, Color> colors;
    private static int cellsWide, cellsTall;

    private static void initColors(){
        colors = new HashMap<String, Color>();
        colors.put(DoorWorld.COLOR_RED, Color.red);
        colors.put(DoorWorld.COLOR_YELLOW, Color.YELLOW);
        colors.put(DoorWorld.COLOR_BLUE, Color.BLUE);
        colors.put(DoorWorld.COLOR_GREEN, Color.GREEN);
        colors.put(DoorWorld.COLOR_MAGENTA, Color.MAGENTA);
        colors.put(DoorWorld.COLOR_BLACK, Color.BLACK);
        colors.put(DoorWorld.COLOR_GRAY, Color.DARK_GRAY);
    }

    public static Visualizer getVisualizer(int w, int h){
        initColors();
        Visualizer v = new Visualizer(getStateRenderLayer(w, h));
        return v;
    }

    public static StateRenderLayer getStateRenderLayer(int w, int h) {
        StateRenderLayer rl = new StateRenderLayer();
        OOStatePainter oopainter = new OOStatePainter();

        cellsWide = w;
        cellsTall = h;

        oopainter.addObjectClassPainter(DoorWorld.CLASS_AGENT, new AgentPainter());
        oopainter.addObjectClassPainter(DoorWorld.CLASS_WALL, new WallPainter());

        rl.addStatePainter(oopainter);

        return rl;
    }


    public static class AgentPainter implements ObjectPainter {

        @Override
        public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob,
                                float cWidth, float cHeight) {
            // set color and size of rover
            g2.setColor(Color.RED);
            float agentWidth = (float) cWidth / cellsWide;
            float agentHeight = (float) cHeight / cellsTall;

            // make the rover a rover agent and set the location
            DoorWorldAgent agent = (DoorWorldAgent) ob;
            int ax = (int) agent.get(DoorWorld.ATT_X);
            int ay = (int) agent.get(DoorWorld.ATT_Y);
            float roverx = ax * agentWidth - cellsWide;
            float rovery = cHeight - (1 + ay) * agentHeight;

            float scale = 0.9f;
            float realWidth = agentWidth * scale;
            float realHeight = agentHeight * scale;
            float realX = roverx + 0.08f * agentWidth;
            float realy = rovery + 0.05f * agentHeight;

            g2.fill(new Ellipse2D.Float(realX, realy, realWidth, realHeight));
        }
    }

    public static class WallPainter implements ObjectPainter {
        @Override
        public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(10));

            float wallWidth = (float) cWidth / cellsWide;
            float wallHeight = (float) cHeight / cellsTall;

            DoorWorldWall w = (DoorWorldWall) ob;
            int startx = (int) w.get(DoorWorld.ATT_START_X);
            int starty = (int) w.get(DoorWorld.ATT_START_Y);
            float wx1 = startx * wallWidth;
            float wy1 = cHeight - starty * wallHeight;
            float wx2, wy2;

            int length = (int) w.get(DoorWorld.ATT_LENGTH);
            boolean isHorizontal = (boolean) w.get(DoorWorld.ATT_IS_HORIZONTAL);
            if (isHorizontal) {
                wx2 = wx1 + length * wallWidth;
                wy2 = wy1;
            } else {
                wx2 = wx1;
                wy2 = wy1 - length * wallHeight;
            }

            g2.drawLine((int) wx1, (int) wy1, (int) wx2, (int) wy2);
        }
    }

}
