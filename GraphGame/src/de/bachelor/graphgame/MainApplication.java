package de.bachelor.graphgame;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import de.ptpservice.DataObserver;
import de.ptpservice.PTPManager;
import de.ptpservice.SessionObserver;

@SuppressLint("HandlerLeak")
public class MainApplication extends Application {
		

	protected static final String TAG = "MainApp";
	
	public void onCreate() {
        super.onCreate();
        graph = new Graph(this);
        graph.setupGraph();
        
        PTPManager.initHelper("GraphGame",this, GraphLobbyActivity.class);
        PTPManager.getInstance().addDataObserver(new DataObserver() {			
			@Override
			public void dataSentToAllPeers(String peersID, int messageType, String[] data) {
				switch (messageType) {
					case Graph.NODE_OWNERSHIP_CHANGED: nodeOwnerChanged(peersID,data);break;
					case Graph.NODE_POSITION_CHANGED: nodePositionChanged(peersID,data);break;
					case Graph.PLAYER_JOINED: graphSent(peersID,data);break;
					default: break;
				}
			}
		});
        PTPManager.getInstance().addSessionObserver(new SessionObserver() {
			
			@Override
			public void sessionLost() {
				Log.e(TAG, "SessionLost");
			}
			
			@Override
			public void memberLeft(String playerID) {
				graph.playerLeft(playerID);
			}
			
			@Override
			public void memberJoined(String arg0) {
				Log.e(TAG, "Me:" + PTPManager.getInstance().getUniqueID()+ " JOiner: " +arg0);
				
				if(!arg0.equals(PTPManager.getInstance().getUniqueID())){
					String levelAsXML = graph.getLevelAsXML();
					PTPManager.getInstance().sendDataToAllPeers(Graph.PLAYER_JOINED, new String[]{levelAsXML});
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
	private void graphSent(String peersID, String[] data) {
		Level levelFromXML = graph.getLevelFromXML(data[0]);
		graph.setLevel(levelFromXML);
	}


}
