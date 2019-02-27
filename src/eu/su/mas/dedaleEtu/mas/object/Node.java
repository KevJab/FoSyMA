package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3725959494229620330L;
	public static final int VOID = 0;
	public static final int GOLD = 1;
	public static final int DIAMOND = 2;
	
	private String name;
	private int nature;
	private int quantity;
	private boolean visited;
	private List<String> neighbours;
	
	public Node(String n, int na, int q) {
		name = n;
		nature = na;
		quantity = q;
		visited  = false;
		neighbours = new ArrayList<>();
	}
	
	public Node(Node other) {
		name = other.getName();
		nature = other.getNature();
		quantity = other.getQuantity();
		visited = other.isVisited();
		neighbours = other.getNeighbours();
	}
	
	public String getName() {
		return name;
	}
	
	public int getNature() {
		return nature;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public boolean isVisited() {
		return visited;
	}
	
	public List<String> getNeighbours(){
		return neighbours;
	}
	
	public void setNature(int n) {
		nature = n;
	}
	
	public void setQuantity(int q) {
		quantity = q;
	}
	
	public void visit() {
		visited = true;
	}
	
	public void addNeighbour(String name) {
		neighbours.add(name);
	}
	
	/**
	 * Does nothing for now; will have to be implemented (maybe with adding a latest-update time in the node?)
	 * @param other the same node, in another agent's graph
	 */
	public void update(Node other) {
		//TODO who's right? me or other?
	}

}
