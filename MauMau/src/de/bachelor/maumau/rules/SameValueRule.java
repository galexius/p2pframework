package de.bachelor.maumau.rules;

import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.Card;

public class SameValueRule implements RuleInterface{
	private GameManager gameManager;

	public SameValueRule(GameManager gameManager){
		this.gameManager = gameManager;
	}
	
	@Override
	public boolean isAllowed(Card card) {	
		Card playedCard = gameManager.getPlayedCard();
		if(playedCard != null)
			return playedCard.value == card.value;
		return false;
	}
}
