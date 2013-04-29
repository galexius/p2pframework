

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
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class P2PService<T extends BusObject> extends Service {

	private static final int EXIT = 1;
	private static final int CONNECT = 2;
	private static final int DISCONNECT = 3;
	private static final int START_DISCOVERY = 4;
    private static final int CANCEL_DISCOVERY = 5;
	private static final int REQUEST_NAME = 6;
	private static final int RELEASE_NAME = 7;
	private static final int BIND_SESSION = 8;
	private static final int UNBIND_SESSION = 9;
	private static final int ADVERTISE = 10;
	private static final int CANCEL_ADVERTISE = 11;
	private static final int JOIN_SESSION = 12;
	private static final int LEAVE_SESSION = 13;
	
	private BusAttachmentState mBusAttachmentState = BusAttachmentState.DISCONNECTED;
	
	private HostChannelState mHostChannelState = HostChannelState.IDLE;
	
	static final String TAG = "P2PService";
	
	private BackgroundHandler mBackgroundHandler = null;
	private String packageName;

	
	private BusAttachment mBus;
	
	private Class<T> interfaceClass;
	private T busObject;
	private BusListener busListener;
	private Object signalHandler;
	private String channelName;
	
	private short contactPort = 27;
	private String objectPath = "/Default";

	private SessionListener sessionListener;
	private SessionPortListener sessionPortListener;
	
	public void onCreate() {
        Log.i(TAG, "onCreate()");
        startBusThread();
 	}
	
	public P2PService(Class<T> intefaceClass, BusAttachment.RemoteMessage remoteMessageBehaviour){
		this.interfaceClass = intefaceClass;	
		packageName = this.getPackageName();
		mBus = new BusAttachment(packageName, remoteMessageBehaviour);
	}
	
	public void registerSignalHandler(Object signalHandler){
		this.signalHandler = signalHandler;
		
	}
	
	public void connectToBusAsync(T busObject,BusListener busListener,String objectPath){
		this.busObject = busObject;
		this.busListener = busListener;
		this.objectPath = objectPath;
		mBackgroundHandler.connect();
	}
	
	public void startDiscoveringChannels(){
		mBackgroundHandler.startDiscovery();		
	}
	
	public void stopDiscoveringChannels(){
		mBackgroundHandler.cancelDiscovery();
	}
	
	public void requestName(String channelName){
		this.channelName = channelName;
		mBackgroundHandler.requestName();
	}
	
	public void releaseName(){
		mBackgroundHandler.releaseName();
	}
	
	public void createSession(SessionPortListener sessionPortListener){
		this.sessionPortListener = sessionPortListener;
		mBackgroundHandler.bindSession();
	}
	
	public void joinSession(SessionListener sessionListener){
		this.sessionListener = sessionListener;
		mBackgroundHandler.joinSession();
	}
	
	public void destroySession(){
		mBackgroundHandler.unbindSession();
	}
	
	public void leaveSession(){
		mBackgroundHandler.leaveSession();
	}
	
	public void advertiseChannelName(){
		mBackgroundHandler.advertise();
	}
	
	public void stopAdvertisingChannelName(){
		mBackgroundHandler.cancelAdvertise();
	}
	
	@SuppressLint("HandlerLeak")
	private final class BackgroundHandler extends Handler {
		public BackgroundHandler(Looper looper) {
			super(looper);
		}
		public void exit() {
			Log.i(TAG, "mBackgroundHandler.exit()");
			Message msg = mBackgroundHandler.obtainMessage(EXIT);
			mBackgroundHandler.sendMessage(msg);
		}
		public void connect() {
			Log.i(TAG, "mBackgroundHandler.connect()");
			Message msg = mBackgroundHandler.obtainMessage(CONNECT);
			mBackgroundHandler.sendMessage(msg);
		}
		
		public void disconnect() {
			Log.i(TAG, "mBackgroundHandler.disconnect()");
			Message msg = mBackgroundHandler.obtainMessage(DISCONNECT);
			mBackgroundHandler.sendMessage(msg);
		}

		public void startDiscovery() {
			Log.i(TAG, "mBackgroundHandler.startDiscovery()");
			Message msg = mBackgroundHandler.obtainMessage(START_DISCOVERY);
			mBackgroundHandler.sendMessage(msg);
		}
		
		public void cancelDiscovery() {
			Log.i(TAG, "mBackgroundHandler.startDiscovery()");
			Message msg = mBackgroundHandler.obtainMessage(CANCEL_DISCOVERY);
			mBackgroundHandler.sendMessage(msg);
		}

		public void requestName() {
			Log.i(TAG, "mBackgroundHandler.requestName()");
			Message msg = mBackgroundHandler.obtainMessage(REQUEST_NAME);
			mBackgroundHandler.sendMessage(msg);
		}

		public void releaseName() {
			Log.i(TAG, "mBackgroundHandler.releaseName()");
			Message msg = mBackgroundHandler.obtainMessage(RELEASE_NAME);
			mBackgroundHandler.sendMessage(msg);
		}

		public void bindSession() {
			Log.i(TAG, "mBackgroundHandler.bindSession()");
			Message msg = mBackgroundHandler.obtainMessage(BIND_SESSION);
			mBackgroundHandler.sendMessage(msg);
		}

		public void unbindSession() {
			Log.i(TAG, "mBackgroundHandler.unbindSession()");
			Message msg = mBackgroundHandler.obtainMessage(UNBIND_SESSION);
			mBackgroundHandler.sendMessage(msg);
		}

		public void advertise() {
			Log.i(TAG, "mBackgroundHandler.advertise()");
			Message msg = mBackgroundHandler.obtainMessage(ADVERTISE);
			mBackgroundHandler.sendMessage(msg);
		}

		public void cancelAdvertise() {
			Log.i(TAG, "mBackgroundHandler.cancelAdvertise()");
			Message msg = mBackgroundHandler.obtainMessage(CANCEL_ADVERTISE);
			mBackgroundHandler.sendMessage(msg);
		}

		public void joinSession() {
			Log.i(TAG, "mBackgroundHandler.joinSession()");
			Message msg = mBackgroundHandler.obtainMessage(JOIN_SESSION);
			mBackgroundHandler.sendMessage(msg);
		}

		public void leaveSession() {
			Log.i(TAG, "mBackgroundHandler.leaveSession()");
			Message msg = mBackgroundHandler.obtainMessage(LEAVE_SESSION);
			mBackgroundHandler.sendMessage(msg);
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
    	assert(mBusAttachmentState == BusAttachmentState.CONNECTED);
      	Status status = mBus.findAdvertisedName(packageName);
    	if (status == Status.OK) {
        	mBusAttachmentState = BusAttachmentState.DISCOVERING;
        	return;
    	} else {
    		Log.e(TAG, "startDiscovery failed");
        	return;
    	}
    }
    
    private void doStopDiscovery() {
        Log.i(TAG, "doStopDiscovery()");
    	assert(mBusAttachmentState == BusAttachmentState.CONNECTED);
      	mBus.cancelFindAdvertisedName(packageName);
      	mBusAttachmentState = BusAttachmentState.CONNECTED;
    }
	
	private void doConnect() {
		Log.i(TAG, "doConnect()");
		org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());
		assert (mBusAttachmentState == BusAttachmentState.DISCONNECTED);
		mBus.useOSLogging(true);
		mBus.setDebugLevel("ALLJOYN_JAVA", 7);
		mBus.registerBusListener(busListener);


		Status status = mBus.registerBusObject(busObject, objectPath);
		if (Status.OK != status) {			
			Log.e(TAG, "Cannot register");
			return;
		}

		status = mBus.connect();
		if (status != Status.OK) {			
			Log.e(TAG, "Cannot connect");
			return;
		}

		status = mBus.registerSignalHandlers(signalHandler);
		if (status != Status.OK) {
			Log.e(TAG, "Cannot register signalHandler");
			return;
		}

		mBusAttachmentState = BusAttachmentState.CONNECTED;
	}

	private boolean doDisconnect() {
		Log.i(TAG, "doDisonnect()");
		assert (mBusAttachmentState == BusAttachmentState.CONNECTED);
		mBus.unregisterBusListener(busListener);
		mBus.disconnect();
		mBusAttachmentState = BusAttachmentState.DISCONNECTED;
		return true;
	}

	private void doRequestName() {
		Log.i(TAG, "doRequestName()");
		int stateRelation = mBusAttachmentState
				.compareTo(BusAttachmentState.DISCONNECTED);
		assert (stateRelation >= 0);

		Status status = mBus.requestName(channelName,
				BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE);
		if (status == Status.OK) {
			mHostChannelState = HostChannelState.NAMED;
		} else {
			Log.e(TAG, "cannot request name");
		}
	}

	private void doReleaseName() {
		Log.i(TAG, "doReleaseName()");


		int stateRelation = mBusAttachmentState
				.compareTo(BusAttachmentState.DISCONNECTED);
		assert (stateRelation >= 0);
		assert (mBusAttachmentState == BusAttachmentState.CONNECTED || mBusAttachmentState == BusAttachmentState.DISCOVERING);

		assert (mHostChannelState == HostChannelState.NAMED);

		mBus.releaseName(channelName);
		mHostChannelState = HostChannelState.IDLE;
	}
	
    private void doAdvertise() {
        Log.i(TAG, "doAdvertise()");
     
    	Log.i(TAG, "Advertised name: " + channelName );
        Status status = mBus.advertiseName(channelName, SessionOpts.TRANSPORT_WLAN);
        
        if (status == Status.OK) {
        	mHostChannelState = HostChannelState.ADVERTISED;
        } else {
        	Log.e(TAG, "doAdvertise() failed");
        	return;
        }
    }
    
   
    private void doCancelAdvertise() {
        Log.i(TAG, "doCancelAdvertise()");
       
    	String wellKnownName = packageName + "." + channelName;
        Status status = mBus.cancelAdvertiseName(wellKnownName, SessionOpts.TRANSPORT_ANY);
        
        if (status != Status.OK) {
        	Log.e(TAG, "cancelAdvertise() failed : " + status);
        	return;
        }
        
     
     	mHostChannelState = HostChannelState.BOUND;
    }

	private void doBindSession() {
		Log.i(TAG, "doBindSession()");

		Mutable.ShortValue mutableContactPort = new Mutable.ShortValue(contactPort);
		SessionOpts sessionOpts = new SessionOpts(SessionOpts.TRAFFIC_MESSAGES,
				true, SessionOpts.PROXIMITY_ANY, SessionOpts.TRANSPORT_ANY);

		Status status = mBus.bindSessionPort(mutableContactPort, sessionOpts,
				sessionPortListener);

		if (status == Status.OK) {
			mHostChannelState = HostChannelState.BOUND;
		} else {
			Log.e(TAG, "Unable to bind session contact port: (" + status + ")");
			return;
		}
	}

	public static enum UseChannelState {
		IDLE, 
		JOINED,
	}

	private void doJoinSession() {
		Log.i(TAG, "doJoinSession()");
		if (mHostChannelState != HostChannelState.IDLE) {
			if (mJoinedToSelf) {
				return;
			}
		}

		 String wellKnownName = packageName + "." + channelName;

		SessionOpts sessionOpts = new SessionOpts(SessionOpts.TRAFFIC_MESSAGES,
				true, SessionOpts.PROXIMITY_ANY, SessionOpts.TRANSPORT_WLAN);
		Mutable.IntegerValue sessionId = new Mutable.IntegerValue();

		Status status = mBus.joinSession(wellKnownName, contactPort, sessionId,
				sessionOpts, sessionListener);
		
		if (status == Status.OK) {
			Log.i(TAG, "doJoinSession(): use sessionId is " + mUseSessionId);
			mUseSessionId = sessionId.value;			
		} else {
			Log.e(TAG, "Unable to join session: (" + status + ")");
			return;
		}
		SignalEmitter emitter = new SignalEmitter(null, mUseSessionId,
				SignalEmitter.GlobalBroadcast.Off);
		mMyInterface = emitter.getInterface(interfaceClass);
		
		Log.i(TAG, "MyInterface: "  + mMyInterface);

	}


	T mMyInterface = null;

	private void doLeaveSession() {
		Log.i(TAG, "doLeaveSession()");
		if (mJoinedToSelf == false) {
			mBus.leaveSession(mUseSessionId);
		}
		mUseSessionId = -1;
		mJoinedToSelf = false;
	}


	int mUseSessionId = -1;

	int mHostSessionId = -1;

	boolean mJoinedToSelf = false;

	
	T mHostInterface = null;

	private void doUnbindSession() {
		Log.i(TAG, "doUnbindSession()");

		mBus.unbindSessionPort(contactPort);
		mHostInterface = null;
		mHostChannelState = HostChannelState.NAMED;
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
		mBackgroundHandler = new BackgroundHandler(busThread.getLooper());
	}

	
	private void stopBusThread() {
		mBackgroundHandler.exit();
	}

	public void destroy() {
		Log.i(TAG, "onDestroy()");
		mBackgroundHandler.disconnect();
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
