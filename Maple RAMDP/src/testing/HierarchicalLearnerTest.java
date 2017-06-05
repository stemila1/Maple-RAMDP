package testing;

import java.util.ArrayList;
import java.util.List;

import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.VisualActionObserver;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import hierarchy.framework.GroundedTask;
import hierarchy.framework.Task;
import ramdp.agent.RAMDPLearningAgent;
import rmaxq.agent.RmaxQLearningAgent;
import taxi.TaxiDomain;
import taxi.TaxiVisualizer;
import taxi.hierarchies.TaxiHierarchy;
import taxi.state.TaxiState;
import utilities.SimpleHashableStateFactory;

public class HierarchicalLearnerTest {

	public static SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory(false);
	
	public static void runRMAXQEpsodes(int numEpisodes, Task root, State initState, double vmax,
			int threshold, double maxDelta, OOSADomain domain){
		
		SimulatedEnvironment env = new SimulatedEnvironment(domain, initState);
//		VisualActionObserver obs = new VisualActionObserver(domain, TaxiVisualizer.getVisualizer(5, 5));
//		obs.initGUI();
//		obs.setDefaultCloseOperation(obs.EXIT_ON_CLOSE);
//		env.addObservers(obs);
		
		List<Episode> episodes = new ArrayList<Episode>();
		RmaxQLearningAgent rmaxq = new RmaxQLearningAgent(root, hashingFactory, initState, vmax, threshold, maxDelta);
		
		for(int i = 1; i <= numEpisodes; i++){
			Episode e = rmaxq.runLearningEpisode(env);
			episodes.add(e);
			System.out.println("Episode " + i + " time " + rmaxq.getTime() / 1000.0);
			env.resetEnvironment();
		}
		
		EpisodeSequenceVisualizer ev = new EpisodeSequenceVisualizer
				(TaxiVisualizer.getVisualizer(5, 5), domain, episodes);
		ev.setDefaultCloseOperation(ev.EXIT_ON_CLOSE);
		ev.initGUI();
	}

	public static void runRAMDPEpisodes(int numEpisode, int maxSteps, Task root,
			State initial, OOSADomain groundDomain,
			int threshold, double discount, double rmax, double maxDelta){
		
		List<Episode> episodes = new ArrayList<Episode>();
		
		GroundedTask rootgt = root.getAllGroundedTasks(initial).get(0);
		
		RAMDPLearningAgent ramdp = new RAMDPLearningAgent(rootgt, threshold, discount, rmax, 
				hashingFactory, maxDelta);
		
		SimulatedEnvironment env = new SimulatedEnvironment(groundDomain, initial);
		
		VisualActionObserver obs = new VisualActionObserver(groundDomain, TaxiVisualizer.getVisualizer(5, 5));
        obs.initGUI();
        obs.setDefaultCloseOperation(obs.EXIT_ON_CLOSE);
        env.addObservers(obs);
		
		for(int i = 1; i <= numEpisode; i++){
			long time = System.currentTimeMillis();
			Episode e = ramdp.runLearningEpisode(env, maxSteps);
			time = System.currentTimeMillis() - time;
			episodes.add(e);
			System.out.println("\nEpisode " + i + " time " + (double)time/1000 );
			env.resetEnvironment();
		}
		
		EpisodeSequenceVisualizer ev = new EpisodeSequenceVisualizer
				(TaxiVisualizer.getVisualizer(5, 5), groundDomain, episodes);
		ev.setDefaultCloseOperation(ev.EXIT_ON_CLOSE);
		ev.initGUI();
	}
	
	public static void main(String[] args) {
		boolean usesFuel = false;
		boolean fickle = false;
//		TaxiState s = TaxiDomain.getClassicState(usesFuel);
		TaxiState s = TaxiDomain.getTinyClassicState(usesFuel);
//		TaxiState s = TaxiDomain.getSmallClassicState(usesFuel);
		Task RAMDProot = TaxiHierarchy.createRAMDPHierarchy(s, fickle);
		OOSADomain base = TaxiHierarchy.getGroundDomain();
//		Task RMAXQroot = TaxiHierarchy.createRMAXQHierarchy(s, false);
		
		int numEpisodes = 10;
		int maxSteps = 10;
		int threshold = 5;
		double discount = 0.99;
		double rmax = 30;
		double maxDelta = 0.0001;
		runRAMDPEpisodes(numEpisodes, maxSteps, RAMDProot, s, base, threshold, discount, rmax, maxDelta);
		
//		runRMAXQEpsodes(100, RMAXQroot, s, 30, 5, 0.01, base);
		
	}
}
