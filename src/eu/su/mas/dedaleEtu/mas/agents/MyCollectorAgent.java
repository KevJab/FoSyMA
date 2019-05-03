package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.BlockingWaitBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.EchoFloodingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.WaitBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.WalkToGoalBehaviour;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class MyCollectorAgent extends MyAbstractAgent {

	private static final long serialVersionUID = 555938552790588773L;
	
	protected void setup(){

		super.setup();
		
		type = "COLLECTOR";
		register();
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.OPEN), "Explore");
		fsm.registerState(new EchoFloodingBehaviour(this, false), "Echo");
		fsm.registerState(new WaitBehaviour(this, WaitBehaviour.MISSION), "Wait&SendMission");
		fsm.registerState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.GOAL), "WalkToTreasure");
		fsm.registerState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.SILO), "WalkToSilo");
		fsm.registerState(new BlockingWaitBehaviour(this, false), "WaitMapUpdate");
		fsm.registerLastState(new EchoFloodingBehaviour(this, true), "EchoWin");
		
		fsm.registerDefaultTransition("Echo", "Wait&SendMission");
		fsm.registerDefaultTransition("Wait&SendMission", "WalkToTreasure");
		fsm.registerDefaultTransition("WalkToTreasure", "WalkToSilo");
		fsm.registerDefaultTransition("WalkToSilo", "WaitMapUpdate");
		
		fsm.registerTransition("Explore", "Explore", 1);
		fsm.registerTransition("Explore", "Echo", 2);
		
		fsm.registerTransition("WaitMapUpdate", "Wait&SendMission", 1);
		fsm.registerTransition("WaitMapUpdate", "EchoWin", 2);
		
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

	@Override
	public void action() {
		int qtyPicked = this.pick();
		myMap.unlock(this.getMyTreasureType());
		myMap.pick(this.getMyTreasureType(), qtyPicked);
	}

}
