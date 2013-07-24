package de.bachelor.pingtest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import de.ptpservice.DataListener;
import de.ptpservice.PTPHelper;

@SuppressLint("HandlerLeak")
public class MainApplication extends Application {		
	class MessageInfoHolder {
		public String[] data;
		public String sentBy;
	}
	
	protected static final String TAG = "MainApp";
	private PingData pingData;
	
	public void onCreate() {
        super.onCreate();
        pingData = new PingData();
        
		PTPHelper.initHelper("Ping",this, PingLobby.class);
		PTPHelper.getInstance().addDataListener(new DataListener() {
			
			@Override
			public void dataSentToAllPeers(String peersID, int messageType, String[] data) {	
				MessageInfoHolder infoHolder = new MessageInfoHolder();
				infoHolder.data = data;
				infoHolder.sentBy = peersID;
				sendMessage(messageType, infoHolder);
			}
		});
	}	
	
	void sendMessage(int messageType,MessageInfoHolder infoHolder){
		Message obtainedMessage = messageHandler.obtainMessage(messageType,infoHolder);
		messageHandler.sendMessage(obtainedMessage);	
	}
	
	private Handler messageHandler = new Handler() {
		
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case PingData.PING: ping((MessageInfoHolder)msg.obj);
    		case PingData.PING_REPLY: pingReply((MessageInfoHolder)msg.obj);
			default: break;
			};
		}
    };

	private void ping(MessageInfoHolder obj) {
		PTPHelper.getInstance().sendDataToAllPeers(PingData.PING_REPLY, obj.data);
	}

	private void pingReply(MessageInfoHolder obj) {
		if(obj.data[0] != null && obj.data[1]!= null && obj.data[1].equals(PTPHelper.getInstance().getUniqueID())){			
			long timeStampSent = Long.valueOf(obj.data[0]);
			long timeSpent = System.currentTimeMillis() - timeStampSent;
			getPingData().addToPingTable(obj.sentBy, timeSpent);
		}
	}

	public PingData getPingData() {
		return pingData;
	}
}
