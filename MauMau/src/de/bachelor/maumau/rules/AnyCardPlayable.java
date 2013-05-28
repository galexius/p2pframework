package de.bachelor.maumau.rules;

import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.Card;

public class AnyCardPlayable implements RuleInterface {
	

	public AnyCardPlayable(GameManager gameManager){
	}

	@Override
	public boolean isAllowed(Card card) {
		return true;
	}

}
