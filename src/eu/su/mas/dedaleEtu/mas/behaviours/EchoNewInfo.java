package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.MySiloAgent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class EchoNewInfo extends OneShotBehaviour {

	private static final long serialVersionUID = 3459772144690581871L;

	public EchoNewInfo(Agent a) {
		super(a);
	}

	@Override
	public void action() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
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
			
			msg.setOntology("echo");
			try {
				msg.setContentObject(myagent.getMyMap());
				msg.setInReplyTo(myagent.getMyKnowledge());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
	
	@Override
	public int onEnd() {
		if (((MyAbstractAgent) this.myAgent).isCommonKnowledge())
			return (this.myAgent instanceof MySiloAgent) ? 3 : 1;
		return 2;
	}

}
