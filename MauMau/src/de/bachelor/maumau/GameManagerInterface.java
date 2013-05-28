package de.bachelor.maumau;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface (name = "de.bachelor.maumau.GameManagerInterface")
public interface GameManagerInterface {
	
	@BusSignal
	public void ChangeOwner(int cardId, String uniqueUserID) throws BusException;
	@BusSignal
	public void PlayCard(int cardId, String uniqueUserID) throws BusException;
	
	@BusSignal
	public void NextTurn(String uniqueUserID,int specialCase) throws BusException;
	
	@BusSignal
	public void HiIAm(String uniqueID,String playerName) throws BusException;
	
	@BusSignal
	public void ByeIWas(String uniqueID,String playerName) throws BusException;

}
