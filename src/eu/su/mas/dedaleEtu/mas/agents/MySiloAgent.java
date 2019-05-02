package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.tuple.Tuple3;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.given.RandomWalkBehaviour;
import jade.core.AID;
import jade.core.behaviours.Behaviour;

public class MySiloAgent extends MyAbstractAgent {
	
	
	private static final long serialVersionUID = -6688123700288962519L;
	
	private Map<AID, Tuple3<Observation, Integer, Integer>> agent_info = new HashMap<>();

	protected void setup(){

		super.setup();
		
		type = "SILO";
		register();

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		//TODO (read more) just like every agent, the tanker will be exploring first
		// then he will create groups of agents (led by a Collector, if possible) to go to  their nearest treasure
		// if a group is short on strength/lockpicking expertise, another agent will receive their goal coordinate as a "side mission"
		// afterwards he'll go to a Node equidistant of all treasures
		lb.add(new RandomWalkBehaviour(this));
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

	@Override
	// the silo doesn't need the action method
	public void action() {}

}
