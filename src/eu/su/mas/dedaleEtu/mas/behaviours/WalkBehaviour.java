package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.behaviours.SimpleBehaviour;

/**
 * A WalkBehaviour takes an <code>int goalType</code> as it is a generic method for all types of A* paths to a certain type of goal </br>
 * The used goalTypes so far are: 
 * <ul>	<li><b>OPEN</b> for open nodes (explorer in exploring phase)</li>
 * 		<li><b>TREASURE</b> for treasure-holding nodes (explorer in lockpicking phase)</li>
 * 		<li><b>GOLD</b>/<b>DIAMOND</b> for nodes containing gold/diamond (collectors, according to their TreasureType)</li>
 * 		<li><b>SILO</b> for the tile on which the silo has last been seen (collectors with full backpack)</li>
 * 		<li><b>GOAL</b> for a specific goal node</li>
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
		
		((MyAbstractAgent) this.myAgent).setYouMove(false); // the communication has ended; need to reset
		
		hasMoved = false;	// used to know if we can actually update the Graphe
		
		//The move action (if any) should be the last action of your behaviour
		hasMoved = ((AbstractDedaleAgent)this.myAgent).moveTo(myMap.getNextTile());
	}

	@Override
	public boolean done() {
		return true;
	}

	
	@Override
	public int onEnd() {
		((MyAbstractAgent)this.myAgent).getMyMap().move(hasMoved);
		return (hasMoved) ? 1 : 2;
	}
}
