package de.bachelor.graphgame;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignalHandler;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import de.ptpservice.PTPHelper;

class Graph implements GraphInterface, BusObject {

	public class IdChange{
		public IdChange(int id2, String owner2) {
			id = id2;
			owner = owner2;
		}
		public int id;
		public String owner;
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
		Node node3 = new Node(0.3f,0.3f,3);
		Node node4 = new Node(0.6f,0.1f,4);
		Node node5 = new Node(0.9f,0.9f,5);
		Node node6 = new Node(0.9f,0.5f,6);
		
		nodes.clear();
		
		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		nodes.add(node4);
		nodes.add(node5);
		nodes.add(node6);
		
		edges.clear();
		
		edges.add(new Edge(node1, node3));
		edges.add(new Edge(node1, node4));
		edges.add(new Edge(node1, node5));
		edges.add(new Edge(node2, node3));
		edges.add(new Edge(node2, node4));
		edges.add(new Edge(node3, node5));
		edges.add(new Edge(node4, node5));		
		edges.add(new Edge(node3, node6));		
		edges.add(new Edge(node4, node6));		
		edges.add(new Edge(node5, node6));		
	}
    
    public void resetGraph(){
    	changedNodes.clear();
    	idChanges.clear();
    	mObservers.clear();
    	setupPoints();
    }
    
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	@BusSignalHandler(iface = "com.example.firstapp.GraphInterface", signal = "MoveNode")
	public synchronized void MoveNode(int id, double x, double y, String uniqueName) throws BusException {
		
		for (Node point : nodes) {
			if(point.getOwner().isEmpty() || point.getOwner().equals(uniqueName)){
				point.x += x;
				point.y += y;
				checkAndAdjust(point);
			}
		}
		if(uniqueName.equals(PTPHelper.getInstance().getUniqueID())){
			Node changedNode = new Node(x,y,id);
			changedNodes.add(changedNode);
			notifyObservers(NODE_POSITION_CHANGED);
			return;
		}
		notifyObservers(GRAPH_CHANGED);
	}

	private void checkAndAdjust(Node point) {
		if(point.x > 1.0d) point.x = 1.0d;
		if(point.y > 1.0d) point.y = 1.0d;
		if(point.x < 0.0d) point.x = 0.0d;
		if(point.y < 0.0d) point.y = 0.0d;
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
				if(uniqueID.equals(PTPHelper.getInstance().getUniqueID())){
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
	
	public boolean isGraphFinished(){
		for(int i = 0;i < edges.size()-1; i++){
			for( int j = i+1; j < edges.size(); j++){
				Point point1 = new Point();
				Point point2 = new Point();
				Point point3 = new Point();
				Point point4 = new Point();
				point1.x  = edges.get(i).getFrom().x;
				point1.y  = edges.get(i).getFrom().y;
				point2.x  = edges.get(i).getTo().x;
				point2.y  = edges.get(i).getTo().y;
				point3.x  = edges.get(j).getFrom().x;
				point3.y  = edges.get(j).getFrom().y;
				point4.x  = edges.get(j).getTo().x;
				point4.y  = edges.get(j).getTo().y;
				Line line1 = new Line();
				line1.point1 = point1;						
				line1.point2 = point2;
				Line line2 = new Line();
				line2.point1 = point3;
				line2.point2 = point4;

				//To avoid intersection on the ends of the lines we shorten them a little
				line1.shorten(0.01d);
				line2.shorten(0.01d);
				
				if(GraphCalculator.linesIntersect(point1.x,point1.y,point2.x,point2.y,point3.x,point3.y,point4.x,point4.y)){
					return false;
				}
			}
		}
		Log.i(TAG, "Intersection False");
		return true;
	}
	
	class Line{
		public Point point1;
		public Point point2;
		public void shorten(double factor){
			if(point1.x > point2.x){
				point1.x -= (point1.x - point2.x) * factor;
				point2.x += (point1.x - point2.x) * factor;
			}else{
				point2.x -= (point2.x - point1.x) * factor;
				point1.x += (point2.x - point1.x) * factor;
			}
			if(point1.y > point2.y){
				point1.y -= (point1.y - point2.y) * factor;
				point2.y += (point1.y - point2.y) * factor;
			}else{
				point2.y -= (point2.y - point1.y) * factor;
				point1.y += (point2.y - point1.y) * factor;
			}
			
		}
	}
	
	class Point{
		public double x;
		public double y;
	}

}