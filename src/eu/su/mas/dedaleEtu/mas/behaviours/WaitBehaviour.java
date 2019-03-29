package eu.su.mas.dedaleEtu.mas.behaviours;


import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

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
			myagent.sendMessage(msg);
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
		// Template: match corresponding performative and I'm not the sender (I don't want to read my own messages)
		//TODO test simple matchperformative
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(performative), MessageTemplate.not(MessageTemplate.MatchSender(this.myAgent.getAID())));
		
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {	
			endVal = 2;	//FSM goes to Send
			((MyAbstractAgent) this.myAgent).setInterlocuteur(msg.getSender());
			System.out.println(msg.getSender().getLocalName()+" replied to " + this.myAgent.getLocalName());
			

			if(type == SEND) {
				Graphe g = null;
				try {
					g = (Graphe) msg.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				myagent.getMyMap().merge(g);
				System.out.println(this.myAgent.getLocalName()+"<---- Graphe received from "+msg.getSender().getLocalName());
			}
		}else{
			String wait = "Wait";
			switch(type) {
			case PING:
				wait = "Ping" + wait;
				break;
			case PINGRESPONSE:
				wait = "PingResponse" + wait;
				break;
			case SEND:
				wait = "Send" + wait;
				break;
			default:
				wait = "";
				break;
			}
			System.out.println(this.myAgent.getLocalName()+ " is sad, nobody wants to talk with them; exiting "+ wait + " state");
			endVal = (myagent.getType().equals("EXPLORER")) ? 1 : 3;	//waited too long; 
		}
	}
	
	@Override
	public int onEnd() {
		reset();
		return endVal;
	}

}
