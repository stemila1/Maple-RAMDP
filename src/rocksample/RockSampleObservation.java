package rocksample;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;

import java.util.Arrays;
import java.util.List;

import static rocksample.RockSamplePO.ATT_QUALITY;

/**
 * Created by steph on 12/7/2017.
 */
public class RockSampleObservation implements MutableState {

    public String obs;

    // todo: Make "Empty" a constant like ATT_QUALITY?
    public RockSampleObservation(){
        obs = "Empty";
    }

    public RockSampleObservation(String obs){
        if(!obs.equals("Empty") || obs.equals("Good") || obs.equals("Bad")){
            throw new RuntimeException("Rock can only be empty, good, or bad.");
        }
        this.obs = obs;
    }

    @Override
    public MutableState set(Object key, Object value) {
        if(!(value instanceof String)){
            throw new RuntimeException("Value must be a String");
        }

        String obs = (String) value;

        if(!obs.equals("Empty") || obs.equals("Good") || obs.equals("Bad")){
            throw new RuntimeException("Rock can only be empty, good, or bad.");
        }

        this.obs = obs;
        return this;
    }

    @Override
    public List<Object> variableKeys() {
        return Arrays.<Object>asList(ATT_QUALITY);
    }

    @Override
    public Object get(Object o) {
        return obs;
    }

    @Override
    public State copy() {
        return new RockSampleObservation(obs);
    }

    @Override
    public String toString(){
        return obs;
    }
}
