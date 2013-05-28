package de.bachelor.maumau;


import org.alljoyn.bus.BusException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.Toast;
import de.bachelor.maumau.GameManager.Card;
import de.bachelor.maumau.GameManager.SpecialCases;
import de.bachelor.maumau.rules.CardsDrawnEvent;

@SuppressWarnings("deprecation")
public class GameActivity extends Activity implements GameManagerObserver {

	MauMauApplication application;
	GameManager gameManager;
	private PlayerListAdapter playerListAdapter;
	private ListView listview;
	private PlayedCardsAdapter playedCardsAdapter;
	private OwnCardsAdapter ownCardsAdapter;
	private View playedCardsLabel;
	private Button startButton;
	private Button drawCardButton;
	private Button nextTurnButton;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.game_layout);

	  final Gallery ownCardsGallery = (Gallery) findViewById(R.id.own_cards_gallery);
	  playedCardsLabel = findViewById(R.id.played_cards_label);	  
	  startButton = (Button) findViewById(R.id.startButon);	  
	  drawCardButton = (Button) findViewById(R.id.draw_card_button);	  
	  nextTurnButton = (Button) findViewById(R.id.next_turn_button);	  
	  playedCardsLabel.setVisibility(View.INVISIBLE);
	  application = (MauMauApplication) getApplication();
	  startButton.setVisibility(application.isHost()? View.VISIBLE: View.GONE);
	  gameManager = application.getGameManager();
	  gameManager.HiIAm(application.getUniqueID(), application.getPlayerName());
	  gameManager.addObserver(this);
	  

	 
	  ownCardsAdapter = new OwnCardsAdapter(this,gameManager);
	  ownCardsGallery.setAdapter(ownCardsAdapter);

	  final Gallery playedCardsGallery = (Gallery) findViewById(R.id.played_cards_gallery);
	  playedCardsAdapter = new PlayedCardsAdapter(this,gameManager);
	  playedCardsGallery.setAdapter(playedCardsAdapter);
	  
	  ownCardsGallery.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(@SuppressWarnings("rawtypes") final AdapterView parent, final View v, final int position, final long id) {
			   Card cardToPlay = (Card) v.getTag();
			   if(gameManager.canPlayCard(cardToPlay)){
				   if(cardToPlay.value == 11){
					   //TODO wish suit
					   playCard(cardToPlay);
				   }else{					   
					   playCard(cardToPlay);
				   }					
			   }
		   }
	  });  
	 	  
	  
	  playedCardsGallery.setOnItemClickListener(new OnItemClickListener() {
		  @Override
		  public void onItemClick(final AdapterView<?> parent, final View v, final int position, final long id) {
		  }
	  });
	  
	  listview = (ListView) findViewById(R.id.listView);
	  
	  playerListAdapter = new PlayerListAdapter(application, this, android.R.layout.test_list_item);
	  listview.setAdapter(playerListAdapter);
	  
	  if(application.isHost()){		  
		  getCards();
	  }
	}
	
	private void playCard(Card cardToPlay) {
		try {
			gameManager.PlayCard(cardToPlay.id, application.getUniqueID());
		} catch (BusException e) {
			e.printStackTrace();
		}
		ownCardsAdapter.notifyDataSetChanged();
		playedCardsAdapter.notifyDataSetChanged();
		
		if(ownCardsAdapter.getCount()== 0){
	    	CharSequence text = "MAU MAU";
	    	int duration = Toast.LENGTH_LONG;

	    	Toast toast = Toast.makeText(this, text, duration);
	    	toast.show();
		}
	}
	
	private void getCards(){
		for(int i = 0; i < GameManager.NUMBER_OF_CARDS_AT_START;i++){
			gameManager.getNewCard();
		}			
	}
	
	@Override
	public void onBackPressed(){
	  super.onBackPressed();
	  application.leaveChannel();
	}
	  
	
	public void start(View view){
		startButton.setVisibility(View.GONE);
		gameManager.startGameAsHost();
	}
	
	public void drawCard(View view){
		//can only draw once per turn
		drawCardButton.setEnabled(false);
		
		int specialCase = gameManager.getSpecialCase();
		Card playedCard = gameManager.getPlayedCard();
		int numberOfCardsToDraw = 1;
		Log.i("GA", "special Case ; " + specialCase);
		if(playedCard.value == 7 && specialCase != 0){
			numberOfCardsToDraw = 2 * specialCase;
		}
		while(numberOfCardsToDraw != 0){
			gameManager.getNewCard();
			numberOfCardsToDraw--;
		}
		CardsDrawnEvent cardsDrawnEvent = new CardsDrawnEvent(gameManager.getPlayCardRuleEnforcer(), gameManager);
		cardsDrawnEvent.updateRuleEnforcer();
		nextTurnButton.setEnabled(true);
	}
	
	public void nextTurn(View view){
		gameManager.setSpecialCase(SpecialCases.DEFAULT);
		gameManager.nextTurn(false);
	}
	
	private void updateButtonsState() {
		drawCardButton.setEnabled(gameManager.isMyTurn() && !(gameManager.getPlayedCard().value == 14));
		nextTurnButton.setEnabled(gameManager.isMyTurn() && (gameManager.getPlayedCard().value == 14 || gameManager.wereCardsDrawnThisTurn()));
	}
	
	@Override
	public void update(int args) {
		switch(args){
			case GameManager.PLAYERS_STATE_CHANGED: runOnUiThread(new Runnable() {			
				@Override
				public void run() {
					playerListAdapter.refresh();
					playerListAdapter.notifyDataSetChanged();
					updateButtonsState();
				}
			});
			break;
			
			case GameManager.NEXT_TURN: runOnUiThread(new Runnable() {			
				@Override
				public void run() {
					playerListAdapter.refresh();
					playerListAdapter.notifyDataSetChanged();					
				}
			});
			break;
			
			case GameManager.CARD_PLAYED: runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					playedCardsAdapter.notifyDataSetChanged();	
					int count = playedCardsAdapter.getCount();
					if(count!= 0){
						playedCardsLabel.setVisibility(View.VISIBLE);
					}else{
						playedCardsLabel.setVisibility(View.INVISIBLE);						
					}
				}
			});break;
			
			case GameManager.DRAW_CARDS: runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					ownCardsAdapter.notifyDataSetChanged();					
				}
			});break;			
			default: break;			
		}		
	}
}



