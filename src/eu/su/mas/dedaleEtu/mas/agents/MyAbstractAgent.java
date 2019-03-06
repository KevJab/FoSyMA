package eu.su.mas.dedaleEtu.mas.agents;

import java.util.HashMap;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.data.MapInformation;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public abstract class MyAbstractAgent extends AbstractDedaleAgent {

	protected String type;
	protected AID interlocuteur = null;
	protected Graphe myMap;
	protected HashMap<String, MapInformation> h = new HashMap<>();
	
	public String getType() {
		return type;
	}
	
	public HashMap<String, MapInformation> getHashMap(){
		return h;
	}
	
	public void setHashMap(HashMap<String, MapInformation> h) {
		this.h = h;
	}
	
	public void setInterlocuteur(AID ag) {
		interlocuteur = ag;
	}
	
	public AID getInterlocuteur() {
		return interlocuteur;
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
	
	public boolean isCompleteMap() {
		return myMap.isComplete();
	}

}
