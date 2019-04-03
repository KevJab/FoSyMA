package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import eu.su.mas.dedaleEtu.mas.object.Node;
import jade.core.behaviours.SimpleBehaviour;

/**
 * A WalkBehaviour takes an <code>int goalType</code> as it is a generic method for all types of A* paths to a certain type of goal </br>
 * The used goalTypes so far are: 
 * <ul>	<li><b>OPEN</b> for open nodes (explorer in exploring phase)</li>
 * 		<li><b>TREASURE</b> for treasure-holding nodes (explorer in lockpicking phase)</li>
 * 		<li><b>GOLD</b>/<b>DIAMOND</b> for nodes containing gold/diamond (collectors, according to their TreasureType)</li>
 * 		<li><b>SILO</b> for the tile on which the silo has last been seen (collectors with full backpack)</li>
 * </ul>
 * 
 * WalkBehaviour's onEnd will return 2 as long as the goal isn't reached; once it is, it returns 1, allowing the WalkToGoalBehaviour to reach his final state
 * 
 * @author Kevin Jabbour
 *
 */
public class WalkBehaviour extends SimpleBehaviour {
	
	private static final long serialVersionUID = 2584222096382769904L;
	private boolean hasMoved = false;
	int type;
	
	public WalkBehaviour(MyAbstractAgent agent, int type) {
		super(agent);
		this.type = type;
	}

	@Override
	public void action() {
		Graphe myMap = ((MyAbstractAgent)this.myAgent).getMyMap();
		// if at the last call of this behaviour, the agent had moved, updates the shortestPath in the graph
		if(hasMoved) {
			myMap.move();
		}
		hasMoved = false;	//resets for the next turn
		
		//Example to retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		System.out.println(this.myAgent.getLocalName()+" -- myCurrentPosition is: "+myPosition);
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
			
			//list of observations associated to the currentPosition
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
			
			int qtyD = 0;
			int qtyG = 0;
			Boolean b=false;
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
				
				myMap.addNode(nbr, qtyG_nbr, qtyD_nbr, new ArrayList<>(), false);
			}
			myMap.addNode(myPosition, qtyG, qtyD, nbrs, true);
			myMap.setMyPos(new Node(myPosition, qtyG, qtyD, nbrs, true));
			((MyAbstractAgent)this.myAgent).getMyMap().setNewGoal(type);
			
			
			
			//The move action (if any) should be the last action of your behaviour
			hasMoved = ((AbstractDedaleAgent)this.myAgent).moveTo(myMap.getNextTile());
		}
	}

	@Override
	public boolean done() {
		return true;
	}

	
	@Override
	public int onEnd() {
		return (((MyAbstractAgent)this.myAgent).getMyMap().isReached()) ? 1 : 2;
	}
}
