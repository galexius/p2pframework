package de.ptpservice;

public interface DataListener {
	public void dataSentToAllPeers(String sentFrom,int arg,byte[] data);
}