package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMessageBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2727785000353107477L;
	
	private boolean finished=false;
	
	public SendMessageBehaviour(final MyAbstractAgent myagent) {
		super(myagent);
		
	}

	/**
	 * <code>this.myAgent</code> sends his map to his "interlocuteur"
	 */
	@Override
	public void action() {

		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
		/*		*/
		
		
		//1°Create the message
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(myagent.getAID());
		msg.addReceiver(new AID(myagent.getInterlocuteur().getLocalName(), AID.ISLOCALNAME));  
			
		//2° compute the random value		
		try {
			msg.setContentObject(myagent.getMyMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
		myagent.sendMessage(msg);
		
		System.out.println(myagent.getLocalName()+" ----> sent his map to " + myagent.getInterlocuteur().getLocalName());
	}

	@Override
	public boolean done() {
		return true;
	}

}
