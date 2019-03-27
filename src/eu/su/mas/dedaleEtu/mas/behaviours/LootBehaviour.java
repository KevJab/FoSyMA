package eu.su.mas.dedaleEtu.mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class LootBehaviour extends SimpleBehaviour {
	
	
	private boolean fullBackpack = false;
	private static final long serialVersionUID = -2232531484898905354L;

	public LootBehaviour(final Agent myagent) {
		super(myagent);

	}

	@Override
	public void action() {
		//TODO Empty placeholder
	}
	
	@Override
	public int onEnd() {
		return (fullBackpack)?1:2; // returns 1 if backpack is full, 2 otherwise
	}

	@Override
	public boolean done() {
		return true;
	}

}
