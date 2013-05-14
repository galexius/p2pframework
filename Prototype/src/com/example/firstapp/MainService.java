package com.example.firstapp;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.firstapp.Graph.IdChange;

@SuppressLint("HandlerLeak")
public class MainService extends Service {

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
	private static final int SEND_NODE_MOVE = 14;
	private static final int SEND_NEW_OWNER = 15;
	
	 private static final int NOTIFICATION_ID = 0xdefaced;
	
	private MainApplication app;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void onCreate() {
        Log.i(TAG, "onCreate()");
        startBusThread();
        app = (MainApplication)getApplication();
//        app.addObserver(this);      
//        app.getGraph().addObserver(this);
        
        CharSequence title = "AllJoyn";
        CharSequence message = "Prototype Hosting Service.";
        Intent intent = new Intent(this, LobbyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		Notification notification = new Notification(R.drawable.red_button, null, System.currentTimeMillis());
        notification.setLatestEventInfo(this, title, message, pendingIntent);
        notification.flags |= Notification.DEFAULT_SOUND | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        Log.i(TAG, "onCreate(): startForeground()");
        startForeground(NOTIFICATION_ID, notification);
       
       
        mBackgroundHandler.connect();
        mBackgroundHandler.startDiscovery();
 	}
	
	private final class BackgroundHandler extends Handler {
		@SuppressLint("HandlerLeak")
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

		public void sendMovePoint() {
			Log.i(TAG, "mBackgroundHandler.sendMessages()");
			Message msg = mBackgroundHandler.obtainMessage(SEND_NODE_MOVE);
			mBackgroundHandler.sendMessage(msg);
		}
		public void sendPointOwnership() {
			Log.i(TAG, "mBackgroundHandler.sendMessages()");
			Message msg = mBackgroundHandler.obtainMessage(SEND_NEW_OWNER);
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
	        case SEND_NODE_MOVE:
		        doSendMessages();
		        break;
	        case SEND_NEW_OWNER:
	        	doSendNewOwner();
	        	break;
	        case EXIT:
                getLooper().quit();
                break;
			default:
				break;
			}
		}

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_APPLICATION_QUIT_EVENT: {
				Log.i(TAG, "mHandler.handleMessage(): APPLICATION_QUIT_EVENT");
				mBackgroundHandler.leaveSession();
				mBackgroundHandler.cancelAdvertise();
				mBackgroundHandler.unbindSession();
				mBackgroundHandler.releaseName();
				mBackgroundHandler.exit();
				destroy();
			}
				break;
			case HANDLE_USE_JOIN_CHANNEL_EVENT: {
				Log.i(TAG, "mHandler.handleMessage(): USE_JOIN_CHANNEL_EVENT");
				mBackgroundHandler.joinSession();
			}
				break;
			case HANDLE_USE_LEAVE_CHANNEL_EVENT: {
				Log.i(TAG, "mHandler.handleMessage(): USE_LEAVE_CHANNEL_EVENT");
				mBackgroundHandler.leaveSession();
			}
				break;
			case HANDLE_HOST_INIT_CHANNEL_EVENT: {
				Log.i(TAG, "mHandler.handleMessage(): HOST_INIT_CHANNEL_EVENT");
			}
				break;
			case HANDLE_HOST_START_CHANNEL_EVENT: {
				Log.i(TAG, "mHandler.handleMessage(): HOST_START_CHANNEL_EVENT");
				mBackgroundHandler.requestName();
				mBackgroundHandler.bindSession();
				mBackgroundHandler.advertise();
			}
				break;
			case HANDLE_HOST_STOP_CHANNEL_EVENT: {
				Log.i(TAG, "mHandler.handleMessage(): HOST_STOP_CHANNEL_EVENT");
				mBackgroundHandler.cancelAdvertise();
				mBackgroundHandler.unbindSession();
				mBackgroundHandler.releaseName();
			}
				break;
			case NODE_POSITION_CHANGED_EVENT: {
				Log.i(TAG, "mHandler.handleMessage(): OUTBOUND_CHANGED_EVENT");
				mBackgroundHandler.sendMovePoint();
			}
				break;
			case HANDLE_POINT_OWNERSHIP_CHANGED: {
				Log.i(TAG, "mHandler.handleMessage(): OUTBOUND_CHANGED_EVENT");
				mBackgroundHandler.sendPointOwnership();
			}
			break;
			default:
				break;
			}
		}

	};

	private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
	private static final int HANDLE_USE_JOIN_CHANNEL_EVENT = 1;
	private static final int HANDLE_USE_LEAVE_CHANNEL_EVENT = 2;
	private static final int HANDLE_HOST_INIT_CHANNEL_EVENT = 3;
	private static final int HANDLE_HOST_START_CHANNEL_EVENT = 4;
	private static final int HANDLE_HOST_STOP_CHANNEL_EVENT = 5;
	private static final int NODE_POSITION_CHANGED_EVENT = 6;
	private static final int HANDLE_POINT_OWNERSHIP_CHANGED = 7;

	private BusAttachment mBus = new BusAttachment(MainApplication.PACKAGE_NAME, BusAttachment.RemoteMessage.Receive);

//	public synchronized void update(Observable o, Object arg) {
//		Log.i(TAG, "update(" + arg + ")");
//		String qualifier = (String) arg;
//
//		if (qualifier.equals(MainApplication.APPLICATION_QUIT_EVENT)) {
//			Message message = mHandler
//					.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
//			mHandler.sendMessage(message);
//		}
//
//		if (qualifier.equals(MainApplication.USE_JOIN_CHANNEL_EVENT)) {
//			Message message = mHandler
//					.obtainMessage(HANDLE_USE_JOIN_CHANNEL_EVENT);
//			mHandler.sendMessage(message);
//		}
//
//		if (qualifier.equals(MainApplication.USE_LEAVE_CHANNEL_EVENT)) {
//			Message message = mHandler
//					.obtainMessage(HANDLE_USE_LEAVE_CHANNEL_EVENT);
//			mHandler.sendMessage(message);
//		}
//
//		if (qualifier.equals(MainApplication.HOST_INIT_CHANNEL_EVENT)) {
//			Message message = mHandler
//					.obtainMessage(HANDLE_HOST_INIT_CHANNEL_EVENT);
//			mHandler.sendMessage(message);
//		}
//
//		if (qualifier.equals(MainApplication.HOST_START_CHANNEL_EVENT)) {
//			Message message = mHandler
//					.obtainMessage(HANDLE_HOST_START_CHANNEL_EVENT);
//			mHandler.sendMessage(message);
//		}
//
//		if (qualifier.equals(MainApplication.HOST_STOP_CHANNEL_EVENT)) {
//			Message message = mHandler
//					.obtainMessage(HANDLE_HOST_STOP_CHANNEL_EVENT);
//			mHandler.sendMessage(message);
//		}
//
//		if (qualifier.equals(Graph.NODE_POSITION_CHANGED)) {
//			Message message = mHandler
//					.obtainMessage(NODE_POSITION_CHANGED_EVENT);
//			mHandler.sendMessage(message);
//		}
//		if (qualifier.equals(Graph.POINT_OWNERSHIP_CHANGED)) {
//			Message message = mHandler
//					.obtainMessage(HANDLE_POINT_OWNERSHIP_CHANGED);
//			mHandler.sendMessage(message);
//		}
//	}

	public static final short CONTACT_PORT = 27;

	public static final String OBJECT_PATH = "/Prototype";

	private class PrototypeBusListener extends BusListener {

		public void foundAdvertisedName(String name, short transport,
				String namePrefix) {
			Log.i(TAG, "mBusListener.foundAdvertisedName(" + name + ")");
		}
		
		public void lostAdvertisedName(String name, short transport,
				String namePrefix) {
			Log.i(TAG, "mBusListener.lostAdvertisedName(" + name + ")");
		}
	}
	private PrototypeBusListener mBusListener = new PrototypeBusListener();

//	@BusSignalHandler(iface = NAME_PREFIX+".GraphInterface", signal = "MoveNode")
//    public void MoveNode(int id,double x,double y) throws BusException{
//		Log.d(TAG, "SignalHandler MoveNode" + x + ", " + y);
//		
//    	Graph graph = app.getGraph();
//    	graph.deleteObserver(MainService.this);
//		graph.MoveNode(id, x, y);
//    	app.remotePointChanged();
//    	graph.addObserver(MainService.this);
//    }
//    
//	@BusSignalHandler(iface = NAME_PREFIX+".GraphInterface", signal = "ChangeOwnerOfNode")
//    public void ChangeOwnerOfNode(int id,String owner) throws BusException	{
//		Log.d(TAG, "SignalHandler ChangeOwner" + id  + " , owner: " + owner);
//		Graph graph = app.getGraph();
//		graph.deleteObserver(MainService.this);
//    	app.getGraph().ChangeOwnerOfNode(id, owner);
//    	app.remotePointChanged();
//    	graph.addObserver(MainService.this);
//    }



	protected String getWellKnownName(){
		 String wellKnownName = MainApplication.PACKAGE_NAME+"." +
				 app.getHostChannelName();
		 return wellKnownName;
	}
	
	private static final String NAME_PREFIX = "com.example.firstapp";
	
    private void doStartDiscovery() {
        Log.i(TAG, "doStartDiscovery()");
    	assert(mBusAttachmentState == BusAttachmentState.CONNECTED);
      	Status status = mBus.findAdvertisedName(NAME_PREFIX);
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
      	mBus.cancelFindAdvertisedName(NAME_PREFIX);
      	mBusAttachmentState = BusAttachmentState.CONNECTED;
    }
	
	private void doConnect() {
		Log.i(TAG, "doConnect()");
		org.alljoyn.bus.alljoyn.DaemonInit.PrepareDaemon(getApplicationContext());
		assert (mBusAttachmentState == BusAttachmentState.DISCONNECTED);
		mBus.useOSLogging(true);
		mBus.setDebugLevel("ALLJOYN_JAVA", 7);
		mBus.registerBusListener(mBusListener);


		Status status = mBus.registerBusObject(app.getGraph(), OBJECT_PATH);
		if (Status.OK != status) {			
			Log.e(TAG, "Cannot register");
			return;
		}

		status = mBus.connect();
		if (status != Status.OK) {			
			Log.e(TAG, "Cannot connect");
			return;
		}
		app.setUniqueID(mBus.getUniqueName());
		app.getGraph().setupPoints();

		status = mBus.registerSignalHandlers(this);
		if (status != Status.OK) {
			Log.e(TAG, "Cannot register signalHandler");
			return;
		}

		mBusAttachmentState = BusAttachmentState.CONNECTED;
	}

	private boolean doDisconnect() {
		Log.i(TAG, "doDisonnect()");
		assert (mBusAttachmentState == BusAttachmentState.CONNECTED);
		mBus.unregisterBusListener(mBusListener);
		mBus.disconnect();
		mBusAttachmentState = BusAttachmentState.DISCONNECTED;
		return true;
	}

	private void doRequestName() {
		Log.i(TAG, "doRequestName()");

		int stateRelation = mBusAttachmentState
				.compareTo(BusAttachmentState.DISCONNECTED);
		assert (stateRelation >= 0);

		String wellKnownName = getWellKnownName();
		Status status = mBus.requestName(wellKnownName,
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

		String wellKnownName = getWellKnownName();
		mBus.releaseName(wellKnownName);
		mHostChannelState = HostChannelState.IDLE;
	}
	
    private void doAdvertise() {
        Log.i(TAG, "doAdvertise()");
     
    	String wellKnownName = NAME_PREFIX + "." + app.getHostChannelName();    
    	Log.i(TAG, "Advertised name: " + wellKnownName );
        Status status = mBus.advertiseName(wellKnownName, SessionOpts.TRANSPORT_WLAN);
        
        if (status == Status.OK) {
        	mHostChannelState = HostChannelState.ADVERTISED;
        } else {
        	Log.e(TAG, "doAdvertise() failed");
        	return;
        }
    }
    
   
    private void doCancelAdvertise() {
        Log.i(TAG, "doCancelAdvertise()");
       
    	String wellKnownName = NAME_PREFIX + "." + app.getHostChannelName();
        Status status = mBus.cancelAdvertiseName(wellKnownName, SessionOpts.TRANSPORT_ANY);
        
        if (status != Status.OK) {
        	Log.e(TAG, "cancelAdvertise() failed : " + status);
        	return;
        }
        
     
     	mHostChannelState = HostChannelState.BOUND;
    }

	private void doBindSession() {
		Log.i(TAG, "doBindSession()");

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);
		SessionOpts sessionOpts = new SessionOpts(SessionOpts.TRAFFIC_MESSAGES,
				true, SessionOpts.PROXIMITY_ANY, SessionOpts.TRANSPORT_WLAN);

		Status status = mBus.bindSessionPort(contactPort, sessionOpts,
				new SessionPortListener() {
					
					public boolean acceptSessionJoiner(short sessionPort,
							String joiner, SessionOpts sessionOpts) {
						Log.i(TAG, "SessionPortListener.acceptSessionJoiner("
								+ sessionPort + ", " + joiner + ", "
								+ sessionOpts.toString() + ")");

						if (sessionPort == CONTACT_PORT) {
							return true;
						}
						return false;
					}

					
					public void sessionJoined(short sessionPort, int id,
							String joiner) {
						Log.i(TAG, "SessionPortListener.sessionJoined("
								+ sessionPort + ", " + id + ", " + joiner + ")");
						mHostSessionId = id;
						SignalEmitter emitter = new SignalEmitter(app.getGraph(), id,
								SignalEmitter.GlobalBroadcast.Off);
						mHostInterface = emitter
								.getInterface(GraphInterface.class);
					}
				});

		if (status == Status.OK) {
			mHostChannelState = HostChannelState.BOUND;
		} else {
			Log.e(TAG, "Unable to bind session contact port: (" + status + ")");
			return;
		}
	}

	public static enum UseChannelState {
		IDLE, /** There is no used chat channel */
		JOINED,
		/** The session for the channel has been successfully joined */
	}

	private void doJoinSession() {
		Log.i(TAG, "doJoinSession()");
		if (mHostChannelState != HostChannelState.IDLE) {
			if (app.getChannelName().equals(app.getHostChannelName())) {
				mJoinedToSelf = true;
				return;
			}
		}

		 String wellKnownName = MainApplication.PACKAGE_NAME + "." +
				 app.getChannelName();

		short contactPort = CONTACT_PORT;
		SessionOpts sessionOpts = new SessionOpts(SessionOpts.TRAFFIC_MESSAGES,
				true, SessionOpts.PROXIMITY_ANY, SessionOpts.TRANSPORT_WLAN);
		Mutable.IntegerValue sessionId = new Mutable.IntegerValue();

		Status status = mBus.joinSession(wellKnownName, contactPort, sessionId,
				sessionOpts, new SessionListener() {
			
					public void sessionLost(int sessionId) {
						Log.i(TAG, "BusListener.sessionLost(" + sessionId + ")");
					}
				});

		
		if (status == Status.OK) {
			Log.i(TAG, "doJoinSession(): use sessionId is " + mUseSessionId);
			mUseSessionId = sessionId.value;			
		} else {
			Log.e(TAG, "Unable to join session: (" + status + ")");
			return;
		}
		SignalEmitter emitter = new SignalEmitter(app.getGraph(), mUseSessionId,
				SignalEmitter.GlobalBroadcast.Off);
		mMyInterface = emitter.getInterface(GraphInterface.class);
		
		Log.i(TAG, "MyInterface: "  + mMyInterface);

	}


	GraphInterface mMyInterface = null;

	private void doLeaveSession() {
		Log.i(TAG, "doLeaveSession()");
		if (mJoinedToSelf == false) {
			mBus.leaveSession(mUseSessionId);
		}
		mUseSessionId = -1;
		mJoinedToSelf = false;
	}


	int mUseSessionId = -1;

	private void doSendMessages() {
		Log.i(TAG, "doSendMessages()");
		Node message;
		while ((message = app.getGraph().getChangedNode()) != null) {
			Log.i(TAG, "doSendMessages(): sending message \"" + message + "\"");
			
			try {
				if(!mJoinedToSelf){
					Log.i(TAG, "sending out");
					mMyInterface.MoveNode(message.getId(),message.x, message.y, null);
				}else{
					if(mHostInterface!= null){
						mHostInterface.MoveNode(message.getId(),message.x, message.y, null);
					}

				}

			} catch (BusException ex) {
				Log.e(TAG, "Bus exception while sending message: (" + ex + ")");
			}
		}
	}
	
	private void doSendNewOwner() {
		Log.i(TAG, "doSendNewOwner()");
		IdChange message;
//		while ((message = app.getGraph().getIdChange()) != null) {
//			Log.i(TAG, "doSendMessages(): sending message id to change: \"" + message + "\"");
//			
//			try {
//				if(!mJoinedToSelf){
//					Log.i(TAG, "sending out");
//					mMyInterface.ChangeOwnerOfNode(message.id, message.owner);
//				}else{
//					if(mHostInterface!= null){
//						mHostInterface.ChangeOwnerOfNode(message.id, message.owner);
//					}
//				}
//			} catch (BusException ex) {
//				Log.e(TAG, "Bus exception while sending message: (" + ex + ")");
//			}
//		}
		
	}

	int mHostSessionId = -1;

	boolean mJoinedToSelf = false;

	
	GraphInterface mHostInterface = null;

	private void doUnbindSession() {
		Log.i(TAG, "doUnbindSession()");

		mBus.unbindSessionPort(CONTACT_PORT);
		mHostInterface = null;
		mHostChannelState = HostChannelState.NAMED;
	}

	public static enum BusAttachmentState {
		DISCONNECTED, /** The bus attachment is not connected to the AllJoyn bus */
		CONNECTED, /** The bus attachment is connected to the AllJoyn bus */
		DISCOVERING		
	}

	
	private BusAttachmentState mBusAttachmentState = BusAttachmentState.DISCONNECTED;

	
	public static enum HostChannelState {
		IDLE, 
		NAMED, 		
		BOUND, 
		ADVERTISED, 		
		CONNECTED		
	}

	private HostChannelState mHostChannelState = HostChannelState.IDLE;

	static final String TAG = "MainService";

	private BackgroundHandler mBackgroundHandler = null;

	
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
		//app.deleteObserver(this);
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