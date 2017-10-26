package rocksample.state;

import rocksample.RockSample;
import utilities.MutableObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by steph on 10/26/2017.
 *
 * Abstract point class with x and y attributes.
 */
public abstract class RockSamplePt extends MutableObject {

    /**
     * default constructor
     */
    public RockSamplePt()
    {
    }

    /**
     * standard x, y
     */
    private final static List<Object> keys = Arrays.<Object>asList(
            RockSample.ATT_X,
            RockSample.ATT_Y
    );

    public RockSamplePt(String name, int x, int y) {
        this(name, (Object) x, (Object) y);
    }

    private RockSamplePt(String name, Object x, Object y) {
        this.set(RockSample.ATT_X, x);
        this.set(RockSample.ATT_Y, y);
        this.setName(name);
    }

    @Override
    public String className() {
        return RockSample.CLASS_PT;
    }

    @Override
    public RockSamplePt copy() {
        return (RockSamplePt) copyWithName(name());
    }

    @Override
    public List<Object> variableKeys() {
        return keys;
    }
}
