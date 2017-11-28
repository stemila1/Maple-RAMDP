package rocksample;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.modellearning.artdp.ARTDP;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.pomdp.qmdp.QMDP;
import burlap.behavior.singleagent.pomdp.wrappedmdpalgs.BeliefSparseSampling;
import burlap.behavior.valuefunction.QProvider;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.mdp.singleagent.pomdp.SimulatedPOEnvironment;
import burlap.mdp.singleagent.pomdp.beliefstate.BeliefState;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
import burlap.shell.EnvironmentShell;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import rocksample.state.RockSampleRock;
import rocksample.state.RockSampleState;
import rocksample.state.RockSampleWall;
import rocksample.state.RoverAgent;
import rocksample.POOODomain;
import rocksample.stateGenerator.RockSampleStateFactory;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import rocksample.SimulatedPOOOEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steph on 11/9/2017.
 */
public class RockSamplePO implements DomainGenerator {

    // object classes
    public static final String CLASS_ROVER = 				"Rover";
    public static final String CLASS_ROCK =                 "Rock";
    public static final String CLASS_WALL =                 "Wall";

    // abstract class
    public static final String CLASS_PT =                   "Point";

    // common attributes
    public static final String ATT_X =                      "x";
    public static final String ATT_Y =                      "y";

    // rock attributes
    public static final String ATT_QUALITY =                "quality";

    // wall attributes
    public static final String ATT_START_X = 				"startX";
    public static final String ATT_START_Y = 				"startY";
    public static final String ATT_LENGTH = 				"length";
    public static final String ATT_IS_HORIZONTAL =			"isHorizontal";

    // colors
    public static final String COLOR_RED = 					"red";
    public static final String COLOR_YELLOW = 				"yellow";
    public static final String COLOR_GREEN = 				"green";
    public static final String COLOR_BLUE = 				"blue";
    public static final String COLOR_MAGENTA =				"magenta";
    public static final String COLOR_BLACK = 				"black";
    public static final String COLOR_GRAY =					"gray";

    // actions
    public static final int NUM_MOVE_ACTIONS = 				4;
    public static final String ACTION_NORTH = 				"north";
    public static final String ACTION_EAST =				"east";
    public static final String ACTION_SOUTH =				"south";
    public static final String ACTION_WEST = 				"west";
    public static final String ACTION_SAMPLE =              "sample";
    public static final String ACTION_CHECK =               "check";

    // action indexes
    public static int IND_NORTH = 							0;
    public static int IND_EAST = 							1;
    public static int IND_SOUTH = 							2;
    public static int IND_WEST = 							3;
    public static int IND_SAMPLE =                          4;
    public static int IND_CHECK =                           5;

    // parameters dictating probabilities of the model
    private RewardFunction rf;
    private TerminalFunction tf;
    private double[][] moveDynamics;
    private boolean noisy;             // whether the sensor is noisy or not
    private double noisyProbability;   // prob sensor is accurate


    public String[] observations = new String[4];

    public RockSamplePO(RewardFunction r, TerminalFunction t,
                      double correctMoveProb, boolean n, double noisyProb){
        rf = r;
        tf = t;
        this.noisy = n;
        this.noisyProbability = noisyProb;
        setMoveDynamics(correctMoveProb);
    }

    public RockSamplePO(double correctMoveProb, boolean n, double noisyProb){
        this.noisy = n;
        this.noisyProbability = noisyProb;
        setMoveDynamics(correctMoveProb);
        this.rf = new RockSampleRewardFunction();
        this.tf = new RockSampleTerminalFunction();
    }

    public RockSamplePO(double[][] movement, boolean n, double noisyProb){
        this.noisy = n;
        this.noisyProbability = noisyProb;
        this.moveDynamics = movement;
        this.rf = new RockSampleRewardFunction();
        this.tf = new RockSampleTerminalFunction();
    }

    public RockSamplePO(){ this(1, false, 0); }

    private void setMoveDynamics(double correctProb) {
        moveDynamics = new double[NUM_MOVE_ACTIONS][NUM_MOVE_ACTIONS];

        for(int choose = 0; choose < NUM_MOVE_ACTIONS; choose++){
            for(int outcome = 0; outcome < NUM_MOVE_ACTIONS; outcome++){
                if(choose == outcome){
                    moveDynamics[choose][outcome] = correctProb;
                }
                // the two directions which are one away get the rest of prob
                else if(Math.abs(choose - outcome) % 2 == 1){
                    moveDynamics[choose][outcome] = (1 - correctProb) / 2;
                }else{
                    moveDynamics[choose][outcome] = 0;
                }
            }
        }
    }

    public POOODomain generateDomain(){
        POOODomain domain = new POOODomain();
        // add the state classes to the domain
        domain.addStateClass(CLASS_ROVER, RoverAgent.class)
                .addStateClass(CLASS_ROCK, RockSampleRock.class)
                .addStateClass(CLASS_WALL, RockSampleWall.class);

        domain.addActionTypes(
                new UniversalActionType(ACTION_NORTH),
                new UniversalActionType(ACTION_SOUTH),
                new UniversalActionType(ACTION_EAST),
                new UniversalActionType(ACTION_WEST),
                new UniversalActionType(ACTION_SAMPLE),

                // check is object parameterized action
                new CheckActionType(ACTION_CHECK, new String[]{CLASS_ROCK}));

        // add observation function
        ObservationFunction of = new RockSampleObservationFunction(this.noisyProbability);
        domain.setObservationFunction(of);

        StateEnumerator senum = new StateEnumerator(domain, new SimpleHashableStateFactory());

        // make a new rocksample model
        RockSampleModel model = new RockSampleModel(moveDynamics);
        FactoredModel rockSampleModel = new FactoredModel(model, rf, tf);
        domain.setModel(rockSampleModel);
        domain.setStateEnumerator(senum);

        StateEnumerator senum = new StateEnumerator(domain, new SimpleHashableStateFactory());

        RockSampleStateFactory rs_statefactory = new RockSampleStateFactory();
        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Good", "Good", "Good"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Good", "Good", "Good"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Bad", "Good", "Good"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Good", "Bad", "Good"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Good", "Good", "Bad"));

        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Bad", "Good", "Good"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Good", "Bad", "Good"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Good", "Good", "Bad"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Bad", "Bad", "Good"));

        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Bad", "Good", "Bad"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Good", "Bad", "Bad"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Bad", "Bad", "Good"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Good", "Bad", "Bad"));

        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Bad", "Good", "Bad"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Good", "Bad", "Bad", "Bad"));
        senum.getEnumeratedID(rs_statefactory.createCustomState("Bad", "Bad", "Bad", "Bad"));

        domain.setStateEnumerator(senum);

        return domain;
    }

    // just returns a random ass state casted to a belief state but ok
    public static POOOBeliefState getInitialBeliefState(POOODomain domain){
        POOOBeliefState bs = new POOOBeliefState(domain, domain.getStateEnumerator());
        bs.initializeBeliefsUniformly();
        return bs;
    }
   // public static BeliefState getInitialBeliefState(POOODomain domain){
   //     State bs = new RockSampleState(1);
    //    return (BeliefState) bs;
   // }

    public static QMDP getQMDPagent(POOODomain domain, HashableStateFactory hs,
                                    RockSampleRewardFunction rf,
                                    RockSampleTerminalFunction tf){
        QMDP qagent = new QMDP(domain, rf, tf, 0.99, hs, 0.001, 1000);
        return qagent;

    }

    public static void main(String[] args){
        RockSamplePO rocksampleBuild = new RockSamplePO();
        POOODomain domain = rocksampleBuild.generateDomain();

        BeliefState initialBelief = RockSamplePO.getInitialBeliefState(domain);
        HashableStateFactory hs = new SimpleHashableStateFactory();

        // TODO: change hardcoded values
        BeliefSparseSampling bss = new BeliefSparseSampling(domain, 0.99,
                                                            hs, 10, -1);

        State s = RockSampleStateFactory.createClassicState();

        SimulatedEnvironment env = new SimulatedEnvironment(domain, s);
        SimulatedEnvironment poooEnv = new SimulatedPOOOEnvironment(domain, s);

        SimulatedEnvironment envToUse = poooEnv;

        List<Episode> eps = new ArrayList<Episode>();

        //QLearning qagent = new QLearning(domain, 0.95, hs, 0, 0.01);

        // commented out to implement SimulatedPOEnvironment to run agent from shell before implementing solver
        /*Planner planner = new ValueIteration(domain, 0.99, hs, 0.001, 100);
        QProvider planner_2 = (QProvider) new ValueIteration(domain, 0.99, hs, 0.001, 100);
        QMDP qagent = new QMDP(domain, planner_2);
        qagent.forceMDPPlanningFromAllStates();

        Visualizer v = RockSampleVisualizer.getVisualizer(5,5);
        EnvironmentShell shell = new EnvironmentShell(domain, envToUse);
        shell.start();
        

        // make the visualizer interactive. check action doesn't work
        VisualExplorer exp = new VisualExplorer(domain, env, v);

        exp.addKeyAction("w", ACTION_NORTH, "");
        exp.addKeyAction("s", ACTION_SOUTH, "");
        exp.addKeyAction("d", ACTION_EAST, "");
        exp.addKeyAction("a", ACTION_WEST, "");
        exp.addKeyAction("x", ACTION_CHECK, "");
        exp.addKeyAction("q", ACTION_SAMPLE, "");

        exp.initGUI();
        */
        /*
        for(int i = 0; i < 1000; i++){
            Episode e = qagent.runLearningEpisode(envToUse, 5000);
            System.out.println(e.rewardSequence);
            eps.add(e);
            envToUse.resetEnvironment();
        }

        */
        RockSampleRewardFunction rf = new RockSampleRewardFunction();
        RockSampleTerminalFunction tf = new RockSampleTerminalFunction();

        // initial state needs to be an EnumerableBeliefState
        String outputPath = "output/";
        QMDP agent = getQMDPagent(domain, hs, rf, tf);
        Policy p = agent.planFromState(s);
        PolicyUtils.rollout(p, s, domain.getModel()).write(outputPath + "bfs");
       // agent.forceMDPPlanningFromAllStates();
       // agent.resetSolver();

        //for(int i = 0; i < 1000; i++){
         //   Policy p = agent.planFromState(s);
           // System.out.println(p.rewardSequence);
          //  eps.add(e);

  //          envToUse.resetEnvironment();
//        }

        EpisodeSequenceVisualizer vis = new EpisodeSequenceVisualizer(RockSampleVisualizer.getVisualizer(5, 5),
                domain, outputPath);
        vis.setDefaultCloseOperation(vis.EXIT_ON_CLOSE);
        vis.initGUI();
    }

}
