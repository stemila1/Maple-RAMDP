package rocksample.state;

import burlap.mdp.core.oo.state.ObjectInstance;

import java.util.Arrays;
import java.util.List;

import rocksample.RockSample;

/**
 * Created by steph on 10/26/2017.
 */
public class RoverAgent extends RockSamplePt {

    /**
     * x, y
     */
    private final static List<Object> keys = Arrays.<Object>asList(
            RockSample.ATT_X,
            RockSample.ATT_Y
    );

    /**
     *      Constructors
     */
    public RoverAgent(String name, int x, int y) {
        this(name, (Object) x, (Object) y);
    }

    private RoverAgent(String name, Object x, Object y) {
        this.set(RockSample.ATT_X, x);
        this.set(RockSample.ATT_Y, y);
        this.setName(name);
    }

    @Override
    public String className() {
        return RockSample.CLASS_ROVER;
    }

    @Override
    public RoverAgent copy() {
        return (RoverAgent) copyWithName(name());
    }

    @Override
    public ObjectInstance copyWithName(String objectName) {
        return new RoverAgent(
                objectName,
                get(RockSample.ATT_X),
                get(RockSample.ATT_Y)
        );
    }

    @Override
    public List<Object> variableKeys() {
        return keys;
    }
}
