package com.example.firstapp;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface (name = "com.example.firstapp.GraphInterface")
public interface GraphInterface {

    @BusSignal
    public void MoveNode(int id,double x,double y, String uniqueName) throws BusException;
    
    @BusSignal
    public void ChangeOwnerOfNode(int id,String owner, String uniqueID) throws BusException;	 
}
