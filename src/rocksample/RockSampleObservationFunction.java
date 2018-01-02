package rocksample;

import burlap.debugtools.RandomFactory;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.observations.DiscreteObservationFunction;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
import burlap.mdp.singleagent.pomdp.observations.ObservationProbability;
import burlap.mdp.singleagent.pomdp.observations.ObservationUtilities;
import rocksample.state.RockSampleRock;
import rocksample.state.RockSampleState;
import rocksample.stateGenerator.RockSampleStateFactory;

//import burlap.mdp.singleagent.GroundedAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static rocksample.RockSamplePO.ACTION_CHECK;
import static rocksample.RockSamplePO.CLASS_ROCK;

/**
 * Created by steph on 11/9/2017.
 */
public class RockSampleObservationFunction implements DiscreteObservationFunction {

    protected double checkAccuracy;

    public void RockSampleState() {
    }

    public RockSampleObservationFunction(double checkAccuracy) {
        this.checkAccuracy = checkAccuracy;
    }

    //@Override
    public boolean canEnumerateObservation() {
        return true;
    }


    public List<State> allObservations() {

        List<State> result = new ArrayList<State>(2);

        result.add(this.observationGoodRock());
        result.add(this.observationBadRock());
        return result;
    }

    // TODO: UPDATE THIS TO DO THE MATH PORTION OF THE OBS PROBABILITY SO CAN CALL WITHIN SAMPLE
    //@Override
    public double getObservationProbability(State observation, State state,
                                            ObjectParameterizedAction action) {
        return this.checkAccuracy;
    }

    protected State observationGoodRock() {
        return new RockSampleObservation("Good");
    }

    protected State observationBadRock() {
        return new RockSampleObservation("Bad");
    }

    @Override
    public State sample(State state, Action action) {
        if (action.actionName().equals(ACTION_CHECK)) {
            ObjectParameterizedAction action_op = (ObjectParameterizedAction) action;

            String n = action_op.getObjectParameters()[0];
            RockSampleState rs_state = (RockSampleState) state;
            int roverX = (int) rs_state.getRoverAtt(RockSample.ATT_X);
            int roverY = (int) rs_state.getRoverAtt(RockSample.ATT_Y);

            int rockX = (int) rs_state.getRockAtt(n, RockSample.ATT_X);
            int rockY = (int) rs_state.getRockAtt(n, RockSample.ATT_Y);

            String rockQual = (String) rs_state.getRockAtt(n, RockSample.ATT_QUALITY);

            /* Now we will do math, though you can move it into observation rock and pass the paramters */
            int dx = (roverX - rockX) * (roverX - rockX); //squaring
            int dy = (roverY - rockY) * (roverY - rockY); //square

            double distance = Math.sqrt(dx + dy);
            double tunable_constant = 20;

            /* Now, we will apply that awkward function */
            double sensor_efficiency = Math.pow(2, -(distance) / tunable_constant);

            /* get random number, and if less than random number */
            //Random rand = new Random();
            double rand = RandomFactory.getMapped(0).nextDouble();
            if (sensor_efficiency > rand) {
                if (rockQual.equals("Good")) {
                    return this.observationGoodRock();
                } else {
                    return this.observationBadRock();
                }
            } else {
                if (rockQual.equals("Good")) {
                    return this.observationBadRock();
                } else {
                    return this.observationGoodRock();
                }
            }
        }
        throw new RuntimeException("Unknown action " + action.actionName() + "; cannot return observation sample.");
    }

    @Override
    public List<ObservationProbability> probabilities (State state, Action action){
        return ObservationUtilities.probabilitiesByEnumeration((DiscreteObservationFunction) this, state, action);
    }

    @Override
    public double probability (State observation, State state, Action action){
        String oVal = (String) observation.get(RockSamplePO.ACTION_CHECK);
        String rockVal = (String) state.get(RockSample.CLASS_ROCK);


            // if action name is check
            // then if the value of the rock is good/bad
            //  return the accuracy
            // otherwise return 0

        // TODO: this might need to be updated to be more like tiger
        if (action.actionName().equals(RockSamplePO.ACTION_CHECK)) {
            return this.checkAccuracy;
        }
        throw new RuntimeException("Unknown action " +
                                   action.actionName() +
                                   "; cannot return observation probability.");
    }
}

