package de.bachelor.maumau.rules;

import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.Card;

public class SuitWishedRule implements RuleInterface {

	private int wishedSuit;

	public SuitWishedRule(GameManager gameManager,int wishedSuit){
		this.wishedSuit = wishedSuit;
	}
	
	@Override
	public boolean isAllowed(Card card) {	
		return wishedSuit == card.suit;
	}

}
