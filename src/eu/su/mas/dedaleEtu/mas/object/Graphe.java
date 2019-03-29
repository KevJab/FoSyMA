package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.su.mas.dedaleEtu.mas.behaviours.WalkToGoalBehaviour;

/**
 * A <code>Graphe</code> has 4 arguments: 
 * <ul><li> <b>nodes</b> which is a <code>List</code> of all <code>Nodes</code> the agent currently knows</li>
 *     
 *     <li> <b>edges</b> which is a <code>HashMap</code> containing the names of the <code>Nodes</code> it is defined by. </br>
 *     Note that if edge (x,y) is in <i>edges</i>, then (y,x) is in it too. This may be heavier, but it is easier to find all edges linked to a certain <code>Node</code>.</li>
 * 	   
 *     <li> <b>goal</b> which is a <code>String</code> representing the name of the closest still open node.</br>
 *     It is updated either when the node named <i>goal</i> has been visited, or if someone we're talking to has visited it.</li>
 *     
 *     <li> <b>myPos</b> - this argument is technically not  needed, it just makes computation lighter as we don't have to look through the whole graph to find the node named <code>AbstractDedaleAgent.getCurrentPosition()</code>. </br>
 *     <i>myPos</i> will simply be the <code>Node</code> at which the agent's currently at.</li>
 * </ul>
 */

public class Graphe implements Serializable{

	private static final long serialVersionUID = -4559546512611281312L;
	private Set<Node> nodes;
	private HashMap<String, Set<String>> edges;
	
	// parameters needed to implement A-Star
	private Node myPos;
	private Node goalNode; 
	private List<Node> shortestPath;
	private boolean reached;
	private int goalType;
	private List<Node> openNodes;
	private List<Node> closedNodes;
	
	/* ----------------
	 *   Constructors
	 * ----------------*/
	
	public Graphe() {
		nodes = new HashSet<>();
		edges = new HashMap<>();
		goalNode = null;
		reached = false;
	}
	
	// Probably not needed; kept just in case
	/* public Graphe(List<Node> n, HashMap<String, String> e, String curPos) {
		nodes = n;
		edges = e;
		if(!getNewGoal(curPos))
			goal = null;
	}*/
	
	
	/* -------------
	 *    Getters
	 * -------------*/
	
	public Set<Node> getNodes(){
		return nodes;
	}
	
	public HashMap<String, Set<String>> getEdges(){
		return edges;
	}
	
	public boolean isReached() {
		return reached;
	}
	
	// This should only be used for testing and printing
	public String getGoal() {
		return goalNode.getName();
	}
	
	/* -----------------
	 *  Private methods
	 * -----------------*/
	
	/**
	 * Adds edges for all known neighbours of <code>Node</code> <i>n</i>. Asserts if <b>this</b> knows <i>n</i> beforehand.
	 * @param n the main node
	 */
	private void addAllNeighbours(Node n) {
		assert this.hasNode(n.getName()) != null: "ce noeud n'existe pas dans le graphe!";
		
		for(String nbr : n.getNeighbours()) {
			this.addEdges(n.getName(), nbr);
		}
	}
	
	/**
	 * Adds edges (<i>nodeX</i>, <i>nodeY</i>) and (<i>nodeY</i>, <i>nodeX</i>) to <b>edges</b>
	 * @param nodeX one end of the edge to add
	 * @param nodeY the other end of the edge
	 */
	private void addEdges(String nodeX, String nodeY) {
		Set<String> edgesX = edges.get(nodeX);
		if(edgesX == null)
			edgesX = new HashSet<>();
		edgesX.add(nodeY);
		
		Set<String> edgesY = edges.get(nodeY);
		if(edgesY == null)
			edgesY = new HashSet<>();
		edgesY.add(nodeX);
		
		edges.put(nodeX, edgesX);
		edges.put(nodeY, edgesY);
	}
	
	/**
	 * Goes through the <code>nodes</code> list and returns the same-named node if it exists, or <b>null</b>.
	 * @param nodeId the id of the node we want
	 * @return either the instance of nodeId in this graph, or <b>null</b>
	 */
	private Node hasNode(String nodeId) {
		for(Node n : nodes) {
			if(n.getName() == nodeId)
				return n;
		}
		return null;
	}
	
	private List<Node> getNeighbourNodes(Node n){
		List<Node> nbrs = new ArrayList<>();
		List<String> nbrName = n.getNeighbours();
		for(Node node : nodes) {
			if(nbrName.contains(node.getName()))
				nbrs.add(node);
		}
		
		return nbrs;
	}
	
	/* ----------------
	 *  Public methods
	 * ----------------*/
	
	
	/** adds Node <i>n</i> from another Graphe during merge
	 *  
	 * @param n
	 */
	public void addNode(Node n) {
		Node newNode = new Node(n);
		nodes.add(newNode);
		addAllNeighbours(newNode); 
	}
	
	public void addNode(String n, int qtyGold, int qtyDiam, List<String> nbrs, boolean visit) {
		Node newNode = new Node(n, qtyGold, qtyDiam, nbrs, visit);
		nodes.add(newNode);
		addAllNeighbours(newNode);
	}
	
	/**
	 * When encountering another <code>Agent</code>, merges both graphs together
	 * @param other the other agent's <code>Graphe</code>
	 */
	//TODO check if correct, I don't think so
	public void merge(Graphe other) {
		Node myNode;
		for(Node n : other.getNodes()) {
			// my instance of the same node
			myNode = this.hasNode(n.getName());
			if(myNode == null) {
				this.addNode(n);	//this will create a new node in my graph, identical to n, and create the edges to all of n's neighbours (even though I may not know them yet)
			} else {
				myNode.update(n);	//this will update the data in myNode, if n is more recent
				
				boolean condition;
				switch(goalType) {
				case WalkToGoalBehaviour.OPEN:
					condition = myNode.isVisited();
					break;
				case WalkToGoalBehaviour.TREASURE:
					condition = (myNode.getQuantityD() == 0) && (myNode.getQuantityG() == 0);
					break;
				case WalkToGoalBehaviour.GOLD:
					condition = myNode.getQuantityG() == 0;
					break;
				case WalkToGoalBehaviour.DIAMOND:
					condition = myNode.getQuantityD() == 0;
					break;
				//dunno what to do in case WalkToGoalBehaviour.SILO
				default:
					condition = false;
					break;
				}
				
				if ((condition)&&(myNode.equals(goalNode))) { // if the goal has been visited by the other agent
					setNewGoal(goalType);
				}
			}
		}
	}
	
	/**
	 * Chooses a new goal node, being always the closest unvisited node to my current position (A* algorithm)
	 * @return <code>true</code> if a new open node has been selected as the goal, <code>false</code> if the graph is complete 
	 */
	public void setNewGoal(int goalType) {
		if(nodes.isEmpty())	// if I have nowhere to go, I can't do anything
			return;
		
		
		reached = false;
		this.goalType = goalType;
		this.openNodes = this.getNeighbourNodes(myPos);
		this.closedNodes = new ArrayList<>();
		
		List<Node> potentialGoals = new ArrayList<>();
		for (Node n : nodes) {
			if((goalType == WalkToGoalBehaviour.OPEN) && (!n.isVisited()))
					potentialGoals.add(n);
			else if(((goalType == WalkToGoalBehaviour.TREASURE) || (goalType == WalkToGoalBehaviour.GOLD)) && (n.getQuantityG() != 0))
				potentialGoals.add(n);
			else if(((goalType == WalkToGoalBehaviour.TREASURE) || (goalType == WalkToGoalBehaviour.DIAMOND)) && (n.getQuantityD() != 0))
				potentialGoals.add(n);
				
		}
		
		for(Node n : potentialGoals) {
			//FIXME Add A* and find the closest potential goal to become goalNode
		}
	}
	
	/**
	 * Used to give the next tile's name to the WalkBehaviour, to be used in the call of <code>AbstractDedaleAgent.moveTo(...)</code>
	 * @return the nodeId of the next tile to visit
	 */
	//TODO doesn't handle deadlocks (agent 1 on tile 1 wants to go to tile 2, agent 2 on tile 2 wants to go to tile 1)
	public String getNextTile(){
		return shortestPath.get(0).getName();
	}
	
	/**
	 * The agent has moved to the next step on his path, so shortestPath's first item is removed. If it gets empty, the goal is reached. </br>
	 * Should only be called if <code>AbstractDedaleAgent.moveTo(...)</code> returns <code>true</code>
	 */
	public void move() {
		myPos = shortestPath.get(0);
		shortestPath.remove(myPos);
		
		if(shortestPath.isEmpty())
			reached = true;
		
	}
}
