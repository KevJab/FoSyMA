package eu.su.mas.dedaleEtu.mas.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.MyExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceivedMessageBehaviour;
import eu.su.mas.dedaleEtu.mas.data.MapInformation;
import eu.su.mas.dedaleEtu.mas.knowledge.MyMapRepresentation;
import jade.core.behaviours.Behaviour;

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
		
		lb.add(new MyExploSoloBehaviour(this,this.myMap));
		lb.add(new ReceivedMessageBehaviour(this));
		
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	
	
}
