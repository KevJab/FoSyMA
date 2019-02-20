package eu.su.mas.dedaleEtu.mas.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;

public class MapInformation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6383660847353890343L;

	private List<String> sentNodes;
	
	private List<Couple<String, String>> sentEdges;
	
	private List<String> toSendNodes;
	
	private List<Couple<String, String>> toSendEdges;
	
	public MapInformation() {
		sentNodes = new ArrayList<>();
		
		sentEdges = new ArrayList<>();
		
		toSendNodes = new ArrayList<>();
		
		toSendEdges = new ArrayList<>();
	}

	public List<String> getSentNodes() {
		return sentNodes;
	}

	public List<Couple<String, String>> getSentEdges() {
		return sentEdges;
	}

	public List<String> getToSendNodes() {
		return toSendNodes;
	}

	public List<Couple<String, String>> getToSendEdges() {
		return toSendEdges;
	}
	
	public void sendInfo() {
		toSendEdges.clear();
		toSendNodes.clear();
	}
}
