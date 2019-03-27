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
import eu.su.mas.dedaleEtu.mas.behaviours.WaitBehaviour;
import eu.su.mas.dedaleEtu.mas.data.MapInformation;
import eu.su.mas.dedaleEtu.mas.knowledge.MyMapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

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
		//TODO for now, I'm considering all these behaviours work as intended
		fsm.registerFirstState(new RandomWalkBehaviour(this), "Walk"); 	//onEnd() -> 1 if not fully explored, 2 otherwise;
		fsm.registerState(new WaitBehaviour(this, WaitBehaviour.PING), "PingWait");
		fsm.registerState(new SayHello(this), "PingSend");					
		fsm.registerState(new WaitBehaviour(this, WaitBehaviour.PINGRESPONSE), "PingResponseWait");				//onEnd() -> 1 if nobody in range, 2 otherwise
		fsm.registerState(new SendMessageBehaviour(this), "Send");
		fsm.registerState(new ReceivedMessageBehaviour(this), "Receive");
		fsm.registerState(new WaitBehaviour(this, WaitBehaviour.SEND), "WaitSend");
		fsm.registerState(new SendMessageBehaviour(this), "Send2");
		fsm.registerLastState(new MyExploSoloBehaviour(this, myMap), "Explore");	//make it cyclic!
		
		fsm.registerTransition("Walk", "Explore", 1);
		fsm.registerTransition("Walk", "PingWait", 2);
		
		fsm.registerTransition("PingWait", "PingSend", 1);
		fsm.registerTransition("PingWait", "WaitSend", 2);
		
		fsm.registerTransition("PingResponseWait", "Walk", 1);
		fsm.registerTransition("PingResponseWait", "Send", 2);
		
		fsm.registerTransition("WaitSend", "PingSend", 1);
		fsm.registerTransition("WaitSend", "Send2", 2);
		
		fsm.registerDefaultTransition("PingSend", "PingResponseWait");
		fsm.registerDefaultTransition("Send", "Receive");
		fsm.registerDefaultTransition("Receive", "Walk");
		fsm.registerDefaultTransition("Send2", "Walk");
		
		lb.add(fsm);
		
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	
	
	
}
