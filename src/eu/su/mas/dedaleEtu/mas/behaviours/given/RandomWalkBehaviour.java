package eu.su.mas.dedaleEtu.mas.behaviours.given;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.behaviours.OneShotBehaviour;

/**************************************
 * 
 * 
 * 				BEHAVIOUR RandomWalk : Illustrates how an agent can interact with, and move in, the environment
 * 
 * 
 **************************************/


public class RandomWalkBehaviour extends OneShotBehaviour{
	
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	private int endVal;

	public RandomWalkBehaviour (final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	/**
	 * The agent walks one step (for now randomly, will have to be modified very soon). </br>
	 * As long as the map is not complete, <code>endVal</code> will be 2 (ie ping + map exchange). </br>
	 * Otherwise, this will be the last step before becoming an unlocker.
	 */
	@Override
	public void action() {
		//Example to retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		System.out.println(this.myAgent.getLocalName()+" -- myCurrentPosition is: "+myPosition);
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);

			/*//Little pause to allow you to follow what is going on
			try {
				System.out.println("Press enter in the console to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			
			//list of observations associated to the currentPosition
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();

			//example related to the use of the backpack for the treasure hunt
			Boolean b=false;
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case DIAMOND:case GOLD:
					
					System.out.println(this.myAgent.getLocalName()+" - My treasure type is : "+((AbstractDedaleAgent) this.myAgent).getMyTreasureType());
					System.out.println(this.myAgent.getLocalName()+" - My current backpack capacity is:"+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
					System.out.println(this.myAgent.getLocalName()+" - Value of the treasure on the current position: "+o.getLeft() +": "+ o.getRight());
					System.out.println(this.myAgent.getLocalName()+" - The agent grabbed :"+((AbstractDedaleAgent) this.myAgent).pick());
					System.out.println(this.myAgent.getLocalName()+" - the remaining backpack capacity is: "+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
					b=true;
					break;
				default:
					break;
				}
			}

			//If the agent picked (part of) the treasure
			if (b){
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs2=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				System.out.println(this.myAgent.getLocalName()+" - State of the observations after trying to pick something "+lobs2);
			}
			
			/* Not used anymore; this behaviour was originally a placeholder to see if the FSM worked as intended
			// Decides what the agent will do next
			if(((MyAbstractAgent) this.myAgent).isCompleteMap()) { // if map is finished
				endVal = 1;
				System.out.println(this.myAgent.getLocalName()+" - My map is complete now; let's explore");
			}
			else {
				System.out.println(this.myAgent.getLocalName()+ " - I should continue discovering the world");
				endVal = 2;
			}*/
			

			//Random move from the current position
			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);//removing the current position from the list of target, not necessary as to stay is an action but allow quicker random move

			//The move action (if any) should be the last action of your behaviour
			((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
		}

		
	}

	@Override
	public int onEnd() {
		return endVal;
	}

}