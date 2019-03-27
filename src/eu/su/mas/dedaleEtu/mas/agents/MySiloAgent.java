package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour;
import jade.core.behaviours.Behaviour;

public class MySiloAgent extends MyAbstractAgent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6688123700288962519L;

	protected void setup(){

		super.setup();
		
		type = "SILO";
		register();

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		//TODO let's think about this
		lb.add(new RandomWalkBehaviour(this));
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

}
