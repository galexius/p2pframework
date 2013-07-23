package de.bachelor.maumau;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import de.ptpservice.DataListener;
import de.ptpservice.PTPHelper;
import de.uniks.jism.xml.XMLIdMap;


@SuppressLint("HandlerLeak")
public class MauMauApplication extends Application {	
	
	class MessageInfoHolder {
		public String[] data;
		public String sentBy;
	}
	
	private GameManager gameManager;
		
	@Override
	public void onCreate(){
		super.onCreate();	
		gameManager = new GameManager(this);
		gameManager.reset();
		PTPHelper.initHelper("MauMau", this, MauMauLobbyView.class);
		PTPHelper.getInstance().addDataListener(new DataListener() {
			
			@Override
			public void dataSentToAllPeers(String sentBy, int messageType, String[] data) {
				MessageInfoHolder messageInfoHolder = new MessageInfoHolder();
				messageInfoHolder.data = data;
				messageInfoHolder.sentBy = sentBy;	
				sendMessage(messageType, messageInfoHolder);
			}
		});
	}		
	
	
	void sendMessage(int messageType,MessageInfoHolder infoHolder){
		Message obtainedMessage = messageHandler.obtainMessage(messageType,infoHolder);
		messageHandler.sendMessage(obtainedMessage);	
	}

	public GameManager getGameManager() {
		return this.gameManager;
	}

	
	private Handler messageHandler = new Handler() {
		
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case GameManager.PLAYERS_STATE_CHANGED: playerStateChanged((MessageInfoHolder)msg.obj);break;
			case GameManager.CARD_PLAYED: cardPlayed((MessageInfoHolder)msg.obj);break;
			case GameManager.OWNER_CHANGED: ownerChanged((MessageInfoHolder)msg.obj);break;
			case GameManager.NEXT_TURN: nextTurn((MessageInfoHolder)msg.obj); break;
			default: break;
			};
		}
    };

	private void playerStateChanged(MessageInfoHolder message) {
		if(gameManager.getJoinedPlayers().containsKey(message.sentBy) && message.data.length == 0){
			gameManager.ByeIWas(message.sentBy);
		}else{
				gameManager.HiIAm(message.sentBy, message.data[0]);
		}
	}


	private void nextTurn(MessageInfoHolder message) {
		try {
			gameManager.NextTurn(message.sentBy, Integer.valueOf(message.data[0]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void ownerChanged(MessageInfoHolder message) {
		XMLIdMap map=new XMLIdMap();
    	map.withCreator(new CardCreator());
    	Card cardToPlay = null;
		cardToPlay = (Card) map.decode(message.data[0]);
		gameManager.ChangeOwner(cardToPlay.id, cardToPlay.owner);
	}


	private void cardPlayed(MessageInfoHolder message) {
		try {
			gameManager.PlayCard(Integer.valueOf(message.data[0]), message.sentBy);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}   

	
	
}
