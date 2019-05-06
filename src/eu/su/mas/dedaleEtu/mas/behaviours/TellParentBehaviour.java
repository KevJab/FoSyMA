package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import dataStructures.tuple.Couple;
import dataStructures.tuple.Tuple4;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class TellParentBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 7759650796997447623L;

	public TellParentBehaviour(Agent a) {
		super(a);
	}

	@Override
	public void action() {
		MyAbstractAgent myagent = (MyAbstractAgent) this.myAgent;
		
		final ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setSender(myagent.getAID());
		msg.addReceiver(new AID(myagent.getInterlocuteur().getLocalName(), AID.ISLOCALNAME));
		
		try {
			int myStrength = 0;
			int myLockpicking = 0;
			for (Couple<Observation, Integer> exp : myagent.getMyExpertise()) {
				if (exp.getLeft() == Observation.STRENGH)
					myStrength = exp.getRight();
				if (exp.getLeft() == Observation.LOCKPICKING)
					myLockpicking = exp.getRight();
			}
			Map<AID, Tuple4<Observation, String, Integer, Integer>> info = myagent.agent_info;
			info.put(myagent.getAID(), new Tuple4<Observation, String, Integer, Integer>(myagent.getMyTreasureType(), myagent.getType(), myStrength, myLockpicking));
			msg.setContentObject((Serializable) info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myagent.sendMessage(msg);
	}

}
