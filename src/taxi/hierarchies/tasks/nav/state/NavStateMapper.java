package taxi.hierarchies.tasks.nav.state;

import java.util.ArrayList;
import java.util.List;

import burlap.mdp.auxiliary.StateMapping;
import burlap.mdp.core.state.State;
import taxi.Taxi;
import taxi.hierarchies.tasks.nav.TaxiNavDomain;
import taxi.state.RockSampleState;

public class NavStateMapper implements StateMapping {

	@Override
	public State mapState(State s) {
		RockSampleState st = (RockSampleState) s;
		
		int tx = (int) st.getTaxiAtt(TaxiNavDomain.ATT_X);
		int ty = (int) st.getTaxiAtt(TaxiNavDomain.ATT_Y);
		TaxiNavAgent taxi = new TaxiNavAgent(Taxi.CLASS_ROVER, tx, ty);
		
		List<TaxiNavLocation> locations = new ArrayList<>();
		for(String locName : st.getLocations()){
			int lx = (int) st.getLocationAtt(locName, TaxiNavDomain.ATT_X);
			int ly = (int) st.getLocationAtt(locName, TaxiNavDomain.ATT_Y);
			locations.add(new TaxiNavLocation(locName, lx, ly));
		}
		
		return new TaxiNavState(taxi, locations);
	}
}

