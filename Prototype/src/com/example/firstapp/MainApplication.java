package com.example.firstapp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import de.p2pservice.Observer;
import de.p2pservice.P2PInfoHolder;
import de.p2pservice.P2PService;
import com.example.firstapp.Graph;

@SuppressLint("HandlerLeak")
public class MainApplication extends Application implements P2PInfoHolder, GraphObserver{
	private static final String TAG = "MainApplication";
	
	public static String PACKAGE_NAME;
	private ComponentName mRunningService;
	private String hostChannelName;
	private String channelName;
	
	public void onCreate() {
		Log.i("MainApp", "onCreate");
        PACKAGE_NAME = getPackageName();
        if(isWifiEnabledAndShowInfo())
        	return;  
        this.graph = new Graph(this);
        this.busObject = new Graph(this);
        Intent service = new Intent(this,ConcreteService.class);
        mRunningService = startService(service);
        if (mRunningService == null) {
            Log.e("", "onCreate(): failed to startService()");
        }
        new Intent(this,MainActivity.class);
        
        Log.i("MainApp", "onCreate finished");
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private boolean isWifiEnabledAndShowInfo(){
		WifiManager mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    WifiInfo currentWifi = mainWifi.getConnectionInfo();
	    if((currentWifi==null || currentWifi.getSSID()== null || currentWifi.getSSID().isEmpty()) && !isWifiAPEnabled(mainWifi)){
	    	Context context = getApplicationContext();
	    	CharSequence text = "No WiFi connection";
	    	int duration = Toast.LENGTH_SHORT;

	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
	    	Log.i(TAG, "ShowWiFi");
	    	return true;
	    }
	    return false;
	}
	
	 private boolean isWifiAPEnabled(WifiManager wifi) {
	        boolean state = false;
	        try {
	            Method method2 = wifi.getClass().getMethod("isWifiApEnabled");
	            state = (Boolean) method2.invoke(wifi);
	        } catch (Exception e) {}
	        return state;
	    }
	
    public void checkin() {
        Log.i(TAG, "checkin()");
    	if (mRunningService == null) {
            Log.i(TAG, "checkin():  Starting the P2PService");
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
    			Log.i(TAG, "handleMessage: " + msg);
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
    
    public void quit() {
    	Log.i("MainApp", "quit");
		mRunningService = null;
    }
    

	public String getHostChannelName() {
		return hostChannelName;
	}
	
	public void setHostChannelName(String name){
		this.hostChannelName = name; 
	}
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String name){
		this.channelName = name; 
	}
	
	public synchronized void addObserver(Observer obs) {
        Log.i(TAG, "addObserver(" + obs + ")");
		if (mObservers.indexOf(obs) < 0) {
			mObservers.add(obs);
		}
	}
	public synchronized void removeObserver(Observer obs) {
        Log.i(TAG, "deleteObserver(" + obs + ")");
		mObservers.remove(obs);
	}
	

	private void notifyObservers(int arg) {
        Log.i(TAG, "notifyObservers(" + arg + ")");
        for (Observer obs : mObservers) {
            Log.i(TAG, "notify observer = " + obs);
            obs.doAction(arg);
        }
	}
	private List<Observer> mObservers = new ArrayList<Observer>();
	private Graph graph = null;
	private GraphInterface remoteGraph = null;

	private String uniqueID  = "";

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
	public synchronized void setRemoteObject(Object remoteObject) {
			
	}
	
	public synchronized Object getRemoteObject(){
		return null;
	}
	
	public BusObject getBusObject(){
		return busObject;		
	}
	
	public Object getSignalHandler(){		
		return this.graph;
	}

	@Override
	public String getUniqueID() {
		return this.uniqueID;
	}

	@Override
	public void setUniqueID(String id) {
		this.uniqueID = id;
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
	
}
