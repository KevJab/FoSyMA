package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.behaviours.WalkToGoalBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

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
	
	// parameters needed to implement nearest goal search
	private Node myPos;
	private Node goalNode; 
	private boolean reached;
	private int goalType;
	private List<Node> forbiddenNodes;
	
	// used in the second phase. siloNode should normally not change afterwards anymore, distantGoal will be sent by the Silo
	private String siloNode;
	private String distantGoal;
	
	/* ----------------
	 *   Constructors
	 * ----------------*/
	
	public Graphe() {
		nodes = new HashSet<>();
		edges = new HashMap<>();
		forbiddenNodes = new ArrayList<>();
		myPos = null;
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
	
	/**
	 * Usual getter for <code>Set&ltNode&gt nodes</code> parameter
	 * @return the parameter <i>nodes</i>, comprised by all nodes currently known by the agent
	 */
	public Set<Node> getNodes(){
		return nodes;
	}
	
	/** 
	 * Method that gets the node named <i>name</i>.
	 * @param name - a <code>String</code> representing the node's name (it's his node_id)
	 * @return the corresponding node object
	 */
	private Node getNode(String name) {
		for(Node n : nodes) {
			if(n.getName().equals(name)) {
				return n;
			}
		}
		return null;
	}
	
	public HashMap<String, Set<String>> getEdges(){
		return edges;
	}
	
	public Set<String> getNodesSeen(){
		Set<String> nodesSeen = new HashSet<>();
		for(Node n : nodes) {
			if (n.isVisited())
				nodesSeen.add(n.getName());
		}
		
		return nodesSeen;
	}
	
	/**
	 * Tells you if the agent reached a <i>goalType</i> type of node. Is only called after the move has been made.
	 * @return true if <code>myPos</code> is a goal type node, false otherwise
	 */
	public boolean goalReached() {
		boolean reachedValue = reached;
		if (reached) 
			System.out.println("Yay! I've reached my goal!");
		reached = false;	// to reset the value of reached
		return reachedValue;
	}
	

	public String getMyPos() {
		return myPos.getName();
	}
	
	// This should only be used for testing and printing
	public String getGoal() {
		return goalNode.getName();
	}
	
	
	public void setMyPos(Node n) {
		myPos = n;
	}
	
	public void setMyPos(String name) {
		myPos = getNode(name);
	}
	
	public void setSiloNode(String node) {
		siloNode = node;
	}
	
	public void setDistantGoal(String node) {
		distantGoal = node;
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
		
		if(n.getNeighbours() != null) {
			for(String nbr : n.getNeighbours()) {
				this.addEdges(n.getName(), nbr);
			}
		}
		//System.out.println("listing all edges: " + edges);
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
		//System.out.println("my neighbours are " + nbrName);
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
		if (nodes.isEmpty()) {		// should only happen at the very first iteration
			myPos = newNode;
		}
		if(nodes.contains(newNode))
			return;
		nodes.add(newNode);
		addAllNeighbours(newNode); 
	}
	
	public void addNode(String n, int qtyGold, int qtyDiam, List<String> nbrs, boolean visit) {
		Node newNode = new Node(n, qtyGold, qtyDiam, nbrs, visit);
		this.addNode(newNode);
	}
	
	public void addNode(String n, int qtyGold, int qtyDiam, boolean visit) {
		this.addNode(n, qtyGold, qtyDiam, new ArrayList<>(), visit);
	}
	
	/**
	 * Adds edges (<i>nodeX</i>, <i>nodeY</i>) and (<i>nodeY</i>, <i>nodeX</i>) to <b>edges</b>
	 * @param nodeX one end of the edge to add
	 * @param nodeY the other end of the edge
	 */
	public void addEdges(String nodeX, String nodeY) {
		Set<String> edgesX;
		if(edges.containsKey(nodeX))
			edgesX = edges.get(nodeX);
		else
			edgesX = new HashSet<>();
		edgesX.add(nodeY);
		
		Set<String> edgesY;
		if(edges.containsKey(nodeY))
			edgesY = edges.get(nodeY);
		else
			edgesY = new HashSet<>();
		edgesY.add(nodeX);
		
		edges.put(nodeX, edgesX);
		edges.put(nodeY, edgesY);
		
		this.getNode(nodeX).addNeighbour(nodeY);
	}
	
	public void forbidNode(String node_id) {
		Node forbidden = this.getNode(node_id);
		if(forbidden != null)
			this.forbiddenNodes.add(forbidden);
	}
	
	/**
	 * When encountering another <code>Agent</code>, merges both graphs together
	 * @param other the other agent's <code>Graphe</code>
	 */
	public void merge(Graphe other) {
		if (other == null)
			return;
		Node myNode;
		for(Node n : other.getNodes()) {
			// my instance of the same node
			myNode = this.hasNode(n.getName());
			if(myNode == null) {
				this.addNode(n);	//this will create a new node in my graph, identical to n, and create the edges to all of n's neighbours (even though I may not know them yet)
			} else {
				myNode.update(n);	//this will update the data in myNode, if n is more recent
				
				/*boolean condition;
				switch(goalType) {
				case WalkToGoalBehaviour.OPEN:
					condition = myNode.isVisited();
					break;
				case WalkToGoalBehaviour.TREASURE:
					condition = (myNode.getquantityDiam() == 0) && (myNode.getquantityGold() == 0);
					break;
				case WalkToGoalBehaviour.GOLD:
					condition = myNode.getquantityGold() == 0;
					break;
				case WalkToGoalBehaviour.DIAMOND:
					condition = myNode.getquantityDiam() == 0;
					break;
				//dunno what to do in case WalkToGoalBehaviour.SILO
				default:
					condition = false;
					break;
				}
				//TO DO redo this; setNewGoal should be called after each step
				if ((condition)||(myNode.equals(goalNode))) { // if the goal has been visited by the other agent
					setNewGoal(goalType);
				}*/
			}
		}
	}
	
	private boolean isGoalType(Node n) {
		return ((goalType == WalkToGoalBehaviour.OPEN) && (!n.isVisited()))|| 
				(((goalType == WalkToGoalBehaviour.TREASURE) || (goalType == WalkToGoalBehaviour.GOLD)) && (n.getquantityGold() != 0)) || 
				(((goalType == WalkToGoalBehaviour.TREASURE) || (goalType == WalkToGoalBehaviour.DIAMOND)) && (n.getquantityDiam() != 0)) ||
				((goalType == WalkToGoalBehaviour.SILO) && (n.getNeighbours().contains(siloNode))) ||
				((goalType == WalkToGoalBehaviour.GOAL) && (n.getName().equals(distantGoal)));
	}
	
	/**
	 * Chooses a new goal node, being always the closest goal node to my current position (Manhattan distance)
	 * @param goalType - The agent's goal type. It will influence the type of goal nodes.
	 */
	public void setNewGoal(int goalType) {
		if(nodes.isEmpty()) {	// if I have nowhere to go, I can't do anything; should only be called by the very first setNewGoal
			goalNode = myPos;
			return;
		}
		
		reached = false;
		this.goalType = goalType;
		
		
		List<Node> potentialGoals = new ArrayList<>();	// a list of potential goal nodes
		List<Node> toVisit = new ArrayList<>();			// a list of all open nodes 
		HashMap<Node, Node> predecessors = new HashMap<>();	// for each visited node, how did we get there?
		HashMap<Node, Integer> dist = new HashMap<>();		// for each potential goal, its distance to myPos (Manhattan)
		List<Node> dejavu = new ArrayList<>();				// a list of closed nodes
		dejavu.add(myPos);
		int current_dist = 0;
		toVisit.add(myPos);
		if(this.isGoalType(myPos)) {
			potentialGoals.add(myPos);
			dist.put(myPos, current_dist);
		}
		while(toVisit.size() > 0) {
			//System.out.println("Iteration; size = "+toVisit.size());
			Node current = toVisit.get(0);
			toVisit.remove(current);
			current_dist++;
			for(Node n : getNeighbourNodes(current)) {
				//System.out.println("neighbours");
				if(this.isGoalType(n)) {
					potentialGoals.add(n);
					dist.put(n, current_dist);
				}
				
				if(!dejavu.contains(n)) {
					//System.out.println("adding said neighbour");
					toVisit.add(n);
					dejavu.add(n);
					predecessors.put(n, current);
				}
			}
		}
		
		int minDist = Integer.MAX_VALUE;		// simulate minDist = +infinite
		Node minNode = null;
		for(Node n : dist.keySet()) {
			if(!forbiddenNodes.contains(n)) {	// if Node n is not forbidden by the other agent
				//System.out.println("There is a potential goal");
				if(dist.get(n) < minDist) {
					minDist = dist.get(n);
					minNode = n;
				}
			}
		}
		goalNode = (minNode != null) ? minNode : myPos;		// goalNode is now the nearest not forbidden potential goal node, or myPos if there are no nodes to see
		
		if(!goalNode.equals(myPos)) {
			//System.out.println("goalNode:" + goalNode);
			if(predecessors.get(goalNode) == null)
				System.out.println("as I thought...");
			if(!predecessors.containsKey(goalNode))
				System.out.println("Well that's weird...");
			while(!predecessors.get(goalNode).equals(myPos)) {
				goalNode = predecessors.get(goalNode);
			}
		}
		System.out.println("My Position: "+myPos.getName());
		System.out.println("Next move : " + goalNode.getName());
	}
	
	/**
	 * Used to give the next tile's name to the WalkBehaviour, to be used in the call of <code>AbstractDedaleAgent.moveTo(...)</code>
	 * @return the nodeId of the next tile to visit
	 */
	public String getNextTile(){
		return goalNode.getName();
	}
	
	/**
	 * The agent has moved to the next step on his path, so shortestPath's first item is removed. If it gets empty, the goal is reached. </br>
	 * Should only be called if <code>AbstractDedaleAgent.moveTo(...)</code> returns <code>true</code>
	 */
	/* Old move
	public void move() {
		myPos = shortestPath.get(0);
		shortestPath.remove(myPos);
		
		if(shortestPath.isEmpty())
			reached = true;
		
	}*/
	/**
	 * Called after the agent's <code>moveTo</code>, with the argument <code>hasMoved</code> being what moveTo returned.</br>
	 * Whether the agent moved or not, it visits the current node and clears the list of forbidden ones. If he has moved, he goes to the next step as well and checks if he reached his goal.
	 * @param hasMoved true if the agent was actually able to move
	 */
	public void move(boolean hasMoved) {
		if(hasMoved) {
			myPos = goalNode;
			// if I am now on a [goalType] type of Node, I have reached my goal
			if(this.isGoalType(myPos)){
				reached = true;	
			}
		}
		
		myPos.visit();
		forbiddenNodes.clear();
	}
	
	public boolean isComplete() {
		for (Node n : nodes) {
			if(!n.isVisited()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean noMoreTreasure(int treasureType) {
		int qtyTreasure = 0;
		
		for (Node n : nodes) {
			if((treasureType == WalkToGoalBehaviour.DIAMOND)||(treasureType == WalkToGoalBehaviour.TREASURE))
				qtyTreasure += n.getquantityDiam();
			if((treasureType == WalkToGoalBehaviour.GOLD)||(treasureType == WalkToGoalBehaviour.TREASURE))
				qtyTreasure += n.getquantityGold();
		}
		
		return qtyTreasure == 0;
	}
	
	/* Currently unused, because I do not know how to use it */
	public MapRepresentation toMapRep() {
		MapRepresentation mr = new MapRepresentation();
		for(Node n : nodes) {
			mr.addNode(n.getName());
		}
		for(String node : edges.keySet()) {
			for(String nbr : edges.get(node)) {
				mr.addEdge(node, nbr);
			}
		}
		
		return mr;
	}
	
	/**
	 * changes the current Node's lock state for the treasure type given
	 * @param treasureType -- the treasure type whose lock the agent removed
	 */
	public void unlock(Observation treasureType) {
		myPos.unlock(treasureType);
	}
	
	/**
	 * picks some or all of the treasure on the current tile
	 * @param treasureType -- the treasure type the agent can pick
	 * @param qty -- how much treasure he took
	 */
	public void pick(Observation treasureType, int qty) {
		myPos.pick(treasureType, qty);
	}
}
