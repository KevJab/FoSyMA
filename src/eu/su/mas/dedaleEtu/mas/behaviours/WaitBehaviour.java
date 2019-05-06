package eu.su.mas.dedaleEtu.mas.behaviours;


import java.util.HashSet;
import java.util.Set;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
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
	public static final int WIN = 5;
	public static final int REQUESTREPLY = 6;
	
	private int type;

	public WaitBehaviour(Agent a, int type) {
		//TODO vary time
		super(a, 500);
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
		} else if (type == REQUESTREPLY) {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription () ;
			sd.setType( "ANY" ); // type of the agent
			dfd.addServices(sd) ;
			DFAgentDescription[] result;
			try {
				result = DFService.search( myagent , dfd);
				//You get the list of all the agents (AID) of said type
				
				//A message is defined by : a performative, a sender, a set of receivers, (a protocol),(a content (and/or contentOBject))
				ACLMessage msg=new ACLMessage(ACLMessage.REQUEST);
				msg.setSender(myagent.getAID());
				
				// I'm not sending a message to myself
				for (DFAgentDescription dfad : result){
					if(!dfad.getName().equals(myagent.getAID()))
						msg.addReceiver(dfad.getName());
				}
				
				//Mandatory to use this method (it takes into account the environment to decide if someone is reachable or not)
				((AbstractDedaleAgent)myagent).sendMessage(msg);
				
			} catch (FIPAException e) {
				e.printStackTrace();
			}
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
		case WIN:
			performative = ACLMessage.CONFIRM;
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
			myagent.setInterlocuteur(msg.getSender());
			System.out.println(msg.getSender().getLocalName()+" replied to " + this.myAgent.getLocalName());
			
			String[] pingresponse_info;
			switch (type) {
			case PING:
				if ("echo".equals(msg.getOntology())) {
					// getting the full map for myself
					Graphe g = null;
					try {
						g = (Graphe) msg.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					myagent.getMyMap().merge(g);
					
					// getting his knowledge
					Set<String> other_knowledge = new HashSet<>(); 
					for(String s : msg.getInReplyTo().split(" ")) {
						other_knowledge.add(s);
					}
					myagent.mergeKnowledge(other_knowledge);
					
					endVal = 3; 
				} else {
					pingresponse_info = msg.getContent().split(",");	// message should be "wish_node,cur_node"
					myagent.setOtherInfo(pingresponse_info[0], pingresponse_info[1]);
				}
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
				// getting the full map for myself
				Graphe gr = null;
				try {
					gr = (Graphe) msg.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				myagent.getMyMap().merge(gr);
				
				// getting his knowledge
				Set<String> other_knowledge = new HashSet<>(); 
				for(String s : msg.getInReplyTo().split(" ")) {
					other_knowledge.add(s);
				}
				boolean newInfo = myagent.mergeKnowledge(other_knowledge);
				
				endVal = (newInfo) ? 2 : 1;		
				break;
			case WIN:
				Graphe gw = null;
				try {
					gw = (Graphe) msg.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				myagent.getMyMap().merge(gw);
				
				Set<String> other_vKnowledge = new HashSet<>(); 
				for(String s : msg.getInReplyTo().split(" ")) {
					other_vKnowledge.add(s);
				}
				myagent.mergeVKnowledge(other_vKnowledge);
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
			endVal = 1;	//waited too long
		}
	}
	
	@Override
	public int onEnd() {
		reset();
		return endVal;
	}

}
