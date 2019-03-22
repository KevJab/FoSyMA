package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.HashMap;

import eu.su.mas.dedaleEtu.mas.agents.MyExplorerAgent;
import eu.su.mas.dedaleEtu.mas.data.MapInformation;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {	
			HashMap<String, MapInformation> h = ((MyExplorerAgent) myAgent).getHashMap();
			String name = msg.getSender().getLocalName();
			if(!h.containsKey(name))
				//TODO Envoyer tout le graphe a la place d'un MapInfo vide
				h.put(name, new MapInformation());
			System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
		}else{
			block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
		}
	}

	public boolean done() {
		return finished;
	}

}

