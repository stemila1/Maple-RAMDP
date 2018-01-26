package doorworld;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import doorworld.DoorWorld;

import java.util.Arrays;
import java.util.List;

import static doorworld.DoorWorld.ATTR_OBS;
import static doorworld.DoorWorld.VAL_UNKNOWN;


public class DoorWorldObservationState implements MutableState {

    public String observation;

    public DoorWorldObservationState() {
        //doorUnlocked = VAL_UNKNOWN;
    }

    // DoorWorldObservationState
    public DoorWorldObservationState(String obs) {
        // just updated this... see if this works.. it doesn't
        set(ATTR_OBS, obs);
        this.observation = obs;
    }

    // set
    @Override
    public MutableState set(Object varKey, Object value) {
        String obs = (String) varKey;
        this.observation = (String) value;

        return this;
    }

    // variableKeys
    @Override
    public List<Object> variableKeys() {
        return Arrays.<Object>asList(ATTR_OBS);
    }

    // get
    @Override
    public Object get(Object o) {
        return observation;
    }

    // copy
    @Override
    public State copy() {
        return new DoorWorldObservationState(observation);
    }
}
