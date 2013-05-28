package de.bachelor.maumau.rules;

import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.Card;

public class SameSuitRule implements RuleInterface {

	private GameManager gameManager;

	public SameSuitRule(GameManager gameManager){
		this.gameManager = gameManager;
	}
	
	@Override
	public boolean isAllowed(Card card) {	
		Card playedCard = gameManager.getPlayedCard();
		if(playedCard != null)
			return playedCard.suit == card.suit;
		return false;
	}

}
