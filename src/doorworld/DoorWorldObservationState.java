package doorworld;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import doorworld.DoorWorld;

import java.util.Arrays;
import java.util.List;

import static doorworld.DoorWorld.VAL_UNKNOWN;


public class DoorWorldObservationState implements MutableState {

    public String observation;

    public DoorWorldObservationState() {
        //doorUnlocked = VAL_UNKNOWN;
    }

    public DoorWorldObservationState(String obs) {
        observation = obs;
    }

    @Override
    public MutableState set(Object varKey, Object value) {
        String obs = (String) varKey;
        this.observation = (String) value;

        return this;
    }

    @Override
    public List<Object> variableKeys() {
        return Arrays.<Object>asList();
    }

    @Override
    public Object get(Object o) {
        return observation;
    }

    @Override
    public State copy() {
        return new DoorWorldObservationState(observation);
    }
}
