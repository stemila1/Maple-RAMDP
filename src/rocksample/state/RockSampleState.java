package rocksample.state;

import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import rocksample.RockSample;

import java.util.*;

/**
 * Created by steph on 10/26/2017.
 */
public class RockSampleState implements MutableOOState {

    public RoverAgent getRover() { return rover; }

    public void setRover (RoverAgent rover) { this.rover = rover; }

    public void setWalls(Map<String, RockSampleWall> walls) { this.walls = walls; }

    private RoverAgent rover;
    private Map<String, RockSampleWall> walls;

    public RockSampleState(RoverAgent rover,
                           List<RockSampleWall> walls) {
        this.rover = rover;

        this.walls = new HashMap<String, RockSampleWall>();
        for(RockSampleWall w : walls) {
            this.walls.put(w.name(), w);
        }
    }

    public RockSampleState(RoverAgent r,
                           Map<String, RockSampleWall> walls) {
        this.rover = r;
        this.walls = walls;
    }

    @Override
    public int numObjects() { return 1 + walls.size(); }

    @Override
    public ObjectInstance object(String oname) {
        if(rover.name().equals(oname))
            return rover;

        // change this ordering once more stuff is added
        ObjectInstance o = walls.get(oname);

        o = walls.get(oname);
        if(o != null)
            return o;

        return null;
    }

    @Override
    public List<ObjectInstance> objects() {
        List<ObjectInstance> objs = new ArrayList<ObjectInstance>();
        objs.add(rover);
        objs.addAll(walls.values());
        return objs;
    }

    @Override
    public List<ObjectInstance> objectsOfClass(String oclass) {
        if(oclass.equals(RockSample.CLASS_ROVER))
            return Arrays.<ObjectInstance>asList(rover);
       else if(oclass.equals(RockSample.CLASS_WALL))
            return new ArrayList<ObjectInstance>(walls.values());
        throw new RuntimeException("No object class " + oclass);
    }

    @Override
    public List<Object> variableKeys() {
        return OOStateUtilities.flatStateKeys(this);
    }

    @Override
    public Object get(Object variableKey) {
        return OOStateUtilities.get(this, variableKey);
    }

    @Override
    public RockSampleState copy() {
        return new RockSampleState(rover, walls);
    }

    @Override
    public MutableState set(Object variableKey, Object value) {
        OOVariableKey key = OOStateUtilities.generateKey(variableKey);

        if(key.obName.equals(rover.name())){
            touchRover().set(variableKey, value);
        }else if(walls.get(key.obName) != null){
            touchWall(key.obName).set(variableKey, value);
        } else {
            throw new RuntimeException("ERROR: unable to set value for " + variableKey);
        }
        return this;
    }

    @Override
    public MutableOOState addObject(ObjectInstance o) {
        if(o instanceof RoverAgent || o.className().equals(RockSample.CLASS_ROVER)){
            touchRover();
            rover = (RoverAgent) o;
        }else if(o instanceof RockSampleWall || o.className().equals(RockSample.CLASS_WALL)){
            touchWalls().put(o.name(), (RockSampleWall) o);
        }else{
            throw new RuntimeException("Can only add certain objects to state.");
        }
        return this;
    }

    @Override
    public MutableOOState removeObject(String oname) {
        throw new RuntimeException("Remove not implemented");
    }

    @Override
    public MutableOOState renameObject(String objectName, String newName) {
        throw new RuntimeException("Rename not implemented");
    }

    //touch methods allow a shallow copy of states and a copy of objects only when modified
    public RoverAgent touchRover(){
        this.rover = rover.copy();
        return rover;
    }

    public RockSampleWall touchWall(String wallName){
        RockSampleWall w = walls.get(wallName).copy();
        touchWalls().remove(wallName);
        walls.put(wallName, w);
        return w;
    }

    public Map<String, RockSampleWall> touchWalls(){
        this.walls = new HashMap<String, RockSampleWall>(walls);
        return walls;
    }

    public String[] getWalls(){
        String[] ret = new String[walls.size()];
        int i = 0;
        for(String name: walls.keySet())
            ret[i++] = name;
        return ret;
    }

    public Object getRoverAtt(String attName){
        return rover.get(attName);
    }

    public String getRoverName(){
        return rover.name();
    }

    //test to see if there is a wall on either side of the taxi
    public boolean wallNorth(){
        int tx = (int) rover.get(RockSample.ATT_X);
        int ty = (int) rover.get(RockSample.ATT_Y);
        for(RockSampleWall w : walls.values()){
            boolean ish = (boolean) w.get(RockSample.ATT_IS_HORIZONTAL);
            int wx = (int) w.get(RockSample.ATT_START_X);
            int wy = (int) w.get(RockSample.ATT_START_Y);
            int wlen = (int) w.get(RockSample.ATT_LENGTH);
            if(ish){
                //wall in above line
                if(ty == wy - 1){
                    //x value in wall bounds
                    if(tx >= wx && tx < wx + wlen){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean wallEast(){
        int tx = (int) rover.get(RockSample.ATT_X);
        int ty = (int) rover.get(RockSample.ATT_Y);
        for(RockSampleWall w : walls.values()){
            boolean ish = (boolean) w.get(RockSample.ATT_IS_HORIZONTAL);
            int wx = (int) w.get(RockSample.ATT_START_X);
            int wy = (int) w.get(RockSample.ATT_START_Y);
            int wlen = (int) w.get(RockSample.ATT_LENGTH);
            if(!ish){
                if(tx == wx - 1){
                    if(ty >= wy && ty < wy + wlen){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean wallSouth(){
        int tx = (int) rover.get(RockSample.ATT_X);
        int ty = (int) rover.get(RockSample.ATT_Y);
        for(RockSampleWall w : walls.values()){
            boolean ish = (boolean) w.get(RockSample.ATT_IS_HORIZONTAL);
            int wx = (int) w.get(RockSample.ATT_START_X);
            int wy = (int) w.get(RockSample.ATT_START_Y);
            int wlen = (int) w.get(RockSample.ATT_LENGTH);
            if(ish){
                if(ty == wy){
                    if(tx >= wx && tx < wx + wlen){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean wallWest(){
        int tx = (int) rover.get(RockSample.ATT_X);
        int ty = (int) rover.get(RockSample.ATT_Y);
        for(RockSampleWall w : walls.values()){
            boolean ish = (boolean) w.get(RockSample.ATT_IS_HORIZONTAL);
            int wx = (int) w.get(RockSample.ATT_START_X);
            int wy = (int) w.get(RockSample.ATT_START_Y);
            int wlen = (int) w.get(RockSample.ATT_LENGTH);
            if(!ish){
                if(tx == wx){
                    if(ty >= wy && ty < wy + wlen){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString(){
        return OOStateUtilities.ooStateToString(this);
    }
}
