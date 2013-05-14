package de.p2pservice;

import org.alljoyn.bus.BusObject;


public interface P2PInfoHolder {
	public void setSignalEmiter(Object signalEmitter);
	public void setRemoteObject(Object remoteObject);
	public String getHostChannelName();	
	public void setHostChannelName(String name);
	public String getChannelName();	
	public void setChannelName(String name);
	public BusObject getBusObject();
	public Object getSignalHandler();
	public void addObserver(Observer obs);
	public void removeObserver(Observer obs);
	public String getUniqueID();
	public void setUniqueID(String id);
	public void addAdvertisedName(String name);
	public void removeAdvertisedName(String name);
}
