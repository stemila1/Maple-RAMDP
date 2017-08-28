package cleanup.hierarchies;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import utilities.MutableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static cleanup.Cleanup.ATT_COLOR;
import static cleanup.Cleanup.CLASS_ROOM;


public class MoveRoom extends MutableObject {

    public static final String ATT_CONNECTED = "connected";

    private final static List<Object> keys = Arrays.<Object>asList(
            ATT_CONNECTED,
            ATT_COLOR
    );

    public MoveRoom(String name, String color, Set<String> connected) {
        this.setName(name);
        this.set(ATT_COLOR, color);
        this.set(ATT_CONNECTED, connected);
    }

    @Override
    public String className() {
        return CLASS_ROOM;
    }

    @Override
    public ObjectInstance copyWithName(String objectName) {
        return new MoveRoom(objectName, (String) get(ATT_COLOR), (Set<String>) this.get(ATT_CONNECTED));
    }

    @Override
    public List<Object> variableKeys() {
        return keys;
    }


}