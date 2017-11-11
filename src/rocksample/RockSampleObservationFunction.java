package rocksample;

import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;

import burlap.mdp.singleagent.GroundedAction;

import java.util.ArrayList;
import java.util.List;

import static rocksample.RockSamplePO.ACTION_CHECK;

/**
 * Created by steph on 11/9/2017.
 */
public class RockSampleObservationFunction extends ObservationFunction {

    protected double checkAccuracy;

    public RockSampleObservationFunction(POOODomain domain, double checkAccuracy){
        super(domain);
        this.checkAccuracy = checkAccuracy;
    }

    @Override
    public boolean canEnumerateObservation(){
        return false;
    }

    @Override
    public List<State> getAllPossibleObservations(){
        List<State> result = new ArrayList<State>(1);

        result.add(this.observationRock());

        return result;
    }

    @Override
    public State sampleObservation(State state, ObjectParameterizedAction action){
        if (action.actionName().equals(ACTION_CHECK)){

            return this.observationRock();
        }

    }

    @Override
    public double getObservationProbability(State observation, State state,
                                            ObjectParameterizedAction action){
        return 1.;
    }

    protected State observationRock(){
        State checkRock = new State();

        return checkRock;
    }

}
