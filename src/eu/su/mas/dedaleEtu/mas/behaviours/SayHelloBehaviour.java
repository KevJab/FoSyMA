package eu.su.mas.dedaleEtu.mas.behaviours;

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
	public static final int HELP = 2;
	private int type;

	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who posses the behaviour
	 *  
	 */
	public SayHelloBehaviour(final Agent myagent, int type) {
		super(myagent);
		this.type = type;
	}

	@Override
	public void action() {
		switch (type) {
		case PING:
			actionPing();
			break;
		case HELP:
			actionHelp();
			break;
		default:
			break;
		}
	}

	private void actionPing() {
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
			msg.setProtocol("UselessProtocol");
			//System.out.println("Agent "+myagent.getLocalName()+ " is trying to reach its friends");
			msg.setContent(myagent.getMyMap().getGoal() +","+myagent.getMyMap().getMyPos());

			// I'm not sending a message to myself
			for (DFAgentDescription dfad : result){
				System.out.println("I am "+ myagent.getAID() + "and the DF tells me about " + dfad.getName());
				if(!dfad.getName().equals(myagent.getAID()))
					msg.addReceiver(dfad.getName());
			}
			
			//Mandatory to use this method (it takes into account the environment to decide if someone is reachable or not)
			((AbstractDedaleAgent)myagent).sendMessage(msg);
			
			System.out.println(this.myAgent.getLocalName()+" says : Hello? anyone here?");
			
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		
	}
	
	private void actionHelp() {
		//TODO call for help and keep track of how often you did; stop after a few iterations
	}

}