package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class SayHelloBehaviour extends OneShotBehaviour{

	private static final long serialVersionUID = -3570245643164391414L;
		
	public static final int PING = 1;
	public static final int ECHO = 2;
	public static final int WIN = 3;
	
	private int endVal;
	private int type;

	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who possesses the behaviour
	 *  
	 */
	public SayHelloBehaviour(final Agent myagent, int type) {
		super(myagent);
		this.type = type;
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
			if (type == WIN) {
				ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
				msg.setSender(myagent.getAID());
				
				try {
					msg.setContentObject(myagent.getMyMap());
					msg.setInReplyTo(myagent.getMyVKnowledge());
				} catch (IOException e) {
					e.printStackTrace();
				}
				// I'm not sending a message to myself
				for (DFAgentDescription dfad : result){
					System.out.println("I am "+ myagent.getAID() + "and the DF tells me about " + dfad.getName());
					if(!dfad.getName().equals(myagent.getAID()))
						msg.addReceiver(dfad.getName());
				}
				
				//Mandatory to use this method (it takes into account the environment to decide if someone is reachable or not)
				((AbstractDedaleAgent)myagent).sendMessage(msg);
				
				
			} else {
				//A message is defined by : a performative, a sender, a set of receivers, (a protocol),(a content (and/or contentOBject))
				ACLMessage msg=new ACLMessage(ACLMessage.REQUEST);
				msg.setSender(myagent.getAID());
				
				if (type == PING) 
					msg.setContent(myagent.getMyMap().getGoal() +","+myagent.getMyMap().getMyPos());
				else if (type == ECHO) {
					msg.setOntology("echo");
					try {
						msg.setContentObject(myagent.getMyMap());
						msg.setInReplyTo(myagent.getMyKnowledge());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// I'm not sending a message to myself
				for (DFAgentDescription dfad : result){
					System.out.println("I am "+ myagent.getAID() + "and the DF tells me about " + dfad.getName());
					if(!dfad.getName().equals(myagent.getAID())) {
						if ((type != ECHO) || (!myagent.knowsAbout(true, dfad.getName())))
							msg.addReceiver(dfad.getName());
					}
				}
				
				//Mandatory to use this method (it takes into account the environment to decide if someone is reachable or not)
				((AbstractDedaleAgent)myagent).sendMessage(msg);
				
				/*if (type == PING) 
					System.out.println(this.myAgent.getLocalName()+" says : Hello? anyone here?");
				else if (type == ECHO) 
					System.out.println(this.myAgent.getLocalName()+" says : The map is complete! Here it is!");*/
					
				if ((type == ECHO) && (myagent.isCommonKnowledge()))	// otherwise there may be an infinite loop
					endVal = 2;
			}
			
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int onEnd() {
		return endVal;
	}
}