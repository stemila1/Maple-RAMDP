package doorworld;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;
import doorworld.state.DoorWorldState;
import doorworld.stateGenerator.DoorWorldStateFactory;

import javax.swing.*;
import java.util.List;

/**
 * Created by Stephanie Milani on 01/11/2018
 */

public class DoorWorld implements DomainGenerator {

    // object classes
    public static final String CLASS_AGENT =            "Agent";
    public static final String CLASS_ROOM =             "Room";
    public static final String CLASS_DOOR =             "Door";

    // common attributes
    public static final String ATT_X =                  "x";
    public static final String ATT_Y =                  "y";
    public static final String ATT_COLOR =              "color";

    // agent attributes
    public static final String ATT_DIR =                "direction";
    public static final String ATT_SHAPE =              "shape";

    // door attributes
    public static final String ATT_CLOSED =             "closed";
    public static final String VAL_CLOSED =             "isClosed";
    public static final String VAL_OPEN =               "isOpen";
    public static final String ATT_LOCKED =             "locked";
    public static final String VAL_LOCKED =             "isLocked";
    public static final String VAL_UNLOCKED =           "isUnlocked";

    // room attributes
    public static final String ATT_TOP =                "top";
    public static final String ATT_BOTTOM =             "bottom";
    public static final String ATT_LEFT =               "left";
    public static final String ATT_RIGHT =              "right";

    // actions
    public static final int NUM_MOVE_ACTIONS = 			4;
    public static final String ACTION_NORTH = 			"north";
    public static final String ACTION_EAST =			"east";
    public static final String ACTION_SOUTH =			"south";
    public static final String ACTION_WEST = 			"west";
    public static final String ACTION_OPEN_DOOR =       "openDoor";
    public static final String[] DIRECTIONS = {ACTION_NORTH, ACTION_SOUTH, ACTION_EAST, ACTION_WEST};

    // colors
    public static final String COLOR_RED = 				"red";
    public static final String COLOR_YELLOW = 			"yellow";
    public static final String COLOR_GREEN = 			"green";
    public static final String COLOR_BLUE = 			"blue";
    public static final String COLOR_MAGENTA =			"magenta";
    public static final String COLOR_BLACK = 			"black";
    public static final String COLOR_GRAY =				"gray";

    @Override
    public Domain generateDomain() {
        OOSADomain domain = new OOSADomain();
        domain.addStateClass(CLASS_AGENT, DoorWorld.class)
                .addStateClass(CLASS_ROOM, DoorWorld.class)
                .addStateClass(CLASS_DOOR, DoorWorld.class);

        domain.addActionTypes(
                new UniversalActionType(ACTION_NORTH),
                new UniversalActionType(ACTION_SOUTH),
                new UniversalActionType(ACTION_EAST),
                new UniversalActionType(ACTION_WEST),
                new UniversalActionType(ACTION_OPEN_DOOR));

        DoorWorldModel model = new DoorWorldModel(0);
        domain.setModel(model);

        return domain;
    }

    // maxRoomXExtent
    public static int maxRoomXExtent(OOState s) {

        int max = 0;
        List<ObjectInstance> rooms = s.objectsOfClass(CLASS_ROOM);
        for (ObjectInstance r : rooms) {
            int right = (Integer) r.get(ATT_RIGHT);
            if (right > max) {
                max = right;
            }
        }
        return max;
    }

    // maxRoomYExtent
    public static int maxRoomYExtent(OOState s) {

        int max = 0;
        List<ObjectInstance> rooms = s.objectsOfClass(CLASS_ROOM);
        for (ObjectInstance r : rooms) {
            int top = (Integer) r.get(ATT_TOP);
            if (top > max) {
                max = top;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        DoorWorld doorWorldBuild = new DoorWorld();
        OOSADomain domain = (OOSADomain) doorWorldBuild.generateDomain();
//        State s = DoorWorldStateFactory.createClassicState();
        State s = DoorWorldStateFactory.generateThreeRoomsThreeDoors(0, 0, 8, 8);

        Visualizer v = DoorWorldVisualizer.getVisualizer(0, 0, 8, 8);
        VisualExplorer exp = new VisualExplorer(domain, v, s);

        exp.addKeyAction("w", ACTION_NORTH,"");
        exp.addKeyAction("s", ACTION_SOUTH,"");
        exp.addKeyAction("d", ACTION_EAST,"");
        exp.addKeyAction("a", ACTION_WEST,"");
        exp.addKeyAction("o", ACTION_OPEN_DOOR, "");

        exp.initGUI();
        exp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
