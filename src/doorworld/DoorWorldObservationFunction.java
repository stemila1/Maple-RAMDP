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

        if(action.actionName().equals(ACTION_NORTH)
                || action.actionName().equals(ACTION_EAST)
                || action.actionName().equals(ACTION_WEST)
                || action.actionName().equals(ACTION_SOUTH)) {
            return this.observationNull();
        } else if(action.actionName().equals(ACTION_OPEN_DOOR)) {
            DoorWorldState s = (DoorWorldState) state;
            DoorWorldDoor door = getDoorToOpen(s);
            if(door == null) {
                return this.observationNull();
            } else {
                double randomNumber = .5;
                double fixedResult = .3;

                if (fixedResult < randomNumber) {
                    if(door.equals(VAL_LOCKED)) {
                        return this.observationLocked();
                    }
                    else {
                        return this.observationUnlocked();
                    }
                } else {
                    if(door.equals(VAL_LOCKED)) {
                        return this.observationUnlocked();
                    } else {
                        return this.observationLocked();
                    }
                }
            }
        }
        throw new RuntimeException("Unknown action " + action.actionName()
                + "; cannot return observation sample.");
    }

    @Override
    public double probability(State observation, State sprime, Action action) {

        // might need to change this to actually get what i want to (look at rs)
        String oVal = (String) observation.get(ATTR_OBS);

        String actionName = action.actionName();
        DoorWorldState state = (DoorWorldState) sprime;

        if(oVal.equals(OBS_NULL)) {
            if(actionName.equals(ACTION_OPEN_DOOR)) {
                return 0.;
            }
            else {
                return 1.;
            }
        }
        if(oVal.equals(OBS_LOCKED) || oVal.equals(OBS_UNLOCKED)) {
            if(actionName.equals(ACTION_OPEN_DOOR)) {
                return .5;
            } else {
                return 0.;
            }
        }
        return 0.;

       /* if(actionName.equals(ACTION_NORTH)
                || actionName.equals(ACTION_EAST)
                || actionName.equals(ACTION_SOUTH)
                || actionName.equals(ACTION_WEST)) {
            if(agentX == agentOx && agentY == agentOy) {
                return 1.;
            }
            else {
                return 0.;
            }
        } */


     //   throw new RuntimeException("Unknown action " + action.actionName()
     //           + "; cannot return observation probability.");
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
