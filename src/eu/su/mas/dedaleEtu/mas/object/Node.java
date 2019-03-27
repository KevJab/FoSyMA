package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Node implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3725959494229620330L;
	
	private String name;
	private int quantityG;
	private int quantityD;
	private boolean visited;
	private List<String> neighbours;
	private Date lastUpdateDate;
	
	public Node(String n, int qtyGold, int qtyDiam, List<String> nbrs, boolean visit, Date date) {
		name = n;
		quantityG = qtyGold;
		quantityD = qtyDiam;
		visited  = visit;
		neighbours = nbrs;
		lastUpdateDate = date;
	}
	
	public Node(String n, int qtyGold, int qtyDiam, String nbr) {
		name = n;
		quantityG = qtyGold;
		quantityD = qtyDiam;
		visited  = false;
		neighbours = new ArrayList<>();
		neighbours.add(nbr);
		lastUpdateDate = new Date();
	}
	
	public Node(Node other) {
		name = other.name;
		quantityG = other.quantityG;
		quantityD = other.quantityD;
		visited = other.visited;
		neighbours = other.neighbours;
		lastUpdateDate = other.lastUpdateDate;
	}
	
	public String getName() {
		return name;
	}
	
	public int getQuantityG() {
		return quantityG;
	}
	
	public int getQuantityD() {
		return quantityD;
	}

	public boolean isVisited() {
		return visited;
	}
	
	public List<String> getNeighbours(){
		return neighbours;
	}
	
	private void setQuantityG(int q) {
		quantityG = q;
	}
	
	private void setQuantityD(int q) {
		quantityD = q;
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
		if(other.lastUpdateDate.after(this.lastUpdateDate)){
			quantityG = other.quantityG;
			quantityD = other.quantityD;
			visited = other.visited;
			neighbours = other.neighbours;
			lastUpdateDate = other.lastUpdateDate;
		}
	}

}
