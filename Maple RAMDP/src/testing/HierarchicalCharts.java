package testing;

import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
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

public class HierarchicalCharts {

	public static void createCrarts(final State s, OOSADomain domain, final Task RAMDPRoot, final Task RMEXQRoot, 
			final int rmax, final int threshold, final double maxDelta, final double discount, int numEpisode, int numTrial){
		final HashableStateFactory hs = new SimpleHashableStateFactory();
		final GroundedTask RAMDPGroot = RAMDPRoot.getAllGroundedTasks(s).get(0); 
		
		SimulatedEnvironment env = new SimulatedEnvironment(domain, s);
//		VisualActionObserver obs = new VisualActionObserver(domain, TaxiVisualizer.getVisualizer(5, 5));
//        obs.initGUI();
//        obs.setDefaultCloseOperation(obs.EXIT_ON_CLOSE);
//        env.addObservers(obs);
		
		LearningAgentFactory rmaxq = new LearningAgentFactory() {
			
			@Override
			public String getAgentName() {
				return "R-MAXQ";
			}
			
			@Override
			public LearningAgent generateAgent() {
				return new RmaxQLearningAgent(RMEXQRoot, hs, s, rmax, threshold, maxDelta);
			}
		};
		
		LearningAgentFactory ramdp = new LearningAgentFactory() {
			
			@Override
			public String getAgentName() {
				return "R-AMDP";
			}
			
			@Override
			public LearningAgent generateAgent() {
				return new RAMDPLearningAgent(RAMDPGroot, threshold, discount, rmax, hs, maxDelta);
			}
		};
		
		LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(env, numTrial, numEpisode, ramdp, rmaxq);
		exp.setUpPlottingConfiguration(500, 300, 2, 1000,
				TrialMode.MOST_RECENT_AND_AVERAGE,
				PerformanceMetric.STEPS_PER_EPISODE,
				PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
				PerformanceMetric.CUMULATIVE_REWARD_PER_EPISODE,
				PerformanceMetric.CUMULATIVE_REWARD_PER_STEP,
				PerformanceMetric.AVERAGE_EPISODE_REWARD,
				PerformanceMetric.MEDIAN_EPISODE_REWARD
				);
		
		exp.startExperiment();
		exp.writeEpisodeDataToCSV("./ramdp_full_state_data_jw.csv");
	}
	
	public static void main(String[] args) {
		boolean fickle = true;
		TaxiState s = TaxiDomain.getSmallClassicState(false);
		Task RAMDProot = TaxiHierarchy.createRAMDPHierarchy(s, fickle);
		OOSADomain base = TaxiHierarchy.getGroundDomain();
		Task RMAXQroot = TaxiHierarchy.createRMAXQHierarchy(s, fickle);
		int numEpisodes = 30;
		int numTrials = 10;
		double discount = 0.9;
		double maxDelta = 0.001;
		int threshold = 5;
		int rmax = 20;
		createCrarts(s, base, RAMDProot, RMAXQroot, rmax, threshold, maxDelta, discount, numEpisodes, numTrials);
	}
}
