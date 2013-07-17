package de.bachelor.graphgame;

import java.io.UnsupportedEncodingException;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import de.ptpservice.DataListener;
import de.ptpservice.PTPHelper;

@SuppressLint("HandlerLeak")
public class MainApplication extends Application {
		
	class MessageInfoHolder {
		public byte[] data;
		public String sentBy;
	}
	
	protected static final String TAG = "MainApp";
	
	public void onCreate() {
        super.onCreate();
        graph = new Graph(this);
        graph.setupPoints();
        
		PTPHelper.initHelper("GraphGame",this, GraphLobbyActivity.class);
		PTPHelper.getInstance().addDataListener(new DataListener() {
			
			@Override
			public void dataSentToAllPeers(String peersID, int messageType, byte[] data) {
				MessageInfoHolder infoHolder = new MessageInfoHolder();
				infoHolder.data = data;
				infoHolder.sentBy = peersID;
				sendMessage(messageType, infoHolder);
			}
		});
	}	
    
    
	private Graph graph = null;

	public Graph getGraph() {
		return this.graph;
	}
	
	public void setGraph(Graph graph){
		this.graph = graph;
	}	
	
	void sendMessage(int messageType,MessageInfoHolder infoHolder){
		Message obtainedMessage = messageHandler.obtainMessage(messageType,infoHolder);
		messageHandler.sendMessage(obtainedMessage);	
	}
	
	private Handler messageHandler = new Handler() {
		
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case Graph.NODE_OWNERSHIP_CHANGED: nodeOwnerChanged((MessageInfoHolder)msg.obj);break;
			case Graph.NODE_POSITION_CHANGED: nodePositionChanged((MessageInfoHolder)msg.obj);break;
			default: break;
			};
		}
    };

	protected void nodeOwnerChanged(MessageInfoHolder obj) {
    	String dataAsString = "";
		try {
			dataAsString = new String(obj.data,PTPHelper.ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	Node node = graph.getNodeFromXML(dataAsString);
	    graph.ChangeOwnerOfNode(node.getId(),node.getOwner(), obj.sentBy);
	}

	protected void nodePositionChanged(MessageInfoHolder obj) {
    	String dataAsString = "";
		try {
			dataAsString = new String(obj.data,PTPHelper.ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	Node node = graph.getNodeFromXML(dataAsString);
	    graph.MoveNode(node.getId(), node.getX(), node.getY(), obj.sentBy);	
	}


}
