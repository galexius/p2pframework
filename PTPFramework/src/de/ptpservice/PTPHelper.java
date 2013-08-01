package de.ptpservice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.bus.annotation.BusSignalHandler;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import de.p2pservice.R;
import de.ptpservice.views.AbstractLobbyActivity;
import de.ptpservice.views.LobbyObserver;

public class PTPHelper {
    static {
        Log.i("P2PHelper","System.loadLibrary(\"alljoyn_java\")");
        System.loadLibrary("alljoyn_java");
    }    
	
	public class MessageInfoHolder {
		public String[] data;
		public String sentBy;
		public int messageType;
	}
    
    class PTPBusObject implements PTPBusObjectInterface,BusObject{
		@Override
		@BusSignal
		public void SendDataToAllPeers(String sentFrom,int arg,String[] data) {}    	
    }   
    
    
    
    class PTPBusHandler implements PTPBusObjectInterface, BusObject {
    	@Override
    	@BusSignalHandler(iface = "de.ptpservice.PTPBusObjectInterface", signal = "SendDataToAllPeers")
    	public void SendDataToAllPeers(String sentFrom,int arg, String[] data) {
    		MessageInfoHolder messageInfoHolder = new MessageInfoHolder();
    		messageInfoHolder.data = data;
    		messageInfoHolder.sentBy = sentFrom;
    		messageInfoHolder.messageType = arg;
    		
    		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.DATA_SENT,messageInfoHolder);
    		messageHandler.sendMessage(obtainedMessage);		
    	}
    }
    
    
    @SuppressLint("HandlerLeak")
	class MessageHandler extends Handler{
    	public static final int BUS_DISCONNECTED = 0;
    	public static final int SESSION_LOST = 1;
    	public static final int MEMBER_JOINED = 2;
    	public static final int MEMBER_LEFT = 3;
    	public static final int ADVERTISED_NAME_FOUND = 4;
    	public static final int ADVERTISED_NAME_LOST = 5;    	
    	public static final int CONNECTION_STATE_CHANED = 6;    	
    	public static final int DATA_SENT = 7;    	
    	
		public MessageHandler(Looper looper) {
			super(looper);
		}
		
    	@Override
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
				case BUS_DISCONNECTED: notifyBusDisconnected(); break;
				case SESSION_LOST: notifySessionLost(); break;
				case MEMBER_JOINED: notifyMemberJoined((String) msg.obj); break;
				case MEMBER_LEFT: notifyMemberLeft((String) msg.obj); break;
				case ADVERTISED_NAME_FOUND: notifyFoundAdvertisedName((String) msg.obj); break;
				case ADVERTISED_NAME_LOST: notifyLostAdvertisedName((String) msg.obj); break;
				case CONNECTION_STATE_CHANED: notifyConnectionStateChanged(msg.arg1); break;
				case DATA_SENT: notifyDataSent((MessageInfoHolder) msg.obj); break;
    		}
    	}

		private void notifyBusDisconnected() {
			for (BusObserver obs : busObservers) {
	            obs.busDisconnected();
	        }	
		}

		private void notifyLostAdvertisedName(String sessionName) {
			for (BusObserver obs : busObservers) {
	            obs.lostAdvertisedName(sessionName);
	        }			
		}
		
		private void notifyFoundAdvertisedName(String sessionName) {
			for (BusObserver obs : busObservers) {
	            obs.foundAdvertisedName(sessionName);
	        }			
		}
		
		private void notifyDataSent(MessageInfoHolder message) {
			for (DataObserver obs : dataObservers) {
				obs.dataSentToAllPeers(message.sentBy, message.messageType, message.data);
			}			
		}

		private void notifyMemberJoined(String uniqueId) {
			for (SessionObserver obs : sessionObservers) {
	            obs.memberJoined(uniqueId);
	        }	
		}

		private void notifyMemberLeft(String uniqueId) {
			for (SessionObserver obs : sessionObservers) {
	            obs.memberLeft(uniqueId);
	        }
		}

		private synchronized void notifySessionLost() {
			for (SessionObserver obs : sessionObservers) {
	            obs.sessionLost();
	        }
		}
		private void notifyConnectionStateChanged(int state) {
			for (LobbyObserver obs : lobbyObservers) {
				obs.connectionStateChanged(state);
			}
			
		}
    }
    
    class HelperBusObserver implements BusObserver{

    	@Override
    	public void lostAdvertisedName(String sessionName) {    		
    		instance.removeAdvertisedName(sessionName);    		
    	}
    	
		@Override
		public void foundAdvertisedName(String sessionName) {
			instance.addAdvertisedName(sessionName);    				
		}

		@Override
		public void busDisconnected() {
						
		}
    }

    
	public final static int CONNECT_BUS = 0;
	public final static int JOIN_SESSION = 1;
	public final static int HOST_SESSION = 2;
	public final static int LEAVE_SESSION = 3;
	public final static int CLOSE_SESSION = 4;
	public final static int DISCONNECT = 5;
	public final static int QUIT = 6;
	
	
	public final static int DISCONNECTED = 7;
	public final static int CONNECTED = 8;
	public final static int SESSION_JOINED = 9;
	public final static int SESSION_LEFT = 10;
	public final static int SESSION_HOSTED = 11;
	public final static int SESSION_CLOSED = 12;
	public static final int SESSION_NAME_EXISTS = 13;
	
	public final static String ENCODING_UTF8 = "UTF-8";
	
	private short contactPort = 100;
	
	private static PTPHelper instance = null;
	private MessageHandler messageHandler;
	
	public static void initHelper(String applicationName, Context context, Class<? extends AbstractLobbyActivity> lobbyClass){
		instance = new PTPHelper(applicationName);	
		instance.addBusObserver(instance.new HelperBusObserver());
		PACKAGE_NAME = context.getPackageName();
        if(!instance.isWifiEnabled(context)){
        	CharSequence text = context.getResources().getText(R.string.no_wifi);
	    	int duration = Toast.LENGTH_LONG;
	    	Toast toast = Toast.makeText(context, text, duration);
	    	toast.show();
	    	Log.i(TAG, "ShowNoWiFi");
        	return;          
        	
        }
         
		Intent service = new Intent(context,PTPService.class);
        boolean bound = context.bindService(service,new PTPServiceConnection(),Service.BIND_AUTO_CREATE);
        if (!bound) {
            Log.e(TAG, "onCreate(): failed to bind Service()");
        }        
        new Intent(context,lobbyClass);
	}
	
	

	public static void deinit(){
		instance.finish();
		instance = null;
	}
	
	private void finish() {
		disconnect();
		quit();		
	}

	public static PTPHelper getInstance(){		
		return instance;
	}
	
	private static final String TAG = "P2PHelper";
	
	
	public static String PACKAGE_NAME;
	private String hostSessionName = "";
	private String sessionName = "";
	private Queue<Integer> notificationQueue = new LinkedList<Integer>();
	private String applicationName;
	
	private PTPHelper(String applicationName) {
		Log.d("P2PHelper", "onConstructor");
		this.applicationName = applicationName;
		this.busObjectInterfaceType = PTPBusObjectInterface.class;
		this.busObject = new PTPBusObject();
		this.signalHandler  = new PTPBusHandler();
		messageHandler = new MessageHandler(Looper.getMainLooper());
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public boolean isWifiEnabled(Context context){
		WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    WifiInfo currentWifi = mainWifi.getConnectionInfo();
	    if((currentWifi==null || currentWifi.getSSID()== null || currentWifi.getSSID().isEmpty()) && !isWifiAPEnabled(mainWifi)){	    	
	    	return false;
	    }
	    return true;
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

	public synchronized String getHostSessionName() {
		return hostSessionName;
	}
	
	private synchronized void setHostSessionName(String name){
		this.hostSessionName = name; 
		isHost = true;
	}
	public synchronized String getSessionName() {
		return sessionName;
	}
	
	private synchronized void setClientSessionName(String name){
		this.sessionName = name; 
		isHost = false;
	}
	
	public synchronized void addObserver(ServiceHelperObserver obs) {
        Log.i(TAG, "addObserver(" + obs + ")");
		if (helperObservers.indexOf(obs) < 0) {
			helperObservers.add(obs);
		}
	}
	public synchronized void removeObserver(ServiceHelperObserver obs) {
        Log.i(TAG, "deleteObserver(" + obs + ")");
		helperObservers.remove(obs);
	}
	
	public void addBusObserver(BusObserver obs) {
		Log.i(TAG, "addObserver(" + obs + ")");
		if (busObservers.indexOf(obs) < 0) {
			busObservers.add(obs);
		}
	}
	public void removeBusObserver(BusObserver obs) {
		Log.i(TAG, "deleteObserver(" + obs + ")");
		busObservers.remove(obs);
	}
	
	public void addSessionObserver(SessionObserver obs) {
		Log.i(TAG, "addObserver(" + obs + ")");
		if (sessionObservers.indexOf(obs) < 0) {
			sessionObservers.add(obs);
		}
	}
	public void removeSessionObserver(SessionObserver obs) {
		Log.i(TAG, "deleteObserver(" + obs + ")");
		sessionObservers.remove(obs);
	}
	
	public void addLobbyObserver(LobbyObserver obs) {
		Log.i(TAG, "addObserver(" + obs + ")");
		if (lobbyObservers.indexOf(obs) < 0) {
			lobbyObservers.add(obs);
		}
	}
	
	public void removeLobbyObserver(LobbyObserver obs) {
		Log.i(TAG, "deleteObserver(" + obs + ")");
		lobbyObservers.remove(obs);
	}

	

	private void notifyHelperObservers(int arg) {
        Log.i(TAG, "notifyObservers(" + arg + ")");
        if(helperObservers.isEmpty()){
        	notificationQueue.add(arg);
        }
        for (ServiceHelperObserver obs : helperObservers) {
            Log.i(TAG, "notify observer = " + obs);
            obs.doAction(arg);
        }
	}
	
	public void sendDataToAllPeers(int arg,String[] data){
		if(signalEmitter==null){
			Log.e(TAG, "SignalEmitter not set yet");
			return;
		}
		PTPBusObjectInterface emitter = (PTPBusObjectInterface) signalEmitter;
		emitter.SendDataToAllPeers(uniqueID, arg, data);
	}

	
	private ArrayList<ServiceHelperObserver> helperObservers = new ArrayList<ServiceHelperObserver>();
	private ArrayList<LobbyObserver> lobbyObservers = new ArrayList<LobbyObserver>();
	private ArrayList<BusObserver> busObservers = new ArrayList<BusObserver>();
	private ArrayList<SessionObserver> sessionObservers = new ArrayList<SessionObserver>();
	private ArrayList<DataObserver> dataObservers = new ArrayList<DataObserver>();
	private ArrayList<String> foundSessions = new ArrayList<String>();
	private ArrayList<SessionJoinRule> joinRules = new ArrayList<SessionJoinRule>();
	private HashMap<String,Object> proxyObjectsMap = new HashMap<String,Object>();

	private String uniqueID  = "";
	private String playerName = "";
	private int connectionState;
	private BusObject busObject;
	private Object signalHandler;
	private Object signalEmitter = null;
	private boolean isHost;
	
	public void joinSession(String sessionName) {
		setClientSessionName(sessionName);
		notifyHelperObservers(PTPService.JOIN_SESSION);
	}
	
	public void connectAndStartDiscover() {
		foundSessions.clear();
		notifyHelperObservers(PTPService.CONNECT);
		notifyHelperObservers(PTPService.START_DISCOVERY);		
	}
	
	public void disconnect() {	
		notifyHelperObservers(PTPService.CANCEL_DISCOVERY);
		notifyHelperObservers(PTPService.DISCONNECT);
	}
	
	
	public void leaveSession() {
		notifyHelperObservers(PTPService.CANCEL_ADVERTISE);
		notifyHelperObservers(PTPService.LEAVE_SESSION);
	}

	public void hostStartSession(String sessionName) {
		setHostSessionName(sessionName);
		notifyHelperObservers(PTPService.UNBIND_SESSION);
		notifyHelperObservers(PTPService.RELEASE_NAME);
		notifyHelperObservers(PTPService.BIND_SESSION);
		notifyHelperObservers(PTPService.REQUEST_NAME);
		notifyHelperObservers(PTPService.ADVERTISE);
		
	}
	
	public void hostStopSession() {
		notifyHelperObservers(PTPService.CANCEL_ADVERTISE);
		notifyHelperObservers(PTPService.UNBIND_SESSION);
		notifyHelperObservers(PTPService.RELEASE_NAME);
	}
	public void quit() {
		notifyHelperObservers(PTPService.EXIT);
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

	public synchronized void setUniqueID(String id) {
		this.uniqueID = id;
	}
	
	public synchronized Object getSignalEmitter(){
		return this.signalEmitter;
	}

	public synchronized void setSignalEmitter(Object signalEmitter){
		this.signalEmitter = signalEmitter;		
	}
	

	private void addAdvertisedName(String name) {
		foundSessions.add(name);
	}


	private void removeAdvertisedName(String name) {
		if(foundSessions.contains(name)){
			foundSessions.remove(name);
		}
	}

	public List<String> getFoundSessions(){
		return foundSessions;
	}


	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public boolean isHost(){
		return isHost;
	}
	

	public synchronized int getConnectionState(){
		return connectionState;
	}
	
	
	public synchronized void acquireMissedNotifications() {
		while (!notificationQueue.isEmpty()) {
			notifyHelperObservers(notificationQueue.poll());			
		}
	}

	public String getApplicationName() {
		return applicationName;
	}
	
	public synchronized void setConnectionState(int state){
		this.connectionState = state;		
		notifyLobbyObserversConnectionStateChanged();
	}
	
	private void notifyLobbyObserversConnectionStateChanged() {
		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.CONNECTION_STATE_CHANED, connectionState,0);
		messageHandler.sendMessage(obtainedMessage);			
	}
	
	public synchronized void notifyBusDisconnected() {
		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.BUS_DISCONNECTED);
		messageHandler.sendMessage(obtainedMessage);	
	}

	public synchronized void notifyLostAdvertisedName(String sessionName) {
		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.ADVERTISED_NAME_LOST, sessionName);
		messageHandler.sendMessage(obtainedMessage);
	}
	
	public synchronized void notifyFoundAdvertisedName(String sessionName) {
		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.ADVERTISED_NAME_FOUND, sessionName);
		messageHandler.sendMessage(obtainedMessage);			
	}

	public synchronized void notifyMemberJoined(String membersId) {
		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.MEMBER_JOINED, membersId);
		messageHandler.sendMessage(obtainedMessage);	
	}

	public synchronized void notifyMemberLeft(String membersId) {
		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.MEMBER_LEFT, membersId);
		messageHandler.sendMessage(obtainedMessage);
	}

	public synchronized void notifySessionLost() {
		Message obtainedMessage = messageHandler.obtainMessage(MessageHandler.SESSION_LOST);
		messageHandler.sendMessage(obtainedMessage);
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
	public synchronized void addProxyObject(String peerID,Object proxyObject){
		if (!proxyObjectsMap.containsKey(peerID)) {
			proxyObjectsMap.put(peerID, proxyObject);
		}
	}
	
	public synchronized void addDataObserver(DataObserver dataObserver){
		if (dataObservers.indexOf(dataObserver) < 0) {
			dataObservers.add(dataObserver);
		}
	}
	
	public synchronized void removeDataObserver(DataObserver dataObserver){
		if (dataObservers.indexOf(dataObserver) >= 0) {
			dataObservers.remove(dataObserver);
		}
	}
	
	public synchronized void removeAllDataObservers(){
		dataObservers.clear();
	}
	
	public synchronized void removeProxyObjectForPeerID(String peerID){
		if (proxyObjectsMap.containsKey(peerID)) {
			proxyObjectsMap.remove(peerID);
		}
	}
	
	public synchronized void removeAllProxyObjects(){
		proxyObjectsMap.clear();
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

	public synchronized short getContactPort() {
		return contactPort;
	}

	public synchronized void setContactPort(short contactPort) {
		this.contactPort = contactPort;
	}
}
