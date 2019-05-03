package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.su.mas.dedale.env.Observation;

public class Node implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3725959494229620330L;
	
	private String name;
	private int quantityGold;
	private boolean lockedGold;
	private int strengthNeededGold;
	private int lockpickingNeededGold;
	
	private int quantityDiam;
	private boolean lockedDiam;
	private int strengthNeededDiam;
	private int lockpickingNeededDiam;
	
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
	public Node(String n, int qtyGold, int qtyDiam,  List<String> nbrs, boolean visit) {
		name = n;
		quantityGold = qtyGold;
		quantityDiam = qtyDiam;
		visited  = visit;
		neighbours = nbrs;
		lastUpdateDate = new Date();
	}
	
	/**
	 * Creates a new node, with the same characteristics as <code>other</code>
	 * @param other the node to copy
	 */
	public Node(Node other) {
		name = other.name;
		quantityGold = other.quantityGold;
		quantityDiam = other.quantityDiam;
		lockedDiam = other.lockedDiam;
		lockedGold = other.lockedGold;
		visited = other.visited;
		lastUpdateDate = other.lastUpdateDate;
		
		neighbours = new ArrayList<>();
		for(String nbr : other.neighbours)
			neighbours.add(nbr);
	}
	
	@Override
	public boolean equals(Object other) {
		
		// If the object is compared with itself then return true   
        if (other == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(other instanceof Node)) { 
            return false; 
        } 
        
        Node n = (Node) other;
		
		return this.name.equals(n.name);
	}
	
	// needed for use in HashMap
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public String toString() {
		return this.name/* + ": (G: "+quantityGold+"; D: "+quantityDiam+")"*/;
	}
	
	/* --------------------------
	 *   Getters 
	 * --------------------------*/
	public String getName() {
		return name;
	}
	
	public int getquantityGold() {
		return quantityGold;
	}
	
	public int getquantityDiam() {
		return quantityDiam;
	}
	
	public int getStrengthNeededGold() {
		return strengthNeededGold;
	}

	public int getStrengthNeededDiam() {
		return strengthNeededDiam;
	}

	public boolean isLocked(Observation treasureType) {
		switch(treasureType) {
		case GOLD:
			return lockedGold;
		case DIAMOND:
			return lockedDiam;
		default:
			return true;
		}
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
	
	public void addNeighbours(List<String> nbrs) {
		this.neighbours = nbrs;
		update();
	}
	
	public void addNeighbour(String nbr) {
		if(this.neighbours == null)
			this.neighbours = new ArrayList<>();
		if(!this.neighbours.contains(nbr))
			this.neighbours.add(nbr);
	}
	
	/**
	 * The agent has visited this Node; sets <code>visited</code> to <b>true</b> and updates <code>lastUpdateDate</code>
	 */
	public void visit() {
		visited = true;
		update();
	}
	
	/**
	 * Updates the node after the agent picked some treasure (gold or diamond)
	 * @param treasureType the treasure type the agent picked
	 * @param qty the amount of treasure the agent was able to pick (return value from Agent.pick() )
	 */
	public void pick(Observation treasureType, int qty) {
		switch (treasureType) {
		case GOLD:
			quantityGold -= qty;
			update();
			break;
		case DIAMOND:
			quantityDiam -= qty;
			update();
		default:
			break;
		}
	}
	
	/** 
	 * The agent has unlocked a treasure on this tile
	 */
	public void unlock(Observation treasureType) {
		switch (treasureType) {
		case GOLD:
			lockedGold = false;
			update();
			break;
		case DIAMOND:
			lockedDiam = false;
			update();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 
	 */
	
	public void unlockDiam() {
		
	}
	
	/**
	 * Updates this node with the data of <code>other</code>, if it is more recent
	 * @param other the same node, in another agent's graph
	 */
	public void update(Node other) {
		if(other.lastUpdateDate.after(this.lastUpdateDate)){
			quantityGold = other.quantityGold;
			quantityDiam = other.quantityDiam;
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

	public boolean canOpen(boolean isDiam, int strength, int lockpick) {
		if (isDiam)
			return (strength >= strengthNeededDiam) && (lockpick >= lockpickingNeededDiam);
		return (strength >= strengthNeededGold) && (lockpick >= lockpickingNeededGold);
	}
}
