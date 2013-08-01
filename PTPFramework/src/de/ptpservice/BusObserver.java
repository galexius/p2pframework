package de.ptpservice;

public interface BusObserver {
	public void foundAdvertisedName(String channelName);
	public void lostAdvertisedName(String channelName);
	public void busDisconnected();
}
