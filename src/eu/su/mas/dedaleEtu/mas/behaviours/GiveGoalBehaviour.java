package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataStructures.tuple.Couple;
import dataStructures.tuple.Tuple4;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import eu.su.mas.dedaleEtu.mas.agents.MySiloAgent;
import eu.su.mas.dedaleEtu.mas.object.Graphe;
import eu.su.mas.dedaleEtu.mas.object.Node;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class GiveGoalBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -2232531484298905354L;
	private Graphe myMap;
	private List<Node> diamNode = new ArrayList<>();
	private List<Node> goldNode = new ArrayList<>();
	private List<HashMap<Node, Integer>> distNodeDiam = new ArrayList<>();
	private List<HashMap<Node, Integer>> distNodeGold = new ArrayList<>();
	private Node center = null;
	private Map<AID, Couple<String, AID>> task = new HashMap<>();
	private boolean hasToMove;
	
	public GiveGoalBehaviour(final Agent myagent, boolean hasToMove) {
		
		super(myagent);
		this.myMap = ((MyAbstractAgent) this.myAgent).getMyMap();
		this.hasToMove = hasToMove;
		
	}
	
	@Override
	public void action() {
		// getting all treasures' position
		for(Node n : myMap.getNodes()) {
			if(n.getquantityDiam() > 0) {
				diamNode.add(n);
			}
			if(n.getquantityGold() > 0) {
				goldNode.add(n);
			}
		}
		//sorting the treasures in a descending order of their value
		Collections.sort(diamNode, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o2.getquantityDiam() - o1.getquantityDiam();
			}
		});
		Collections.sort(goldNode, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o2.getquantityGold() - o1.getquantityGold();
			}
		});
		
		if(hasToMove) {
			// center of the treasures (Silo's optimal position)
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
			} else if(diamNode.size() == 1){
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
				// very special case; do sthg
			}
		}

		
		
		MySiloAgent myagent = (MySiloAgent) this.myAgent; // since this behaviour can only be accessed by a Silo agent, there's no issue
		Map<AID, Tuple4<Observation, String, Integer, Integer>> info = myagent.agent_info;
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_REF);
		msg.setReplyWith(center.getName());
		
		List<AID> available_explorers = new ArrayList<>();
		Map<AID, Couple<Integer, Integer>> diam_collectors = new HashMap<>();
		Map<AID, Couple<Integer, Integer>> gold_collectors = new HashMap<>();
		for (AID agent_id : info.keySet()) {
			if (info.get(agent_id).get_2().equals("Explorer"))
				available_explorers.add(agent_id);
			else if ((info.get(agent_id).get_2().equals("Collector")) && (info.get(agent_id).get_1() == Observation.DIAMOND))
				diam_collectors.put(agent_id, new Couple<Integer, Integer>(info.get(agent_id).get_3(), info.get(agent_id).get_4()));
			else
				gold_collectors.put(agent_id, new Couple<Integer, Integer>(info.get(agent_id).get_3(), info.get(agent_id).get_4()));
		}
		
		Node goalNode;
		
		// give a mission to all diamond collectors, eventually also to a few explorers
		for (AID agent_id : diam_collectors.keySet()) {
			msg.addReceiver(agent_id);
			
			if(diamNode.isEmpty()) 
				break;
			goalNode = diamNode.remove(0);
			task.put(agent_id, new Couple<String, AID>(goalNode.getName(), null));
			
			int str = info.get(agent_id).get_3();
			int lck = info.get(agent_id).get_4();
			
			while(!goalNode.canOpen(true, str, lck)) {
				AID explo = available_explorers.remove(0);
				task.put(explo, new Couple<String, AID>(goalNode.getName(), agent_id));
				
				str += info.get(explo).get_3();
				lck += info.get(explo).get_4();
			}
		}
		
		// give a mission to all gold collectors, eventually also to a few explorers
		for (AID agent_id : gold_collectors.keySet()) {
			if (goldNode.isEmpty())
				break;
			goalNode = goldNode.remove(0);
			task.put(agent_id, new Couple<String, AID>(goalNode.getName(), null));
			
			int str = info.get(agent_id).get_3();
			int lck = info.get(agent_id).get_4();
			
			while(!goalNode.canOpen(false, str, lck)) {
				AID explo = available_explorers.remove(0);
				task.put(explo, new Couple<String, AID>(goalNode.getName(), agent_id));
				
				str += info.get(explo).get_3();
				lck += info.get(explo).get_4();
			}
		}
		
		// if there's a goal and some explorers left, all of them will go open the next chest
		goalNode = (!diamNode.isEmpty()) ? diamNode.get(0) : ((!goldNode.isEmpty()) ? goldNode.get(0) : null);
		if ((!available_explorers.isEmpty()) && (goalNode != null)) {
			AID explorer_leader = available_explorers.remove(0);
			
			task.put(explorer_leader, new Couple<String, AID>(goalNode.getName(), null));
			
			for (AID explo : available_explorers) {
				task.put(explo, new Couple<String, AID>(goalNode.getName(), explorer_leader));
			}
		}
		
		try {
			msg.setContentObject((Serializable) task);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
