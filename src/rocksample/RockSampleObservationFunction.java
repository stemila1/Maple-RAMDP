package rocksample;

import burlap.debugtools.RandomFactory;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.observations.DiscreteObservationFunction;
import burlap.mdp.singleagent.pomdp.observations.ObservationFunction;
import burlap.mdp.singleagent.pomdp.observations.ObservationProbability;
import burlap.mdp.singleagent.pomdp.observations.ObservationUtilities;
import rocksample.state.RockSampleRock;
import rocksample.state.RockSampleState;
import rocksample.stateGenerator.RockSampleStateFactory;

//import burlap.mdp.singleagent.GroundedAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static rocksample.RockSamplePO.ACTION_CHECK;
import static rocksample.RockSamplePO.CLASS_ROCK;

/**
 * Created by steph on 11/9/2017.
 */
public class RockSampleObservationFunction implements DiscreteObservationFunction{

    protected double checkAccuracy;

    public void RockSampleState(){
    }

    public RockSampleObservationFunction(double checkAccuracy){
        this.checkAccuracy = checkAccuracy;
    }

    //@Override
    public boolean canEnumerateObservation(){
        return true;
    }



    public List<State> allObservations() {

        List<State> result = new ArrayList<State>(16);


        RockSampleStateFactory rs_statefactory = new RockSampleStateFactory();
        result.add(rs_statefactory.createCustomState("Good", "Good", "Good", "Good"));
        result.add(rs_statefactory.createCustomState("Bad", "Good", "Good", "Good"));
        result.add(rs_statefactory.createCustomState("Good", "Bad", "Good", "Good"));
        result.add(rs_statefactory.createCustomState("Good", "Good", "Bad", "Good"));
        result.add(rs_statefactory.createCustomState("Good", "Good", "Good", "Bad"));

        result.add(rs_statefactory.createCustomState("Bad", "Bad", "Good", "Good"));
        result.add(rs_statefactory.createCustomState("Bad", "Good", "Bad", "Good"));
        result.add(rs_statefactory.createCustomState("Bad", "Good", "Good", "Bad"));
        result.add(rs_statefactory.createCustomState("Good", "Bad", "Bad", "Good"));

        result.add(rs_statefactory.createCustomState("Good", "Bad", "Good", "Bad"));
        result.add(rs_statefactory.createCustomState("Good", "Good", "Bad", "Bad"));
        result.add(rs_statefactory.createCustomState("Bad", "Bad", "Bad", "Good"));
        result.add(rs_statefactory.createCustomState("Bad", "Good", "Bad", "Bad"));

        result.add(rs_statefactory.createCustomState("Bad", "Bad", "Good", "Bad"));
        result.add(rs_statefactory.createCustomState("Good", "Bad", "Bad", "Bad"));
        result.add(rs_statefactory.createCustomState("Bad", "Bad", "Bad", "Bad"));


        return result;
    }


    //@Override
    public State sampleObservation(State state, Action action_1){
        /* Need to include checks for all ACTION_CHECK */
        /* Pass in rock number to the ObservationRock(int rockNum) */

        System.out.println("ENTERED OBS");
        if (action_1.actionName().equals(ACTION_CHECK)){
            ObjectParameterizedAction action = (ObjectParameterizedAction) action_1;
            String n = action.getObjectParameters()[0];
            RockSampleState rs_state = (RockSampleState) state;
            int roverX = (int) rs_state.getRoverAtt(RockSample.ATT_X);
            int roverY = (int) rs_state.getRoverAtt(RockSample.ATT_Y);

            int rockX = (int) rs_state.getRockAtt(n, RockSample.ATT_X);
            int rockY = (int) rs_state.getRockAtt(n, RockSample.ATT_Y);

            String rockQual = (String) rs_state.getRockAtt(n, RockSample.ATT_QUALITY);

            /* Now we will do math, though you can move it into observation rock and pass the paramters */
            int dx = (roverX-rockX) * (roverX-rockX); //squaring
            int dy = (roverY-rockY) * (roverY-rockY); //square

            double distance = Math.sqrt(dx+ dy);
            double tunable_constant = 20;

            /* Now, we will apply that awkward function */
            double sensor_efficiency = Math.pow(2,-(distance)/tunable_constant);

            /* get random number, and if less than random number */
            //Random rand = new Random();
            double rand = RandomFactory.getMapped(0).nextDouble();
            if(sensor_efficiency > rand){
                if(rockQual.equals("Good")){
                    rockQual = "Bad";
                }
                else{
                    rockQual = "Good";
                }
            }
            this.checkAccuracy = sensor_efficiency;
            /* have applied the mask */

            /* this is stupid code, b/c I don't want to use a map */
            int indx;
            if(n.equals("Rock0")){
                indx = 0;
            }
            else if(n.equals("Rock1")){
                indx=1;
            }
            else if(n.equals("Rock2")){
                indx = 2;
            }
            else if(n.equals("Rock3")){
                indx=3;
            }
            else{

                System.out.println("ERROR FOOL");
                indx = 0;
            }
            return this.observationRock(indx, rockQual);

        }
        return null;
    }

    /*
    //@Override
    public State sampleObservation(State state, ObjectParameterizedAction action){
        // Need to include checks for all ACTION_CHECK
        // Pass in rock number to the ObservationRock(int rockNum)
        if (action.actionName().equals(ACTION_CHECK)){
            String n = action.getObjectParameters()[0];
            RockSampleState rs_state = (RockSampleState) state;
            int roverX = (int) rs_state.getRoverAtt(RockSample.ATT_X);
            int roverY = (int) rs_state.getRoverAtt(RockSample.ATT_Y);

            int rockX = (int) rs_state.getRockAtt(n, RockSample.ATT_X);
            int rockY = (int) rs_state.getRockAtt(n, RockSample.ATT_Y);

            String rockQual = (String) rs_state.getRockAtt(n, RockSample.ATT_QUALITY);

            // Now we will do math, though you can move it into observation rock and pass the paramters
            int dx = (roverX-rockX) * (roverX-rockX); //squaring
            int dy = (roverY-rockY) * (roverY-rockY); //square

            double distance = Math.sqrt(dx+ dy);
            double tunable_constant = 20;

            /* Now, we will apply that awkward function
            double sensor_efficiency = Math.pow(2,-(distance)/tunable_constant);

            // get random number, and if less than random number
            //Random rand = new Random();
            double rand = RandomFactory.getMapped(0).nextDouble();
            if(sensor_efficiency > rand){
                if(rockQual.equals("Good")){
                    rockQual = "Bad";
                }
                else{
                    rockQual = "Good";
                }
            }

            // have applied the mask


            return this.observationRock(rockQual);
        }
        return null;
    } */

    //@Override
    public double getObservationProbability(State observation, State state,
                                            ObjectParameterizedAction action){
        return this.checkAccuracy;
    }


    /*protected State observationRock(String rockQual){

        // Go into RockSampleDomain, get the rock value
        RockSampleState checkRock = new RockSampleState(1);
        //String qual = (String) checkRock.getRockAtt(rock, RockSample.ATT_QUALITY);

        // Now have to do the randomness mask


        return checkRock;
    } */

    protected State observationRock(int indx, String rockQual){

        /* Go into RockSampleDomain, get the rock value */
        RockSampleState checkRock = new RockSampleState(indx,rockQual);
        //String qual = (String) checkRock.getRockAtt(rock, RockSample.ATT_QUALITY);

        // Now have to do the randomness mask


        return checkRock;
    }


    @Override
    public State sample(State state, Action action){
        if (action.actionName().equals(ACTION_CHECK)){
            ObjectParameterizedAction action_op = (ObjectParameterizedAction) action;

            //   get the value of the rock being checked

        //   get a random number
        //   if r < the accuracy
        //     return the true observation of the rock
        //   else return the opposite

        String n = action_op.getObjectParameters()[0];
        RockSampleState rs_state = (RockSampleState) state;
        int roverX = (int) rs_state.getRoverAtt(RockSample.ATT_X);
        int roverY = (int) rs_state.getRoverAtt(RockSample.ATT_Y);

        int rockX = (int) rs_state.getRockAtt(n, RockSample.ATT_X);
        int rockY = (int) rs_state.getRockAtt(n, RockSample.ATT_Y);

        String rockQual = (String) rs_state.getRockAtt(n, RockSample.ATT_QUALITY);

            /* Now we will do math, though you can move it into observation rock and pass the paramters */
        int dx = (roverX-rockX) * (roverX-rockX); //squaring
        int dy = (roverY-rockY) * (roverY-rockY); //square

        double distance = Math.sqrt(dx+ dy);
        double tunable_constant = 20;

            /* Now, we will apply that awkward function */
        double sensor_efficiency = Math.pow(2,-(distance)/tunable_constant);

            /* get random number, and if less than random number */
        //Random rand = new Random();
        double rand = RandomFactory.getMapped(0).nextDouble();
        if(sensor_efficiency > rand){
            if(rockQual.equals("Good")){
                rockQual = "Bad";
            }
            else{
                rockQual = "Good";
            }
        }

            /* have applied the mask */

            /* this is stupid code, b/c I don't want to use a map */
        int indx;
        if(n.equals("Rock0")){
            rs_state.set("Rock0",rockQual);
            indx = 0;
        }
        else if(n.equals("Rock1")){
            rs_state.set("Rock1",rockQual);
            indx=1;
        }
        else if(n.equals("Rock2")){
            rs_state.set("Rock2",rockQual);
            indx = 2;
        }
        else if(n.equals("Rock3")){
            rs_state.set("Rock3",rockQual);
            indx=3;
        }
        else{

            System.out.println("ERROR FOOL");
            indx = 0;
        }
        return rs_state;

    }
    return state;
    //throw new RuntimeException("Unknown action " + action.actionName() + "; cannot return observation sample.");

}




    public List<ObservationProbability> probabilities(State state, Action action) {
        return ObservationUtilities.probabilitiesByEnumeration((DiscreteObservationFunction) this, state, action);
    }

    @Override
    public double probability(State observation, State state, Action action){
        String oVal = (String)observation.get(RockSamplePO.ACTION_CHECK);
        String rockVal = (String)state.get(RockSample.CLASS_ROCK);


        // if action name is check
        // then if the value of the rock is good/bad
        //  return the accuracy
        // otherwise return 0

        if(action.actionName().equals(RockSamplePO.ACTION_CHECK)) {
            return this.checkAccuracy;
        }
        else{
            return 1.0;
        }
        /*throw new RuntimeException("Unknown action " +
                                   action.actionName() +
                                   "; cannot return observation probability."); */

    }

}
