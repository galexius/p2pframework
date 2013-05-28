package com.example.firstapp;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.p2pservice.P2PApplication;
import de.p2pservice.P2PService;

@SuppressLint("HandlerLeak")
public class MainApplication extends P2PApplication implements GraphObserver{
	
	protected static final String TAG = "MainApp";
	private ComponentName mRunningService;
	
	public void onCreate() {
        super.onCreate();
        this.graph = new Graph(this);
        this.busObject = new Graph(this);
        Intent service = new Intent(this,ConcreteService.class);
        mRunningService = startService(service);
        if (mRunningService == null) {
            Log.e(TAG, "onCreate(): failed to startService()");
        }
        new Intent(this,MyLobbyActivity.class);
        
        Log.i("MainApp", "onCreate finished");
	}
	
	
	
    public void checkin() {
    	if (mRunningService == null) {
            Intent intent = new Intent(this, ConcreteService.class);
            mRunningService = startService(intent);
            if (mRunningService == null) {
                Log.i(TAG, "checkin(): failed to startService()");
            }    		
    	}
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
	
	public synchronized void joinChannel() {
		notifyObservers(P2PService.JOIN_SESSION);

	}
	
	public synchronized void connectAndStartDiscover() {		
		notifyObservers(P2PService.CONNECT);
		notifyObservers(P2PService.START_DISCOVERY);		
	}
	
	
	public synchronized void leaveChannel() {
		notifyObservers(P2PService.LEAVE_SESSION);
	}

	public synchronized void hostInitChannel() {
		notifyObservers(P2PService.UNBIND_SESSION);
		notifyObservers(P2PService.RELEASE_NAME);
		notifyObservers(P2PService.BIND_SESSION);
		
	}

	public synchronized void hostStartChannel() {	
		notifyObservers(P2PService.REQUEST_NAME);
		notifyObservers(P2PService.ADVERTISE);
	}
	
	public synchronized void hostStopChannel() {
		notifyObservers(P2PService.UNBIND_SESSION);
	}

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
		
}
