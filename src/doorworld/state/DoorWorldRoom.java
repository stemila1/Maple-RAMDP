package doorworld.state;

import burlap.mdp.core.oo.state.ObjectInstance;
import doorworld.DoorWorld;
import utilities.MutableObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Stephanie Milani on 01/11/2018
 */

public class DoorWorldRoom extends MutableObject {

    private final static List<Object> keys = Arrays.asList(
            DoorWorld.ATT_LEFT,
            DoorWorld.ATT_RIGHT,
            DoorWorld.ATT_BOTTOM,
            DoorWorld.ATT_TOP,
            DoorWorld.ATT_COLOR
    );

    // DoorWorldRoom
    public DoorWorldRoom(String name, int left, int right, int bottom, int top, String color) {
        this.set(DoorWorld.ATT_LEFT, left);
        this.set(DoorWorld.ATT_RIGHT, right);
        this.set(DoorWorld.ATT_BOTTOM, bottom);
        this.set(DoorWorld.ATT_TOP, top);
        this.set(DoorWorld.ATT_COLOR, color);
        this.name = name;
    }

    // className
    @Override
    public String className() {
        return DoorWorld.CLASS_ROOM;
    }

    // copyWithName
    @Override
    public ObjectInstance copyWithName(String name) {
        return new DoorWorldRoom(name,
                (int) get(DoorWorld.ATT_LEFT),
                (int) get(DoorWorld.ATT_RIGHT),
                (int) get(DoorWorld.ATT_BOTTOM),
                (int) get(DoorWorld.ATT_TOP),
                (String) get(DoorWorld.ATT_COLOR));
    }

    // variableKeys
    @Override
    public List<Object> variableKeys() {
        return keys;
    }
}
