package rocksample;

import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steph on 11/9/2017.
 * Basically reimplement OOSADomain but using things that
 * are partially observable -- taking place of OOSA Domain
 */

public class POOODomain extends PODomain implements OODomain
{
    protected Map<String, Class<?>> stateClassesMap = new HashMap<String, Class<?>>();

    protected Map<String, PropositionalFunction> propFunctionMap = new HashMap<String, PropositionalFunction>();

    @Override
    public List<Class<?>> stateClasses(){
        return new ArrayList<Class<?>>(stateClassesMap.values());
    }

    @Override
    public Class<?> stateClass(String className) {
        return stateClassesMap.get(className);
    }

    @Override
    public POOODomain addStateClass(String className, Class<?> stateClass) {
        this.stateClassesMap.put(className, stateClass);
        return this;
    }

    @Override
    public List<PropositionalFunction> propFunctions() {
        return new ArrayList<PropositionalFunction>(this.propFunctionMap.values());
    }

    @Override
    public PropositionalFunction propFunction(String name) {
        return this.propFunctionMap.get(name);
    }

    @Override
    public POOODomain addPropFunction(PropositionalFunction prop) {
        this.propFunctionMap.put(prop.getName(), prop);
        return this;
    }


    public ObservationFunction getObserationFunction(){
        return this.obsevationFunction ;
    }

    @Override
    public void setObservationFunction(ObservationFunction observationFunction){
       this.obsevationFunction = observationFunction;
    }

}
