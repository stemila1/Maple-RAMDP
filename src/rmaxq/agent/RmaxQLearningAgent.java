package rmaxq.agent;

import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.debugtools.RandomFactory;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.statehashing.HashableState;
import burlap.statehashing.HashableStateFactory;
import hierarchy.framework.GroundedTask;
import hierarchy.framework.Task;

import java.text.SimpleDateFormat;
import java.util.*;

public class RmaxQLearningAgent implements LearningAgent {

	//n(s,a)
	private HashMap<GroundedTask, HashMap< HashableState, Integer>> stateActionCountsByTask;

	//n(s,a,s')
	private HashMap<GroundedTask, HashMap< HashableState, HashMap<HashableState, Integer>>> totalTransitionsByTask;

	//r(s,a)
	private HashMap<GroundedTask, HashMap< HashableState, Double>> totalRewardsByTask;

	private HashMap<GroundedTask, HashMap< HashableState, HashMap< HashableState, Double>>> storedTransitionsByTask;

	private HashMap<GroundedTask, HashMap< HashableState, Double>> storedValueByTask;

    private List<HashableState> reachableStates = new ArrayList<>();

	private double maxDeltaInPolicy;
	private double maxDeltaInModel;
	private int threshold;
	private Task root;
	private GroundedTask rootSolve;
	private HashableStateFactory hashingFactory;
	private double Vmax;
	private Environment env;
	private State initialState;
	private long time = 0;
	private int numSteps = 0;

	private Stack<GroundedTask> taskStack;

	public RmaxQLearningAgent(Task root, HashableStateFactory hs, State initState, double vmax, int threshold, double maxDeltaInPolicy, double maxDeltaInModel){
		this.root = root;
		this.hashingFactory = hs;
		this.initialState = initState;
		this.Vmax = vmax;
		this.threshold = threshold;
		this.maxDeltaInPolicy = maxDeltaInPolicy;
		this.maxDeltaInModel = maxDeltaInModel;
		this.taskStack = new Stack<>();
        reachableStates = StateReachability.getReachableStates(initialState, root.getDomain(), hashingFactory);
        stateActionCountsByTask = new HashMap<>();
        totalTransitionsByTask = new HashMap<>();
        totalRewardsByTask = new HashMap<>();
        storedTransitionsByTask = new HashMap<>();
        totalRewardsByTask = new HashMap<>();
        storedValueByTask = new HashMap<>();
        storedTransitionsByTask = new HashMap<>();

	}

	public Episode runLearningEpisode(Environment env) {
		return runLearningEpisode(env, -1);
	}

	public Episode runLearningEpisode(Environment env, int maxSteps) {
		this.env = env;
		this.numSteps = 0;
		Episode e = new Episode(initialState);
		rootSolve = root.getAllGroundedTasks(env.currentObservation()).get(0);

		time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
		Date resultdate = new Date(time);
		System.out.println(sdf.format(resultdate));
		HashableState hs = hashingFactory.hashState(env.currentObservation());
		taskStack.push(rootSolve);
		e = rmaxq(hs, e, maxSteps);
		time = System.currentTimeMillis() - time;

		return e;
	}

    private Episode rmaxq(HashableState hs, Episode e, int maxSteps) {

		while (!isTerminal(rootSolve, hs)) {
			GroundedTask task = rmaxqChoose(hs);
			Action action = task.getAction();
			EnvironmentOutcome outcome = env.executeAction(action);
			e.transition(outcome);
			State sPrime = outcome.op;
			HashableState hsPrime = hashingFactory.hashState(sPrime);

			double reward = outcome.r;
            //r(s,a) += r
            updateTotalReward(task, hs, reward);

            //n(s,a) ++
            incrementStateActionCount(task, hs);

            //n(s,a,s')++
            incrementTotalTransitionCount(task, hs, hsPrime);


			hs = hsPrime;
			numSteps++;
		}

		return e;
	}

    private GroundedTask rmaxqChoose(HashableState hs) {

		GroundedTask task = taskStack.peek();
		while (isTerminal(task, hs)) {
			taskStack.pop();
			task = taskStack.peek();
		}
		while (!task.isPrimitive()) {
			rmaxqPlan(task, hs);
			GroundedTask childTask = pi(task, hs);
			task = childTask;
			taskStack.push(task);
		}
		// returns a primtive action to execute
		return task;
	}

	private GroundedTask pi(GroundedTask task, HashableState hs) {
	    List<GroundedTask> childTasks = task.getGroundedChildTasks(hs.s());
        int randomIndex = RandomFactory.getMapped(0).nextInt(childTasks.size());
	    GroundedTask childTask = childTasks.get(randomIndex);
	    return childTask;
    }

    private void rmaxqPlan(GroundedTask task, HashableState hs) {
		List<GroundedTask> childTasks = task.getGroundedChildTasks(hs.s());
		for (GroundedTask childTask : childTasks) {
			rmaxqEvaluate(childTask, hs);
		}

		// this is for updating the mdp / model of composite (non-primitive) tasks

		// update rewards
        HashMap<HashableState, Double> rewardFunction = totalRewardsByTask.get(task);
		if (rewardFunction == null) {
            rewardFunction = new HashMap<>();
        }
        rewardFunction.put(hs, storedValueByTask.get(task).get(hs));

		// update transition probs
        HashMap<HashableState, HashMap<HashableState, Double>> transitionFunction = storedTransitionsByTask.get(task);
        if (transitionFunction == null) {
            transitionFunction = new HashMap<>();
        }
        HashMap<HashableState, Double> transition = transitionFunction.get(hs);
        if (transition == null) {
            transition = new HashMap<>();
        }
        for (HashableState hsPrime : terminalStatesByTask.get(task)) {
            transition.put(hsPrime, storedTerminalByTask);
        }
        transitionFunction.put(hs, transition);
        storedTransitionsByTask.put(task, transitionFunction);

		// update temporary goal rewards
		// update temporary task termination
		// do value iteration
		// update q
		// compute policy pi
	}

    private void rmaxqEvaluate(GroundedTask task, HashableState hs) {
		if (task.isPrimitive()) {
			computeModelPrimitive(task, hs);
		} else {
			rmaxqPlan(task, hs);
			// update V

			// update Omega
		}
	}

    private void computeModelPrimitive(GroundedTask task, HashableState hs) {

		int stateActionCount = n(task, hs);
		double newValue;
		if (stateActionCount < threshold) {
			newValue = Vmax;
		} else {
			newValue = getRmaxR(task, hs);
		}
		storeValue(task, hs, newValue);
		for (HashableState hsx : reachableStates) {
			double newOmega;
			if (stateActionCount < threshold) {
				newOmega = 0.0;
			} else {
				newOmega = getRmaxP(task, hs, hsx);
			}
			storeOmega(task, hs, hsx, newOmega);
		}
	}

	private double getRmaxR(GroundedTask task, HashableState hs) {
	    return totalRewardsByTask.get(task).get(hs) / (1.0 * stateActionCountsByTask.get(task).get(hs));
    }

    private double getRmaxP(GroundedTask task, HashableState hs, HashableState hsPrime) {
        Integer totalTransitions = totalTransitionsByTask.get(task).get(hs).get(hsPrime);
        if (totalTransitions == null) {
            totalTransitions = 0;
        }
	    return  totalTransitions / (1.0 * stateActionCountsByTask.get(task).get(hs));
    }

	private void storeValue(GroundedTask task, HashableState hs, double newValue) {
        HashMap<HashableState, Double> storedValues = storedValueByTask.get(task);
		if (storedValues == null) {
		    storedValues = new HashMap<>();
		    storedValueByTask.put(task, storedValues);
        }
        storedValues.put(hs, newValue);
	}

	private void storeOmega(GroundedTask task, HashableState hs, HashableState hsx, double newProbability) {
		storeTransitionProbability(task, hs, hsx, newProbability);
	}

	private void storeTransitionProbability(GroundedTask task, HashableState hs, HashableState hsPrime, double transitionProbability) {
		HashMap<HashableState,HashMap<HashableState, Double>> storedTransitions = storedTransitionsByTask.get(task);
		if (storedTransitions == null) {
			storedTransitions = new HashMap<>();
			storedTransitionsByTask.put(task, storedTransitions);
		}
		HashMap<HashableState, Double> transitionsFromState = storedTransitions.get(hs);
		if (transitionsFromState == null) {
			transitionsFromState = new HashMap<>();
			storedTransitions.put(hs, transitionsFromState);
		}
		transitionsFromState.put(hsPrime, transitionProbability);
	}


	private int n(GroundedTask task, HashableState hs) {
		HashMap<HashableState, Integer> stateCounts = stateActionCountsByTask.get(task);
		if (stateCounts == null) {
			stateCounts = new HashMap<>();
			stateActionCountsByTask.put(task, stateCounts);
		}
		Integer count = stateCounts.get(hs);
		if (count == null) {
			count = 0;
			stateCounts.put(hs, count);
		}
		return count;
	}

    private void updateTotalReward(GroundedTask task, HashableState hs, double reward) {
        if (reward == 0.0) {
            // skip zero rewards, don't need to store or update
            return;
        }
        HashMap<HashableState, Double> taskTotalRewards = totalRewardsByTask.get(task);
        if (taskTotalRewards == null) {
            taskTotalRewards = new HashMap<>();
            totalRewardsByTask.put(task, taskTotalRewards);
        }
        Double totalReward = taskTotalRewards.get(hs);
        if (totalReward == null) {
            totalReward = 0.0;
        }
        totalReward = totalReward + reward;
        taskTotalRewards.put(hs, totalReward);
        totalRewardsByTask.put(task, taskTotalRewards);
    }

    private void incrementStateActionCount(GroundedTask task, HashableState hs) {
        int count = n(task, hs);
        count = count + 1;
        stateActionCountsByTask.get(task).put(hs, count);
    }

    private void incrementTotalTransitionCount(GroundedTask task, HashableState hs, HashableState hsPrime) {
        HashMap<HashableState, HashMap<HashableState, Integer>> totalTransitions = totalTransitionsByTask.get(task);
        if (totalTransitions == null) {
            totalTransitions = new HashMap<>();
            totalTransitionsByTask.put(task, totalTransitions);
        }
        HashMap<HashableState, Integer> transitionsFromState = totalTransitions.get(hs);
        if (transitionsFromState == null) {
            transitionsFromState = new HashMap<>();
            totalTransitions.put(hs, transitionsFromState);
        }
        Integer transitionCount = transitionsFromState.get(hsPrime);
        if (transitionCount == null) {
            transitionCount = 0;
        }
        transitionCount = transitionCount + 1;
        transitionsFromState.put(hsPrime, transitionCount);
        totalTransitions.put(hs, transitionsFromState);
        totalTransitionsByTask.put(task, totalTransitions);
    }

	private HashMap<GroundedTask, HashMap<HashableState, Boolean>> cachedTerminalCheck = new HashMap<>();
	private boolean isTerminal(GroundedTask task, HashableState hs) {
		HashMap<HashableState, Boolean> taskTerminalCheck = cachedTerminalCheck.get(task);
		if (taskTerminalCheck == null) { taskTerminalCheck = new HashMap<>(); cachedTerminalCheck.put(task, taskTerminalCheck); }
		Boolean terminal = taskTerminalCheck.get(hs);
		if (terminal == null) {
			State s = hs.s();
			terminal = task.isComplete(s) || task.isFailure(s) || rootSolve.isComplete(s);
			taskTerminalCheck.put(hs, terminal);
		}
		return terminal;
	}

	public long getTime() {
		return time;
	}
}