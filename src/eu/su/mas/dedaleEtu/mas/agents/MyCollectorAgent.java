package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.EmptyBackpackBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.LootBehaviour;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class MyCollectorAgent extends MyAbstractAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 555938552790588773L;
	
	
	protected void setup(){

		super.setup();
		
		type = "COLLECTOR";
		register();
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new RandomWalkBehaviour(this), "WalkToTreasure");
		fsm.registerState(new LootBehaviour(this), "Loot");
		fsm.registerState(new RandomWalkBehaviour(this), "WalkToSilo");
		fsm.registerState(new EmptyBackpackBehaviour(this), "EmptyBackpack");
		
		fsm.registerTransition("WalkToTreasure", "Loot", 1);
		fsm.registerTransition("WalkToTreasure", "WalkToTreasure", 2);
		
		fsm.registerTransition("Loot", "WalkToSilo", 1);
		fsm.registerTransition("Loot", "WalkToTreasure", 2);
		
		fsm.registerTransition("WalkToSilo", "EmptyBackpack", 1);
		fsm.registerTransition("WalkToSilo", "WalkToSilo", 2);
		
		fsm.registerDefaultTransition("EmptyBackpack", "WalkToTreasure");
		
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

}
