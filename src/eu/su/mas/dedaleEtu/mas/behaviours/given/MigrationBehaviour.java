package eu.su.mas.dedaleEtu.mas.behaviours.given;

import java.util.Random;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.SimpleBehaviour;

public class MigrationBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = -5537762282231232623L;
	
	/**
	 * an array of all containers. It contains [containerName, containerHostname]
	 */
	private String[][] containers = {{"Ithaq_c2", "PPTI-14-408-07"},
									 {"Ithaq_c3", "PPTI-14-408-07"},
									 {"Ithaq_c4", "PPTI-14-408-07"},
									 {"Ithaq_Machine13", "PPTI-14-408-13"}};
	private Random myRand;
	
	public MigrationBehaviour(Agent a) {
		super(a);
		myRand = new Random();
	}

	@Override
	public void action() {
		int i = myRand.nextInt(containers.length);
		ContainerID cID = new ContainerID();
		cID.setName(containers[i][0]);
		cID.setPort("8888");
		cID.setAddress(containers[i][1]);
		
		this.myAgent.doMove(cID); 	// last method to call in the behaviour
	}

	@Override
	public boolean done() {
		return false;
	}

}
