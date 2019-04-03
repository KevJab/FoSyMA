package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.behaviours.SimpleBehaviour;

public class SetGoalBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 902851789463823991L;
	
	// variables to identify which actual behaviour it is
	public static final int INIT = 0;
	public static final int SENDER = 1;
	public static final int RECEIVER = 2;
	
	private int goalType;
	private int behaviourType;
	private boolean reached = false;
	
	public SetGoalBehaviour(AbstractDedaleAgent agent, int goalType, int behaviourType) {
		super(agent);
		this.goalType = goalType;
		this.behaviourType = behaviourType;
	}

	@Override
	public void action() {
		switch (behaviourType) {
		case INIT:
			actionInit();
			break;
		case SENDER:
			actionSender();
			break;
		case RECEIVER:
			actionReceiver();
			break;
		default:
			break;
		}
	}

	/**
	 * The action taken by the agent at the start of the WalkToGoalBehaviour FSM
	 */
	private void actionInit() {
		Graphe myMap = ((MyAbstractAgent) this.myAgent).getMyMap();
		myMap.setNewGoal(goalType);
	}

	/**
	 * The action taken by the agent that sent a ping.</br> 
	 * He knows he has to change goals (if he didn't have to, he wouldn't have come to this Behaviour)
	 */
	private void actionSender() {
		Graphe myMap = ((MyAbstractAgent) this.myAgent).getMyMap();
		myMap.setNewGoal(goalType);
	}

	
	private void actionReceiver() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		Graphe myMap = myagent.getMyMap();
		
		//TODO try to find another way; if not, keep the same goal 
	}

	@Override
	public int onEnd() {
		if((behaviourType == INIT) && reached)
			return 1;
		return 2;
	}
	
	@Override
	public boolean done() {
		return true;
	}

}
