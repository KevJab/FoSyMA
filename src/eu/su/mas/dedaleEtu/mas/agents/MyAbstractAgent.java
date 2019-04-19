package eu.su.mas.dedaleEtu.mas.agents;

import java.util.HashMap;
import java.util.Map;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.data.MapInformation;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import eu.su.mas.dedaleEtu.mas.object.Node;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public abstract class MyAbstractAgent extends AbstractDedaleAgent {

	protected String type;
	protected AID interlocuteur = null;
	protected String[] otherInfo = new String[2];
	protected Graphe myMap = new Graphe();
	protected boolean youMove = false;		// boolean value to send to another agent, indicating whether he moves or not
	protected Map<AID, Boolean> commonKnowledge;
	//protected HashMap<String, Graphe> allAgentsInfo = new HashMap<>();
	
	/**
	 * Returns the agent type
	 * @return "COLLECTOR", "EXPLORER" or "SILO"
	 */
	public String getType() {
		return type;
	}
	
	public Graphe getMyMap() {
		return myMap;
	}
	
	/*public HashMap<String, Graphe> getAllAgentsInfo(){
		return allAgentsInfo;
	}*/
	
	public void setInterlocuteur(AID ag) {
		interlocuteur = ag;
	}
	
	public AID getInterlocuteur() {
		return interlocuteur;
	}
	
	public void setOtherInfo(String other_wish_node, String other_cur_node) {
		otherInfo[0] = other_wish_node;
		otherInfo[1] = other_cur_node;
		myMap.forbidNode(other_wish_node);
		myMap.forbidNode(other_cur_node);
	}
	
	/**
	 * returns the String array <i>info</i> for the other agent (the one with <code>AID</code> <b>interlocuteur</b>), containing: 
	 * <ul><li>at index 0, where the other agent wants to go next</li>
	 * <li>at index 1, where the other agent is</li></ul>
	 * @return
	 */
	public String[] getOtherInfo() {
		return otherInfo;
	}
	
	public boolean doYouMove() {
		return youMove;
	}
	
	public void setYouMove(boolean value) {
		youMove = value;
	}
	
	public void setCommonKnowledge() {
		if(!commonKnowledge.isEmpty())
			return;
		
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription () ;
		sd.setType( "ANY" ); // type of the agent
		dfd.addServices(sd) ;
		DFAgentDescription[] result;
		try {
			result = DFService.search(this, dfd);
			//You get the list of all the agents (AID) of said type
			
			for (DFAgentDescription dfad : result){
				if(!dfad.getName().equals(this.getAID()))
					commonKnowledge.put(dfad.getName(), false);
			}
			
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isCommonKnowledge() {
		for (AID aid: commonKnowledge.keySet()) {
			if(!commonKnowledge.get(aid)) {
				return false;
			}
		}
		return true;
	}
	
	public void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID()); // The agent AID
		
		ServiceDescription sd = new ServiceDescription () ;
		sd.setType( type ); // You have to give a name to each service your agent offers
		sd.setName(getLocalName());//(local)name of the agent
		dfd.addServices(sd) ;
		//Register the service
		
		ServiceDescription sd2 = new ServiceDescription () ;
		sd2.setType( "ANY" ); // You have to give a name to each service your agent offers
		sd2.setName(getLocalName());//(local)name of the agent
		dfd.addServices(sd2) ;
		//Register the service
		
		try {
			DFService.register( this , dfd ) ;
		} catch (FIPAException fe) {
			fe.printStackTrace() ; 
		}
	}
	
}
