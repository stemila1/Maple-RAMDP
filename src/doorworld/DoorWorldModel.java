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
    public double illegalActionReward;
    public double goalReward;

    // DoorWorldModel
    public DoorWorldModel(double noReward, double illegalActionReward, double goalReward) {
        this.noReward = noReward;
        this.illegalActionReward = illegalActionReward;
        this.goalReward = goalReward;
    }

    // transitions
    @Override
    public List<TransitionProb> transitions(State s, Action a) {
        // transitions are probably messed up
        String actionName = a.actionName();
        int goalX = 6;
        int goalY = 6;
        // if the action is one of the movement actions -- think that this might be the issue
        // seems like with tiger, they make each action have a variety of outcomes -- how does
        // rocksample handle it?
        if(actionName.equals(ACTION_NORTH)) {
            DoorWorldState ns = moveNorth((DoorWorldState) s, a);
            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_NORTH);

            // just added this check -- not sure if it makes sense
            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, goalReward, true))
                );
            } else {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, noReward, false)));
            }
        } else if(actionName.equals(ACTION_EAST)) {
            DoorWorldState ns = moveEast((DoorWorldState) s, a);

            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_EAST);
            // just added this check -- not sure if it makes sense
            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, goalReward, true))
                );
            } else {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, noReward, false)));
            }
        } else if(actionName.equals(ACTION_SOUTH)) {
            DoorWorldState ns = moveSouth((DoorWorldState) s, a);

            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_SOUTH);

            // just added this check -- not sure if it makes sense
            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, goalReward, true))
                );
            } else {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, noReward, false)));
            }
        } else if(actionName.equals(ACTION_WEST)) {
            DoorWorldState ns = moveWest((DoorWorldState) s, a);

            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_WEST);

            // just added this check -- not sure if it makes sense
            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, goalReward, true))
                );
            } else {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ns, noReward, false)));
            }
        } else if(actionName.equals(ACTION_OPEN_DOOR)) {
            State ds = s.copy();
            DoorWorldDoor door = getDoorToOpen((DoorWorldState) ds);
            // if the door exists
            if(door == null) {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, s, illegalActionReward, false))
                );
            }
            else if(door != null && door.get(ATT_LOCKED) == VAL_LOCKED) {
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, s, noReward, false))
                );
            // if door exists and is unlocked
            } else {
                door.set(ATT_CLOSED, VAL_OPEN);
                // TODO: update transitions to x% of time not open the door even if it is unlocked
                return Arrays.asList(
                        new TransitionProb(1., new EnvironmentOutcome(s, a, ds, noReward, false))
                );
            }
        }
        throw new RuntimeException("Unknown action " + a.toString());
    }

    // sample
    // TODO: make test for goal loc prop fn?
    @Override
    public EnvironmentOutcome sample(State state, Action action) {
        String actionName = action.actionName();
        int goalX = 6;
        int goalY = 6;

        // movement actions
        if(actionName.equals(ACTION_NORTH)) {
            DoorWorldState ns = moveNorth((DoorWorldState) state, action);

            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_NORTH);

            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return new EnvironmentOutcome(state, action, ns, goalReward, true);
            } else {
                return new EnvironmentOutcome(state, action, ns, noReward, false);
            }

        } else if(actionName.equals(ACTION_EAST)) {
            DoorWorldState ns = moveEast((DoorWorldState) state, action);

            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_EAST);

            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return new EnvironmentOutcome(state, action, ns, goalReward, true);
            } else {
                return new EnvironmentOutcome(state, action, ns, noReward, false);
            }
        } else if(actionName.equals(ACTION_SOUTH)) {
            DoorWorldState ns = moveSouth((DoorWorldState) state, action);

            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_SOUTH);
            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return new EnvironmentOutcome(state, action, ns, goalReward, true);
            } else {
                return new EnvironmentOutcome(state, action, ns, noReward, false);
            }
        } else if(actionName.equals(ACTION_WEST)) {
            DoorWorldState ns = moveWest((DoorWorldState) state, action);

            // update direction
            DoorWorldAgent nAgent = ns.touchAgent();
            nAgent.set(ATT_DIR, ACTION_WEST);
            if((int) nAgent.get(ATT_X) == goalX && (int) nAgent.get(ATT_Y) == goalY) {
                return new EnvironmentOutcome(state, action, ns, goalReward, true);
            } else {
                return new EnvironmentOutcome(state, action, ns, noReward, false);
            }
        } else if(actionName.equals(ACTION_OPEN_DOOR)) {
            DoorWorldState ns = (DoorWorldState) state;
            DoorWorldDoor door = getDoorToOpen((DoorWorldState) state);

            // if there is no door
            if(door == null) {
                return new EnvironmentOutcome(state, action, state,
                        illegalActionReward, false);
            } else if(door.get(ATT_LOCKED) == VAL_UNLOCKED) {
                door.set(ATT_CLOSED, VAL_OPEN);
                return new EnvironmentOutcome(state, action, ns, noReward, false);
            } else {
                return new EnvironmentOutcome(state, action, state,
                        noReward, false);
            }
        }

        throw new RuntimeException("Unknown action " + action.toString());
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

        DoorWorldDoor door = getDoor(ns);
        if(door != null) {
            door.set(ATT_CLOSED, VAL_CLOSED);
        }

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

        DoorWorldDoor door = getDoor(ns);
        if(door != null) {
            door.set(ATT_CLOSED, VAL_CLOSED);
        }

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

        DoorWorldDoor door = getDoor(ns);
        if(door != null) {
            door.set(ATT_CLOSED, VAL_CLOSED);
        }

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

        DoorWorldDoor door = getDoor(ns);
        if(door != null) {
            door.set(ATT_CLOSED, VAL_CLOSED);
        }

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
