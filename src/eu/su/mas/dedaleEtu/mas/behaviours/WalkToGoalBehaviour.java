package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
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
	
	/**
	 * A boolean indicating whether the goal has been reached or not. Is used as the value to end the behaviour
	 */	
	public WalkToGoalBehaviour(final MyAbstractAgent myagent, int type) {
		super(myagent);
		
		this.registerFirstState(new WalkBehaviour(myagent, type), "Walk"); 	//onEnd() -> 1 if goal reached, 2 otherwise;
		this.registerState(new WaitBehaviour(myagent, WaitBehaviour.PING), "PingWait");
		this.registerState(new SayHello(myagent), "PingSend");					
		this.registerState(new WaitBehaviour(myagent, WaitBehaviour.PINGRESPONSE), "PingResponseWait");				//onEnd() -> 1 if nobody in range, 2 otherwise
		this.registerState(new SendMessageBehaviour(myagent), "Send");
		this.registerState(new ReceivedMessageBehaviour(myagent), "Receive");
		this.registerState(new WaitBehaviour(myagent, WaitBehaviour.SEND), "SendWait");
		this.registerState(new SendMessageBehaviour(myagent), "Send2");
		this.registerLastState(new OneShotBehaviour() {public void action() {}}, "End");	// does nothing, aside terminating the FSM
		
		this.registerTransition("Walk", "End", 1);
		this.registerTransition("Walk", "PingWait", 2);
		
		this.registerTransition("PingWait", "PingSend", 1);
		this.registerTransition("PingWait", "SendWait", 2);
		this.registerTransition("PingWait", "Walk", 3);
		
		this.registerTransition("PingResponseWait", "Walk", 1);
		this.registerTransition("PingResponseWait", "Send", 2);
		// no need to add a state with return value 3 since non-explorers will never get to the PingResponseWait state
		
		this.registerTransition("SendWait", "PingSend", 1);
		this.registerTransition("SendWait", "Send2", 2);
		this.registerTransition("SendWait", "Walk", 3);
		
		this.registerDefaultTransition("PingSend", "PingResponseWait");
		this.registerDefaultTransition("Send", "Receive");
		this.registerDefaultTransition("Receive", "Walk");
		this.registerDefaultTransition("Send2", "Walk");
	}
	

	

}
