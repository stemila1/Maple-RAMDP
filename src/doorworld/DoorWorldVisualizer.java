package doorworld;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.visualizer.OOStatePainter;
import burlap.visualizer.ObjectPainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldState;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DoorWorldVisualizer {
    private static Map<String, Color> colors;
    private static int cellsWide, cellsTall;

    // initColors
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

    // getVisualizer
    public static Visualizer getVisualizer(int minX, int minY, int maxX, int maxY){
        initColors();
        Visualizer v = new Visualizer(getStateRenderLayer(minX, minY, maxX, maxY));
        return v;
    }

    // getStateRenderLayer
    public static StateRenderLayer getStateRenderLayer(int minX, int minY, int maxX, int maxY) {
        StateRenderLayer rl = new StateRenderLayer();
        OOStatePainter oopainter = new OOStatePainter();

        int w = maxX - minX;
        int h = maxY - minY;
        cellsWide = w;
        cellsTall = h;

        oopainter.addObjectClassPainter(DoorWorld.CLASS_ROOM, new RoomPainter(minX, minY, maxX, maxY));
        oopainter.addObjectClassPainter(DoorWorld.CLASS_DOOR, new DoorPainter(minX, minY, maxX, maxY));
        oopainter.addObjectClassPainter(DoorWorld.CLASS_AGENT, new AgentPainter());

        rl.addStatePainter(oopainter);

        return rl;
    }


    public static class AgentPainter implements ObjectPainter {

        // paintObject
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

    public static class RoomPainter implements ObjectPainter {

        protected int minX = -1;
        protected int minY = -1;
        protected int maxX = -1;
        protected int maxY = -1;

        // RoomPainter
        public RoomPainter(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        // paintObject
        @Override
        public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {

            DoorWorldState cws = (DoorWorldState) s;
            float domainXScale = DoorWorld.maxRoomXExtent(cws) + 1f;
            float domainYScale = DoorWorld.maxRoomYExtent(cws) + 1f;

            if (maxX != -1) {
                domainXScale = maxX;
                domainYScale = maxY;
            }

            // determine then normalized width
            float width = (1.0f / domainXScale) * cWidth;
            float height = (1.0f / domainYScale) * cHeight;

            int top = (Integer) ob.get(DoorWorld.ATT_TOP);
            int left = (Integer) ob.get(DoorWorld.ATT_LEFT);
            int bottom = (Integer) ob.get(DoorWorld.ATT_BOTTOM);
            int right = (Integer) ob.get(DoorWorld.ATT_RIGHT);

            Color rcol = colorForName(ob.get(DoorWorld.ATT_COLOR).toString());
            float[] hsb = new float[3];
            Color.RGBtoHSB(rcol.getRed(), rcol.getGreen(), rcol.getBlue(), hsb);
            hsb[1] = 0.4f;
            rcol = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);

            for (int i = left; i <= right; i++) {
                for (int j = bottom; j <= top; j++) {

                    float rx = i * width;
                    float ry = cHeight - height - j * height;

                    if (i == left || i == right || j == bottom || j == top) {
                        g2.setColor(Color.black);
                        g2.fill(new Rectangle2D.Float(rx, ry, width, height));
                    } else {
                        g2.setColor(rcol);
                        g2.fill(new Rectangle2D.Float(rx, ry, width, height));
                    }
                }
            }

        }

    }

    public static class DoorPainter implements ObjectPainter {


        protected int minX = -1;
        protected int minY = -1;
        protected int maxX = -1;
        protected int maxY = -1;

        // DoorPainter
        public DoorPainter(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        // paintObject
        @Override
        public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
            float domainXScale = DoorWorld.maxRoomXExtent(s) + 1f;
            float domainYScale = DoorWorld.maxRoomYExtent(s) + 1f;

            if (maxX != -1) {
                domainXScale = maxX;
                domainYScale = maxY;
            }

            //determine then normalized width
            float width = (1.0f / domainXScale) * cWidth;
            float height = (1.0f / domainYScale) * cHeight;

            int top = (Integer) ob.get(DoorWorld.ATT_TOP);
            int left = (Integer) ob.get(DoorWorld.ATT_LEFT);
            int bottom = (Integer) ob.get(DoorWorld.ATT_BOTTOM);
            int right = (Integer) ob.get(DoorWorld.ATT_RIGHT);

            g2.setColor(Color.white);

            String lockedState = ob.get(DoorWorld.ATT_LOCKED).toString();
            if (lockedState.equals("unknown")) {
                g2.setColor(Color.gray);
            } else if (lockedState.equals("locked")) {
                g2.setColor(Color.black);
            }


            for (int i = left; i <= right; i++) {
                for (int j = bottom; j <= top; j++) {

                    float rx = i * width;
                    float ry = cHeight - height - j * height;
                    g2.fill(new Rectangle2D.Float(rx, ry, width, height));

                }
            }
        }
    }

    // colorForName
    protected static Color colorForName(String colName) {
        Color col = Color.darkGray;
        Field field;
        try {
            field = Class.forName("java.awt.Color").getField(colName);
            col = (Color) field.get(null);
        } catch (Exception e) { }
        return col;

    }

}
