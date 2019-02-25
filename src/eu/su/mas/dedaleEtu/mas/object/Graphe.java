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
		nodes.add(n);
	}
	
	public void addEdges(String mainNode, String neighbourgh) {
		edges.put(mainNode, neighbourgh);
	}
	
}
