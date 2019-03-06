package eu.su.mas.dedaleEtu.mas.behaviours;


import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitBehaviour extends WakerBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7427316588458684865L;
	private int endVal;
	
	public static final int PING = 1;
	public static final int PINGRESPONSE = 2;
	public static final int SEND = 3;
	
	private int type;

	public WaitBehaviour(Agent a, int type) {
		super(a, 2000);
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onStart() {
		if(type == SEND) {
			//TODO send pingresponse (ACLMessage.AGREE)
		}
	}
	
	@Override
	public void onWake() {
		int performative;
		switch(type) {
		case PING:
			performative = ACLMessage.REQUEST;
			break;
		case PINGRESPONSE:
			performative = ACLMessage.AGREE;
			break;
		case SEND:
			performative = ACLMessage.INFORM;
			break;
		default:
			performative = -1;
			break;
		}
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(performative);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {	
			endVal = 2;	//FSM goes to Send
			((MyAbstractAgent) this.myAgent).setInterlocuteur(msg.getSender());
		}else{
			endVal = 1;	//waited too long
		}
	}
	
	@Override
	public int onEnd() {
		return endVal;
	}

}
