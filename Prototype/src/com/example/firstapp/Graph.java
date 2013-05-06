package com.example.firstapp;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;

import android.util.Log;

class Graph implements GraphInterface, BusObject {
	private MainApplication app;

	public class IdChange{
		public IdChange(int id2, String owner2) {
			id = id2;
			owner = owner2;
		}
		public int id;
		public String owner;
	}
	
	public Graph(MainApplication app){
		this.app = app;	
		addObserver(app);
	}
	
	public static final int NODE_POSITION_CHANGED = 1;
	public static final int POINT_OWNERSHIP_CHANGED = 2;
	public static final int GRAPH_CHANGED = 3;
	
	private static final String TAG = "Graph";
	ArrayList<Node> nodes = new ArrayList<Node>();
	ArrayList<Node> changedNodes = new ArrayList<Node>();
    ArrayList<Edge> edges = new ArrayList<Edge>();
    ArrayList<IdChange> idChanges = new ArrayList<Graph.IdChange>();
    private List<GraphObserver> mObservers = new ArrayList<GraphObserver>();
    
    public void setupPoints() {
    	Log.d("Graph", "setupPoints()");
		Node node1 = new Node(0.1f,0.1f,1);
		Node node2 = new Node(0.1f,0.9f,2);
		Node node3 = new Node(0.6f,0.6f,3);
		Node node4 = new Node(0.9f,0.1f,4);
		Node node5 = new Node(0.9f,0.9f,5);
		
		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		nodes.add(node4);
		nodes.add(node5);
		
		edges.add(new Edge(node1, node3));
		edges.add(new Edge(node1, node4));
		edges.add(new Edge(node1, node5));
		edges.add(new Edge(node2, node3));
		edges.add(new Edge(node2, node4));
		edges.add(new Edge(node3, node5));
		edges.add(new Edge(node4, node5));
		
	}
    
	@Override
	@BusSignalHandler(iface = "com.example.firstapp.GraphInterface", signal = "MoveNode")
	public synchronized void MoveNode(int id, double x, double y, String uniqueName) throws BusException {
		
		for (Node point : nodes) {
			if(point.getId()==id){
				point.x += x;
				point.y += y;
				if(uniqueName.equals(app.getUniqueID())){
					Node changedNode = new Node(x,y,id);
					changedNodes.add(changedNode);
					notifyObservers(NODE_POSITION_CHANGED);
					return;
				}
				notifyObservers(GRAPH_CHANGED);
				return;
			}
		}
	}

	public ArrayList<Node> getAllNodes() throws BusException {
		return nodes;
	}

	public ArrayList<Edge> getAllEdge() throws BusException {
		return edges;
		
	}

	@Override
	@BusSignalHandler(iface = "com.example.firstapp.GraphInterface", signal = "ChangeOwnerOfNode")
	public synchronized void ChangeOwnerOfNode(int id, String owner, String uniqueID) throws BusException {
		for (Node node : nodes) {
			if(node.getId() == id){
				node.setOwner(owner);
				if(uniqueID.equals(app.getUniqueID())){
					addIdOfChangedPoint(new IdChange(id,owner));
					notifyObservers(POINT_OWNERSHIP_CHANGED);
					return;
				}
				notifyObservers(GRAPH_CHANGED);
				return;
			}
		}
	}

	public synchronized void addObserver(GraphObserver obs) {
        Log.i(TAG, "addObserver(" + obs + ")");
		if (mObservers.indexOf(obs) < 0) {
			mObservers.add(obs);
		}
	}
	
	public synchronized void deleteObserver(GraphObserver obs) {
        Log.i(TAG, "deleteObserver(" + obs + ")");
		mObservers.remove(obs);
	}
	

	private void notifyObservers(int arg) {
        Log.i(TAG, "notifyObservers(" + arg + ")");
        for (GraphObserver obs : mObservers) {
            Log.i(TAG, "notify observer = " + obs);
            obs.update(arg);
        }
	}

	public IdChange getIdChange() {
		if(!idChanges.isEmpty()){
			IdChange change = idChanges.get(0);
			idChanges.remove(change);
			return change;
		}
		return null;
	}

	public void addIdOfChangedPoint(IdChange change) {
		idChanges.add(change);
	}

	public Node getChangedNode() {
		if(!changedNodes.isEmpty()){
			Node node = changedNodes.get(0);
			changedNodes.remove(node);
			return node;
		}
		return null;
	}

}