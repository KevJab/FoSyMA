package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.MyExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceivedMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SayHello;
import eu.su.mas.dedaleEtu.mas.behaviours.SendMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.data.MapInformation;
import eu.su.mas.dedaleEtu.mas.knowledge.MyMapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class MyExplorerAgent extends AbstractDedaleAgent {
	
	private static final long serialVersionUID = -6431752876590433727L;
	
	private MyMapRepresentation myMap;
	
	private HashMap<String, MapInformation> h = new HashMap<>();

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	
	
	public HashMap<String, MapInformation> getHashMap(){
		return h;
	}
	
	public void setHashMap(HashMap<String, MapInformation> h) {
		this.h = h;
	}
	
	protected void setup(){

		super.setup();
		

		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/
		
		//lb.add(new MyExploSoloBehaviour(this,this.myMap));
		//lb.add(new ReceivedMessageBehaviour(this));
		
		FSMBehaviour fsm = new FSMBehaviour(this);
		//TODO to use FSMBehaviour, these events need to redefine the public int onEnd() method
		//TODO for now, I'm considering all these behaviours work as intended
		fsm.registerFirstState(new RandomWalkBehaviour(this), "Walk"); 	//onEnd() -> 1 if not fully explored, 2 otherwise
		fsm.registerState(new SayHello(this), "Ping");					//onEnd() -> 1 if nobody in range, 2 otherwise
		fsm.registerState(new SendMessageBehaviour(this), "Send");
		fsm.registerState(new ReceivedMessageBehaviour(this), "Receive");
		fsm.registerLastState(new MyExploSoloBehaviour(this, myMap), "Explore");	//make it cyclic!
		
		fsm.registerTransition("Walk", "Explore", 2);
		fsm.registerTransition("Walk", "Ping", 1);
		fsm.registerTransition("Ping", "Walk", 1);
		fsm.registerTransition("Ping", "Send", 2);
		fsm.registerDefaultTransition("Send", "Receive");
		fsm.registerDefaultTransition("Receive", "Walk");
		
		lb.add(fsm);
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	public void merge(MyMapRepresentation other_map) {
		
	}
	
	
	
}
