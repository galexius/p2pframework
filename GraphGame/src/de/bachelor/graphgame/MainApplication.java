package de.bachelor.graphgame;

import android.annotation.SuppressLint;
import android.app.Application;
import de.ptpservice.DataObserver;
import de.ptpservice.PTPHelper;

@SuppressLint("HandlerLeak")
public class MainApplication extends Application {
		

	protected static final String TAG = "MainApp";
	
	public void onCreate() {
        super.onCreate();
        graph = new Graph(this);
        graph.setupGraph();
        
		PTPHelper.initHelper("GraphGame",this, GraphLobbyActivity.class);
		PTPHelper.getInstance().addDataObserver(new DataObserver() {
			
			@Override
			public void dataSentToAllPeers(String peersID, int messageType, String[] data) {
				switch (messageType) {
					case Graph.NODE_OWNERSHIP_CHANGED: nodeOwnerChanged(peersID,data);break;
					case Graph.NODE_POSITION_CHANGED: nodePositionChanged(peersID,data);break;
					default: break;
				}
			}
		});
	}	
    
    
	private Graph graph = null;

	public Graph getGraph() {
		return this.graph;
	}
	
	public void setGraph(Graph graph){
		this.graph = graph;
	}	

	private void nodeOwnerChanged(String sentBy,String[] data) {
    	Node node = graph.getNodeFromXML(data[0]);
	    graph.changeOwnerOfNode(node.getId(),node.getOwner(), sentBy);
	}

	private void nodePositionChanged(String sentBy,String[] data) {
    	Node node = graph.getNodeFromXML(data[0]);
	    graph.moveNode(node.getId(), node.getX(), node.getY(), sentBy);	
	}


}
