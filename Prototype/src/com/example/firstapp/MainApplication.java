package com.example.firstapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

public class MainApplication extends Application implements Observable{
	private static final String TAG = "MainApplication";
	public static final String APPLICATION_QUIT_EVENT = "APPLICATION_QUIT_EVENT";
	public static final String USE_JOIN_CHANNEL_EVENT = "USE_JOIN_CHANNEL_EVENT";
	public static final String USE_LEAVE_CHANNEL_EVENT = "USE_LEAVE_CHANNEL_EVENT";
	public static final String HOST_INIT_CHANNEL_EVENT = "HOST_INIT_CHANNEL_EVENT";
	public static final String HOST_START_CHANNEL_EVENT = "HOST_START_CHANNEL_EVENT";
	public static final String HOST_STOP_CHANNEL_EVENT = "HOST_STOP_CHANNEL_EVENT";
	public static final String REMOTE_POINT_CHANGED_EVENT = "REMOTE_POINT_CHANGED_EVENT";
	
	public static String PACKAGE_NAME;
	private ComponentName mRunningService;
	private String hostChannelName;
	private String channelName;
	private String userId ="";
	private Node remotePoint;

	
	public void onCreate() {
		Log.i("MainApp", "onCreate");
        PACKAGE_NAME = getPackageName();
        Intent intent = new Intent(this, MainService.class);
        mRunningService = startService(intent);
        if (mRunningService == null) {
            Log.e("", "onCreate(): failed to startService()");
        }
        
        Log.i("MainApp", "onCreate finished");
	}
	
    public void checkin() {
        Log.i(TAG, "checkin()");
    	if (mRunningService == null) {
            Log.i(TAG, "checkin():  Starting the MainService");
            Intent intent = new Intent(this, MainService.class);
            mRunningService = startService(intent);
            if (mRunningService == null) {
                Log.i(TAG, "checkin(): failed to startService()");
            }    		
    	}
    }
	
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

	
	synchronized public Node getRemotePoint() {
		return this.remotePoint;
	}
	
	synchronized public void setRemotePoint(Node p){		
		this.remotePoint = p;
		remotePointChanged();
	}

	public synchronized void addObserver(Observer obs) {
        Log.i(TAG, "addObserver(" + obs + ")");
		if (mObservers.indexOf(obs) < 0) {
			mObservers.add(obs);
		}
	}
	public synchronized void deleteObserver(Observer obs) {
        Log.i(TAG, "deleteObserver(" + obs + ")");
		mObservers.remove(obs);
	}
	

	private void notifyObservers(Object arg) {
        Log.i(TAG, "notifyObservers(" + arg + ")");
        for (Observer obs : mObservers) {
            Log.i(TAG, "notify observer = " + obs);
            obs.update(this, arg);
        }
	}
	private List<Observer> mObservers = new ArrayList<Observer>();
	private Graph graph = new Graph();
	private int idOfNodeToChange;
	
	public synchronized void useJoinChannel() {
		notifyObservers(USE_JOIN_CHANNEL_EVENT);
	}
	
	public synchronized void remotePointChanged() {
		notifyObservers(REMOTE_POINT_CHANGED_EVENT);
	}
	
	public synchronized void useLeaveChannel() {
		notifyObservers(USE_LEAVE_CHANNEL_EVENT);
	}

	public synchronized void hostInitChannel() {
		notifyObservers(HOST_INIT_CHANNEL_EVENT);
	}

	public synchronized void hostStartChannel() {
		notifyObservers(HOST_START_CHANNEL_EVENT);
	}
	
	public synchronized void hostStopChannel() {
		notifyObservers(HOST_STOP_CHANNEL_EVENT);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Graph getGraph() {
		return this.graph;
	}
	
	public void setGraph(Graph graph){
		this.graph = graph;
	}
	
	public int getNodeIdToChange(){
		return idOfNodeToChange;
	}	

}
