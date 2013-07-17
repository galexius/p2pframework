package de.bachelor.maumau.rules;

import de.bachelor.maumau.Card;
import de.bachelor.maumau.GameManager;

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
