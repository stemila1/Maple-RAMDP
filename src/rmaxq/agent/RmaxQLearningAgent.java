package rmaxq.agent;

import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.debugtools.RandomFactory;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
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
		e = rmaxq(hs, e, maxSteps);
		time = System.currentTimeMillis() - time;

		return e;
	}

	public Episode rmaxq(HashableState hs, Episode e, int maxSteps) {

		while (!isTerminal(rootSolve, hs)) {
			GroundedTask task = rmaxqChoose(hs);
			Action action = task.getAction();
			EnvironmentOutcome outcome = env.executeAction(action);
			e.transition(outcome);
			State sPrime = outcome.op;
			HashableState hsPrime = hashingFactory.hashState(sPrime);
			numSteps++;
		}

		return e;
	}

	public GroundedTask rmaxqChoose(HashableState hs) {

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

	public void rmaxqPlan(GroundedTask task, HashableState hs) {
		List<GroundedTask> childTasks = task.getGroundedChildTasks(hs.s());
		for (GroundedTask childTask : childTasks) {
			rmaxqEvaluate(childTask, hs);
		}
		// update rewards
		// update transition probs
		// update temporary goal rewards
		// update temporary task termination
		// do value iteration
		// update q
		// compute policy pi
	}

	public void rmaxqEvaluate(GroundedTask task, HashableState hs) {
		if (task.isPrimitive()) {
			// update V with rmax
			double newValue;
			double newOmega;
			if (n(task, hs) < threshold) {
				newValue = Vmax;
				newOmega = 0.0;
			} else {
				newValue = R(task, hs);
				newOmega = P(task, hs, hsPrime);
			}
			storeValue(task, hs, newValue);
			// update Omega with rmax
		} else {
			rmaxqPlan(task, hs);
			// update V
			// update Omega
		}
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