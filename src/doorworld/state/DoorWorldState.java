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
    private static final int DEFAULT_MIN_X = 0;
    private static final int DEFAULT_MIN_Y = 0;

    private int width;
    private int height;
    private DoorWorldAgent agent;
    private Map<String, DoorWorldRoom> rooms;
    private Map<String, DoorWorldDoor> doors;

    public DoorWorldState(int w, int h, DoorWorldAgent agent, List<DoorWorldRoom> rooms) {
        this.width = w;
        this.height = h;
        this.agent = agent;
        this.rooms = new HashMap<String, DoorWorldRoom>();
    }

    public DoorWorldState(int w, int h, DoorWorldAgent agent, Map<String, DoorWorldRoom> rooms,
                          Map<String, DoorWorldDoor> doors) {
        this.width = w;
        this.height = h;
        this.agent = agent;
        this.rooms = rooms;
        this.doors = doors;
    }

    public DoorWorldState(String name, String locked) {
        set(name, locked);
    }

    @Override
    public MutableOOState addObject(ObjectInstance objectInstance) {
        if(objectInstance instanceof DoorWorldAgent
                || objectInstance.className().equals(DoorWorld.CLASS_AGENT)) {
            touchAgent();
            agent = (DoorWorldAgent) objectInstance;
        } else if(objectInstance instanceof DoorWorldRoom
                || objectInstance.className().equals(DoorWorld.CLASS_ROOM)) {
            touchRooms().put(objectInstance.name(), (DoorWorldRoom) objectInstance);
        } else if(objectInstance instanceof DoorWorldDoor
                || objectInstance.className().equals(DoorWorld.CLASS_DOOR)) {
            touchDoors().put(objectInstance.name(), (DoorWorldDoor) objectInstance);
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

    // numObjects
    @Override
    public int numObjects() {
        return 1 + rooms.size() + doors.size();
    }

    // object
    @Override
    public ObjectInstance object(String name) {
        if(agent.name().equals(name)) {
            return agent;
        }
        ObjectInstance o = rooms.get(name);
        if(o != null) {
            return o;
        }
        o = doors.get(name);
        if(o != null) {
            return o;
        }
        return null;
    }

    // objects
    @Override
    public List<ObjectInstance> objects() {
        List<ObjectInstance> objs = new ArrayList<ObjectInstance>();
        objs.add(agent);
        objs.addAll(rooms.values());
        objs.addAll(doors.values());
        return objs;
    }

    // objectsOfClass
    @Override
    public List<ObjectInstance> objectsOfClass(String className) {
        if(className.equals(DoorWorld.CLASS_AGENT)) {
            return Arrays.asList(agent);
        } else if(className.equals(DoorWorld.CLASS_ROOM)) {
            return new ArrayList<ObjectInstance>(rooms.values());
        } else if(className.equals(DoorWorld.CLASS_DOOR)) {
            return new ArrayList<ObjectInstance>(doors.values());
        }
        throw new RuntimeException("No object class " + className);
    }

    // set
    @Override
    public MutableState set(Object varKey, Object value) {
        OOVariableKey key = OOStateUtilities.generateKey(varKey);
        if(key.obName.equals(agent.name())) {
            touchAgent().set(varKey, value);
        }  else if(rooms.get(key.obName) != null) {
            touchRoom(key.obName).set(varKey, value);
        } else if(doors.get(key.obName) != null) {
            touchDoor(key.obName).set(varKey, value);
        } else {
            throw new RuntimeException("ERROR: unable to set value for " + varKey);
        }

        return this;
    }

    // variableKeys
    @Override
    public List<Object> variableKeys() {
        return OOStateUtilities.flatStateKeys(this);
    }

    // get
    @Override
    public Object get(Object varKey) {
        return OOStateUtilities.get(this, varKey);
    }

    // copy
    @Override
    public State copy() {
        return new DoorWorldState(width, height, agent, rooms, doors);
    }

    // getAgentAtt
    public Object getAgentAtt(String attName) {
        return agent.get(attName);
    }

    // wallAt
    public boolean wallAt(int x, int y) {

        if (x < DEFAULT_MIN_X || x >= DEFAULT_MIN_X + width || y < DEFAULT_MIN_Y || y >= DEFAULT_MIN_Y + height) {
            return true;
        }

        // check if any room has a wall at x,y
        for (DoorWorldRoom room : rooms.values()) {
            if (wallAt(room, x, y)) {
                return true;
            }
        }
        return false;
    }

    // wallAt
    private boolean wallAt(ObjectInstance r, int x, int y) {
        if(r == null) {
            return true;
        }

        // if out of bounds
        if(x < DEFAULT_MIN_X || x >= width || y < DEFAULT_MIN_Y || y >= height) {
            return true;
        }

        // get room parameters
        int top = (Integer) r.get(DoorWorld.ATT_TOP);
        int left = (Integer) r.get(DoorWorld.ATT_LEFT);
        int bottom = (Integer) r.get(DoorWorld.ATT_BOTTOM);
        int right = (Integer) r.get(DoorWorld.ATT_RIGHT);

        // agent along wall of room check
        if (((x == left || x == right) && y >= bottom && y <= top)
                || ((y == bottom || y == top) && x >= left && x <= right)) {
            //then only way for this to be a valid pos is if a door contains this point
            ObjectInstance door = doorContainingPoint(x, y);
            if (door == null) {
                return true;
            }
        }
        return false;
    }

    // regionContainsPoint
    public static boolean regionContainsPoint(ObjectInstance o, int x, int y, boolean countBound) {
        int top = (Integer) o.get(DoorWorld.ATT_TOP);
        int left = (Integer) o.get(DoorWorld.ATT_LEFT);
        int bottom = (Integer) o.get(DoorWorld.ATT_BOTTOM);
        int right = (Integer) o.get(DoorWorld.ATT_RIGHT);

        if(countBound) {
            if(y >= bottom && y <= top && x >= left && x <= right) {
                return true;
            }
        } else {
            if(y > bottom && y < top && x > left && x < right) {
                return true;
            }
        }
        return false;
    }

    // regionContainingPoint
    protected static ObjectInstance regionContainingPoint(List<ObjectInstance> obs, int x, int y, boolean countBound) {
        for(ObjectInstance o : obs) {
            if(regionContainsPoint(o, x, y, countBound)) {
                return o;
            }
        }
        return null;
    }

    // doorContainingPoint
    public DoorWorldDoor doorContainingPoint(int x, int y) {
        List<ObjectInstance> doors = objectsOfClass(DoorWorld.CLASS_DOOR);
        return (DoorWorldDoor) regionContainingPoint(doors, x, y, true);
    }

    // touchAgent
    // Returns a shallow copy of the agent
    public DoorWorldAgent touchAgent() {
        this.agent = agent.copy();
        return agent;
    }

    // touchRoom
    public DoorWorldRoom touchRoom(String roomName) {
        DoorWorldRoom r = (DoorWorldRoom) rooms.get(roomName).copy();
        touchRooms().remove(roomName);
        rooms.put(roomName, r);
        return r;
    }

    // touchRooms
    // Returns a shallow copy of all of the rooms
    public Map<String, DoorWorldRoom> touchRooms() {
        this.rooms = new HashMap<String, DoorWorldRoom>(rooms);
        return rooms;
    }

    // touchDoor
    public DoorWorldDoor touchDoor(String doorName) {
        DoorWorldDoor d = (DoorWorldDoor) doors.get(doorName).copy();
        touchDoors().remove(doorName);
        doors.put(doorName, d);
        return d;
    }

    // touchDoors
    public Map<String, DoorWorldDoor> touchDoors() {
        this.doors = new HashMap<String, DoorWorldDoor> (doors);
        return doors;
    }
}