package de.bachelor.maumau.rules;

import de.bachelor.maumau.Card;
import de.bachelor.maumau.GameManager;

public class AnyCardPlayable implements RuleInterface {
	

	public AnyCardPlayable(GameManager gameManager){
	}

	@Override
	public boolean isAllowed(Card card) {
		return true;
	}

}
