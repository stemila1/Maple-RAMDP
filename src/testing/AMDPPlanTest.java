package testing;

import java.util.ArrayList;
import java.util.List;

import amdp.planning.AMDPPlanner;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.debugtools.RandomFactory;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import hierarchy.framework.Task;
import taxi.TaxiVisualizer;
import taxi.hierarchies.TaxiHierarchy;
import taxi.state.TaxiState;
import taxi.stateGenerator.TaxiStateFactory;

public class AMDPPlanTest {
	
	public static void plan(Task root, State init, HashableStateFactory hs, OOSADomain baseDomain,
			double gamma, double maxDelta, int maxRollouts, int numEpisodes){
		
		AMDPPlanner amdp = new AMDPPlanner(root, gamma, hs, maxDelta, maxRollouts);
		List<Episode> eps = new ArrayList<Episode>();

		for(int i = 0; i < numEpisodes; i++){
			System.out.println(i);
			eps.add(amdp.planFromState(init));
		}
		
		EpisodeSequenceVisualizer ev = new EpisodeSequenceVisualizer
				(TaxiVisualizer.getVisualizer(5, 5), baseDomain, eps);;
		ev.setDefaultCloseOperation(ev.EXIT_ON_CLOSE);
		ev.initGUI();
	}
	
	public static void main(String[] args) {
		double correctMoveprob = 0.99;
		double fickleProb = 0.40;
		double gamma = 0.95;
		double maxDelta = 0.01;
		int maxRollouts = 100;
		int numEpisodes = 10;
		
		RandomFactory.seedMapped(0, 3456543L);
		
		TaxiState s = TaxiStateFactory.createClassicState();
		Task AMDProot = TaxiHierarchy.createAMDPHierarchy(correctMoveprob, fickleProb);
		OOSADomain base = TaxiHierarchy.getBaseDomain();
		plan(AMDProot, s, new SimpleHashableStateFactory(), base, gamma, maxDelta, maxRollouts, numEpisodes);
	}
}
