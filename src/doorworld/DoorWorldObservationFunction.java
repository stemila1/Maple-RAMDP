package doorworld;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.observations.DiscreteObservationFunction;
import burlap.mdp.singleagent.pomdp.observations.ObservationProbability;
import burlap.mdp.singleagent.pomdp.observations.ObservationUtilities;
import doorworld.state.DoorWorldDoor;
import doorworld.state.DoorWorldState;

import java.util.ArrayList;
import java.util.List;

import static doorworld.DoorWorld.*;

/**
 *  Created by Stephanie Milani
 */

public class DoorWorldObservationFunction implements DiscreteObservationFunction {

    public DoorWorldObservationFunction() {

    }

    @Override
    public List<State> allObservations() {
        List<State> result = new ArrayList(2);

        result.add(this.observationLocked());
        result.add(this.observationUnlocked());
        result.add(this.observationNull());

        return result;
    }

    @Override
    public State sample(State state, Action action) {

        throw new RuntimeException("Unknown action " + action.actionName()
                + "; cannot return observation sample.");
    }

    @Override
    public double probability(State observation, State sprime, Action action) {

        // might need to change this to actually get what i want to (look at rs)
        String oVal = (String) observation.get(ATTR_OBS);

        String actionName = action.actionName();
        DoorWorldState state = (DoorWorldState) sprime;
        ObjectInstance agent = state.touchAgent();
        // first should test whether the goals are completed - need obs_complete

        DoorWorldDoor door = getDoorToOpen((DoorWorldState) state);
        String isOpen = (String) door.get(ATT_LOCKED);

        // if the agent is opening the door
        if(actionName.equals(ACTION_OPEN_DOOR)) {

            // if observe that door is locked and door is locked
            if(oVal.equals(OBS_LOCKED) && isOpen.equals(VAL_LOCKED)) {
                return 1.; // this is where to include any degradation of accuracy
            // if observe that door is locked and door is actually unlocked
            } else if(oVal.equals(OBS_LOCKED) && isOpen.equals(VAL_UNLOCKED)) {
                return 0.; // this is where to include any degradation of accuracy
            // if observe that door is unlocked and door is unlocked
            } else if(oVal.equals(OBS_UNLOCKED) && isOpen.equals(VAL_UNLOCKED)) {
                return 1.; // this is where to include any degradation of accuracy
            // if observe that door is unlocked and door is locked
            } else if(oVal.equals(OBS_UNLOCKED) && isOpen.equals(VAL_LOCKED)) {
                return 0.; // this is where to include any degradation of accuracy
            // if observe that door is null
            } else {
                return 0;
            }
        }


        throw new RuntimeException("Unknown action " + action.actionName()
                + "; cannot return observation probability.");
    }

    // getDoorToOpen
    public DoorWorldDoor getDoorToOpen(DoorWorldState s) {
        int agentX = (int) s.getAgentAtt(ATT_X);
        int agentY = (int) s.getAgentAtt(ATT_Y);

        int xdelta = 0;
        int ydelta = 0;
        String agentDir = s.getAgentAtt(ATT_DIR).toString();

        if(agentDir == ACTION_NORTH) {
            ydelta = 1;
        } else if (agentDir == ACTION_EAST) {
            xdelta = 1;
        } else if (agentDir == ACTION_SOUTH) {
            ydelta = -1;
        } else {
            xdelta = -1;
        }

        int nx = agentX + xdelta;
        int ny = agentY + ydelta;

        DoorWorldDoor door = s.doorContainingPoint(nx, ny);
        return door;
    }

    // probabilities
    @Override
    public List<ObservationProbability> probabilities(State state, Action action) {
        return ObservationUtilities.probabilitiesByEnumeration(this, state, action);
    }

    // observationLocked
    protected State observationLocked() {
        return new DoorWorldObservationState(OBS_LOCKED);
    }

    // observationUnlocked
    protected State observationUnlocked() {
        return new DoorWorldObservationState(OBS_UNLOCKED);
    }

    // observationNull
    protected State observationNull() {
        return new DoorWorldObservationState(OBS_NULL);
    }
}
