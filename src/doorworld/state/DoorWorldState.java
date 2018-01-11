package doorworld.state;

import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import doorworld.DoorWorld;


import java.util.*;

public class DoorWorldState implements MutableOOState {

    private DoorWorldAgent agent;
    private Map<String, DoorWorldWall> walls;
    private Map<String, DoorWorldRoom> rooms;
    // private Map<String, DoorWorldDoor> doors;

    public DoorWorldState(DoorWorldAgent agent, List<DoorWorldWall> walls) {
        this.agent = agent;

        this.walls = new HashMap<String, DoorWorldWall>();
        for (DoorWorldWall w: walls) {
            this.walls.put(w.name(), w);
        }
    }

    public DoorWorldState(DoorWorldAgent a, Map<String, DoorWorldWall> walls) {
        this.agent = a;
        this.walls = walls;
    }

    @Override
    public MutableOOState addObject(ObjectInstance objectInstance) {
        if(objectInstance instanceof DoorWorldAgent
                || objectInstance.className().equals(DoorWorld.CLASS_AGENT)) {
            touchAgent();
            agent = (DoorWorldAgent) objectInstance;
        } else if(objectInstance instanceof DoorWorldWall
                || objectInstance.className().equals(DoorWorld.CLASS_WALL)) {
            touchWalls().put(objectInstance.name(), (DoorWorldWall) objectInstance);
        } else if(objectInstance instanceof DoorWorldRoom
                || objectInstance.className().equals(DoorWorld.CLASS_ROOM)){
            touchRooms().put(objectInstance.name(), (DoorWorldRoom) objectInstance);
        } else {
            throw new RuntimeException("Can only add certain objects.");
        }
        return this;
    }

    @Override
    public MutableOOState removeObject(String name) {
        throw new RuntimeException("Remove not implemented");
    }

    @Override
    public MutableOOState renameObject(String currName, String newName) {
        throw new RuntimeException("Rename not implemented");
    }

    @Override
    public int numObjects() {
        return 1 + walls.size();
    }

    @Override
    public ObjectInstance object(String name) {
        if(agent.name().equals(name)) {
            return agent;
        }
        ObjectInstance o = walls.get(name);
        if(o != null) {
            return o;
        }

        return null;
    }

    @Override
    public List<ObjectInstance> objects() {
        List<ObjectInstance> objs = new ArrayList<ObjectInstance>();
        objs.add(agent);
        objs.addAll(walls.values());
        return objs;
    }

    @Override
    public List<ObjectInstance> objectsOfClass(String className) {
        if(className.equals(DoorWorld.CLASS_AGENT)) {
            return Arrays.asList(agent);
        }
        else if(className.equals(DoorWorld.CLASS_WALL)) {
            return new ArrayList<ObjectInstance>(walls.values());
        }
        throw new RuntimeException("No object class " + className);
    }

    @Override
    public MutableState set(Object varKey, Object value) {
        OOVariableKey key = OOStateUtilities.generateKey(varKey);
        if(key.obName.equals(agent.name())) {
            touchAgent().set(varKey, value);
        } else if(walls.get(key.obName) != null) {
            touchWall(key.obName).set(varKey, value);
        } else {
            throw new RuntimeException("ERROR: unable to set value for " + varKey);
        }
        return this;
    }

    @Override
    public List<Object> variableKeys() {
        return OOStateUtilities.flatStateKeys(this);
    }

    @Override
    public Object get(Object varKey) {
        return OOStateUtilities.get(this, varKey);
    }

    @Override
    public State copy() {
        return new DoorWorldState(agent, walls);
    }

    public Object getAgentAtt(String attName) {
        return agent.get(attName);
    }

    // touchAgent
    // Returns a shallow copy of the agent
    public DoorWorldAgent touchAgent() {
        this.agent = agent.copy();
        return agent;
    }

    // touchWall
    // Given a wall name, returns a shallow copy of the wall
    public DoorWorldWall touchWall(String wallName) {
        DoorWorldWall w = walls.get(wallName).copy();
        touchWalls().remove(wallName);
        walls.put(wallName, w);
        return w;
    }

    // touchWalls
    // Returns a shallow copy of all of the walls
    public Map<String, DoorWorldWall> touchWalls() {
        this.walls = new HashMap<String, DoorWorldWall>(walls);
        return walls;
    }

    // touchRooms
    // Returns a shallow copy of all of the rooms
    public Map<String, DoorWorldRoom> touchRooms() {
        this.rooms = new HashMap<String, DoorWorldRoom>(rooms);
        return rooms;
    }
}
