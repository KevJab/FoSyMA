package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class WalkToGoalBehaviour extends FSMBehaviour {
	private static final long serialVersionUID = 7361851680230247600L;
	
	// All potential values for type
	public static final int OPEN = 0;
	public static final int TREASURE = 1;
	public static final int GOLD = 2;
	public static final int DIAMOND = 3;
	public static final int SILO = 4;
	
	private int myType;
	
	/**
	 * A boolean indicating whether the goal has been reached or not. Is used as the value to end the behaviour
	 */	
	public WalkToGoalBehaviour(final MyAbstractAgent myagent, int type) {
		super(myagent);
		myType = type;
		
		/*------------
		 * Behaviours
		 *------------*/
		
		// all SetGoalBehaviours
		this.registerFirstState(new SetGoalBehaviour(myagent, type, SetGoalBehaviour.INIT), "SetGoal"); 	//onEnd() -> 1 if goal reached, 2 otherwise;
		this.registerState(new SetGoalBehaviour(myagent, type, SetGoalBehaviour.RECEIVER), "ResetGoalR"); 	// the ping receiver (R) checks if he needs to update his next step
		this.registerState(new SetGoalBehaviour(myagent, type, SetGoalBehaviour.SENDER), "ResetGoalS"); 	// the ping sender (S) needs to update his next step
		
		// all WaitBehaviours (onEnd = 1 if waited too long, 2 otherwise)
		this.registerState(new WaitBehaviour(myagent, WaitBehaviour.PING), "PingWait");						// the agent checks if someone is nearby and talking
		this.registerState(new WaitBehaviour(myagent, WaitBehaviour.PINGRESPONSE), "PingResponseWait");		// the agent said hello and checks if someone nearby replies
		this.registerState(new WaitBehaviour(myagent, WaitBehaviour.SEND), "SendWait");						// the agent heard "hello" and replied; waits for the other guy to send his map
		
		// all map-exchanging behaviours (receiving/sending)
		this.registerState(new SendMessageBehaviour(myagent), "Send");			// the initial talker sends his map
		this.registerState(new ReceivedMessageBehaviour(myagent), "Receive");	// the initial talker receives his map
		this.registerState(new SendMessageBehaviour(myagent), "Send2");			// the respondent sends his map
		
		// walking one step closer to the goal
		this.registerState(new WalkBehaviour(myagent, type), "Walk"); 	
		
		// pretty self explanatory; says hello, sending (wish_node, cur_node)
		this.registerState(new SayHelloBehaviour(myagent, SayHelloBehaviour.PING), "PingSend");					
		
		// this behaviour does nothing else other than terminate the FSM
		this.registerLastState(new OneShotBehaviour() {public void action() {}}, "End");
		
		
		/*-------------
		 * Transitions
		 *-------------*/
		
		this.registerTransition("SetGoal", "End", 1);
		this.registerTransition("SetGoal", "PingWait", 2);
		
		this.registerTransition("PingWait", "PingSend", 1);
		this.registerTransition("PingWait", "ResetGoalR", 2);
		
		this.registerTransition("PingResponseWait", "Walk", 1);
		this.registerTransition("PingResponseWait", "Send", 2);
		this.registerTransition("PingResponseWait", "ResetGoalS", 3);
		
		this.registerTransition("SendWait", "PingSend", 1);
		this.registerTransition("SendWait", "Send2", 2);
		
		this.registerTransition("Walk", "SetGoal", 1);
		this.registerTransition("Walk", "PingWait", 2);
		
		this.registerDefaultTransition("Receive", "Walk");
		this.registerDefaultTransition("Send2", "Walk");
		this.registerDefaultTransition("PingSend", "PingResponseWait");
		this.registerDefaultTransition("Send", "Receive");
		this.registerDefaultTransition("ResetGoalR", "SendWait");
		this.registerDefaultTransition("ResetGoalS", "Send");
		
	}
	
	@Override
	public int onEnd() {
		Graphe myMap = ((MyAbstractAgent)this.myAgent).getMyMap();
		reset();
		switch (myType) {
		case OPEN:
			if(myMap.isComplete())
				return 2;
			return 1;
		case TREASURE:
			if(myMap.noMoreTreasure(TREASURE))
				return 2;
			return 1;
		case GOLD:case DIAMOND:
			if(myMap.noMoreTreasure(myType))
				return 2;
			return 1;
		default:
			return 1;
		}
	}
	

}
