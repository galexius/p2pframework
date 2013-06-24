package de.ptpservice;

public interface BusObserver {
	public void notifyFoundAdvertisedName(String channelName);
	public void notifyLostAdvertisedName(String channelName);
	public void notifyBusDisconnected();
}
