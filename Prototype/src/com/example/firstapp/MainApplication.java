package com.example.firstapp;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.p2pservice.P2PApplication;
import de.p2pservice.P2PService;
import de.p2pservice.views.LobbyActivity;

@SuppressLint("HandlerLeak")
public class MainApplication extends P2PApplication<GraphInterface> implements GraphObserver{
	
	protected static final String TAG = "MainApp";
	
	public void onCreate() {
        super.onCreate();
	}	
  
	private Handler messageHandler = new Handler() {
		
    	public void handleMessage(Message msg) {
    		try{
				switch (msg.what) {
				case Graph.NODE_POSITION_CHANGED:
					Node node;
					while(( node = graph.getChangedNode()) != null){
						remoteGraph.MoveNode(node.getId(), node.x, node.y, getUniqueID());
					}break;
					
				case Graph.POINT_OWNERSHIP_CHANGED:				
					Graph.IdChange idChange;
					while(( idChange = graph.getIdChange()) != null){
						remoteGraph.ChangeOwnerOfNode(idChange.id, idChange.owner, getUniqueID());
					}break;
					
				default: break;
				}
    		}catch(BusException e){
    			Log.e(TAG, "BusException: " + e);
    		};
		}
    };
    
    
	private Graph graph = null;
	private GraphInterface remoteGraph = null;

	private BusObject busObject;

	private boolean remoteObjectSet = false;
	
	public Graph getGraph() {
		return this.graph;
	}
	
	public void setGraph(Graph graph){
		this.graph = graph;
	}

	@Override
	public BusObject getBusObject(){
		return busObject;		
	}
	
	@Override
	public Object getSignalHandler(){		
		return this.graph;
	}

	@Override
	public void update(int args) {
		Message obtainedMessage = messageHandler.obtainMessage(args);
		messageHandler.sendMessage(obtainedMessage);		
	}

	@Override
	public void setSignalEmiter(Object signalEmitter) {
		this.remoteGraph = (GraphInterface) signalEmitter;	
		remoteObjectSet = true;
	}
	
	public boolean isRemoteObjectSet(){
		return remoteObjectSet;
	}

	

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void returnToLobby() {
		if(!hostChannelName.isEmpty()){
			notifyObservers(P2PService.CANCEL_ADVERTISE);
			notifyObservers(P2PService.LEAVE_SESSION);
		}else{
			notifyObservers(P2PService.LEAVE_SESSION);
		}		
	}

	@Override
	protected void initBusObject() {
        this.busObject = new Graph(this);
	}

	@Override
	protected void initSignalHandler() {
        this.graph = new Graph(this);
	}

	@Override
	protected Class<? extends P2PService<GraphInterface>> getConcreteServiceClass() {
		return ConcreteService.class;
	}

	@Override
	protected Class<? extends LobbyActivity> getLobbyClass() {
		return MyLobbyActivity.class;
	}

	@Override
	public Class<GraphInterface> getBusObjectInterfaceType() {
		return GraphInterface.class;
	}

}
