package de.bachelor.graphgame;

import java.util.ArrayList;

public class Level {

	public static final String PROPERTY_ID = "id";
	public static final String PROPERTY_NODE = "Node";
	public static final String PROPERTY_EDGE = "Edge";
	private int id;
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	public void setId(int id){
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void removeAllNodes(){
		nodes.clear();
	}
	
	public void addToNodeList(Node node){
		nodes.add(node);
	}
	
	public ArrayList<Node> getAllNodes(){
		return nodes;
	}
	
	public void removeAllEdges(){
		edges.clear();
	}
	
	public void addToEdgeList(Edge edge){
		edges.add(edge);
	}
	
	public ArrayList<Edge> getAllEdges(){
		return edges;
	}
}
