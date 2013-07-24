package de.bachelor.maumau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import de.bachelor.maumau.rules.CardPlayedEvent;
import de.bachelor.maumau.rules.NoCardsPlayedRule;
import de.bachelor.maumau.rules.RuleEnforcer;
import de.bachelor.maumau.rules.SameSuitRule;
import de.bachelor.maumau.rules.SameValueRule;
import de.bachelor.maumau.rules.YourTurnRule;
import de.ptpservice.PTPHelper;
import de.uniks.jism.xml.XMLIdMap;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class GameManager {
		private static final String TAG = "GM";	
		
		public class SpecialCases{			
			public final static int DEFAULT = 0;
			public final static int SEVEN_PLAYED_ONCE = 1;
			public final static int SEVEN_PLAYED_TWICE = 2;
			public final static int SEVEN_PLAYED_TREE_TIMES = 3;
			public final static int SEVEN_PLAYED_FOUR_TIMES = 4;
			public final static int SUIT_WISHED_CLUB = 5;
			public final static int SUIT_WISHED_DIAMOND = 6;
			public final static int SUIT_WISHED_HEART = 7;
			public final static int SUIT_WISHED_SPADE = 8;			
			public final static int SKIP_TURN = 9;			
		}
		
		private static final String PLAYED_CARD = "PlayedCard";
		
		public static final int DECK_CHANGED = 0;
		public static final int PLAYERS_STATE_CHANGED = 1;
		public static final int CARD_PLAYED = 2;
		public static final int OWNER_CHANGED = 3;
		public static final int TELL_OWNED_CARDS = 4;
		public static final int DRAW_CARDS = 5;
		public static final int NEXT_TURN = 6;
		
		
		public static final int NUMBER_OF_CARDS_AT_START = 6;		
		private static final int NUMBER_OF_VALUES = 14;
		
		private boolean isInitState = true;
		private final boolean IS_SKAT = true;
		
		private List<Card> deckOfCards = new ArrayList<Card>();
		private Card playedCard = null;
		private Map<String,String> joinedPlayers = new HashMap<String,String>();
		private Random random = new Random(System.currentTimeMillis());
		private Bitmap allCards = null;
		private List<GameManagerObserver> observers = new ArrayList<GameManagerObserver>();
		private Stack<Integer> ownedCardIds = new Stack<Integer>();
		private RuleEnforcer playCardRuleEnforcer = new RuleEnforcer();
		private String currentPlayersID = "";
		private int specialCase = SpecialCases.DEFAULT;
		private String lastCardPlayedBy = "";
		private boolean cardsDrawnThisTurn;
		private boolean cardPlayedThisTurn;

		private boolean gameStarted = false;
		
		public GameManager(MauMauApplication application){
			getPlayCardRuleEnforcer().addInclusiveRule(new YourTurnRule(this));
			getPlayCardRuleEnforcer().addExclusiveRule(new SameSuitRule(this));
			getPlayCardRuleEnforcer().addExclusiveRule(new SameValueRule(this));
			getPlayCardRuleEnforcer().addExclusiveRule(new NoCardsPlayedRule(this));
		}
		
		public void initDeck() {			
			deckOfCards.clear();
			int id = 0;
			for(int i = 0; i < 4;i++){
				for(int j = IS_SKAT ? 7 : 2;j <= NUMBER_OF_VALUES;j++){
					Card card = new Card();
					card.owner = "";
					card.suit = i;
					card.value = j;	
					card.id = id++;
					deckOfCards.add(card);
				}
			}
		}
		
		public Bitmap getBitmap(Context context,Card card){
			if(allCards == null){
				allCards = BitmapFactory.decodeResource(context.getResources(),
		             R.drawable.cards);
			}			
			int cardWidth = allCards.getWidth()/13;
			int cardHeight = allCards.getHeight()/5;
			int value = card.value;
			if(value == 14) value = 1; //Ace is positioned at the beginning of the bitmap
			Bitmap cardBitmap = Bitmap.createBitmap(allCards,(value-1)*(cardWidth), (card.suit)*(cardHeight),cardWidth, cardHeight);
			return cardBitmap;			
		}
		
		public Bitmap getCardsBackBitmap(Context context){
			Card card = new Card();
			card.suit = 4;
			card.value = 3;
			return getBitmap(context, card);			
		}
		
		public Card getNewCard(){
			List<Card> notOwnedCards = new ArrayList<Card>();
			for (Card card : deckOfCards) {
				if(card.owner.isEmpty()){
					notOwnedCards.add(card);
				}
			}
			
			if(notOwnedCards.isEmpty()) return null;
			if(notOwnedCards.size() == 1) return notOwnedCards.get(0);
			cardsDrawnThisTurn = true;
			int cardPosition = random.nextInt(notOwnedCards.size()-1);
			Card card = notOwnedCards.get(cardPosition);
			card.owner = PTPHelper.getInstance().getUniqueID();
			sendCardOwnerChanged(card);
			notifyObservers(DRAW_CARDS);
			
			this.specialCase = SpecialCases.DEFAULT;
			return card;
		}	
		
		private String getXMLStringForCard(Card card){			 	    	
	    	XMLIdMap map=new XMLIdMap();
	    	map.withCreator(new CardCreator());
	    	return map.encode(card).toString();
		}
		
		private void sendCardOwnerChanged(Card card) {
			String xmlStringForCard = getXMLStringForCard(card);
			PTPHelper.getInstance().sendDataToAllPeers(OWNER_CHANGED, new String[]{xmlStringForCard});
		}

		public synchronized void ChangeOwner(int cardId, String uniqueUserID){
			Card cardById = getCardById(cardId);
			cardById.owner = uniqueUserID;
			drawCardsIfNeeded();
			notifyObservers(PLAYERS_STATE_CHANGED);
		}
		
		public synchronized void NextTurn(String previousID,int specialCase){
			this.specialCase = specialCase;
			if(playedCard!=null){
				CardPlayedEvent cardPlayedEvent = new CardPlayedEvent(this.playedCard, playCardRuleEnforcer,this);
				cardPlayedEvent.updateRuleEnforcer();
			}
			currentPlayersID = getNextPlayersId(previousID);
			cardsDrawnThisTurn = false;
			notifyObservers(PLAYERS_STATE_CHANGED);
		}
		
		public void startGameAsHost(){
			setGameStarted(true);
			cardsDrawnThisTurn = false;
			setAndNotifyNextTurn(PTPHelper.getInstance().getUniqueID());
		}
		
		private void drawCardsIfNeeded() {
			if(isInitState && !PTPHelper.getInstance().isHost()){
				Set<String> keySet = joinedPlayers.keySet();
				boolean allPlayersSetUp = true;
				for (String key : keySet) {
					if(key.equals(PTPHelper.getInstance().getUniqueID())) continue;
					
					int size = getCardsForPlayersId(key).size();
					if(!(size == NUMBER_OF_CARDS_AT_START)){
						allPlayersSetUp = false;
						break;
					}
				}
				if(allPlayersSetUp){
					for(int i = 0;i < NUMBER_OF_CARDS_AT_START;i++){
						getNewCard();
					}
					notifyObservers(DRAW_CARDS);
					isInitState = false;
				}				
			}
		}

		public synchronized void PlayCard(int cardId, String uniqueUserID) {
			Card card = getCardById(cardId);
			this.setLastCardPlayedBy(uniqueUserID);
			card.owner = PLAYED_CARD;
			
			if(playedCard != null){
				playedCard.owner= "";
			}
			playedCard = card;
			if(uniqueUserID.equals(PTPHelper.getInstance().getUniqueID())){
				cardPlayedThisTurn = true;
				sendCardPlayed(card);
				nextTurn();
			}
			
			notifyObservers(CARD_PLAYED);
			notifyObservers(PLAYERS_STATE_CHANGED);			
		}

		private void sendCardPlayed(Card card) {
			PTPHelper.getInstance().sendDataToAllPeers(CARD_PLAYED, new String[]{(card.id+"")});			
		}

		private Card getCardById(int cardId) {
			for (Card card : deckOfCards) {
				if(card.id == cardId) return card;
			}
			return null;
		}

		public List<Card> getOwnCards() {			
			return getCardsForPlayersId(PTPHelper.getInstance().getUniqueID());			
		}

		public List<Card> getCardsForPlayersId(String uniqueID) {
			ArrayList<Card> ownedCards = new ArrayList<Card>();
			for (Card card : deckOfCards) {
				if(card.owner.equals(uniqueID)){
					ownedCards.add(card);
				}
			}
			return ownedCards;
		}
		
		public int getNumberOfCardsForPlayer(String uniqueID){
			return getCardsForPlayersId(uniqueID).size();
		}

		public Card getPlayedCard() {
			return playedCard;
		}

		public synchronized void HiIAm(String uniqueID,String playerName) {
			Log.i(TAG, "HiIAm: " + uniqueID + " , " + playerName);			
			if(joinedPlayers.get(uniqueID) == null ){
				
				joinedPlayers.put(uniqueID, playerName);
				notifyObservers(PLAYERS_STATE_CHANGED);
				notifyOthersAboutYourself();
				sendOwnedCards();
			}
		}	
		
		private void sendOwnedCards() {
			for (Card card : getOwnCards()) {
				sendCardOwnerChanged(card);
			}	
		}
		
		private void notifyObservers(int arg) {
			for (GameManagerObserver observer : observers) {
				observer.update(arg);
			}
		}

		public synchronized void ByeIWas(String uniqueID) {
			if(joinedPlayers.get(uniqueID) !=null ){
				joinedPlayers.remove(uniqueID);
				releaseCardsForUserID(uniqueID);
				notifyObservers(PLAYERS_STATE_CHANGED);
			}
		}

		private void releaseCardsForUserID(String uniqueID) {
			for (Card card : deckOfCards) {
				if(uniqueID.equals(card.owner)){
					card.owner = "";
				}
			}
			
		}

		public Map<String,String> getJoinedPlayers() {
			return joinedPlayers;
		}

		public void addObserver(GameManagerObserver observer){
			observers.add(observer);
		}
		public void removeObserver(GameManagerObserver observer){
			observers.remove(observer);
		}
		
		public synchronized int getOwnedCardId(){
			if(!ownedCardIds.isEmpty()){
				return ownedCardIds.pop();
			}
			return -1;
		}

		public boolean isMyTurn() {
			return PTPHelper.getInstance().getUniqueID().equals(currentPlayersID);
		}	
		
		public String getCurrentPlayersID(){
			return currentPlayersID;
		}
		
		public boolean canPlayCard(Card card){
			return getPlayCardRuleEnforcer().allRulesPassed(card);
		}

		public void nextTurn() {
			updateSpecialCaseOnTurnsEnd();
			
			cardsDrawnThisTurn = false;
			cardPlayedThisTurn = false;
			String nextPlayersID = getNextPlayersId(PTPHelper.getInstance().getUniqueID());
			setAndNotifyNextTurn(nextPlayersID);
		}

		private void updateSpecialCaseOnTurnsEnd() {
			if( !lastCardPlayedBy.equals(PTPHelper.getInstance().getUniqueID()) && playedCard.value != 11){
				specialCase = SpecialCases.DEFAULT;
				return;
			}
			if(playedCard.value == 7){
				if(specialCase <= SpecialCases.SEVEN_PLAYED_TREE_TIMES) specialCase ++;
				else specialCase = SpecialCases.SEVEN_PLAYED_ONCE;
			}
			if((playedCard.value == 8 || playedCard.value == 14) && cardPlayedThisTurn){
				specialCase = SpecialCases.SKIP_TURN;
			}
		}

		private void setAndNotifyNextTurn(String uniqueID) {
			currentPlayersID = uniqueID;
			notifyObservers(NEXT_TURN);
			sendNextTurn();
		}

		private void sendNextTurn() {
			PTPHelper.getInstance().sendDataToAllPeers(NEXT_TURN, new String[]{(""+specialCase)});
		}

		public RuleEnforcer getPlayCardRuleEnforcer() {
			return playCardRuleEnforcer;
		}

		public int getSpecialCase() {
			return specialCase;
		}
		
		public void setSpecialCase(int specialCase) {
			this.specialCase= specialCase ;
		}

		public String getLastCardPlayedBy() {
			return lastCardPlayedBy;
		}

		private void setLastCardPlayedBy(String lastCardPlayedBy) {
			this.lastCardPlayedBy = lastCardPlayedBy;
		}
		
		public boolean wereCardsDrawnThisTurn() {
			return cardsDrawnThisTurn;
		}
		
		public String getNextPlayersId(String currentID){
			Set<String> keySet = getJoinedPlayers().keySet();
		 	ArrayList<String> arrayList = new ArrayList<String>(keySet);
		 	int myIdPosition = -1;
		    for(int i = 0; i < arrayList.size(); i++){
		    	if(arrayList.get(i).equals(currentID)) {
		    		myIdPosition = i;
		    		break;
		    	}
			}		    
		    int playersIdPosition  = (myIdPosition + 1) % arrayList.size();
		    if(playersIdPosition < 0){
		    	playersIdPosition += arrayList.size();
		    }
		    return arrayList.get(playersIdPosition);
			
		}

		public void reset() {
			observers.clear();
			ownedCardIds.clear();
			initDeck();
			specialCase = 0;
			lastCardPlayedBy = "";
			currentPlayersID = "";
			joinedPlayers.clear();
			playedCard = null;
			gameStarted = false;
			
			playCardRuleEnforcer.removeAllExclusiveRules();
			playCardRuleEnforcer.removeAllInclusiveRules();
			playCardRuleEnforcer.addInclusiveRule(new YourTurnRule(this));
			playCardRuleEnforcer.addExclusiveRule(new SameSuitRule(this));
			playCardRuleEnforcer.addExclusiveRule(new SameValueRule(this));
			playCardRuleEnforcer.addExclusiveRule(new NoCardsPlayedRule(this));
		}

		public void notifyOthersAboutYourself() {			
				HiIAm(PTPHelper.getInstance().getUniqueID(), PTPHelper.getInstance().getPlayerName());
				PTPHelper.getInstance().sendDataToAllPeers(PLAYERS_STATE_CHANGED, new String[]{PTPHelper.getInstance().getPlayerName()});
		}

		public boolean isGameStarted() {
			return gameStarted;
		}

		public void setGameStarted(boolean gameStarted) {
			this.gameStarted = gameStarted;
		}


}
