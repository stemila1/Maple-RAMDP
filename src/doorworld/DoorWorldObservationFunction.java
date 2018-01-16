package doorworld;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.model.TransitionProb;
import burlap.mdp.singleagent.pomdp.observations.DiscreteObservationFunction;
import burlap.mdp.singleagent.pomdp.observations.ObservationProbability;
import burlap.mdp.singleagent.pomdp.observations.ObservationUtilities;
import doorworld.state.DoorWorldDoor;
import doorworld.state.DoorWorldState;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<State> result = new ArrayList(3);

        result.add(this.observationLocked());
        result.add(this.observationUnlocked());
        result.add(this.observationNull());
        result.add(this.observationComplete());

        return result;
    }

    @Override
    public State sample(State state, Action action) {
        DoorWorldState sprime = (DoorWorldState) state;
        int goalX = 6;
        int goalY = 6;

        if(action.actionName().equals(ACTION_NORTH)
                || action.actionName().equals(ACTION_EAST)
                || action.actionName().equals(ACTION_WEST)
                || action.actionName().equals(ACTION_SOUTH)) {
            // get agent loc & test to see if at goal
            if((int) sprime.getAgentAtt(ATT_X) == goalX && (int) sprime.getAgentAtt(ATT_Y) == goalY) {
                return this.observationComplete();
            }
            else {
                return this.observationNull();
            }
        } else if(action.actionName().equals(ACTION_OPEN_DOOR)) {
            DoorWorldDoor door = getDoorToOpen(sprime);
            if(door == null) {
                return this.observationNull();
            } else if(door != null && door.get(ATT_CLOSED) == VAL_OPEN) {
                return this.observationUnlocked();
            } else {
                return this.observationLocked();
            }
        }
        throw new RuntimeException("Unknown action " + action.actionName()
                + "; cannot return observation sample.");
    }

    // TODO: update this as well
    @Override
    public double probability(State observation, State sprime, Action action) {

        String oVal = (String) observation.get(ATTR_OBS);

        String actionName = action.actionName();
        DoorWorldState state = (DoorWorldState) sprime;
        int goalX = 6;
        int goalY = 6;
        // if any of the movement actions
        //    if OVal equals complete
        //      if agent is in right loc
        //         return 1.;
        //      else return 0.;
        //    else if Oval equals Null
        //       if agent != right loc
        //          return 1;
        //       else return 0;
        if(actionName.equals(ACTION_NORTH)
                || actionName.equals(ACTION_EAST)
                || actionName.equals(ACTION_SOUTH)
                || actionName.equals(ACTION_WEST)) {
            if(oVal.equals(OBS_COMPLETE)) {
                if((int) state.getAgentAtt(ATT_X) == goalX
                        && (int) state.getAgentAtt(ATT_Y) == goalY) {
                    return 1.;
                } else {
                    return 0.;
                }
            } else if(oVal.equals(OBS_NULL)) {
                if((int) state.getAgentAtt(ATT_X) == goalX
                        && (int) state.getAgentAtt(ATT_Y) == goalY) {
                    return 0.;
                }
                else {
                    return 1.;
                }
            } else {
                throw new RuntimeException("Observation" + oVal.toString() + " not permitted with this state");
            }
        }
        if(actionName.equals(ACTION_OPEN_DOOR)) {
            DoorWorldDoor door = getDoorToOpen(state);
            if(oVal.equals(OBS_NULL)) {
                if(door == null) {
                    return 1.;
                } else {
                    return 0.;
                }
            } else if(oVal.equals(OBS_UNLOCKED)) {
                if(door != null && door.get(ATT_CLOSED) == VAL_OPEN) {
                    return 1.;
                } else {
                    return 0.;
                }
            } else if(oVal.equals(OBS_LOCKED)) {
                if(door != null && door.get(ATT_CLOSED) == VAL_OPEN) {
                    return 0.;
                } else {
                    return 1.;
                }
            } else {
                throw new RuntimeException("Observation " + oVal.toString() + "not permitted with this state");
            }
        }
        // if opendooraction
        //      get underlying door in state
        //      if Oval equals Null
        //        if door DNE
        //           return 1.;
        //        else return 0;
        //      else if Oval equal Unlocked
        //        if door is open
        //           return 1.;
        //        else return 0;
        //      else
        //         if door is open
        //            return 0.;
        //         else return 1.;

        /*if(oVal.equals(OBS_NULL)) {
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
        */
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

    // observationComplete
    protected State observationComplete() { return new DoorWorldObservationState(OBS_COMPLETE); }
}
