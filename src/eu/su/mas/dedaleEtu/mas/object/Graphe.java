package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Graphe implements Serializable{

	private static final long serialVersionUID = -4559546512611281312L;
	private List<Node> nodes;
	private HashMap<String, String> edges;
	
	public Graphe() {
		nodes = new ArrayList<>();
		edges = new HashMap<>();
	}
	
	public Graphe(List<Node> n, HashMap<String, String> e) {
		nodes = n;
		edges = e;
	}
	
	public List<Node> getNodes(){
		return nodes;
	}
	
	public HashMap<String, String> getEdges(){
		return edges;
	}
	
	public void addNode(Node n) {
		nodes.add(new Node(n));
	}
	
	public void addNode(String id,int type, int qty){
		if (this.hasNode(id) == null) {
			Node n = new Node(id, type, qty);
			nodes.add(n);
		}
	}
	
	public void addAllNeighbours(Node n) {
		assert this.hasNode(n.getName()) != null: "ce noeud n'existe pas dans le graphe!";
		
		for(String nbr : n.getNeighbours()) {
			this.addEdges(n.getName(), nbr);
		}
	}
	
	public void addEdges(String mainNode, String neighbour) {
		edges.put(mainNode, neighbour);
		//edges.put(neighbour, mainNode);
	}
	
	/**
	 * Goes through the <code>nodes</code> list and returns the same-named node if it exists, or <b>null</b>.
	 * @param nodeId the id of the node we want
	 * @return either the instance of nodeId in this graph, or <b>null</b>
	 */
	public Node hasNode(String nodeId) {
		for(Node n : nodes) {
			if(n.getName() == nodeId)
				return n;
		}
		return null;
	}
	
	/**
	 * When encountering another agent, merges both graphs together
	 * @param other the other agent's graph
	 */
	public void merge(Graphe other) {
		Node myNode;
		for(Node n : other.getNodes()) {
			// my instance of the same node
			myNode = this.hasNode(n.getName());
			if(myNode == null) {
				this.addNode(n);
			} else {
				// if the other has visited node n, I won't have to visit it myself
				if( n.isVisited() ) {
					if(myNode != null) {
						myNode.visit();
					}
				}
				this.addAllNeighbours(n);
				myNode.update(n);
			}
		}
			
	}
	
	/** 
	 * @return <code>true</code> if graph has no more open nodes, <code>false</code> otherwise
	 */
	public boolean isComplete() {
		for(Node n : nodes) {
			if (!n.isVisited())
				return false;
		}
		return true;
	}
	
}
