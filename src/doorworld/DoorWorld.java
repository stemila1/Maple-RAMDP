package doorworld;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.pomdp.BeliefPolicyAgent;
import burlap.behavior.singleagent.pomdp.wrappedmdpalgs.BeliefSparseSampling;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.Domain;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.SimulatedPOEnvironment;
import burlap.mdp.singleagent.pomdp.beliefstate.BeliefState;
import burlap.mdp.singleagent.pomdp.beliefstate.TabularBeliefState;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.ReflectiveHashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import doorworld.state.DoorWorldDoor;
import doorworld.state.DoorWorldState;
import doorworld.stateGenerator.DoorWorldStateFactory;
import rocksample.POOO.POOODomain;
import rocksample.POOO.SimulatedPOOOEnvironment;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String VAL_UNKNOWN =            "isUnknown";

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

    public static final String ATTR_OBS =               "observations";
    public static final String OBS_LOCKED =             "locked";
    public static final String OBS_UNLOCKED =           "unlocked";
    public static final String OBS_NULL =               "null";

    // colors
    public static final String COLOR_RED = 				"red";
    public static final String COLOR_YELLOW = 			"yellow";
    public static final String COLOR_GREEN = 			"green";
    public static final String COLOR_BLUE = 			"blue";
    public static final String COLOR_MAGENTA =			"magenta";
    public static final String COLOR_BLACK = 			"black";
    public static final String COLOR_GRAY =				"gray";
    public static final String COLOR_ORANGE =           "orange";
    public static final String COLOR_WHITE =            "white";
    public static final String COLOR_CYAN =             "cyan";
    public static final String COLOR_LIGHT_GRAY =       "lightGray";

    @Override
    public Domain generateDomain() {
      //  OOSADomain domain = new OOSADomain();
        POOODomain domain = new POOODomain();

        domain.addStateClass(CLASS_AGENT, DoorWorld.class)
                .addStateClass(CLASS_ROOM, DoorWorld.class)
                .addStateClass(CLASS_DOOR, DoorWorld.class);

        domain.addActionTypes(
                new UniversalActionType(ACTION_NORTH),
                new UniversalActionType(ACTION_SOUTH),
                new UniversalActionType(ACTION_EAST),
                new UniversalActionType(ACTION_WEST),
                new UniversalActionType(ACTION_OPEN_DOOR));

        // make & set observation function
        ObservationFunction of = new DoorWorldObservationFunction();
        domain.setObservationFunction(of);

        // make & set model
        DoorWorldModel model = new DoorWorldModel(0, -10, 10, 100);
        domain.setModel(model);

        StateEnumerator senum = new StateEnumerator(domain, new SimpleHashableStateFactory());
        getEnumeratedIDs(senum);
        domain.setStateEnumerator(senum);

        return domain;
    }
    public static void getEnumeratedIDs(StateEnumerator senum) {

        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_UNLOCKED, VAL_UNLOCKED, VAL_UNLOCKED));
        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_UNLOCKED, VAL_UNLOCKED, VAL_LOCKED));
        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_UNLOCKED, VAL_LOCKED, VAL_LOCKED));
        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_UNLOCKED, VAL_LOCKED, VAL_UNLOCKED));
        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_LOCKED, VAL_LOCKED, VAL_LOCKED));
        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_LOCKED, VAL_UNLOCKED, VAL_UNLOCKED));
        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_LOCKED, VAL_LOCKED, VAL_UNLOCKED));
        senum.getEnumeratedID(DoorWorldStateFactory.createCustomState(0, 0, 8,8,
                VAL_LOCKED, VAL_UNLOCKED, VAL_LOCKED));
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

    public static TabularBeliefState getInitialBeliefState(PODomain domain) {
        TabularBeliefState bs = new TabularBeliefState(domain, domain.getStateEnumerator());
        bs.initializeBeliefsUniformly();
        return bs;
    }

    public static void main(String[] args) {
        int maxX = 8;
        int maxY = 8;

        DoorWorld doorWorldBuild = new DoorWorld();

        POOODomain domain = (POOODomain) doorWorldBuild.generateDomain();
     //   OOSADomain domain = (OOSADomain) doorWorldBuild.generateDomain();
        State s = DoorWorldStateFactory.generateThreeRoomsThreeDoors(0, 0, maxX, maxY);
   //     State s = DoorWorldStateFactory.generateNineRoomsTenDoors(0,0, 13, 13);
        HashableStateFactory hs = new SimpleHashableStateFactory();

        // janky partially observable stuff
        BeliefState initialBelief = DoorWorld.getInitialBeliefState(domain);
        BeliefSparseSampling bss = new BeliefSparseSampling(domain, 0.99,
                new ReflectiveHashableStateFactory(), 10, -1);
        Policy p = new GreedyQPolicy(bss);


        // don't think am setting to right curr state but we will see...
        SimulatedPOOOEnvironment env = new SimulatedPOOOEnvironment(domain);
        env.setCurStateTo(DoorWorldStateFactory.generateThreeRoomsThreeDoors(0, 0, maxX, maxY));

        BeliefPolicyAgent agent = new BeliefPolicyAgent(domain, env, p);
        agent.setBeliefState(initialBelief);

        agent.setEnvironment(env);

        List<Episode> eps = new ArrayList();
        Episode ea = agent.actUntilTerminalOrMaxSteps(10);
        for (int i = 0; i < ea.numTimeSteps()-1; i++) {
          //  Episode ea = agent.actUntilTerminalOrMaxSteps(1);
            System.out.println(ea.action(i) + " " + ea.reward(i + 1));
         //   eps.add(ea);
         //   env.resetEnvironment();
        }
        eps.add(ea);

        EpisodeSequenceVisualizer v = new EpisodeSequenceVisualizer(
                DoorWorldVisualizer.getVisualizer(0, 0, maxX, maxY), domain, eps);
        v.setDefaultCloseOperation(v.EXIT_ON_CLOSE);
        v.initGUI();


        // for fully observable domain
        /*SimulatedEnvironment env = new SimulatedEnvironment(domain, s);

        List<Episode> eps = new ArrayList();
        QLearning qagent = new QLearning(domain, 0.95, hs, 0, 0.1);

        for(int i = 0; i < 100; i++) {
            Episode e = qagent.runLearningEpisode(env, 5000);
            System.out.println(e.rewardSequence);
            eps.add(e);
            env.resetEnvironment();
        }
        EpisodeSequenceVisualizer v = new EpisodeSequenceVisualizer(
                DoorWorldVisualizer.getVisualizer(0, 0, maxX, maxY), domain, eps);
        v.setDefaultCloseOperation(v.EXIT_ON_CLOSE);
        v.initGUI();


        // for manually controlling agent
        /*Visualizer v = DoorWorldVisualizer.getVisualizer(0, 0, 8, 8);
        VisualExplorer exp = new VisualExplorer(domain, v, s);

        exp.addKeyAction("w", ACTION_NORTH,"");
        exp.addKeyAction("s", ACTION_SOUTH,"");
        exp.addKeyAction("d", ACTION_EAST,"");
        exp.addKeyAction("a", ACTION_WEST,"");
        exp.addKeyAction("o", ACTION_OPEN_DOOR, "");

        exp.initGUI();
        exp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); */


    }
}
