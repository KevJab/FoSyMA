package eu.su.mas.dedaleEtu.mas.behaviours;

import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class OpenTreasureBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 3728788416559808886L;
	private int endVal;

	/**
	 * A behaviour in which the agent tries to unlock the treasure on the current tile, if possible.</br>
	 * <code>public int onEnd()</code> returns 1 if the agent successfully picked the treasure, 2 otherwise
	 */
	public OpenTreasureBehaviour(final Agent myagent) {
		super(myagent);
	}
	
	@Override
	public void action() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent; 
		boolean isUnlocked = myagent.openLock(myagent.getMyTreasureType());
		
		if(isUnlocked) {
			Graphe myMap = myagent.getMyMap();
			
			int qtyPicked = myagent.pick();
			myMap.unlock(myagent.getMyTreasureType());
			myMap.pick(myagent.getMyTreasureType(), qtyPicked);
			
			endVal = 1;
		} else {
			// call for help!
			endVal = 2;
		}
	}

	@Override
	public int onEnd() {
		return endVal;
	}
}
