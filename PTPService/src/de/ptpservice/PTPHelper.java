package de.ptpservice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.alljoyn.bus.BusObject;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import de.ptpservice.views.AbstractLobbyActivity;
import de.ptpservice.views.LobbyObserver;



public class PTPHelper {
    static {
        Log.i("P2PHelper","System.loadLibrary(\"alljoyn_java\")");
        System.loadLibrary("alljoyn_java");
    } 
    
    class HelperBusObserver implements BusObserver{

    	@Override
    	public synchronized void notifyLostAdvertisedName(String channelName) {    		
    		instance.removeAdvertisedName(channelName);    		
    	}
    	
		@Override
		public void notifyFoundAdvertisedName(String channelName) {
			instance.addAdvertisedName(channelName);    				
		}

		@Override
		public void notifyBusDisconnected() {
						
		}
    }
	
    public static class Attributes{
    	public static final String APP_NAME = "ApplicationName";
    }
    
	public final static int CONNECT_BUS = 0;
	public final static int JOIN_CHANNEL = 1;
	public final static int HOST_CHANNEL = 2;
	public final static int LEAVE_CHANNEL = 3;
	public final static int CLOSE_CHANNEL = 4;
	public final static int DISCONNECT = 5;
	public final static int QUIT = 6;
	
	
	public final static int DISCONNECTED = 7;
	public final static int CONNECTED = 8;
	
	private static PTPHelper instance = null;
	
	public static void initHelper(String applicationName,Class<?> theInterfaceType, Context context,BusObject busObject,Object signalHandler, Class<? extends AbstractLobbyActivity> lobbyClass){
		instance = new PTPHelper(applicationName,theInterfaceType,busObject,signalHandler,lobbyClass);	
		instance.addBusObserver(instance.new HelperBusObserver());
		PACKAGE_NAME = context.getPackageName();
        if(instance.isWifiEnabledAndShowInfo(context))
        	return;          
         
		Intent service = new Intent(context,PTPService.class);
        boolean bound = context.bindService(service,new PTPServiceConnection(),Service.BIND_AUTO_CREATE);
        if (!bound) {
            Log.e(TAG, "onCreate(): failed to bind Service()");
        }
        new Intent(context,lobbyClass);
	}
	
	public static PTPHelper getInstance(){		
		return instance;
	}
	
	protected static final String TAG = "P2PHelper";
	
	public static String PACKAGE_NAME;
	protected String hostChannelName = "";
	protected String channelName = "";
	protected Queue<Integer> notificationQueue = new LinkedList<Integer>();
	protected Class<? extends AbstractLobbyActivity> lobbyClass;
	private String applicationName;
	
	private PTPHelper(String applicationName, Class<?> theInterfaceType,BusObject busObject,Object signalHandler, Class<? extends AbstractLobbyActivity> lobbyClass) {
		Log.d("P2PHelper", "onConstructor");
		this.applicationName = applicationName;
		this.busObjectInterfaceType = theInterfaceType;
		this.busObject = busObject;
		this.signalHandler  = signalHandler;
		this.lobbyClass = lobbyClass;	        
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private boolean isWifiEnabledAndShowInfo(Context context){
		WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    WifiInfo currentWifi = mainWifi.getConnectionInfo();
	    if((currentWifi==null || currentWifi.getSSID()== null || currentWifi.getSSID().isEmpty()) && !isWifiAPEnabled(mainWifi)){
	    	CharSequence text = "No WiFi connection";
	    	int duration = Toast.LENGTH_LONG;
	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
	    	Log.i(TAG, "ShowNoWiFi");
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
	 
	private Class<?> busObjectInterfaceType;


	public Class<?> getBusObjectInterfaceType(){
		return busObjectInterfaceType;		
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
	
	public void setClientChannelName(String name){
		this.channelName = name; 
	}
	
	public synchronized void addObserver(HelperObserver obs) {
        Log.i(TAG, "addObserver(" + obs + ")");
		if (helperObservers.indexOf(obs) < 0) {
			helperObservers.add(obs);
		}
	}
	public synchronized void removeObserver(HelperObserver obs) {
        Log.i(TAG, "deleteObserver(" + obs + ")");
		helperObservers.remove(obs);
	}
	
	public synchronized void addBusObserver(BusObserver obs) {
		Log.i(TAG, "addObserver(" + obs + ")");
		if (busObservers.indexOf(obs) < 0) {
			busObservers.add(obs);
		}
	}
	public synchronized void removeBusObserver(BusObserver obs) {
		Log.i(TAG, "deleteObserver(" + obs + ")");
		busObservers.remove(obs);
	}
	
	public synchronized void addSessionObserver(SessionObserver obs) {
		Log.i(TAG, "addObserver(" + obs + ")");
		if (sessionObservers.indexOf(obs) < 0) {
			sessionObservers.add(obs);
		}
	}
	public synchronized void removeSessionObserver(SessionObserver obs) {
		Log.i(TAG, "deleteObserver(" + obs + ")");
		sessionObservers.remove(obs);
	}
	
	public synchronized void addLobbyObserver(LobbyObserver obs) {
		Log.i(TAG, "addObserver(" + obs + ")");
		if (lobbyObservers.indexOf(obs) < 0) {
			lobbyObservers.add(obs);
		}
	}
	
	public synchronized void removeLobbyObserver(LobbyObserver obs) {
		Log.i(TAG, "deleteObserver(" + obs + ")");
		lobbyObservers.remove(obs);
	}
	

	protected void notifyObservers(int arg) {
        Log.i(TAG, "notifyObservers(" + arg + ")");
        if(helperObservers.isEmpty()){
        	notificationQueue.add(arg);
        }
        for (HelperObserver obs : helperObservers) {
            Log.i(TAG, "notify observer = " + obs);
            obs.doAction(arg);
        }
	}
	private List<HelperObserver> helperObservers = new ArrayList<HelperObserver>();
	private List<LobbyObserver> lobbyObservers = new ArrayList<LobbyObserver>();
	private ArrayList<BusObserver> busObservers = new ArrayList<BusObserver>();
	private ArrayList<SessionObserver> sessionObservers = new ArrayList<SessionObserver>();
	private List<String> foundChannels = new ArrayList<String>();
	private List<SessionJoinRule> joinRules = new ArrayList<SessionJoinRule>();

	private String uniqueID  = "";
	private String playerName = "";
	private int connectionState;
	private BusObject busObject;
	private Object signalHandler;
	private Object signalEmitter;
	
	public synchronized void joinChannel() {
		notifyObservers(PTPService.JOIN_SESSION);
	}
	
	public synchronized void connectAndStartDiscover() {
		foundChannels.clear();
		notifyObservers(PTPService.CONNECT);
		notifyObservers(PTPService.START_DISCOVERY);		
	}
	
	public synchronized void disconnect() {	
		notifyObservers(PTPService.CANCEL_DISCOVERY);
		notifyObservers(PTPService.DISCONNECT);
	}
	
	
	public synchronized void leaveChannel() {
		notifyObservers(PTPService.CANCEL_ADVERTISE);
		notifyObservers(PTPService.LEAVE_SESSION);
	}

	public synchronized void hostInitChannel() {
		notifyObservers(PTPService.UNBIND_SESSION);
		notifyObservers(PTPService.RELEASE_NAME);
		notifyObservers(PTPService.BIND_SESSION);
		
	}

	public synchronized void hostStartChannel() {	
		notifyObservers(PTPService.REQUEST_NAME);
		notifyObservers(PTPService.ADVERTISE);
	}
	
	public synchronized void hostStopChannel() {
		notifyObservers(PTPService.UNBIND_SESSION);
	}
	public synchronized void quit() {
		notifyObservers(PTPService.EXIT);
	}
	
	public BusObject getBusObject(){
		return busObject;
	}
	
	public Object getSignalHandler(){
		return signalHandler;
	}

	public String getUniqueID() {
		return this.uniqueID;
	}

	public void setUniqueID(String id) {
		this.uniqueID = id;
	}
	
	public Object getSignalEmitter(){
		return this.signalEmitter;
	}

	public void setSignalEmiter(Object signalEmitter){
		this.signalEmitter = signalEmitter;		
	}
	

	public void addAdvertisedName(String name) {
		foundChannels.add(name);
	}


	public void removeAdvertisedName(String name) {
		if(foundChannels.contains(name)){
			foundChannels.remove(name);
		}
	}

	public List<String> getFoundChannels(){
		return foundChannels;
	}


	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public boolean isHost(){
		return hostChannelName.equals(channelName);
	}
	
	public void setConnectionState(int state){
		this.connectionState = state;		
		notifyLobbyObserversStateChanged();
	}
	
	private void notifyLobbyObserversStateChanged() {
		for (LobbyObserver obs : lobbyObservers) {
            obs.connectionStateChanged(connectionState);
        }		
	}

	public int getConnectionState(){
		return connectionState;
	}
	
	
	public void acquireMissedNotifications() {
		while (!notificationQueue.isEmpty()) {
			notifyObservers(notificationQueue.poll());			
		}
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public synchronized void notifyBusDisconnected() {
		for (BusObserver obs : busObservers) {
            obs.notifyBusDisconnected();
        }	
	}

	public synchronized void notifyLostAdvertisedName(String channelName) {
		for (BusObserver obs : busObservers) {
            obs.notifyLostAdvertisedName(channelName);
        }			
	}
	
	public synchronized void notifyFoundAdvertisedName(String channelName) {
		for (BusObserver obs : busObservers) {
            obs.notifyFoundAdvertisedName(channelName);
        }			
	}

	public synchronized void notifyMemberJoined(String uniqueId) {
		for (SessionObserver obs : sessionObservers) {
            obs.notifyMemberJoined(uniqueId);
        }	
	}

	public synchronized void notifyMemberLeft(String uniqueId) {
		for (SessionObserver obs : sessionObservers) {
            obs.notifyMemberLeft(uniqueId);
        }
	}

	public synchronized void notifySessionLost() {
		for (SessionObserver obs : sessionObservers) {
            obs.notifySessionLost();
        }
	}
	
	public synchronized void addJoinRule(SessionJoinRule joinRule){
		if (joinRules.indexOf(joinRule) < 0) {
			joinRules.add(joinRule);
		}
	}
	
	public synchronized void removeJoinRule(SessionJoinRule joinRule){
		if (joinRules.indexOf(joinRule) >= 0) {
			joinRules.remove(joinRule);
		}
	}
	
	public synchronized void removeAllJoinRules(){
		joinRules.clear();
	}
	
	public synchronized boolean canJoin(String joinersUniqueId){
		for (SessionJoinRule joinRule : joinRules) {
			if(!joinRule.canJoin(joinersUniqueId))
				return false;
		}
		return true;
	}
}
