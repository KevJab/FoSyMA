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
	
	public void addAllNeighbours(Node n) {
		assert this.hasNode(n) != null: "ce noeud n'existe pas dans le graphe!";
		
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
	 * @param node the node we want
	 * @return either the same node, but in our graph, or <b>null</b>
	 */
	public Node hasNode(Node node) {
		for(Node n : nodes) {
			if(n.getName() == node.getName())
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
			myNode = this.hasNode(n);
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
	
}
