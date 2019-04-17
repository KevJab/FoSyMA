package eu.su.mas.dedaleEtu.mas.agents.dummies;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.MigrationBehaviour;

public class MyDummyMigrationAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -6243840202574922770L;

	protected void setup() { //Automatically called at agent's creation
		super.setup();
		
		addBehaviour(new MigrationBehaviour(this));
		
	}

	protected void beforeMove() { //Automatically called before doMove()
		super.beforeMove();
		System.out.println("I migrate");
	}
	
	protected void afterMove() { //Automatically called after doMove()
		super.afterMove();
		System.out.println("I migrated");
		addBehaviour(new MigrationBehaviour(this));
	}
}
