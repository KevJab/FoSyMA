package eu.su.mas.dedaleEtu.mas.agents;

import java.util.HashSet;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public abstract class MyAbstractAgent extends AbstractDedaleAgent {
	
	private static int NB_AGENTS = 0;

	protected String type;
	protected AID interlocuteur = null;
	protected String[] otherInfo = new String[2];
	protected Graphe myMap = new Graphe();
	protected boolean youMove = false;		// boolean value to send to another agent, indicating whether he moves or not
	protected Set<String> commonKnowledge = new HashSet<>();	// a list of all AIDs (in String form) of agents I am certain know the whole map
	
	protected String goalNode = null;
	protected String siloNode = null;
	public String siloName = null;
	protected int distanceToSilo = Integer.MAX_VALUE;
	protected AID leader = null;
	
	protected void setup() {
		super.setup();
		
		NB_AGENTS++;
		commonKnowledge.add(this.getAID().toString());	//even though it is not true at initialization, it will be true (and needed) when commonKnowledge will be used
	}
	
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
	
	public void addKnowledge(AID other) {
		commonKnowledge.add(this.getAID().toString());
		commonKnowledge.add(other.toString());
	}
	
	public void mergeKnowledge(Set<String> other) {
		commonKnowledge.addAll(other);
	}
	
	public boolean isCommonKnowledge() {
		return commonKnowledge.size() == NB_AGENTS;
	}
	
	public String getMyKnowledge() {
		String res = "";
		
		for(String agent : commonKnowledge)
			res += agent + " ";
		
		return res;
	}
	
	public void setSiloNode(String node) {
		siloNode = node;
		myMap.setSiloNode(node);
	}
	
	public void setGoalNode(Couple<String, AID> goal) {
		goalNode = goal.getLeft();
		leader = goal.getRight();
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
	
	public abstract void action();
	
}
