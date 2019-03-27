package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.MyAbstractAgent;
import jade.core.Agent;

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
	
	/**
	 * General Node constructor, with all parameters
	 * @param n the name of the node
	 * @param qtyGold its quantity of gold
	 * @param qtyDiam its quantity of diamond
	 * @param nbrs the list of neighbours
	 * @param visit whether this node has been visited or not
	 * @param date its latest update Date
	 */
	/*public Node(String n, int qtyGold, int qtyDiam, List<String> nbrs, boolean visit, Date date) {
		name = n;
		quantityG = qtyGold;
		quantityD = qtyDiam;
		visited  = visit;
		neighbours = nbrs;
		lastUpdateDate = date;
	}*/
	
	/**
	 * Creates a new node, with the same characteristics as <code>other</code>
	 * @param other the node to copy
	 */
	public Node(Node other) {
		name = other.name;
		quantityG = other.quantityG;
		quantityD = other.quantityD;
		visited = other.visited;
		lastUpdateDate = other.lastUpdateDate;
		
		neighbours = new ArrayList<>();
		for(String nbr : other.neighbours)
			neighbours.add(nbr);
	}
	
	/* --------------------------
	 *   Getters 
	 * --------------------------*/
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
	
	
	
	/*-------------------------------
	 * Methods modifying the Node
	 * (always updating the date)
	 *-------------------------------*/
	
	/**
	 * Updates the node after the agent picked some gold
	 * @param qty the amount of gold the agent was able to pick (return value from Agent.pick() )
	 */
	public void pickGold(int qty) {
		quantityG -= qty;
		update();
	}
	
	/**
	 * Updates the node after the agent picked some diamonds
	 * @param qty the amount of diamonds the agent was able to pick (return value from Agent.pick() )
	 */
	public void pickDiam(int qty) {
		quantityD -= qty;
		update();
	}
	
	/**
	 * The agent has visited this Node; sets <code>visited</code> to <b>true</b> and updates <code>lastUpdateDate</code>
	 */
	public void visit() {
		visited = true;
		update();
	}
	
	/**
	 * Updates this node with the data of <code>other</code>, if it is more recent
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
	 /**
	  * Updates <code>lastUpdateDate</code>
	  */
	private void update() {
		lastUpdateDate = new Date();
	}

}
