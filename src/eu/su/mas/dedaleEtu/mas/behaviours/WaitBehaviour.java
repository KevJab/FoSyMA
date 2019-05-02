package eu.su.mas.dedaleEtu.mas.behaviours;


import java.util.HashSet;
import java.util.Set;

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
	public static final int ECHO = 4;
	
	private int type;

	public WaitBehaviour(Agent a, int type) {
		//TODO maybe 2 seconds of wait is too much
		super(a, 1000);
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
			
			msg.setContent(myagent.doYouMove() + "," + myagent.getMyMap().getGoal() + ","+ myagent.getMyMap().getMyPos());
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
		case PING: case ECHO:
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
		MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(performative);
		//final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(performative), MessageTemplate.not(MessageTemplate.MatchSender(this.myAgent.getAID())));
		if (type == ECHO)
			msgTemplate = MessageTemplate.and(msgTemplate, MessageTemplate.MatchOntology("echo"));
		
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {	
			endVal = 2;	//[type] message received
			((MyAbstractAgent) this.myAgent).setInterlocuteur(msg.getSender());
			System.out.println(msg.getSender().getLocalName()+" replied to " + this.myAgent.getLocalName());
			
			String[] pingresponse_info;
			switch (type) {
			case PING:
				pingresponse_info = msg.getContent().split(",");	// message should be "wish_node,cur_node"
				myagent.setOtherInfo(pingresponse_info[0], pingresponse_info[1]);
				break;
			case PINGRESPONSE:
				pingresponse_info = msg.getContent().split(","); 	// message received should be "need_to_update,wish_node,cur_node"
				if (Boolean.parseBoolean(pingresponse_info[0])) { 	// I need to update (if I don't, endVal is already at 2; check about 15 lines above)
					endVal = 3;
					myagent.setOtherInfo(pingresponse_info[1], pingresponse_info[2]);
				}
				break;
			case SEND:
				Graphe g = null;
				try {
					g = (Graphe) msg.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				myagent.getMyMap().merge(g);
				System.out.println(this.myAgent.getLocalName()+"<---- Graphe received from "+msg.getSender().getLocalName());
				break;
			case ECHO:
				String[] agents_AIDs = msg.getInReplyTo().split(" ");
				
				Set<String> other_knowledge = new HashSet<>();
				for (String aid : agents_AIDs)
					other_knowledge.add(aid);
				
				myagent.mergeKnowledge(other_knowledge);
				
				endVal = (myagent.isCommonKnowledge()) ? 2 : 1;		
				break;
			default:
				break;
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
			case ECHO:
				wait += "Echo";
			default:
				wait = "";
				break;
			}
			System.out.println(this.myAgent.getLocalName()+ " is sad, nobody wants to talk with them; exiting "+ wait + " state");
			endVal = 1;	//waited too long; 
		}
	}
	
	@Override
	public int onEnd() {
		reset();
		return endVal;
	}

}
