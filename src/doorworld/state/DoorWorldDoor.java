package doorworld.state;

import burlap.mdp.core.oo.state.ObjectInstance;
import doorworld.DoorWorld;
import utilities.MutableObject;

import java.util.Arrays;
import java.util.List;

public class DoorWorldDoor extends MutableObject {

    private final static List<Object> keys = Arrays.asList(
            DoorWorld.ATT_X,
            DoorWorld.ATT_Y,
            DoorWorld.ATT_LEFT,
            DoorWorld.ATT_RIGHT,
            DoorWorld.ATT_BOTTOM,
            DoorWorld.ATT_TOP,
            DoorWorld.ATT_LOCKED,
            DoorWorld.ATT_COLOR
    );

    public DoorWorldDoor(String name, int left, int right, int bottom, int top, String locked, String color) {
        this.set(DoorWorld.ATT_X, left);
        this.set(DoorWorld.ATT_Y, bottom);
        this.set(DoorWorld.ATT_LEFT, left);
        this.set(DoorWorld.ATT_RIGHT, right);
        this.set(DoorWorld.ATT_BOTTOM, bottom);
        this.set(DoorWorld.ATT_TOP, top);
        this.set(DoorWorld.ATT_LOCKED, locked);
        this.set(DoorWorld.ATT_COLOR, color);
        this.name = name;
    }

    public DoorWorldDoor(String name, int left, int right, int bottom, int top, String lockableState) {
        this(name, left, right, bottom, top, lockableState, DoorWorld.COLOR_GRAY);
    }

    // className
    @Override
    public String className() {
        return DoorWorld.CLASS_DOOR;
    }

    // copyWithName
    @Override
    public ObjectInstance copyWithName(String name) {
        return new DoorWorldDoor(name,
                (int) get(DoorWorld.ATT_LEFT),
                (int) get(DoorWorld.ATT_RIGHT),
                (int) get(DoorWorld.ATT_BOTTOM),
                (int) get(DoorWorld.ATT_TOP),
                (String) get(DoorWorld.ATT_LOCKED),
                (String) get(DoorWorld.ATT_COLOR));
    }

    // variableKeys
    @Override
    public List<Object> variableKeys() {
        return keys;
    }
}
