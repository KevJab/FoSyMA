package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.Map;

import dataStructures.tuple.Couple;
import dataStructures.tuple.Tuple4;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.MySiloAgent;
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
	public static final int REQUEST = 4;
	public static final int INFO = 5;
	
	private int type;
	private int endVal;
	
	public BlockingWaitBehaviour(MyAbstractAgent myagent, int type) {
		super(myagent);
		this.type = type;
	}
	
	@Override
	public void onStart() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
		if (myagent instanceof MySiloAgent) {
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
		if (type == REQUEST)
			template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		if (type == INFO)
			template = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
		
		ACLMessage msg = myagent.blockingReceive(template);
		try {
			if ((type == SILO) || (type == AGENT)) {
				Graphe g = (Graphe) msg.getContentObject();
				myagent.getMyMap().merge(g);
			} else if (type == MISSION) {
				Map<AID, Couple<String, AID>> tasks = (Map<AID, Couple<String,AID>>) msg.getContentObject();
				myagent.setGoalNode(tasks.get(myagent.getAID()));
			} else if (type == REQUEST) {
				myagent.setInterlocuteur(msg.getSender());
			} else {
				if ("myName".equals(msg.getProtocol())) {
					myagent.addChild(msg.getSender());
					endVal = 2;
				} else {
					Map<AID, Tuple4<Observation, String, Integer, Integer>> ag_info = (Map<AID, Tuple4<Observation, String, Integer, Integer>>) msg.getContentObject();
					endVal = (myagent.mergeAgentInfo(msg.getSender(), ag_info)) ? 1 : 2;
				}
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
		} else if (type == REQUEST) {
			ACLMessage info = new ACLMessage(ACLMessage.PROPOSE);
			info.setSender(myagent.getAID());
			info.setProtocol("myName");
		}
	}

	@Override
	public int onEnd() {
		reset();
		if (type == INFO)
			return endVal;
		return ((MyAbstractAgent) this.myAgent).getMyMap().isComplete() ? 2 : 1;
	}
}
