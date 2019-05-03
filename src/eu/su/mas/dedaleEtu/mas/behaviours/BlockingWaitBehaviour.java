package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.Map;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class BlockingWaitBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 7127500452644379989L;
	
	public static final int SILO = 1;
	public static final int AGENT = 2;
	public static final int MISSION = 3;
	
	private int type;
	
	public BlockingWaitBehaviour(MyAbstractAgent myagent, int type) {
		super(myagent);
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
		MessageTemplate template = null;
		if (type == SILO)
			template = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
		if(type == AGENT)
			template = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM), MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE));
		if (type == MISSION)
			template = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
		
		ACLMessage msg = myagent.blockingReceive(template);
		try {
			if ((type == SILO) || (type == AGENT)) {
				Graphe g = (Graphe) msg.getContentObject();
				myagent.getMyMap().merge(g);
			} else {
				Map<AID, Couple<String, AID>> tasks = (Map<AID, Couple<String,AID>>) msg.getContentObject();
				myagent.setGoalNode(tasks.get(myagent.getAID()));
			}
			
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		
		if (type == AGENT) {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription () ;
			sd.setType( "ANY" ); // type of the agent
			dfd.addServices(sd);
			DFAgentDescription[] result;
			try {
				result = DFService.search( myagent , dfd);
				//You get the list of all the agents (AID) of said type
				
				
				//A message is defined by : a performative, a sender, a set of receivers, (a protocol),(a content (and/or contentOBject))
				ACLMessage news = new ACLMessage(ACLMessage.PROPAGATE);
				news.setSender(myagent.getAID());
				
				try {
					news.setContentObject(myagent.getMyMap());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// I'm not sending a message to myself
				for (DFAgentDescription dfad : result){
					if(!dfad.getName().equals(myagent.getAID()))
						news.addReceiver(dfad.getName());
				}
				
				//Mandatory to use this method (it takes into account the environment to decide if someone is reachable or not)
				((AbstractDedaleAgent)myagent).sendMessage(news);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int onEnd() {
		return ((MyAbstractAgent) this.myAgent).getMyMap().isComplete() ? 2 : 1;
	}
}
