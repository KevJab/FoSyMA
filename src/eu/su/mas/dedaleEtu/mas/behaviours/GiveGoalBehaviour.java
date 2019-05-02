package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import eu.su.mas.dedaleEtu.mas.object.Node;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class GiveGoalBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -2232531484298905354L;
	private Graphe myMap;
	private List<Node> diamNode = new ArrayList<>();
	private List<Node> goldNode = new ArrayList<>();
	private List<HashMap<Node, Integer>> distNodeDiam = new ArrayList<>();
	private List<HashMap<Node, Integer>> distNodeGold = new ArrayList<>();
	private Node center = null;
	private HashMap<AID, Couple<Node, AID>> task = new HashMap<>();
	
	public GiveGoalBehaviour(final Agent myagent) {
		
		super(myagent);
		this.myMap = ((MyAbstractAgent) this.myAgent).getMyMap();
		
	}
	
	@Override
	public void action() {
		// recuperer la position des tresors 
		for(Node n : myMap.getNodes()) {
			if(n.getquantityDiam() > 0)
				diamNode.add(n);
			if(n.getquantityGold() > 0)
				goldNode.add(n);
		}
		
		// centre des trésors 
		if(diamNode.size() > 1) {
			for(Node n : diamNode) {  // parcours en largeur depuis tous les tresors
				List<Node> toVisit = new ArrayList<>();			// a list of all open nodes 
				HashMap<Node, Integer> dist = new HashMap<>();		// for each potential goal, its distance to myPos (Manhattan)
				List<Node> dejavu = new ArrayList<>();				// a list of closed nodes
				dejavu.add(n);
				int current_dist = 0;
				toVisit.add(n);
				dist.put(n, current_dist);
				while(toVisit.size() > 0) {
					//System.out.println("Iteration; size = "+toVisit.size());
					Node current = toVisit.get(0);
					toVisit.remove(current);
					current_dist++;
					for(Node node : myMap.getNeighbourNodes(current)) {
						//System.out.println("neighbours");
							dist.put(node, current_dist);
						}
						
						if(!dejavu.contains(n)) {
							//System.out.println("adding said neighbour");
							toVisit.add(n);
							dejavu.add(n);
						}
					}
				distNodeDiam.add(dist);
				}
			for(Node n : distNodeDiam.get(0).keySet()) {  // on cherche un noeud a egal distance de tous les tresors
				boolean b = true;
				int c = distNodeDiam.get(0).get(n);
				for(HashMap<Node, Integer> h : distNodeDiam) {
					b = h.get(n) == c;
				}
				if(b == true) {
					center = n;
					break;
				}
			}
		}
		else if(diamNode.size() == 1){
			center = diamNode.get(0);
		}

		
		if(goldNode.size() > 1) {
			for(Node n : goldNode) {
				List<Node> toVisit = new ArrayList<>();			// a list of all open nodes 
				HashMap<Node, Integer> dist = new HashMap<>();		// for each potential goal, its distance to myPos (Manhattan)
				List<Node> dejavu = new ArrayList<>();				// a list of closed nodes
				dejavu.add(n);
				int current_dist = 0;
				toVisit.add(n);
				dist.put(n, current_dist);
				while(toVisit.size() > 0) {
					//System.out.println("Iteration; size = "+toVisit.size());
					Node current = toVisit.get(0);
					toVisit.remove(current);
					current_dist++;
					for(Node node : myMap.getNeighbourNodes(current)) {
						//System.out.println("neighbours");
							dist.put(node, current_dist);
						}
						
						if(!dejavu.contains(n)) {
							//System.out.println("adding said neighbour");
							toVisit.add(n);
							dejavu.add(n);
						}
					}
				distNodeGold.add(dist);
				}	
			for(Node n : distNodeGold.get(0).keySet()) {  // on cherche un noeud a egal distance de tous les tresors
				boolean b = true;
				int c = distNodeGold.get(0).get(n);
				for(HashMap<Node, Integer> h : distNodeGold) {
					b = h.get(n) == c;
				}
				if(b == true) {
					center = n;
					break;
				}
			}
		}
		else if(goldNode.size() == 1){
			center = goldNode.get(0);
		}
		if(center == null) {
			//faire QQchose c'est un cas tres particulier
		}
		else {
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription () ;
			sd.setType( "ANY" ); // type of the agent
			dfd.addServices(sd) ;
			DFAgentDescription[] result;
			try {
			    result = DFService.search( myAgent , dfd);
			// I'm not sending a message to myself
			    for (DFAgentDescription dfad : result){
			        System.out.println("I am "+ myAgent.getAID() + "and the DF tells me about " + dfad.getName());
			        if(!dfad.getName().equals(myAgent.getAID()))
			            task.put(dfad.getName(), null);
			    }
			} catch (FIPAException e) {
			    e.printStackTrace();
			}    //You get the list of all the agents (AID) of said type
			
			/*
			 * 
			 * LA STRATEGIE
			 * 
			 * 
			 * 1) PRENDRE UN DES NOEUDS AVEC UN COFFRE (GOLD / DIA) 
			 * 2) METTRE LA VALUE DE CE COFFRE A 0 POUR LE SILLOT UNIQUEMENT
			 * 3) PRENDRE 1 COLLECTOR + X EXPLORER POUR DEBLOQUER LE COFFRE ET LE DIRE DE VENIR AU CENTRE APRES (this.center)
			 * 4) SI IL RESTE DES AGENTS A PORTEE RECOMMENCER 1)
			 * 
			 * 
			 * */
		}
	}
}
