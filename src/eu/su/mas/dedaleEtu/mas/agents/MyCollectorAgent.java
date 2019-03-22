package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;

public class MyCollectorAgent extends MyAbstractAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 555938552790588773L;
	
	public MyCollectorAgent() {
		super();
		type = "COLLECTOR";
		
		register();
	}
	
	protected void setup(){

		super.setup();
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

}
