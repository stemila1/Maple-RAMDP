package doorworld;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.model.FullModel;
import burlap.mdp.singleagent.model.TransitionProb;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldDoor;
import doorworld.state.DoorWorldState;

import java.util.Arrays;
import java.util.List;

import static doorworld.DoorWorld.*;

/**
 * Created by Stephanie Milani on 01/11/2018
 */

public class DoorWorldModel implements FullModel {
    public double noReward;

    public DoorWorldModel(double noReward) {
        this.noReward = noReward;
    }

    // transitions
    @Override
    public List<TransitionProb> transitions(State s, Action a) {
        String actionName = a.actionName();

        // if the action is one of the movement actions
        if(actionName.equals(ACTION_NORTH)
            || actionName.equals(ACTION_SOUTH)
            || actionName.equals(ACTION_EAST)
            || actionName.equals(ACTION_WEST)) {
            return Arrays.asList(
                    new TransitionProb(1., new EnvironmentOutcome(s, a, s, noReward, false))
            );
        } else if(actionName.equals(ACTION_OPEN_DOOR)) {
            State ds = s.copy();
            DoorWorldDoor door = getDoorNearby((DoorWorldState) s);
            if(door != null) {
                return Arrays.asList(
                        new TransitionProb(0.5, new EnvironmentOutcome(s, a,
                                new DoorWorldState(door.name(), VAL_LOCKED), noReward, false)),
                        new TransitionProb(0.5, new EnvironmentOutcome(s, a,
                                new DoorWorldState(door.name(), VAL_UNLOCKED), noReward, false))
                );
            } else {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, s, noReward, false)));
            }
        }
        throw new RuntimeException("Unknown action " + a.toString());
    }

    // sample
    @Override
    public EnvironmentOutcome sample(State state, Action action) {
        String actionName = action.actionName();

        // movement actions
        if(actionName.equals(ACTION_NORTH)) {
            DoorWorldState ns = moveNorth((DoorWorldState) state, action);
            return new EnvironmentOutcome(state, action, ns, noReward, false);
        } else if(actionName.equals(ACTION_EAST)) {
            DoorWorldState ns = moveEast((DoorWorldState) state, action);
            return new EnvironmentOutcome(state, action, ns, noReward, false);
        } else if(actionName.equals(ACTION_SOUTH)) {
            DoorWorldState ns = moveSouth((DoorWorldState) state, action);
            return new EnvironmentOutcome(state, action, ns, noReward, false);
        } else if(actionName.equals(ACTION_WEST)) {
            DoorWorldState ns = moveWest((DoorWorldState) state, action);
            return new EnvironmentOutcome(state, action, ns, noReward, false);
        } else if(actionName.equals(ACTION_OPEN_DOOR)) {
            DoorWorldState ns = (DoorWorldState) state;
            DoorWorldDoor door = getDoorNearby((DoorWorldState) state);

            // if there is no door
            if(door == null) {
                return new EnvironmentOutcome(state, action, state, noReward, false);
            } else if (door.get(ATT_LOCKED) == VAL_UNLOCKED) {
                door.set(ATT_CLOSED, VAL_OPEN);
                return new EnvironmentOutcome(state, action, ns, noReward, false);
            } else {
                return new EnvironmentOutcome(state, action, state, noReward, false);
            }
        }

        throw new RuntimeException("Unknown action " + action.toString());
    }

    // getDoorNearby
    public DoorWorldDoor getDoorNearby(DoorWorldState s) {
        int agentX = (int) s.getAgentAtt(ATT_X);
        int agentY = (int) s.getAgentAtt(ATT_Y);

        DoorWorldDoor doorEast = s.doorContainingPoint(agentX + 1, agentY);
        DoorWorldDoor doorNorth = s.doorContainingPoint(agentX, agentY + 1);
        DoorWorldDoor doorSouth = s.doorContainingPoint(agentX, agentY - 1);
        DoorWorldDoor doorWest = s.doorContainingPoint(agentX - 1, agentY);

        if(doorEast != null) {
            return doorEast;
        } else if(doorNorth != null) {
            return doorNorth;
        } else if(doorSouth != null) {
            return doorSouth;
        } else if(doorWest != null) {
            return doorWest;
        } else {
            return null;
        }
    }

    // getDoor
    public DoorWorldDoor getDoor(DoorWorldState s) {
        int agentX = (int) s.getAgentAtt(ATT_X);
        int agentY = (int) s.getAgentAtt(ATT_Y);

        DoorWorldDoor door = s.doorContainingPoint(agentX, agentY);
        return door;
    }

    // moveNorth
    public DoorWorldState moveNorth(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int x = (int) state.getAgentAtt(ATT_X);
        int ny = (int) state.getAgentAtt(ATT_Y) + 1;

        // TODO: make these checks more efficient
        // if there isn't a wall
        if(!ns.wallAt(x, ny)) {
            DoorWorldAgent nagent = ns.touchAgent();
            nagent.set(ATT_Y, ny);

            DoorWorldDoor nDoor = getDoor(ns);

            // if there isn't a door in the way
            if(nDoor == null) {
                return ns;
            }

            // if there is an open door
            if(nDoor.get(ATT_CLOSED) == VAL_OPEN) {
                return ns;
            } else {
                return state;
            }
        } else {
            return state;
        }

    }

    // moveEast
    public DoorWorldState moveEast(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int y = (int) state.getAgentAtt(ATT_Y);
        int nx = (int) state.getAgentAtt(ATT_X) + 1;

        // TODO: make these checks more efficient
        // if there isn't a wall
        if(!ns.wallAt(nx, y)) {
            DoorWorldAgent nagent = ns.touchAgent();
            nagent.set(ATT_X, nx);

            DoorWorldDoor nDoor = getDoor(ns);

            // if there isn't a door in the way
            if(nDoor == null) {
                return ns;
            }

            // if there is an open door
            if(nDoor.get(ATT_CLOSED) == VAL_OPEN) {
                return ns;
            } else {
                return state;
            }

        } else {
            return state;
        }

    }

    // moveSouth
    public DoorWorldState moveSouth(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int x = (int) state.getAgentAtt(ATT_X);
        int ny = (int) state.getAgentAtt(ATT_Y) - 1;

        if(!ns.wallAt(x, ny)) {
            DoorWorldAgent nagent = ns.touchAgent();
            nagent.set(ATT_Y, ny);

            DoorWorldDoor nDoor = getDoor(ns);

            // if there isn't a door in the way
            if(nDoor == null) {
                return ns;
            }

            // if there is an open door
            if(nDoor.get(ATT_CLOSED) == VAL_OPEN) {
                return ns;
            } else {
                return state;
            }
        } else {
            return state;
        }

    }

    // moveWest
    public DoorWorldState moveWest(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int y = (int) state.getAgentAtt(ATT_Y);
        int nx = (int) state.getAgentAtt(ATT_X) - 1;

        if(!ns.wallAt(nx, y)) {
            DoorWorldAgent nagent = ns.touchAgent();
            nagent.set(ATT_X, nx);

            DoorWorldDoor nDoor = getDoor(ns);

            // if there isn't a door in the way
            if(nDoor == null) {
                return ns;
            }

            // if there is an open door
            if(nDoor.get(ATT_CLOSED) == VAL_OPEN) {
                return ns;
            } else {
                return state;
            }
        } else {
            return state;
        }
    }

    @Override
    public boolean terminal(State state) {
        return false;
    }
}
