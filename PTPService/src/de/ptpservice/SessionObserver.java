package de.ptpservice;

public interface SessionObserver {
	public void notifyMemberJoined(String uniqueId);
	public void notifyMemberLeft(String uniqueId);
	public void notifySessionLost();
	
}
