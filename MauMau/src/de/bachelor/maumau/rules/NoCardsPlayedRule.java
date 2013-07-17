package de.bachelor.maumau.rules;

import de.bachelor.maumau.Card;
import de.bachelor.maumau.GameManager;

public class NoCardsPlayedRule implements RuleInterface{
	private GameManager gameManager;


	public NoCardsPlayedRule(GameManager gameManager){
		this.gameManager = gameManager;
	}

	@Override
	public boolean isAllowed(Card card) {
		return gameManager.getPlayedCard() == null;
	}
}
