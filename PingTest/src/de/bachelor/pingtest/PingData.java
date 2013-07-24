package de.bachelor.pingtest;

import java.util.ArrayList;
import java.util.HashMap;

public class PingData {
	private HashMap<String, Long> pingTable = new HashMap<String, Long>();
	private ArrayList<PingObserver> observers = new ArrayList<PingObserver>();
	
	public static final int PING = 1;
	public static final int PING_REPLY = 2;
	
	
	public void addToPingTable(String id, long timeStamp){
		getPingTable().put(id, timeStamp);
		notifyObservers();
	}


	public HashMap<String, Long> getPingTable() {
		return pingTable;
	}
	
	public void addObserver(PingObserver observer){
		observers.add(observer);
	}
	public void removeObserver(PingObserver observer){
		observers.remove(observer);
	}
	
	private void notifyObservers(){
		for (PingObserver observer : observers) {
			observer.pingTableChanged();
		}
	}

}
