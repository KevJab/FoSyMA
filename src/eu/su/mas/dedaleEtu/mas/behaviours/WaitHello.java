package eu.su.mas.dedaleEtu.mas.behaviours;


import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WaitHello extends WakerBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7427316588458684865L;
	private int endVal;

	public WaitHello(Agent a) {
		super(a, 2000);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onWake() {
		//1) receive the message
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null) {	
			endVal = 2;	//FSM goes to Send
			((MyAbstractAgent) this.myAgent).setInterlocuteur(msg.getSender());
		}else{
			endVal = 1;	//FSM goes back to RandomWalk
		}
	}
	
	@Override
	public int onEnd() {
		return endVal;
	}

}
