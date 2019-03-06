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

	@Override
	public void action() {
		// TODO Auto-generated method stub

		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
		/*		*/
		
		
		//1°Create the message
		final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(myagent.getAID());
		msg.addReceiver(new AID(myagent.getInterlocuteur().getLocalName(), AID.ISLOCALNAME));  
			
		//2° compute the random value		
		try {
			msg.setContentObject(myagent.getHashMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myagent.send(msg);
		
		System.out.println(myagent.getLocalName()+" ----> sent his map to " + myagent.getInterlocuteur().getLocalName());
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int onEnd() {
		return -1;  //TODO
	}

}
