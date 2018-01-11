package doorworld;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.model.FullModel;
import burlap.mdp.singleagent.model.TransitionProb;
import doorworld.state.DoorWorldAgent;
import doorworld.state.DoorWorldState;

import java.util.Arrays;
import java.util.List;

import static doorworld.DoorWorld.*;

public class DoorWorldModel implements FullModel {
    public double noReward;

    public DoorWorldModel(double noReward) {
        this.noReward = noReward;
    }

    @Override
    public List<TransitionProb> transitions(State s, Action a) {
        String actionName = a.actionName();

        if(actionName.equals(ACTION_NORTH)
            || actionName.equals(ACTION_SOUTH)
            || actionName.equals(ACTION_EAST)
            || actionName.equals(ACTION_WEST)) {
            return Arrays.asList(
                    new TransitionProb(1., new EnvironmentOutcome(s, a, s, noReward, false))
            );
        }
        throw new RuntimeException("Unknown action " + a.toString());
    }

    @Override
    public EnvironmentOutcome sample(State state, Action action) {
        String actionName = action.actionName();

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
        }

        throw new RuntimeException("Unknown action " + action.toString());
    }

    public DoorWorldState moveNorth(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int ny = (int) state.getAgentAtt(ATT_Y) + 1;
        DoorWorldAgent nagent = ns.touchAgent();
        nagent.set(ATT_Y, ny);
        return ns;
    }

    public DoorWorldState moveEast(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int nx = (int) state.getAgentAtt(ATT_X) + 1;
        DoorWorldAgent nagent = ns.touchAgent();
        nagent.set(ATT_X, nx);
        return ns;
    }

    public DoorWorldState moveSouth(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int ny = (int) state.getAgentAtt(ATT_Y) - 1;
        DoorWorldAgent nagent = ns.touchAgent();
        nagent.set(ATT_Y, ny);
        return ns;
    }

    public DoorWorldState moveWest(DoorWorldState state, Action action) {
        DoorWorldState ns = (DoorWorldState) state.copy();

        int nx = (int) state.getAgentAtt(ATT_X) - 1;
        DoorWorldAgent nagent = ns.touchAgent();
        nagent.set(ATT_X, nx);
        return ns;
    }

    @Override
    public boolean terminal(State state) {
        return false;
    }
}
