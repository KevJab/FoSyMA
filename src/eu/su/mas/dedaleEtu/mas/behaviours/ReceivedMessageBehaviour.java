package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.HashMap;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyExplorerAgent;
import eu.su.mas.dedaleEtu.mas.data.MapInformation;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceivedMessageBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 9081111202507795289L;

	private boolean finished=false;

	/**
	 * 
	 * This behaviour is a one Shot.
	 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
	 * @param myagent
	 */
	public ReceivedMessageBehaviour(final Agent myagent) {
		super(myagent);

	}


	public void action() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			

		final ACLMessage msg = myagent.receive(msgTemplate);
		if (msg != null) {	
			String name = msg.getSender().getLocalName();
			String interloc = myagent.getInterlocuteur().getLocalName();
			if(!name.equals(interloc)) {
				System.out.println(myagent.getLocalName() + " - Hey! "+name+"! C'est pas à toi que je parle, c'est à "+interloc);
			} else {
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
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}

	public boolean done() {
		return finished;
	}

}

