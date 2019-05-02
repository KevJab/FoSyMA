package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.WalkToGoalBehaviour;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class MyExplorerAgent extends MyAbstractAgent {
	
	private static final long serialVersionUID = -6431752876590433727L;
	
	
	
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
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		//lb.add(new MyExploSoloBehaviour(this,this.myMap));
		//lb.add(new ReceivedMessageBehaviour(this));
		
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		fsm.registerFirstState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.OPEN), "Explore");
		fsm.registerState(new WalkToGoalBehaviour(this, WalkToGoalBehaviour.TREASURE), "Lockpick");
		// this behaviour does nothing else other than terminate the FSM
		fsm.registerLastState(new OneShotBehaviour() {private static final long serialVersionUID = 1L;
														public void action() {}}, "End");
		
		fsm.registerTransition("Explore", "Explore",1);
		fsm.registerTransition("Explore", "Lockpick", 2);
		
		fsm.registerTransition("Lockpick", "Lockpick", 1);
		fsm.registerTransition("Lockpick", "End", 2);
				
		lb.add(fsm);
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}



	@Override
	public void action() {
		this.openLock(Observation.DIAMOND);
		this.openLock(Observation.GOLD);
	}
	
	
	
}
