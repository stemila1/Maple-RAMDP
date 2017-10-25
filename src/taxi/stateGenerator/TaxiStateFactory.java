
package taxi.stateGenerator;

import java.util.ArrayList;
import java.util.List;

import taxi.Taxi;
import taxi.state.RockSampleState;
import taxi.state.RoverAgent;
import taxi.state.TaxiLocation;
import taxi.state.TaxiPassenger;

public class TaxiStateFactory {
	//generates taxi states
	
	public static RockSampleState createClassicState(){
		RoverAgent taxi = new RoverAgent(Taxi.CLASS_ROVER + 0, 0, 3);
		
		List<TaxiLocation> locations = new ArrayList<TaxiLocation>();
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 0, 0, 4, Taxi.COLOR_RED));
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 1, 0, 0, Taxi.COLOR_YELLOW));
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 2, 3, 0, Taxi.COLOR_BLUE));
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 3, 4, 4, Taxi.COLOR_GREEN));
		
		List<TaxiPassenger> passengers = new ArrayList<TaxiPassenger>();
		passengers.add(new TaxiPassenger(Taxi.CLASS_PASSENGER + 0, 3, 0, Taxi.CLASS_LOCATION + 0));
//		passengers.add(new TaxiPassenger(Taxi.CLASS_PASSENGER + 1, 0, 0, Taxi.CLASS_LOCATION + 3));

		return new RockSampleState(taxi, passengers, locations);
	}
	
	public static RockSampleState createSmallState(){
		RoverAgent taxi = new RoverAgent(Taxi.CLASS_ROVER + 0, 0, 4);
		
		List<TaxiLocation> locations = new ArrayList<TaxiLocation>();
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 0, 0, 2, Taxi.COLOR_RED));
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 1, 0, 0, Taxi.COLOR_YELLOW));
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 2, 0, 1, Taxi.COLOR_BLUE));
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 3, 0, 3, Taxi.COLOR_GREEN));
	
		List<TaxiPassenger> passengers = new ArrayList<TaxiPassenger>();
		passengers.add(new TaxiPassenger(Taxi.CLASS_PASSENGER + 0, 0, 1, Taxi.CLASS_LOCATION + 0));

			
		return new RockSampleState(taxi, passengers, locations);
	}
	
	public static RockSampleState createTinyState(){
		RoverAgent taxi = new RoverAgent(Taxi.CLASS_ROVER + 0, 0, 1);
		
		List<TaxiLocation> locations = new ArrayList<TaxiLocation>();
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 0, 0, 0, Taxi.COLOR_RED));
		locations.add(new TaxiLocation(Taxi.CLASS_LOCATION + 1, 0, 1, Taxi.COLOR_BLUE));
		
		List<TaxiPassenger> passenger = new ArrayList<TaxiPassenger>();
		passenger.add(new TaxiPassenger(Taxi.CLASS_PASSENGER + 0, 0, 0, Taxi.CLASS_LOCATION + 1));

		
		return new RockSampleState(taxi, passenger, locations);
	}
}
