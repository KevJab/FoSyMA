package eu.su.mas.dedaleEtu.mas.object;

import java.io.Serializable;

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
	
	public Node(String n, int na, int q) {
		name = n;
		nature = na;
		quantity = q;
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
	
	public void setNature(int n) {
		nature = n;
	}
	
	public void setQuantity(int q) {
		quantity = q;
	}

}
