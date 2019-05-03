package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.BlockingWaitBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.EchoFloodingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.WalkToGoalBehaviour;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class MyExplorerAgent extends MyAbstractAgent {
	
	private static final long serialVersionUID = -6431752876590433727L;
	private boolean isLeader = false;
	
	
	
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */

	
	protected void setup(){

		super.setup();
		type = "EXPLORER";
		register();

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.OPEN), "Explore");
		fsm.registerState(new EchoFloodingBehaviour(this, false), "Echo");
		fsm.registerState(new BlockingWaitBehaviour(this, BlockingWaitBehaviour.MISSION), "Wait&SendMission");
		fsm.registerState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.GOAL), "WalkToTreasure");
		fsm.registerState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.SILO), "WalkToSilo");
		fsm.registerState(new BlockingWaitBehaviour(this, BlockingWaitBehaviour.AGENT), "WaitMapUpdate");
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
		if (isLeader) {
			this.openLock(Observation.DIAMOND);
			myMap.unlock(Observation.DIAMOND);
			
			this.openLock(Observation.GOLD);
			myMap.unlock(Observation.GOLD);
			
			isLeader = false;
		}
		
	}
	
	
	
}
