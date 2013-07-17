package de.ptpservice;

import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface (name = "de.ptpservice.PTPBusObjectInterface")
public interface PTPBusObjectInterface {	
	@BusSignal
	public void SendDataToAllPeers(String sentFrom,int arg, byte[] data);
}
