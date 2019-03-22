package eu.su.mas.dedaleEtu.mas.behaviours;


import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitBehaviour extends WakerBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7427316588458684865L;
	private int endVal;
	
	public static final int PING = 1;
	public static final int PINGRESPONSE = 2;
	public static final int SEND = 3;
	
	private int type;

	public WaitBehaviour(Agent a, int type) {
		//TODO maybe 2 seconds of wait is too much
		super(a, 2000);
		this.type = type;
	}
	
	/**
	 * if this is WaitSend, sends a PingResponse (performative ACLMessage.AGREE), else does nothing
	 */
	@Override
	public void onStart() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		if(type == SEND) {
			//1°Create the message
			final ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
			msg.setSender(myagent.getAID());
			msg.addReceiver(new AID(myagent.getInterlocuteur().getLocalName(), AID.ISLOCALNAME));  
			
			msg.setContent("PingResponse");
			myagent.send(msg);
		}
	}
	
	/**
	 * Waits for 2 seconds, and checks if the agent received a message. </br>
	 * <code>endVal</code>'s value will be: <ul><li> 2 if message received </li>
	 * 											<li> 1 if waited too long</li></ul>
	 * 
	 */
	@Override
	public void onWake() {
		int performative;
		switch(type) {
		case PING:
			performative = ACLMessage.REQUEST;
			break;
		case PINGRESPONSE:
			performative = ACLMessage.AGREE;
			break;
		case SEND:
			performative = ACLMessage.INFORM;
			break;
		default:
			performative = -1;
			break;
		}
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(performative);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {	
			endVal = 2;	//FSM goes to Send
			((MyAbstractAgent) this.myAgent).setInterlocuteur(msg.getSender());
		}else{
			endVal = 1;	//waited too long
		}
	}
	
	@Override
	public int onEnd() {
		return endVal;
	}

}
