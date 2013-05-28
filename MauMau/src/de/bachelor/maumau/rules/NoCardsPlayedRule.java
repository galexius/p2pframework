package de.bachelor.maumau.rules;

import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.Card;

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
