package rocksample;

import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.singleagent.pomdp.PODomain;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;

import java.util.List;

/**
 * Created by steph on 11/9/2017.
 * Basically reimplement OOSADomain but using things that
 * are partially observable -- taking place of OOSA Domain
 */
public class POOODomain extends PODomain implements OODomain
{

    @Override
    public List<Class<?>> stateClasses() {
        return null;
    }

    @Override
    public Class<?> stateClass(String s) {
        return null;
    }

    @Override
    public POOODomain addStateClass(String s, Class<?> aClass) {
        return null;
    }

    @Override
    public List<PropositionalFunction> propFunctions() {
        return null;
    }

    @Override
    public PropositionalFunction propFunction(String s) {
        return null;
    }

    @Override
    public POOODomain addPropFunction(PropositionalFunction propositionalFunction) {
        return null;
    }


    public ObservationFunction getObserationFunction(){
        return this.obsevationFunction ;
    }

    @Override
    public void setObservationFunction(ObservationFunction observationFunction){
       this.obsevationFunction = observationFunction;
    }


}
