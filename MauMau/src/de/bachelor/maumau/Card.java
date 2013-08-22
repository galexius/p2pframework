package de.bachelor.maumau;

import de.ptpservice.PTPManager;

public class Card{

	public final static String PROPERTY_VALUE = "value";
	public final static String PROPERTY_SUIT = "suit";
	public final static String PROPERTY_ID = "id";
	public final static String PROPERTY_OWNER = "owner";
	
	
	public final static int CLUB = 0;
	public final static int DIAMOND = 1;
	public final static int HEART = 2;			
	public final static int SPADE = 3;
	public int value;
	public int suit;
	public int id;
	public String owner;
	
	public String getValueString(){
		String[] data = null;
		
		
		PTPManager.getInstance().connectAndStartDiscover();
		PTPManager.getInstance().joinSession("game1");
		
		PTPManager.getInstance().sendDataToAllPeers(0, data);
		
		
		switch(value) {
			case 14: return "A";
			case 11: return "J";
			case 12: return "D";
			case 13: return "K";
			default : return ""+value;
		}
		
		
		
		
	}
}