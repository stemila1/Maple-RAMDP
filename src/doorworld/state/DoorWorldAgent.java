package doorworld.state;

import burlap.mdp.core.oo.state.ObjectInstance;
import doorworld.DoorWorld;
import utilities.MutableObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Stephanie Milani on 01/11/2018
 */

public class DoorWorldAgent extends MutableObject {

    private final static List<Object> keys = Arrays.asList(
            DoorWorld.ATT_X,
            DoorWorld.ATT_Y
    );

    // DoorWorldAgent
    public DoorWorldAgent(String name, int x, int y) { this(name, (Object) x, (Object) y);}

    // DoorWorldAgent
    public DoorWorldAgent(String name, Object x, Object y) {
        this.set(DoorWorld.ATT_X, x);
        this.set(DoorWorld.ATT_Y, y);
        this.setName(name);
    }

    // copy
    @Override
    public DoorWorldAgent copy() { return (DoorWorldAgent) copyWithName(name()); }

    // className
    // Returns the name of the class
    @Override
    public String className() {
        return DoorWorld.CLASS_AGENT;
    }

    // copyWithName
    @Override
    public ObjectInstance copyWithName(String name) {
        return new DoorWorldAgent(
                name,
                get(DoorWorld.ATT_X),
                get(DoorWorld.ATT_Y)
        );
    }

    // variableKeys
    @Override
    public List<Object> variableKeys() {
        return keys;
    }
}
