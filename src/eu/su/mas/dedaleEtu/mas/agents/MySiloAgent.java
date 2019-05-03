package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.tuple.Tuple4;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.BlockingWaitBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.EchoFloodingBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.GiveGoalBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.WalkToGoalBehaviour;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class MySiloAgent extends MyAbstractAgent {
	
	
	private static final long serialVersionUID = -6688123700288962519L;
	
	public Map<AID, Tuple4<Observation, String, Integer, Integer>> agent_info = new HashMap<>();
	// map containing, for each agent (identified by his AID), his TreasureType, type (explorer, collector), strength and lockpicking power

	// a map with all other agents' tasks. For each agent (AID), you have the goalNode and the groupLeader (their new interlocuteur)
	
	protected void setup(){

		super.setup();
		
		type = "SILO";
		register();

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.OPEN), "Explore");
		fsm.registerState(new EchoFloodingBehaviour(this, false), "Echo");
		fsm.registerState(new GiveGoalBehaviour(this, true), "GiveMissions");
		fsm.registerState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.GOAL), "MyPos");
		fsm.registerState(new BlockingWaitBehaviour(this, BlockingWaitBehaviour.SILO), "WaitMsg");
		fsm.registerState(new GiveGoalBehaviour(this, false), "NewMissions");
		fsm.registerLastState(new EchoFloodingBehaviour(this, true), "EchoWin");
		
		fsm.registerDefaultTransition("Echo", "GiveMissions");
		fsm.registerDefaultTransition("GiveMissions", "MyPos");
		fsm.registerDefaultTransition("MyPos", "WaitMsg");
		fsm.registerDefaultTransition("NewMissions", "WaitMsg");
		
		fsm.registerTransition("Explore", "Explore", 1);
		fsm.registerTransition("Explore", "Echo", 2);
		
		fsm.registerTransition("WaitMsg", "NewMissions", 1);
		fsm.registerTransition("WaitMsg", "EchoWin", 2);
		
		lb.add(fsm);
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the agent "+this.getLocalName()+ " is started");

	}
	
	@Override
	// the silo doesn't need the action method, but it is required as it is abstract in MyAbstractAgent
	public void action() {}

}
