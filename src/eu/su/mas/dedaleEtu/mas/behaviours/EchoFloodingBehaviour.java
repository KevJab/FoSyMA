package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class EchoFloodingBehaviour extends FSMBehaviour {

	private static final long serialVersionUID = -2137086472580164303L;
	
	private boolean everyoneKnows;
	
	public EchoFloodingBehaviour(MyAbstractAgent a, boolean isDone) {
		super(a);
		if(isDone) {
			
			this.registerFirstState(new SayHelloBehaviour(a, SayHelloBehaviour.WIN), "Inform"); 
			this.registerState(new WaitBehaviour(myAgent, WaitBehaviour.WIN), "Wait");
			this.registerLastState(new OneShotBehaviour() {
				private static final long serialVersionUID = 8357221004356169749L;

				public void action() {}
			}, "End");
			

			this.registerTransition("Inform", "Wait", 1);
			this.registerTransition("Inform", "End", 2);
			
			this.registerDefaultTransition("Wait", "Inform");
			
		} else {
			/* ------------
			 *    States
			 * ------------*/
			this.registerFirstState(new EchoNewInfo(a), "NewInfo");
			this.registerState(new SayHelloBehaviour(a, SayHelloBehaviour.ECHO), "Inform"); 
			this.registerState(new WaitBehaviour(a, WaitBehaviour.ECHO), "WaitEcho");
			this.registerState(new BlockingWaitBehaviour(a, BlockingWaitBehaviour.REQUEST), "WaitRequest");
			this.registerState(new WaitBehaviour(a, WaitBehaviour.REQUESTREPLY), "WaitReply");
			this.registerState(new BlockingWaitBehaviour(a, BlockingWaitBehaviour.INFO), "WaitChildren");
			this.registerState(new BlockingWaitBehaviour(a, BlockingWaitBehaviour.INFO), "WaitInfo");
			this.registerState(new SendMessageBehaviour(a), "TellParent"); //FIXME second
			
			this.registerLastState(new OneShotBehaviour() {
				private static final long serialVersionUID = 8357221004356169749L;
				public void action() {}
			}, "End");
			
			
			/* -----------------
			 *    Transitions   
			 * -----------------*/
			this.registerTransition("NewInfo", "WaitRequest", 1);
			this.registerTransition("NewInfo", "WaitEcho", 2);
			this.registerTransition("NewInfo", "RequestInfo", 3);
			
			this.registerTransition("WaitEcho", "Inform", 1);
			this.registerTransition("WaitEcho", "NewInfo", 2);
			
			this.registerTransition("WaitReply", "WaitChildren", 1);
			this.registerTransition("WaitReply", "TellParent", 2);
			
			this.registerTransition("WaitChildren", "TellParent", 1);
			this.registerTransition("WaitChildren", "WaitChildren", 2);
			
			this.registerTransition("WaitInfo", "End", 1);
			this.registerTransition("WaitInfo", "WaitInfo", 2);
			
			this.registerDefaultTransition("Inform", "WaitEcho");
			this.registerDefaultTransition("WaitRequest", "SendRequest");
			this.registerDefaultTransition("SendRequest", "WaitReply");
			this.registerDefaultTransition("TellParent", "End");
			this.registerDefaultTransition("RequestInfo", "WaitInfo");
			
		}
		
		
	}
}
