package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class EchoFloodingBehaviour extends FSMBehaviour {

	private static final long serialVersionUID = -2137086472580164303L;
	
	private boolean everyoneKnows;
	
	public EchoFloodingBehaviour(MyAbstractAgent a, boolean isDone) {
		super(a);
		//FIXME rework this
		if(isDone) {
			this.registerFirstState(new SayHelloBehaviour(a, SayHelloBehaviour.WIN), "Inform"); 
			this.registerState(new WaitBehaviour(myAgent, WaitBehaviour.WIN), "Wait");
			this.registerLastState(new OneShotBehaviour() {
				private static final long serialVersionUID = 8357221004356169749L;

				public void action() {}
			}, "End");
		} else {
			this.registerFirstState(new SayHelloBehaviour(a, SayHelloBehaviour.ECHO), "Inform"); 
			this.registerState(new WaitBehaviour(myAgent, WaitBehaviour.ECHO), "Wait");
			this.registerLastState(new OneShotBehaviour() {
				private static final long serialVersionUID = 8357221004356169749L;
				public void action() {}
			}, "End");
			
			this.registerTransition("Wait", "Inform", 1);
			this.registerTransition("Wait", "End", 2);
			
			this.registerDefaultTransition("Inform", "Wait");
		}
	}
	
	@Override
	public int onEnd() {
		reset();
		return everyoneKnows ? 1 : 2;
	}
}
