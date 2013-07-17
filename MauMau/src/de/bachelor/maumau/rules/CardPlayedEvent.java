package de.bachelor.maumau.rules;

import android.annotation.TargetApi;
import android.os.Build;
import de.bachelor.maumau.Card;
import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.SpecialCases;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CardPlayedEvent {
	
	private Card card;
	private GameManager gameManager;
	private RuleEnforcer ruleEnforcer;

	public CardPlayedEvent(Card card, RuleEnforcer ruleEnforcer, GameManager gameManager){
		this.card = card;
		this.ruleEnforcer = ruleEnforcer;
		this.gameManager = gameManager;
	}
	
	public void updateRuleEnforcer(){
		switch(card.value){
			case 7: DrawTwoCards();break;
			case 8: nextPlayerSkipsTurn();break;
			case 11: wishSuit();break;
			case 14: nextPlayerSkipsTurn();break;
			default: setupStandartRules(); break;
		}
	}

	private void setupStandartRules() {
		ruleEnforcer.removeAllExclusiveRules();
		
		ruleEnforcer.addExclusiveRule(new SameSuitRule(gameManager));
		ruleEnforcer.addExclusiveRule(new SameValueRule(gameManager));
		ruleEnforcer.addExclusiveRule(new JackAllowedRule(gameManager));
	}

	private void wishSuit() {
		int specialCase = gameManager.getSpecialCase();
		if(specialCase >= SpecialCases.SUIT_WISHED_CLUB && specialCase <= SpecialCases.SUIT_WISHED_SPADE){
			ruleEnforcer.removeAllExclusiveRules();
			ruleEnforcer.addExclusiveRule(new SuitWishedRule(gameManager,specialCase - SpecialCases.SUIT_WISHED_CLUB));
		}else{
			ruleEnforcer.addExclusiveRule(new AnyCardPlayable(gameManager));
		}
	}
	
	
	private void nextPlayerSkipsTurn() {
		if(gameManager.getSpecialCase() == SpecialCases.SKIP_TURN){
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
