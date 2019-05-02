package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.EmptyBackpackBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.LootBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.WalkToGoalBehaviour;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class MyCollectorAgent extends MyAbstractAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 555938552790588773L;
	
	//TODO make Collectors initially Explorers too
	//TODO once the whole map is explored, define an order for the treasures
	
	
	protected void setup(){

		super.setup();
		
		type = "COLLECTOR";
		register();
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		int type;
		if (this.getMyTreasureType() == Observation.DIAMOND) {
			type = WalkToGoalBehaviour.DIAMOND;
		} else if (this.getMyTreasureType() == Observation.GOLD) {
			type = WalkToGoalBehaviour.GOLD;
		} else { //Observation.ANY_TREASURE
			type = WalkToGoalBehaviour.TREASURE;
		}
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new WalkToGoalBehaviour(this, type), "WalkToTreasure");
		fsm.registerState(new LootBehaviour(this), "Loot");
		fsm.registerState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.SILO), "WalkToSilo");
		fsm.registerState(new EmptyBackpackBehaviour(this), "EmptyBackpack");
		// this behaviour does nothing else other than terminate the FSM
		fsm.registerLastState(new OneShotBehaviour() {private static final long serialVersionUID = 1L; public void action() {}}, "End");
		
		fsm.registerDefaultTransition("WalkToTreasure", "Loot");
		
		fsm.registerTransition("Loot", "WalkToSilo", 1);
		fsm.registerTransition("Loot", "WalkToTreasure", 2);
		
		fsm.registerTransition("WalkToSilo", "EmptyBackpack", 1);
		fsm.registerTransition("WalkToSilo", "WalkToSilo", 2);
		
		fsm.registerTransition("EmptyBackpack", "WalkToTreasure", 1);
		fsm.registerTransition("EmptyBackpack", "End", 2);
		
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

	@Override
	public void action() {
		this.pick();
	}

}
