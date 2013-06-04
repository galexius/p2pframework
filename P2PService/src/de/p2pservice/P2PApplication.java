package de.p2pservice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.alljoyn.bus.BusObject;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;



public abstract class P2PApplication extends Application {
	public final static int CONNECT_BUS = 0;
	public final static int JOIN_CHANNEL = 1;
	public final static int HOST_CHANNEL = 2;
	public final static int LEAVE_CHANNEL = 3;
	public final static int CLOSE_CHANNEL = 4;
	public final static int DISCONNECT = 5;
	public final static int QUIT = 6;
	
	protected static final String TAG = "P2PApplication";
	
	public static String PACKAGE_NAME;
	protected String hostChannelName = "";
	protected String channelName = "";
	
	public void onCreate() {
		Log.d("P2PApplication", "onCreate");
        PACKAGE_NAME = getPackageName();
		initBusObject();
		initSignalHandler();
        if(isWifiEnabledAndShowInfo())
        	return;  
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
	

	protected void notifyObservers(int arg) {
        Log.i(TAG, "notifyObservers(" + arg + ")");
        for (Observer obs : mObservers) {
            Log.i(TAG, "notify observer = " + obs);
            obs.doAction(arg);
        }
	}
	private List<Observer> mObservers = new ArrayList<Observer>();
	private List<String> foundChannels = new ArrayList<String>();

	private String uniqueID  = "";
	private Object remoteObject;
	private String playerName = "";
	
	public synchronized void joinChannel() {
		notifyObservers(P2PService.JOIN_SESSION);
	}
	
	public synchronized void connectAndStartDiscover() {
		initBusObject();
		initSignalHandler();
		notifyObservers(P2PService.CONNECT);
		notifyObservers(P2PService.START_DISCOVERY);		
	}
	
	public synchronized void disconnect() {	
		notifyObservers(P2PService.CANCEL_DISCOVERY);
		notifyObservers(P2PService.DISCONNECT);
	}
	
	
	public synchronized void leaveChannel() {
		notifyObservers(P2PService.LEAVE_SESSION);
		notifyObservers(P2PService.CANCEL_ADVERTISE);
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
	public synchronized void quit() {
		notifyObservers(P2PService.EXIT);
	}

	
	public synchronized void setRemoteObject(Object remoteObject) {
		this.remoteObject = remoteObject;			
	}
	
	public synchronized Object getRemoteObject(){
		return remoteObject;
	}
	
	abstract public BusObject getBusObject();
	
	abstract public Object getSignalHandler();

	public String getUniqueID() {
		return this.uniqueID;
	}

	public void setUniqueID(String id) {
		this.uniqueID = id;
	}

	abstract public void setSignalEmiter(Object signalEmitter);
	

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
	
	abstract protected void initBusObject();
	abstract protected void initSignalHandler();

}
