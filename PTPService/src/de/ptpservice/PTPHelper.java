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
import de.ptpservice.views.LobbyActivity;
import de.ptpservice.views.LobbyObserver;



public class PTPHelper<T> {
    static {
        Log.i("P2PHelper","System.loadLibrary(\"alljoyn_java\")");
        System.loadLibrary("alljoyn_java");
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
	
	@SuppressWarnings("rawtypes")
	private static PTPHelper instance = null;
	
	public static <T> void initHelper(Class<T> theInterfaceType, Context context,BusObject busObject,Object signalHandler,Class<? extends PTPService<T>> concreteServiceClass, Class<? extends LobbyActivity> lobbyClass){
		instance = new PTPHelper<T>(theInterfaceType, context,busObject,signalHandler,concreteServiceClass,lobbyClass);		
		PACKAGE_NAME = context.getPackageName();
        if(instance.isWifiEnabledAndShowInfo(context))
        	return;          
                
		Intent service = new Intent(context,concreteServiceClass);
        boolean bound = context.bindService(service,new PTPServiceConnection(),Service.BIND_AUTO_CREATE);
        if (!bound) {
            Log.e(TAG, "onCreate(): failed to bindService()");
        }
        new Intent(context,lobbyClass);
	}
	
	@SuppressWarnings("rawtypes")
	public static PTPHelper getInstance(){
		return instance;
	}
	
	protected static final String TAG = "P2PHelper";
	
	public static String PACKAGE_NAME;
	protected String hostChannelName = "";
	protected String channelName = "";
	protected Queue<Integer> notificationQueue = new LinkedList<Integer>();
	
	private PTPHelper(Class<T> theInterfaceType, Context context,BusObject busObject,Object signalHandler,Class<? extends PTPService<T>> concreteServiceClass, Class<? extends LobbyActivity> lobbyClass) {
		Log.d("P2PHelper", "onConstructor");
		this.busObjectInterfaceType = theInterfaceType;
		this.busObject = busObject;
		this.signalHandler  = signalHandler;
		this.concreteServiceClass = concreteServiceClass;
		this.lobbyClass = lobbyClass;	        
	}
		
	protected Class<? extends PTPService<T>> concreteServiceClass;
	protected Class<? extends LobbyActivity> lobbyClass;

	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private boolean isWifiEnabledAndShowInfo(Context context){
		WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    WifiInfo currentWifi = mainWifi.getConnectionInfo();
	    if((currentWifi==null || currentWifi.getSSID()== null || currentWifi.getSSID().isEmpty()) && !isWifiAPEnabled(mainWifi)){
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
	 
	protected Class<T> busObjectInterfaceType;

	public Class<T> getBusObjectInterfaceType(){
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
	
	public void setChannelName(String name){
		this.channelName = name; 
	}
	
	public synchronized void addObserver(Observer obs) {
        Log.i(TAG, "addObserver(" + obs + ")");
		if (observers.indexOf(obs) < 0) {
			observers.add(obs);
		}
	}
	public synchronized void removeObserver(Observer obs) {
        Log.i(TAG, "deleteObserver(" + obs + ")");
		observers.remove(obs);
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
        if(observers.isEmpty()){
        	notificationQueue.add(arg);
        }
        for (Observer obs : observers) {
            Log.i(TAG, "notify observer = " + obs);
            obs.doAction(arg);
        }
	}
	private List<Observer> observers = new ArrayList<Observer>();
	private List<LobbyObserver> lobbyObservers = new ArrayList<LobbyObserver>();
	private List<String> foundChannels = new ArrayList<String>();

	private String uniqueID  = "";
	private Object remoteObject;
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

	
	public synchronized void setRemoteObject(Object remoteObject) {
		this.remoteObject = remoteObject;			
	}
	
	public synchronized Object getRemoteObject(){
		return remoteObject;
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
	
	public void doAction(int action){
		switch(action){
		case HOST_CHANNEL: hostInitChannel(); hostStartChannel(); break;
		case JOIN_CHANNEL: joinChannel(); break;
		case LEAVE_CHANNEL: leaveChannel(); break;
		case CLOSE_CHANNEL: hostStopChannel(); break;
		case QUIT: quit(); break;
		default: break;
		}
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
            obs.ConnectionStateChanged();
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

}
