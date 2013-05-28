package de.bachelor.maumau.rules;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.TargetApi;
import android.os.Build;
import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.Card;
import de.bachelor.maumau.GameManager.SpecialCases;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CardPlayedEvent {
	
	private Card card;
	private GameManager gameManager;
	private RuleEnforcer ruleEnforcer;
	private String myId;

	public CardPlayedEvent(Card card, RuleEnforcer ruleEnforcer, GameManager gameManager, String myId){
		this.card = card;
		this.ruleEnforcer = ruleEnforcer;
		this.gameManager = gameManager;
		this.myId = myId;
	}
	
	public void updateRuleEnforcer(){
		switch(card.value){
			case 7: DrawTwoCards();break;
			case 11: wishSuit();break;
			case 14: nextPlayerSkipsTurn();break;
			default: setupStandartRules(); break;
		}
	}

	private void setupStandartRules() {
		ruleEnforcer.removeAllExclusiveRules();
		
		ruleEnforcer.addExclusiveRule(new SameSuitRule(gameManager));
		ruleEnforcer.addExclusiveRule(new SameValueRule(gameManager));
	}

	private void wishSuit() {
		int specialCase = gameManager.getSpecialCase();
		if(specialCase >= SpecialCases.SUIT_WISHED_CLUB){
			ruleEnforcer.removeAllExclusiveRules();
			ruleEnforcer.addExclusiveRule(new SuitWishedRule(gameManager,specialCase - SpecialCases.SUIT_WISHED_CLUB));
		}else{
			ruleEnforcer.addExclusiveRule(new AnyCardPlayable(gameManager));
		}
	}
	
	private boolean didPreviousPlayerPlayedTheCard(){
		Set<String> keySet = gameManager.getJoinedPlayers().keySet();
	 	ArrayList<String> arrayList = new ArrayList<String>(keySet);
	 	int myIdPosition = -1;
	    for(int i = 0; i < arrayList.size(); i++){
	    	if(arrayList.get(i).equals(myId)) {
	    		myIdPosition = i;
	    		break;
	    	}
		}
	    
	    if(myIdPosition > 0) return arrayList.get(myIdPosition-1).equals(gameManager.getLastCardPlayedBy());
	    if(myIdPosition == 0) return arrayList.get(arrayList.size()-1).equals(gameManager.getLastCardPlayedBy());
	    
		return false;
		
	}

	private void nextPlayerSkipsTurn() {
		if(didPreviousPlayerPlayedTheCard()){
			ruleEnforcer.removeAllExclusiveRules();		
			ruleEnforcer.addExclusiveRule(new SameValueRule(gameManager));	
		}else{
			setupStandartRules();
		}
	}

	private void DrawTwoCards() {
		ruleEnforcer.removeAllExclusiveRules();		
		ruleEnforcer.addExclusiveRule(new SameValueRule(gameManager));
	}
	
}
