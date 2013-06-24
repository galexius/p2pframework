package de.bachelor.graphgame;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignal;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.ptpservice.PTPHelper;

@SuppressLint("HandlerLeak")
public class MainApplication extends Application implements GraphObserver{
	
	class GraphDummyObject implements BusObject,GraphInterface{

		@Override
		@BusSignal
		public void MoveNode(int id, double x, double y, String uniqueName)	throws BusException {}

		@Override
		@BusSignal
		public void ChangeOwnerOfNode(int id, String owner, String uniqueID) throws BusException {}		
	}
		
	protected static final String TAG = "MainApp";
	
	public void onCreate() {
        super.onCreate();
        graph = new Graph();
        graph.addObserver(this);  
        graph.setupPoints();
        
		PTPHelper.initHelper("GraphGame",GraphInterface.class, this, new GraphDummyObject(), graph, GraphLobbyActivity.class);
	}	
  
	private Handler messageHandler = new Handler() {
		
    	public void handleMessage(Message msg) {
    		try{
    			GraphInterface remoteGraph = (GraphInterface) PTPHelper.getInstance().getSignalEmitter();
				switch (msg.what) {
				case Graph.NODE_POSITION_CHANGED:
					Node node;
					while(( node = graph.getChangedNode()) != null){
						remoteGraph.MoveNode(node.getId(), node.x, node.y, PTPHelper.getInstance().getUniqueID());
					}break;
					
				case Graph.POINT_OWNERSHIP_CHANGED:				
					Graph.IdChange idChange;
					while(( idChange = graph.getIdChange()) != null){
						remoteGraph.ChangeOwnerOfNode(idChange.id, idChange.owner, PTPHelper.getInstance().getUniqueID());
					}break;
					
				default: break;
				}
    		}catch(BusException e){
    			Log.e(TAG, "BusException: " + e);
    		};
		}
    };
    
    
	private Graph graph = null;

	public Graph getGraph() {
		return this.graph;
	}
	
	public void setGraph(Graph graph){
		this.graph = graph;
	}

	

	@Override
	public void update(int args) {
		Message obtainedMessage = messageHandler.obtainMessage(args);
		messageHandler.sendMessage(obtainedMessage);		
	}	


}
