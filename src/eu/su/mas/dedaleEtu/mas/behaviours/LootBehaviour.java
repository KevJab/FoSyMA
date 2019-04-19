package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class LootBehaviour extends FSMBehaviour {
	
	
	private static final long serialVersionUID = -2232531484898905354L;

	public LootBehaviour(final Agent myagent) {
		super(myagent);
		//TODO redo this
		this.registerFirstState(new OpenTreasureBehaviour(myagent), "Open");
		this.registerLastState(new OneShotBehaviour() {private static final long serialVersionUID = 1L; public void action(){ } }, "End");
		
		this.registerTransition("Open", "End", 1);
		this.registerTransition("Open", "Help", 2);
		
		this.registerTransition("Help", "Help", 1);
		this.registerTransition("Help", "End", 2);
		this.registerTransition("Help", "Open", 3);
	}
	

	@Override
	public int onEnd() {
		return (((MyAbstractAgent) this.myAgent).getBackPackFreeSpace() == 0) ? 1 : 2; // returns 1 if backpack is full, 2 otherwise
	}


}
