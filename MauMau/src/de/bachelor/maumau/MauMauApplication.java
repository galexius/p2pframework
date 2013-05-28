package de.bachelor.maumau;

import java.util.ArrayList;
import java.util.List;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.bachelor.maumau.GameManager.Card;
import de.p2pservice.P2PApplication;

@SuppressLint("HandlerLeak")
public class MauMauApplication extends P2PApplication implements GameManagerObserver{
	
    private GameManager busObject;
	private GameManager gameManager;
	private Object signalEmitter;
	private List<String> connctedPlayers = new ArrayList<String>();

	@Override
	public void onCreate(){
		super.onCreate();
		this.gameManager = new GameManager(this);
		gameManager.initDeck();
		gameManager.addObserver(this);
        Intent service = new Intent(this,MauMauService.class);
        ComponentName mRunningService = startService(service);
        if (mRunningService == null) {
            Log.e(TAG, "onCreate(): failed to startService()");
        }
        new Intent(this,MauMauLobbyView.class);
	}	
	
	@Override
	public BusObject getBusObject() {
		if(busObject == null)
			busObject = new GameManager(this);
		return busObject;
	}

	@Override
	public Object getSignalHandler() {
		return this.gameManager;
	}

	@Override
	public void setSignalEmiter(Object signalEmitter) {
		this.signalEmitter = signalEmitter;
		sendMessage(GameManager.PLAYERS_STATE_CHANGED);
	}
	
	void sendMessage(int args){
		Message obtainedMessage = messageHandler.obtainMessage(args);
		messageHandler.sendMessage(obtainedMessage);	
	}


	public boolean isRemoteObjectSet() {
		return true;
	}


	public GameManager getGameManager() {
		return this.gameManager;
	}

	public List<String> getConnectedPlayers() {
		return connctedPlayers;		
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
		GameManagerInterface remoteGameManagers = (GameManagerInterface) signalEmitter;
		if(remoteGameManagers!=null){
			try {
				remoteGameManagers.HiIAm(getUniqueID(),getPlayerName());
			} catch (BusException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendCardOwnerChanged(){
		int id = 0;
		while((id = gameManager.getOwnedCardId()) != -1){
			GameManagerInterface gameManagers = (GameManagerInterface) signalEmitter;
			if(gameManagers!=null){
				try {
					gameManagers.ChangeOwner(id, getUniqueID());
				} catch (BusException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void sendNextTurn() {
		GameManagerInterface gameManagers = (GameManagerInterface) signalEmitter;
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
		GameManagerInterface gameManagers = (GameManagerInterface) signalEmitter;
		if(gameManagers!=null){
			try {
				for (Card card : ownCards) {
					gameManagers.ChangeOwner(card.id, getUniqueID());
				}
			} catch (BusException e) {
				e.printStackTrace();
			}
		}

	}

	private void sendCardPlayed() {
		Card playedCard = gameManager.getPlayedCard();
		GameManagerInterface gameManagers = (GameManagerInterface) signalEmitter;
		if(gameManagers!=null){
			try {
				Log.i(TAG, "sendCardPlayed");
				gameManagers.PlayCard(playedCard.id, getUniqueID());
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
