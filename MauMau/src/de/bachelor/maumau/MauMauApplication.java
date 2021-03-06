package de.bachelor.maumau;

import android.annotation.SuppressLint;
import android.app.Application;
import de.ptpservice.DataObserver;
import de.ptpservice.PTPManager;
import de.ptpservice.SessionJoinRule;
import de.ptpservice.SessionObserver;
import de.uniks.jism.xml.XMLIdMap;


@SuppressLint("HandlerLeak")
public class MauMauApplication extends Application {	
	
	private GameManager gameManager;
		
	@Override
	public void onCreate(){
		super.onCreate();	
		gameManager = new GameManager();
		gameManager.reset();
		PTPManager.initHelper("MauMau", this, MauMauLobbyView.class);
		PTPManager.getInstance().addDataObserver(new DataObserver() {			
			@Override
			public void dataSentToAllPeers(String sentBy, int messageType, String[] data) {
				switch (messageType) {
				case GameManager.PLAYERS_STATE_CHANGED: playerStateChanged(sentBy,data);break;
				case GameManager.CARD_PLAYED: cardPlayed(sentBy,data);break;
				case GameManager.OWNER_CHANGED: ownerChanged(sentBy,data);break;
				case GameManager.NEXT_TURN: nextTurn(sentBy,data); break;
				default: break;
				};
			}
		});
		PTPManager.getInstance().addJoinRule(new SessionJoinRule() {			
			@Override
			public boolean canJoin(String arg0) {
				return !gameManager.isGameStarted() && gameManager.getJoinedPlayers().size() < 3;
			}
		});
		
		PTPManager.getInstance().addSessionObserver(new SessionObserver() {
			
			@Override
			public void sessionLost() {
				
			}
			
			@Override
			public void memberLeft(String uniqueID) {
				gameManager.ByeIWas(uniqueID);
			}
			
			@Override
			public void memberJoined(String arg0) {
			}
		});
	}		


	public GameManager getGameManager() {
		return this.gameManager;
	}	
	

	private void playerStateChanged(String sentBy,String[] data) {
		if(gameManager.getJoinedPlayers().containsKey(sentBy) && data.length == 0){
			gameManager.ByeIWas(sentBy);
		}else{
			gameManager.HiIAm(sentBy, data[0]);
		}
	}


	private void nextTurn(String sentBy,String[] data) {
		try {
			gameManager.NextTurn(sentBy, Integer.valueOf(data[0]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void ownerChanged(String sentBy,String[] data) {
		XMLIdMap map=new XMLIdMap();
    	map.withCreator(new CardCreator());
    	Card cardToPlay = null;
		cardToPlay = (Card) map.decode(data[0]);
		gameManager.ChangeOwner(cardToPlay.id, cardToPlay.owner);
	}


	private void cardPlayed(String sentBy,String[] data) {
		try {
			gameManager.PlayCard(Integer.valueOf(data[0]), sentBy);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}   

	
	
}
