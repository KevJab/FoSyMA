package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;

public class MySiloAgent extends MyAbstractAgent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6688123700288962519L;

	public MySiloAgent() {
		super();
		type = "SILO";
		
		register();
	}
	
	protected void setup(){

		super.setup();
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

}
