package de.bachelor.maumau;

import java.util.List;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusSignal;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Message;
import de.bachelor.maumau.GameManager.Card;
import de.ptpservice.PTPHelper;


@SuppressLint("HandlerLeak")
public class MauMauApplication extends Application implements GameManagerObserver{
	
	class GameManagerDummyObject implements GameManagerInterface,BusObject{

		@Override
		@BusSignal
		public void ChangeOwner(int cardId, String uniqueUserID)
				throws BusException {
		}

		@Override
		@BusSignal
		public void PlayCard(int cardId, String uniqueUserID)
				throws BusException {			
		}

		@Override
		@BusSignal
		public void NextTurn(String uniqueUserID, int specialCase)
				throws BusException {
		}

		@Override
		@BusSignal
		public void HiIAm(String uniqueID, String playerName)
				throws BusException {
		}

		@Override
		@BusSignal
		public void ByeIWas(String uniqueID, String playerName)
				throws BusException {			
		}
		
	}
	
	private GameManager gameManager;
		
	@Override
	public void onCreate(){
		super.onCreate();	
		gameManager = new GameManager(this);
		gameManager.reset();
		gameManager.addObserver(this);
		PTPHelper.initHelper("MauMau",GameManagerInterface.class, this, new GameManagerDummyObject(), gameManager, MauMauLobbyView.class);
	}		
	
	
	void sendMessage(int args){
		Message obtainedMessage = messageHandler.obtainMessage(args);
		messageHandler.sendMessage(obtainedMessage);	
	}

	public GameManager getGameManager() {
		return this.gameManager;
	}

	
	private Handler messageHandler = new Handler() {
		
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case GameManager.PLAYERS_STATE_CHANGED: notifyPeersAboutMe();break;
			case GameManager.CARD_PLAYED: sendCardPlayed();break;
			case GameManager.OWNER_CHANGED: sendCardOwnerChanged();break;
			case GameManager.TELL_OWNED_CARDS: sendOwnedCards(); break;
			case GameManager.NEXT_TURN: sendNextTurn(); break;
			default: break;
			};
		}
    };   

	
	void notifyPeersAboutMe(){
		GameManagerInterface remoteGameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
		if(remoteGameManagers!=null){
			try {
				remoteGameManagers.HiIAm(PTPHelper.getInstance().getUniqueID(),PTPHelper.getInstance().getPlayerName());
			} catch (BusException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendCardOwnerChanged(){
		int id = 0;
		while((id = gameManager.getOwnedCardId()) != -1){
			GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
			if(gameManagers!=null){
				try {
					gameManagers.ChangeOwner(id, PTPHelper.getInstance().getUniqueID());
				} catch (BusException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void sendNextTurn() {
		GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
		if(gameManagers!=null){
			try {
				gameManagers.NextTurn(gameManager.getCurrentPlayersID(),gameManager.getSpecialCase());
			} catch (BusException e) {
				e.printStackTrace();
			}
		}
	}	
	
	private void sendOwnedCards() {
		List<Card> ownCards = gameManager.getOwnCards();
		GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
		if(gameManagers!=null){
			try {
				for (Card card : ownCards) {
					gameManagers.ChangeOwner(card.id, PTPHelper.getInstance().getUniqueID());
				}
			} catch (BusException e) {
				e.printStackTrace();
			}
		}

	}

	private void sendCardPlayed() {
		Card playedCard = gameManager.getPlayedCard();
		GameManagerInterface gameManagers = (GameManagerInterface) PTPHelper.getInstance().getSignalEmitter();
		if(gameManagers!=null){
			try {
				gameManagers.PlayCard(playedCard.id, PTPHelper.getInstance().getUniqueID());
			} catch (BusException e) {
				e.printStackTrace();
			}
		}		
	}

	@Override
	public void update(int args) {
		sendMessage(args);
	}	
	
	
}
