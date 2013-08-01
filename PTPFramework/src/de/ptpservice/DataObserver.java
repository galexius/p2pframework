package de.ptpservice;

public interface DataObserver {
	public void dataSentToAllPeers(String sentFrom,int arg,String[] data);
}
