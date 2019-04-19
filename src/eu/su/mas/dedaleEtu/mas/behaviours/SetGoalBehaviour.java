package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class SetGoalBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 902851789463823991L;
	
	// variables to identify which actual behaviour it is
	/**
	 * value of <code>behaviourType</code> if this is the WalkToGoalBehaviour's initial goal setting behaviour
	 */
	public static final int INIT = 0;
	/**
	 * value of <code>behaviourType</code> if this is the WalkToGoalBehaviour's goal setting behaviour for the one sending the ping
	 */
	public static final int SENDER = 1;
	/**
	 * value of <code>behaviourType</code> if this is the WalkToGoalBehaviour's goal setting behaviour for the one receiving the ping
	 */
	public static final int RECEIVER = 2;
	
	private int goalType;
	private int behaviourType;
	private boolean reached;
	private Graphe myMap;
	
	public SetGoalBehaviour(AbstractDedaleAgent agent, int goalType, int behaviourType) {
		super(agent);
		this.goalType = goalType;
		this.behaviourType = behaviourType;
		this.myMap = ((MyAbstractAgent) this.myAgent).getMyMap();
	}

	@Override
	public void action() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
		final MessageTemplate msgTemplate = MessageTemplate.MatchOntology("echo");
		final ACLMessage msg = myagent.receive(msgTemplate);
		if(msg != null) {
			Graphe g = null;
			try {
				g = (Graphe) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			myagent.getMyMap().merge(g);
			
			reached = true;
			return;
		}
		
		
		
		// check if I am on a goal (done here because I want to look around regardless)
		if(myMap.goalReached()) {
			reached = true;
			return;
		}
		
		// actually set a new goal (depending on which behaviour in the FSM this actually is)
		switch (behaviourType) {
		case INIT:
			actionInit();
			break;
		case SENDER:
			actionSender();
			break;
		case RECEIVER:
			actionReceiver();
			break;
		default:
			break;
		}
	}

	/**
	 * The action taken by the agent at the start of the WalkToGoalBehaviour FSM
	 */
	private void actionInit() {
		reached = false;
		
		/* ---------------
		 *   Look around
		 * ---------------*/
		//Example to retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		System.out.println(this.myAgent.getLocalName()+" -- myCurrentPosition is: "+myPosition);
		System.out.println(this.myAgent.getLocalName()+" -- I've already been to: "+((MyAbstractAgent) this.myAgent).getMyMap().getNodesSeen());
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
			
			//list of observations associated to the currentPosition
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
			
			int qtyD = 0;
			int qtyG = 0;
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
				case DIAMOND:case GOLD:
					if (o.getLeft().equals(Observation.GOLD))
						qtyG += o.getRight();
					else
						qtyD += o.getRight();
					System.out.println(this.myAgent.getLocalName()+" - My treasure type is : "+((AbstractDedaleAgent) this.myAgent).getMyTreasureType());
					System.out.println(this.myAgent.getLocalName()+" - My current backpack capacity is:"+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
					System.out.println(this.myAgent.getLocalName()+" - Value of the treasure on the current position: "+o.getLeft() +": "+ o.getRight());
					//System.out.println(this.myAgent.getLocalName()+" - The agent grabbed :"+((AbstractDedaleAgent) this.myAgent).pick());
					System.out.println(this.myAgent.getLocalName()+" - the remaining backpack capacity is: "+ ((AbstractDedaleAgent) this.myAgent).getBackPackFreeSpace());
					break;
				default:
					break;
				}
			}
			
			myMap.addNode(myPosition, qtyG, qtyD, true);
			
			/* Not needed for now, we only have explorers
			 * //If the agent picked (part of) the treasure
			if (b){
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs2=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				System.out.println(this.myAgent.getLocalName()+" - State of the observations after trying to pick something "+lobs2);
			}*/
			
			// looks at all observable nodes (ie neighbouring nodes)
			List<String> nbrs = new ArrayList<>();
			for(int i = 1; i < lobs.size(); i++) {
				String nbr = lobs.get(i).getLeft();
				nbrs.add(nbr);
				
				int qtyD_nbr = 0;
				int qtyG_nbr = 0;
				for(Couple<Observation,Integer> o : lobs.get(i).getRight()) {
					switch (o.getLeft()) {
					case DIAMOND:case GOLD:
						if (o.getLeft().equals(Observation.GOLD))
							qtyG_nbr += o.getRight();
						else
							qtyD_nbr += o.getRight();
						break;
					default:
						break;
					}
				}
				myMap.addEdges(myPosition, nbr);
				myMap.addNode(nbr, qtyG_nbr, qtyD_nbr, false);
			}
			
			myMap.setNewGoal(goalType);
		}
		
		
	}

	/**
	 * The action taken by the agent that sent a ping.</br> 
	 * He knows he has to change goals (if he didn't have to, he wouldn't have come to this Behaviour)
	 */
	private void actionSender() {
		myMap.setNewGoal(goalType);
	}

	
	private void actionReceiver() {
		String previousGoal = myMap.getGoal();
		myMap.setNewGoal(goalType);
		if(myMap.getGoal().equals(previousGoal)) {
			((MyAbstractAgent)this.myAgent).setYouMove(true);
		}
	}

	@Override
	public int onEnd() {
		if((behaviourType == INIT) && reached)
			return 1;
		return 2;
	}
	
	@Override
	public boolean done() {
		return true;
	}

}
