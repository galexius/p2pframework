package de.ptpservice;

public interface SessionObserver {
	public void memberJoined(String uniqueId);
	public void memberLeft(String uniqueId);
	public void sessionLost();
	
}
