package de.bachelor.maumau;

import java.io.UnsupportedEncodingException;

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
		public byte[] data;
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
			public void dataSentToAllPeers(String sentBy, int messageType, byte[] data) {
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

	protected void playerStateChanged(MessageInfoHolder message) {
		try {
			if(gameManager.getJoinedPlayers().containsKey(message.sentBy) && new String(message.data,PTPHelper.ENCODING_UTF8).isEmpty()){
				gameManager.ByeIWas(message.sentBy);
			}else{
					gameManager.HiIAm(message.sentBy, new String(message.data,PTPHelper.ENCODING_UTF8));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	protected void nextTurn(MessageInfoHolder message) {
		try {
			gameManager.NextTurn(message.sentBy, Integer.valueOf(new String(message.data,PTPHelper.ENCODING_UTF8)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	protected void ownerChanged(MessageInfoHolder message) {
		XMLIdMap map=new XMLIdMap();
    	map.withCreator(new CardCreator());
    	Card cardToPlay = null;
		try {
			cardToPlay = (Card) map.decode(new String(message.data,PTPHelper.ENCODING_UTF8));
			gameManager.ChangeOwner(cardToPlay.id, cardToPlay.owner);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}


	protected void cardPlayed(MessageInfoHolder message) {
		try {
			gameManager.PlayCard(Integer.valueOf(new String(message.data,PTPHelper.ENCODING_UTF8)), message.sentBy);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}   

	
//	void notifyPeersAboutMe(){
//		GameManagerInterface remoteGameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
//		if(remoteGameManagers!=null){
//			try {
//				remoteGameManagers.HiIAm(PTPHelper.getInstance().getUniqueID(),PTPHelper.getInstance().getPlayerName());
//			} catch (BusException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	private void sendCardOwnerChanged(){
//		int id = 0;
//		while((id = gameManager.getOwnedCardId()) != -1){
//			GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
//			if(gameManagers!=null){
//				try {
//					gameManagers.ChangeOwner(id, PTPHelper.getInstance().getUniqueID());
//				} catch (BusException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	
//	private void sendNextTurn() {
//		GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
//		if(gameManagers!=null){
//			try {
//				gameManagers.NextTurn(gameManager.getCurrentPlayersID(),gameManager.getSpecialCase());
//			} catch (BusException e) {
//				e.printStackTrace();
//			}
//		}
//	}	
//	
//	private void sendOwnedCards() {
//		List<Card> ownCards = gameManager.getOwnCards();
//		GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
//		if(gameManagers!=null){
//			try {
//				for (Card card : ownCards) {
//					gameManagers.ChangeOwner(card.id, PTPHelper.getInstance().getUniqueID());
//				}
//			} catch (BusException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	private void sendCardPlayed() {
//		Card playedCard = gameManager.getPlayedCard();
//		GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
//		if(gameManagers!=null){
//			try {
//				gameManagers.PlayCard(playedCard.id, PTPHelper.getInstance().getUniqueID());
//			} catch (BusException e) {
//				e.printStackTrace();
//			}
//		}		
//	}
//
//	@Override
//	public void update(int args) {
//		sendMessage(args);
//	}	
	
	
}
