package taxi.hierarchies.tasks.get;

import java.util.ArrayList;
import java.util.List;

import burlap.debugtools.RandomFactory;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import taxi.hierarchies.tasks.bringon.BringonActionType.BringonAction;
import taxi.hierarchies.tasks.bringon.TaxiBringonDomain;
import taxi.hierarchies.tasks.get.state.TaxiGetAgent;
import taxi.hierarchies.tasks.get.state.TaxiGetPassenger;
import taxi.hierarchies.tasks.get.state.TaxiGetState;
import taxi.hierarchies.tasks.nav.NavigateActionType.NavigateAction;
import taxi.hierarchies.tasks.nav.TaxiNavDomain;

public class TaxiGetModel implements FullStateModel {

	@Override
	public State sample(State s, Action a) {
		List<StateTransitionProb> stpList = this.stateTransitions(s,a);
        double roll = RandomFactory.getMapped(0).nextDouble();
        double curSum = 0.;
        for(int i = 0; i < stpList.size(); i++){
            curSum += stpList.get(i).p;
            if(roll < curSum){
                return stpList.get(i).s;
            }
        }
        throw new RuntimeException("Probabilities don't sum to 1.0: " + curSum);
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		List<StateTransitionProb> tps = new ArrayList<>();
		TaxiGetState state = (TaxiGetState) s;
	
		if(a.actionName().startsWith(TaxiBringonDomain.ACTION_BRINGON)) {
			bringon(state, (BringonAction)a, tps);
		} else if(a.actionName().startsWith(TaxiNavDomain.ACTION_NAVIGATE)) {
			navigate(state, (NavigateAction)a, tps);
		}
		return tps;
	}

	/**
	 * put the requested passenger into the taxi
	 * @param s the current state
	 * @param a the get action type
	 * @param tps the list of transition probabilities
	 */
	public void bringon(TaxiGetState s, BringonAction a, List<StateTransitionProb> tps){
		TaxiGetState ns = s.copy();
		String passenger = a.getPassenger();

		TaxiGetPassenger np = s.touchPassenger(passenger);
		np.set(TaxiGetDomain.ATT_LOCATION, TaxiGetDomain.IN_TAXI);

		tps.add(new StateTransitionProb(ns, 1));
	}

	/**
	 * put the requested passenger into the taxi
	 * @param s the current state
	 * @param a the get action type
	 * @param tps the list of transition probabilities
	 */
	public void navigate(TaxiGetState s, NavigateAction a, List<StateTransitionProb> tps){
		TaxiGetState ns = s.copy();
		String goal = a.getGoalLocation();

		TaxiGetAgent nt = s.touchTaxi();
		nt.set(TaxiGetDomain.ATT_LOCATION, goal);

		tps.add(new StateTransitionProb(ns, 1));
	}
}
