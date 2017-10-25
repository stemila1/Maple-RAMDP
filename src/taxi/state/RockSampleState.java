package taxi.state;

import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.MutableState;
import taxi.Taxi;
import taxi.hierarchies.interfaces.PassengerParameterizable;

import java.util.*;

public class RockSampleState implements MutableOOState, PassengerParameterizable{

	public RoverAgent getTaxi() {
		return taxi;
	}

	public void setTaxi(RoverAgent taxi) {
		this.taxi = taxi;
	}

	public void setPassengers(Map<String, TaxiPassenger> passengers) {
		this.passengers = passengers;
	}

	public void setLocations(Map<String, TaxiLocation> locations) {
		this.locations = locations;
	}

	//contains a taxi, passengers, locations and walls
	private RoverAgent taxi;
	private Map<String, TaxiPassenger> passengers;
	private Map<String, TaxiLocation> locations;
	
	public RockSampleState(RoverAgent taxi, List<TaxiPassenger> passengers, List<TaxiLocation> locations) {
		this.taxi = taxi;
		
		this.passengers = new HashMap<String, TaxiPassenger>();
		for(TaxiPassenger p : passengers){
			this.passengers.put(p.name(), p);
		}
		
		this.locations = new HashMap<String, TaxiLocation>();
		for(TaxiLocation l : locations){
			this.locations.put(l.name(), l);
		}
	}
	
	public RockSampleState(RoverAgent t, Map<String, TaxiPassenger> pass, Map<String, TaxiLocation> locs) {
		this.taxi = t;
		this.passengers = pass;
		this.locations = locs;
	}
	
	@Override
	public int numObjects() {
		return 1 + passengers.size() + locations.size();
	}

	@Override
	public ObjectInstance object(String oname) {
		if(taxi.name().equals(oname))
			return taxi;
		
		ObjectInstance o = passengers.get(oname);
		if(o != null)
			return o;
		
		o = locations.get(oname);
		if(o != null)
			return o;

		
		return null;
	}

	@Override
	public List<ObjectInstance> objects() {
		List<ObjectInstance> objs = new ArrayList<ObjectInstance>();
		objs.add(taxi);
		objs.addAll(passengers.values());
		objs.addAll(locations.values());
		return objs;
	}

	@Override
	public List<ObjectInstance> objectsOfClass(String oclass) {
		if(oclass.equals(Taxi.CLASS_ROVER))
			return Arrays.<ObjectInstance>asList(taxi);
		else if(oclass.equals(Taxi.CLASS_PASSENGER))
			return new ArrayList<ObjectInstance>(passengers.values());
		else if(oclass.equals(Taxi.CLASS_LOCATION))
			return new ArrayList<ObjectInstance>(locations.values());
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
		return new RockSampleState(taxi, passengers, locations);
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
		OOVariableKey key = OOStateUtilities.generateKey(variableKey);
		
		if(key.obName.equals(taxi.name())){
			touchTaxi().set(variableKey, value);
		}else if(passengers.get(key.obName) != null){
			touchPassenger(key.obName).set(variableKey, value);
		}else if(locations.get(key.obName) != null){
			touchLocation(key.obName).set(variableKey, value);
		} else {
			throw new RuntimeException("ERROR: unable to set value for " + variableKey);
		}
		return this;
	}

	@Override
	public MutableOOState addObject(ObjectInstance o) {
		if(o instanceof RoverAgent || o.className().equals(Taxi.CLASS_ROVER)){
			touchTaxi();
			taxi = (RoverAgent) o;
		}else if(o instanceof TaxiPassenger || o.className().equals(Taxi.CLASS_PASSENGER)){
			touchPassengers().put(o.name(), (TaxiPassenger) o);			
		}else if(o instanceof TaxiLocation || o.className().equals(Taxi.CLASS_LOCATION)){
			touchLocations().put(o.name(), (TaxiLocation) o);
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
	public RoverAgent touchTaxi(){
		this.taxi = taxi.copy();
		return taxi;
	}
	
	public TaxiPassenger touchPassenger(String passName){
		TaxiPassenger p = passengers.get(passName).copy();
		touchPassengers().remove(passName);
		passengers.put(passName, p);
		return p;
	}
	
	public TaxiLocation touchLocation(String locName){
		TaxiLocation l = locations.get(locName).copy();
		touchLocations().remove(locName);
		locations.put(locName, l);
		return l;
	}

	
	public Map<String, TaxiPassenger> touchPassengers(){
		this.passengers = new HashMap<String, TaxiPassenger>(passengers);
		return passengers;
	}
	
	public Map<String, TaxiLocation> touchLocations(){
		this.locations = new HashMap<String, TaxiLocation>(locations);
		return locations;
	}

	
	//get values from objects
	public String[] getPassengers(){
		String[] ret = new String[passengers.size()];
		int i = 0;
		for(String name : passengers.keySet())
			ret[i++] = name;
		return ret;
	}

	@Override
	public String getPassengerLocation(String pname) {
		int px = (int) passengers.get(pname).get(Taxi.ATT_X);
		int py = (int) passengers.get(pname).get(Taxi.ATT_Y);

		for(String loc : getLocations()){
			int lx = (int) locations.get(loc).get(Taxi.ATT_X);
			int ly = (int) locations.get(loc).get(Taxi.ATT_Y);
			if(px == lx && py == ly)
				return loc;
		}
		return Taxi.ON_ROAD;
	}
//
//	@Override
//	public String getTaxiLocation() {
//		int tx = (int) taxi.get(Taxi.ATT_X);
//		int ty = (int) taxi.get(Taxi.ATT_Y);
//
//		for(String loc : getLocations()){
//			int lx = (int) getLocationAtt(loc, Taxi.ATT_X);
//			int ly = (int) getLocationAtt(loc, Taxi.ATT_Y);
//			if(lx == tx && ly == ty)
//				return loc;
//		}
//		return "Not a depot";
//	}

	public String[] getLocations(){
		String[] ret = new String[locations.size()];
		int i = 0;
		for(String name : locations.keySet())
			ret[i++] = name;
		return ret;
	}

	
	public Object getTaxiAtt(String attName){
		return taxi.get(attName);
	}

	public String getTaxiName(){
		return taxi.name();
	}
	
	public Object getPassengerAtt(String passname, String attName){
		return passengers.get(passname).get(attName);
	}
	
	public Object getLocationAtt(String locName, String attName){
		return locations.get(locName).get(attName);
	}

		/** commenting out because it might be useful in the future
	//test to see if there is a wall on either side of the taxi
	public boolean wallNorth(){
		int tx = (int) taxi.get(Taxi.ATT_X);
		int ty = (int) taxi.get(Taxi.ATT_Y);
		for(RockSampleWall w : walls.values()){
			boolean ish = (boolean) w.get(Taxi.ATT_IS_HORIZONTAL);
			int wx = (int) w.get(Taxi.ATT_START_X);
			int wy = (int) w.get(Taxi.ATT_START_Y);
			int wlen = (int) w.get(Taxi.ATT_LENGTH);
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
		int tx = (int) taxi.get(Taxi.ATT_X);
		int ty = (int) taxi.get(Taxi.ATT_Y);
		for(RockSampleWall w : walls.values()){
			boolean ish = (boolean) w.get(Taxi.ATT_IS_HORIZONTAL);
			int wx = (int) w.get(Taxi.ATT_START_X);
			int wy = (int) w.get(Taxi.ATT_START_Y);
			int wlen = (int) w.get(Taxi.ATT_LENGTH);
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
		int tx = (int) taxi.get(Taxi.ATT_X);
		int ty = (int) taxi.get(Taxi.ATT_Y);
		for(RockSampleWall w : walls.values()){
			boolean ish = (boolean) w.get(Taxi.ATT_IS_HORIZONTAL);
			int wx = (int) w.get(Taxi.ATT_START_X);
			int wy = (int) w.get(Taxi.ATT_START_Y);
			int wlen = (int) w.get(Taxi.ATT_LENGTH);
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
		int tx = (int) taxi.get(Taxi.ATT_X);
		int ty = (int) taxi.get(Taxi.ATT_Y);
		for(RockSampleWall w : walls.values()){
			boolean ish = (boolean) w.get(Taxi.ATT_IS_HORIZONTAL);
			int wx = (int) w.get(Taxi.ATT_START_X);
			int wy = (int) w.get(Taxi.ATT_START_Y);
			int wlen = (int) w.get(Taxi.ATT_LENGTH);
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
	*/
	@Override
	public String toString(){
//		String out = "{\n";
//		out += taxi.toString() + "\n";
//
//		for(TaxiPassenger p : passengers.values()){
//			out += p.toString() + "\n";
//		}
//
//		for(TaxiLocation l : locations.values()){
//			out += l.toString() + "\n";
//		}
//		return out;
		return OOStateUtilities.ooStateToString(this);
	}
}

