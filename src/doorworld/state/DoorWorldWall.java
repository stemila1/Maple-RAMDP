package doorworld.state;

import burlap.mdp.core.oo.state.ObjectInstance;
import doorworld.DoorWorld;
import utilities.MutableObject;

import java.util.Arrays;
import java.util.List;

public class DoorWorldWall extends MutableObject {

    private final static List<Object> keys = Arrays.<Object>asList(
            DoorWorld.ATT_START_X,
            DoorWorld.ATT_START_Y,
            DoorWorld.ATT_LENGTH,
            DoorWorld.ATT_IS_HORIZONTAL
    );

    public DoorWorldWall(String name, int startX, int startY, int length, boolean isH) {
        this(name, (Object) startX, (Object) startY, (Object) length, (Object) isH);
    }

    public DoorWorldWall(String name, Object startX, Object startY, Object length, Object isH) {
        this.set(DoorWorld.ATT_START_X, startX);
        this.set(DoorWorld.ATT_START_Y, startY);
        this.set(DoorWorld.ATT_LENGTH, length);
        this.set(DoorWorld.ATT_IS_HORIZONTAL, isH);
        this.setName(name);
    }

    @Override
    public DoorWorldWall copy() {
        return (DoorWorldWall) copyWithName(name());
    }

    @Override
    public ObjectInstance copyWithName(String objectName) {
        return new DoorWorldWall(objectName,
                get(DoorWorld.ATT_START_X),
                get(DoorWorld.ATT_START_Y),
                get(DoorWorld.ATT_LENGTH),
                get(DoorWorld.ATT_IS_HORIZONTAL));
    }

    @Override
    public String className() { return DoorWorld.CLASS_WALL; }

    @Override
    public List<Object> variableKeys() { return keys; }
}
