package doorworld;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;
import doorworld.state.DoorWorldState;
import doorworld.stateGenerator.DoorWorldStateFactory;

import javax.swing.*;

public class DoorWorld implements DomainGenerator {

    // object classes
    public static final String CLASS_AGENT =            "Agent";
    public static final String CLASS_ROOM =             "Room";
    public static final String CLASS_WALL =             "Wall";
    public static final String CLASS_DOOR =             "Door";

    // common attributes
    public static final String ATT_X =                  "x";
    public static final String ATT_Y =                  "y";

    // wall attributes
    public static final String ATT_START_X = 			"startX";
    public static final String ATT_START_Y = 			"startY";
    public static final String ATT_LENGTH = 			"length";
    public static final String ATT_IS_HORIZONTAL =		"isHorizontal";

    // door attributes
    public static final String ATT_LOCKED =             "locked";

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
                .addStateClass(CLASS_WALL, DoorWorld.class);

        domain.addActionTypes(
                new UniversalActionType(ACTION_NORTH),
                new UniversalActionType(ACTION_SOUTH),
                new UniversalActionType(ACTION_EAST),
                new UniversalActionType(ACTION_WEST));

        DoorWorldModel model = new DoorWorldModel(0);
        domain.setModel(model);

        return domain;
    }

    public static void main(String[] args) {
        DoorWorld doorWorldBuild = new DoorWorld();
        OOSADomain domain = (OOSADomain) doorWorldBuild.generateDomain();
        State s = DoorWorldStateFactory.createClassicState();

        Visualizer v = DoorWorldVisualizer.getVisualizer(8, 8);
        VisualExplorer exp = new VisualExplorer(domain, v, s);

        exp.addKeyAction("w", ACTION_NORTH,"");
        exp.addKeyAction("s", ACTION_SOUTH,"");
        exp.addKeyAction("d", ACTION_EAST,"");
        exp.addKeyAction("a", ACTION_WEST,"");

        exp.initGUI();
        exp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
