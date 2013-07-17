package de.bachelor.maumau.rules;

import de.bachelor.maumau.Card;
import de.bachelor.maumau.GameManager;

public class YourTurnRule implements RuleInterface {
	
	private GameManager gameManager;

	public YourTurnRule(GameManager gameManager){
		this.gameManager = gameManager;
	}

	@Override
	public boolean isAllowed(Card card) {
		return gameManager.isMyTurn();
	}

}
