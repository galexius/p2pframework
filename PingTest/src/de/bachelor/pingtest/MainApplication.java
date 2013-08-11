package de.bachelor.pingtest;

import android.annotation.SuppressLint;
import android.app.Application;
import de.ptpservice.DataObserver;
import de.ptpservice.PTPManager;

@SuppressLint("HandlerLeak")
public class MainApplication extends Application {		
	
	protected static final String TAG = "MainApp";
	private PingData pingData;
	
	public void onCreate() {
        super.onCreate();
        pingData = new PingData();
        
		PTPManager.initHelper("Ping",this, PingLobby.class);
		PTPManager.getInstance().addDataObserver(new DataObserver() {
			
			@Override
			public void dataSentToAllPeers(String peersID, int messageType, String[] data) {	
				switch (messageType) {
	    		case PingData.PING: ping(peersID,data);
	    		case PingData.PING_REPLY: pingReply(peersID,data);
				default: break;
				};
			}
		});
	}	
	

	private void ping(String peersID, String[] data) {
		PTPManager.getInstance().sendDataToAllPeers(PingData.PING_REPLY, data);
	}

	private void pingReply(String peersID, String[] data) {
		if(data[0] != null && data[1]!= null && data[1].equals(PTPManager.getInstance().getUniqueID())){			
			long timeStampSent = Long.valueOf(data[0]);
			long timeSpent = System.currentTimeMillis() - timeStampSent;
			getPingData().addToPingTable(peersID, timeSpent);
		}
	}

	public PingData getPingData() {
		return pingData;
	}
}
