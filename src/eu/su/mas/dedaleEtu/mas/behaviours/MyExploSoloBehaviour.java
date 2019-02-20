package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MyMapRepresentation;
import jade.core.behaviours.SimpleBehaviour;

public class MyExploSoloBehaviour extends SimpleBehaviour{
	
	private boolean finished = false;
	
	private static final long serialVersionUID = 8578659731496787661L;
	
	/**
	 * Nodes known but not yet visited
	 */
	private List<String> openNodes;
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	
	private MyMapRepresentation myMap;

	public MyExploSoloBehaviour(AbstractDedaleAgent myagent, MyMapRepresentation mp) {
		super(myagent);
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.myMap = mp;
	}

	@Override
	public void action() {
		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
	
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myAgent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//1) remove the current node from openlist and add it to closedNodes.
			this.closedNodes.add(myPosition);
			this.openNodes.remove(myPosition);


			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Couple<String, List<Couple<Observation, Integer>>> node = iter.next();
				String nodeId = node.getLeft();
				
				List<Couple<Observation, Integer>> nodeContents = node.getRight();
				if (!this.closedNodes.contains(nodeId)){
					if (!this.openNodes.contains(nodeId)){
						this.openNodes.add(nodeId);
						
						if(nodeContents.isEmpty())
							this.myMap.addNode(nodeId, MyMapRepresentation.VOID, 0);
						else {
							for (Couple<Observation, Integer> cont : nodeContents) {
								switch(cont.getLeft()) {
								case DIAMOND:
									this.myMap.addNode(nodeId, MyMapRepresentation.DIAMOND, cont.getRight());
									break;
								case GOLD:
									this.myMap.addNode(nodeId, MyMapRepresentation.GOLD, cont.getRight());
									break;
								default:
									this.myMap.addNode(nodeId, MyMapRepresentation.VOID, 0);
									break;
								}
							}
						}
						
						this.myMap.addEdge(myPosition, nodeId);	
					}else{
						//the node exist, but not necessarily the edge
						this.myMap.addEdge(myPosition, nodeId);
					}
					if (nextNode==null) nextNode=nodeId;
				}
			}

			//3) while openNodes is not empty, continues.
			if (this.openNodes.isEmpty()){
				//Explo finished
				finished=true;
				System.out.println("Exploration successufully done, behaviour removed.");
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNode=this.myMap.getShortestPath(myPosition, this.openNodes.get(0)).get(0);
				}
				((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
			}
		}		
	}

	@Override
	public boolean done() {
		return finished;
	}

}