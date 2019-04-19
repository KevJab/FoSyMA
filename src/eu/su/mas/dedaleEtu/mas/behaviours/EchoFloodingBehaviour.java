package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class EchoFloodingBehaviour extends FSMBehaviour {

	private static final long serialVersionUID = -2137086472580164303L;
	
	private boolean everyoneKnows;
	
	public EchoFloodingBehaviour(MyAbstractAgent a) {
		super(a);
		
		this.registerState(new SayHelloBehaviour(a, SayHelloBehaviour.ECHO), "Inform"); 
	}
	
	@Override
	public int onEnd() {
		reset();
		return everyoneKnows ? 1 : 2;
	}
}
