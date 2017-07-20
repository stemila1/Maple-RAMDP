package testing;

import java.util.ArrayList;
import java.util.List;

import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.mdp.auxiliary.StateGenerator;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.VisualActionObserver;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import hierarchy.framework.GroundedTask;
import hierarchy.framework.Task;
import ramdp.agent.RAMDPLearningAgent;
import rmaxq.agent.RmaxQLearningAgent;
import taxi.TaxiVisualizer;
import taxi.hierarchies.TaxiHierarchy;
import taxi.state.TaxiState;
import taxi.stateGenerator.RandomPassengerClassicTaxiState;
import taxi.stateGenerator.RandomPassengerSmallTaxiState;
import taxi.stateGenerator.TaxiStateFactory;

public class HierarchicalLearnerTest {

	public static void runRAMDPEpisodes(int numEpisode, int maxSteps, Task root,
			State initial, OOSADomain groundDomain,
			int threshold, double discount, double rmax, double maxDelta, boolean randomStart){
		List<Episode> episodes = new ArrayList<Episode>();
		SimulatedEnvironment env;
		env = new SimulatedEnvironment(groundDomain, initial);
		GroundedTask rootgt = root.getAllGroundedTasks(initial).get(0);
		
		RAMDPLearningAgent ramdp = new RAMDPLearningAgent(rootgt, threshold, discount, rmax, 
				new SimpleHashableStateFactory(true), maxDelta);
//		VisualActionObserver obs = new VisualActionObserver(groundDomain, TaxiVisualizer.getVisualizer(5, 5));
//        obs.initGUI();
//        obs.setDefaultCloseOperation(obs.EXIT_ON_CLOSE);
//        env.addObservers(obs);
		
		for(int i = 1; i <= numEpisode; i++){
			long time = System.currentTimeMillis();
			Episode e = ramdp.runLearningEpisode(env, maxSteps);
			time = System.currentTimeMillis() - time;
			episodes.add(e);
			System.out.println("Episode " + i + " time " + (double)time/1000 );
			env.resetEnvironment();
		}
		
		EpisodeSequenceVisualizer ev = new EpisodeSequenceVisualizer
				(TaxiVisualizer.getVisualizer(5, 5), groundDomain, episodes);
		ev.setDefaultCloseOperation(ev.EXIT_ON_CLOSE);
		ev.initGUI();
	}
	
	public static void runRMAXQEpsodes(int numEpisodes, int maxSteps, Task root, State initState, double vmax,
			int threshold, double maxDelta, OOSADomain domain){
		HashableStateFactory hs = new SimpleHashableStateFactory(true);
		
		SimulatedEnvironment env = new SimulatedEnvironment(domain, initState);
//		VisualActionObserver obs = new VisualActionObserver(domain, TaxiVisualizer.getVisualizer(5, 5));
//		obs.initGUI();
//		obs.setDefaultCloseOperation(obs.EXIT_ON_CLOSE);
//		env.addObservers(obs);
		
		List<Episode> episodes = new ArrayList<Episode>();
		RmaxQLearningAgent rmaxq = new RmaxQLearningAgent(root, hs, initState, vmax, threshold, maxDelta);
		
		for(int i = 1; i <= numEpisodes; i++){
			Episode e = rmaxq.runLearningEpisode(env, maxSteps);
			episodes.add(e);
			System.out.println("Episode " + i + " time " + rmaxq.getTime() / 1000.0);
			env.resetEnvironment();
		}
		
		EpisodeSequenceVisualizer ev = new EpisodeSequenceVisualizer
				(TaxiVisualizer.getVisualizer(5, 5), domain, episodes);
		ev.setDefaultCloseOperation(ev.EXIT_ON_CLOSE);
		ev.initGUI();
	}
	
	public static void main(String[] args) {
		double correctMoveprob = 1;
		double fickleProb = 0.333;
		int numEpisodes = 1000;
		int maxSteps = 100;
		int rmaxThreshold = 5;
		double gamma = 0.99;
		double rmax = 20;
		double maxDelta = 0.00001;
		boolean randomStart = false;
		String stateSetup = "small";
		Task RAMDProot = TaxiHierarchy.createAMDPHierarchy(correctMoveprob, fickleProb);
		OOSADomain base = TaxiHierarchy.getBaseDomain();
//		Task RMAXQroot = TaxiHierarchy.createRMAXQHierarchy(correctMoveprob, fickleProb);
		StateGenerator sg = null;
		State initial = null;
		if(randomStart) {
			if ("classic".equals(stateSetup)) {
				sg = new RandomPassengerClassicTaxiState();
			} else if ("small".equals(stateSetup)) {
				sg = new RandomPassengerSmallTaxiState();
			} else {
				throw new RuntimeException("unknown stateSetup type");
			}
			initial = sg.generateState();
		} else {
			if ("classic".equals(stateSetup)) {
				initial = TaxiStateFactory.createClassicState();
			} else if ("small".equals(stateSetup)) {
				initial = TaxiStateFactory.createSmallState();
			} else {
				throw new RuntimeException("unknown stateSetup type");
			}
		}

		runRAMDPEpisodes(numEpisodes, maxSteps, RAMDProot, initial, base, rmaxThreshold, gamma, rmax, maxDelta, randomStart);
//		runRMAXQEpsodes(numEpisodes, maxSteps, RMAXQroot, s, rmax, rmaxThreshold, maxDelta, base);
	}
}
