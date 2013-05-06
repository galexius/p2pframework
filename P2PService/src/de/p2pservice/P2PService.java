package de.p2pservice;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public abstract class P2PService<T> extends Service implements Observer {

	public static final int EXIT = 1;
	public static final int CONNECT = 2;
	public static final int DISCONNECT = 3;
	public static final int START_DISCOVERY = 4;
	public static final int CANCEL_DISCOVERY = 5;
	public static final int REQUEST_NAME = 6;
	public static final int RELEASE_NAME = 7;
	public static final int BIND_SESSION = 8;
	public static final int UNBIND_SESSION = 9;
	public static final int ADVERTISE = 10;
	public static final int CANCEL_ADVERTISE = 11;
	public static final int JOIN_SESSION = 12;
	public static final int LEAVE_SESSION = 13;
	
	private BusAttachmentState busAttachmentState = BusAttachmentState.DISCONNECTED;
	
	private HostChannelState hostChannelState = HostChannelState.IDLE;
	
	static final String TAG = "P2PService";
	
	private BackgroundHandler backgroundHandler = null;
	protected String packageName;

	
	private BusAttachment bus;
	
	protected String channelName = "Channel";
	
	protected short contactPort = 100;
	protected String objectPath = "/ObjectPath";

	
	public void onCreate() {

		Log.i(TAG, "onCreate()");
		packageName = this.getPackageName();
		Context applicationContext = getApplicationContext();		
		p2pInfoHolder = (P2PInfoHolder) applicationContext;
		if(p2pInfoHolder== null){
			Log.e(TAG, "Context must implement " + P2PInfoHolder.class.getSimpleName());
			return;
		}
		p2pInfoHolder.addObserver(this);
		org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());
		bus = new BusAttachment(packageName,  BusAttachment.RemoteMessage.Receive);
        startBusThread();
        backgroundHandler.connect();
        backgroundHandler.startDiscovery();
 	}
	
	protected abstract Class<T> getBusObjectInterface();
	
	public synchronized void doAction(int arg){

			switch(arg){
				case CONNECT: backgroundHandler.connect();break;
				case DISCONNECT: backgroundHandler.disconnect();break;
				case START_DISCOVERY: backgroundHandler.startDiscovery();break;
				case CANCEL_DISCOVERY: backgroundHandler.cancelDiscovery();break;
				case REQUEST_NAME: backgroundHandler.requestName();break;
				case RELEASE_NAME: backgroundHandler.releaseName();break;
				case BIND_SESSION: backgroundHandler.bindSession();break;
				case UNBIND_SESSION: backgroundHandler.unbindSession();break;
				case ADVERTISE: backgroundHandler.advertise();break;
				case CANCEL_ADVERTISE: backgroundHandler.cancelAdvertise();break;
				case JOIN_SESSION: backgroundHandler.joinSession();break;
				case LEAVE_SESSION: backgroundHandler.leaveSession();break;
			    default: break;
			}
	}
	
	@SuppressLint("HandlerLeak")
	private final class BackgroundHandler extends Handler {
		public BackgroundHandler(Looper looper) {
			super(looper);
		}
		public void exit() {
			Log.i(TAG, "backgroundHandler.exit()");
			Message msg = backgroundHandler.obtainMessage(EXIT);
			backgroundHandler.sendMessage(msg);
		}
		public void connect() {
			Log.i(TAG, "backgroundHandler.connect()");
			Message msg = backgroundHandler.obtainMessage(CONNECT);
			backgroundHandler.sendMessage(msg);
		}
		
		public void disconnect() {
			Log.i(TAG, "backgroundHandler.disconnect()");
			Message msg = backgroundHandler.obtainMessage(DISCONNECT);
			backgroundHandler.sendMessage(msg);
		}

		public void startDiscovery() {
			Log.i(TAG, "backgroundHandler.startDiscovery()");
			Message msg = backgroundHandler.obtainMessage(START_DISCOVERY);
			backgroundHandler.sendMessage(msg);
		}
		
		public void cancelDiscovery() {
			Log.i(TAG, "backgroundHandler.startDiscovery()");
			Message msg = backgroundHandler.obtainMessage(CANCEL_DISCOVERY);
			backgroundHandler.sendMessage(msg);
		}

		public void requestName() {
			Log.i(TAG, "backgroundHandler.requestName()");
			Message msg = backgroundHandler.obtainMessage(REQUEST_NAME);
			backgroundHandler.sendMessage(msg);
		}

		public void releaseName() {
			Log.i(TAG, "backgroundHandler.releaseName()");
			Message msg = backgroundHandler.obtainMessage(RELEASE_NAME);
			backgroundHandler.sendMessage(msg);
		}

		public void bindSession() {
			Log.i(TAG, "backgroundHandler.bindSession()");
			Message msg = backgroundHandler.obtainMessage(BIND_SESSION);
			backgroundHandler.sendMessage(msg);
		}

		public void unbindSession() {
			Log.i(TAG, "backgroundHandler.unbindSession()");
			Message msg = backgroundHandler.obtainMessage(UNBIND_SESSION);
			backgroundHandler.sendMessage(msg);
		}

		public void advertise() {
			Log.i(TAG, "backgroundHandler.advertise()");
			Message msg = backgroundHandler.obtainMessage(ADVERTISE);
			backgroundHandler.sendMessage(msg);
		}

		public void cancelAdvertise() {
			Log.i(TAG, "backgroundHandler.cancelAdvertise()");
			Message msg = backgroundHandler.obtainMessage(CANCEL_ADVERTISE);
			backgroundHandler.sendMessage(msg);
		}

		public void joinSession() {
			Log.i(TAG, "backgroundHandler.joinSession()");
			Message msg = backgroundHandler.obtainMessage(JOIN_SESSION);
			backgroundHandler.sendMessage(msg);
		}

		public void leaveSession() {
			Log.i(TAG, "backgroundHandler.leaveSession()");
			Message msg = backgroundHandler.obtainMessage(LEAVE_SESSION);
			backgroundHandler.sendMessage(msg);
		}
		

		/**
		 * The message handler for the worker thread that handles background
		 * tasks for the AllJoyn bus.
		 */
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CONNECT:
	            doConnect();
            	break;
	        case DISCONNECT:
		        doDisconnect();
		    	break;
            case START_DISCOVERY:
	            doStartDiscovery();
            	break;
	        case CANCEL_DISCOVERY:
		        doStopDiscovery();
		    	break;
	        case REQUEST_NAME:
		        doRequestName();
		    	break;
	        case RELEASE_NAME:
		        doReleaseName();
		    	break;		
	        case BIND_SESSION:
		        doBindSession();
		    	break;
	        case UNBIND_SESSION:
		        doUnbindSession();
		        break;
	        case ADVERTISE:
		        doAdvertise();
		    	break;
	        case CANCEL_ADVERTISE:
		        doCancelAdvertise();		        
		    	break;	
	        case JOIN_SESSION:
		        doJoinSession();
		    	break;
	        case LEAVE_SESSION:
		        doLeaveSession();
		        break;
	        case EXIT:
                getLooper().quit();
                break;
			default:
				break;
			}
		}

	}
	
    private void doStartDiscovery() {
        Log.i(TAG, "doStartDiscovery()");
    	assert(busAttachmentState == BusAttachmentState.CONNECTED);
      	Status status = bus.findAdvertisedName(packageName);
    	if (status == Status.OK) {
        	busAttachmentState = BusAttachmentState.DISCOVERING;
        	return;
    	} else {
    		Log.e(TAG, "startDiscovery failed");
        	return;
    	}
    }
    
    private void doStopDiscovery() {
        Log.i(TAG, "doStopDiscovery()");
    	assert(busAttachmentState == BusAttachmentState.CONNECTED);
      	bus.cancelFindAdvertisedName(packageName);
      	busAttachmentState = BusAttachmentState.CONNECTED;
    }
	
	private void doConnect() {
		Log.i(TAG, "doConnect()");
		
		assert (busAttachmentState == BusAttachmentState.DISCONNECTED);
		bus.useOSLogging(true);
		bus.setDebugLevel("ALLJOYN_JAVA", 7);
		busListener = new BusListener();
		bus.registerBusListener(busListener);
		BusObject busObject =p2pInfoHolder.getBusObject(); 
		Status status;
		if(busObject != null){
			status = bus.registerBusObject(p2pInfoHolder.getBusObject(), objectPath);
			if (Status.OK != status) {			
				Log.e(TAG, "Cannot register : " + status);
				return;
			}
		}

		status = bus.connect();
		if (status != Status.OK) {			
			Log.e(TAG, "Cannot connect");
			return;
		}

		status = bus.registerSignalHandlers(p2pInfoHolder.getSignalHandler());
		if (status != Status.OK) {
			Log.e(TAG, "Cannot register signalHandler");
			return;
		}
		busAttachmentState = BusAttachmentState.CONNECTED;
		p2pInfoHolder.setUniqueID(bus.getUniqueName());
	}

	private boolean doDisconnect() {
		Log.i(TAG, "doDisonnect()");
		assert (busAttachmentState == BusAttachmentState.CONNECTED);
		bus.unregisterBusListener(busListener);
		bus.disconnect();
		busAttachmentState = BusAttachmentState.DISCONNECTED;
		return true;
	}

	private void doRequestName() {
		Log.i(TAG, "doRequestName()");
		int stateRelation = busAttachmentState
				.compareTo(BusAttachmentState.DISCONNECTED);
		assert (stateRelation >= 0);

		Status status = bus.requestName(packageName + "." +channelName,
				BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);
		if (status == Status.OK) {
			hostChannelState = HostChannelState.NAMED;
		} else {
			Log.e(TAG, "cannot request name");
		}
	}

	private void doReleaseName() {
		Log.i(TAG, "doReleaseName()");


		int stateRelation = busAttachmentState
				.compareTo(BusAttachmentState.DISCONNECTED);
		assert (stateRelation >= 0);
		assert (busAttachmentState == BusAttachmentState.CONNECTED || busAttachmentState == BusAttachmentState.DISCOVERING);

		assert (hostChannelState == HostChannelState.NAMED);

		bus.releaseName(channelName);
		hostChannelState = HostChannelState.IDLE;
	}
	
    private void doAdvertise() {
        Log.i(TAG, "doAdvertise()");
     
    	Log.i(TAG, "Advertised name: " + channelName );
        Status status = bus.advertiseName(packageName+"."+channelName, SessionOpts.TRANSPORT_WLAN);
        
        if (status == Status.OK) {
        	hostChannelState = HostChannelState.ADVERTISED;
        } else {
        	Log.e(TAG, "doAdvertise() failed");
        	return;
        }
    }
    
   
    private void doCancelAdvertise() {
        Log.i(TAG, "doCancelAdvertise()");
       
    	String wellKnownName = packageName + "." + channelName;
        Status status = bus.cancelAdvertiseName(wellKnownName, SessionOpts.TRANSPORT_ANY);
        
        if (status != Status.OK) {
        	Log.e(TAG, "cancelAdvertise() failed : " + status);
        	return;
        }
     	hostChannelState = HostChannelState.BOUND;
    }

	private void doBindSession() {
		Log.i(TAG, "doBindSession()");

		Mutable.ShortValue mutableContactPort = new Mutable.ShortValue(contactPort);
		SessionOpts sessionOpts = new SessionOpts(SessionOpts.TRAFFIC_MESSAGES,
				true, SessionOpts.PROXIMITY_ANY, SessionOpts.TRANSPORT_WLAN);

		sessionPortListener = new SessionPortListener(){
			public boolean acceptSessionJoiner(short sessionPort,
								String joiner, SessionOpts sessionOpts) {
					Log.d(TAG, "Accept Session Joiner: " + joiner);
					if(sessionPort == contactPort){					
						return true;
					}
					return false;
			}						
			public void sessionJoined(short sessionPort, int id,
						String joiner) {
					
					Log.i(TAG, "SessionPortListener.sessionJoined("
							+ sessionPort + ", " + id + ", " + joiner + ")");
					hostSessionId = id;
					
					SignalEmitter emitter = new SignalEmitter(p2pInfoHolder.getBusObject(), id,
							SignalEmitter.GlobalBroadcast.Off);
					hostInterface = emitter
							.getInterface(getBusObjectInterface());
					p2pInfoHolder.setSignalEmiter(hostInterface);
			}
		};
		
		Status status = bus.bindSessionPort(mutableContactPort, sessionOpts,
				sessionPortListener);

		if (status == Status.OK) {
			hostChannelState = HostChannelState.BOUND;
		} else {
			Log.e(TAG, "Unable to bind session contact port: (" + status + ")");
			return;
		}		
		joinedToSelf = true;
	}

	public static enum UseChannelState {
		IDLE, 
		JOINED,
	}

	private void doJoinSession() {
		Log.i(TAG, "doJoinSession()");
		if (hostChannelState != HostChannelState.IDLE) {
			if (joinedToSelf) {
				return;
			}
		}

		String wellKnownName = packageName + "." + channelName;

		SessionOpts sessionOpts = new SessionOpts(SessionOpts.TRAFFIC_MESSAGES,
				true, SessionOpts.PROXIMITY_ANY, SessionOpts.TRANSPORT_WLAN);
		Mutable.IntegerValue sessionId = new Mutable.IntegerValue();

		Status status = bus.joinSession(wellKnownName, contactPort, sessionId,
			sessionOpts, new SessionListener(){
				public void sessionLost(int sessionId) {
					Log.i(TAG, "BusListener.sessionLost(" + sessionId + ")");
				}
		});
		
		if (status == Status.OK) {
			useSessionId = sessionId.value;			
			Log.i(TAG, "doJoinSession(): use sessionId is " + useSessionId);
		} else {
			Log.e(TAG, "Unable to join session: (" + status + ")");
			return;
		}
		SignalEmitter emitter = new SignalEmitter(p2pInfoHolder.getBusObject(), useSessionId,
				SignalEmitter.GlobalBroadcast.Off);
		clientInterface = emitter.getInterface(getBusObjectInterface());
		
		p2pInfoHolder.setSignalEmiter(clientInterface);
	}


	T clientInterface = null;
	T hostInterface = null;
	
	private void doLeaveSession() {
		Log.i(TAG, "doLeaveSession()");
		if (joinedToSelf == false) {
			bus.leaveSession(useSessionId);
		}
		useSessionId = -1;
		joinedToSelf = false;
	}


	int useSessionId = -1;

	int hostSessionId = -1;

	boolean joinedToSelf = false;

	
	
	private P2PInfoHolder p2pInfoHolder;
	private BusListener busListener;
	private SessionPortListener sessionPortListener;

	private void doUnbindSession() {
		Log.i(TAG, "doUnbindSession()");

		bus.unbindSessionPort(contactPort);
		hostInterface = null;
		hostChannelState = HostChannelState.NAMED;
	}

	public static enum BusAttachmentState {
		DISCONNECTED,
		CONNECTED, 
		DISCOVERING		
	}

	
	public static enum HostChannelState {
		IDLE, 
		NAMED, 		
		BOUND, 
		ADVERTISED, 		
		CONNECTED		
	}

	
	private void startBusThread() {
		HandlerThread busThread = new HandlerThread("BackgroundHandler");
		busThread.start();
		backgroundHandler = new BackgroundHandler(busThread.getLooper());
	}

	
	private void stopBusThread() {
		backgroundHandler.exit();
	}

	public void destroy() {
		Log.i(TAG, "onDestroy()");
		backgroundHandler.disconnect();
		stopBusThread();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    static {
        Log.i(TAG, "System.loadLibrary(\"alljoyn_java\")");
        System.loadLibrary("alljoyn_java");
    }    


}
