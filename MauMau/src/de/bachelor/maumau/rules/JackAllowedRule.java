package de.bachelor.maumau.rules;

import de.bachelor.maumau.GameManager;
import de.bachelor.maumau.GameManager.Card;

public class JackAllowedRule implements RuleInterface{

	public JackAllowedRule(GameManager gameManager){
	}
	
	@Override
	public boolean isAllowed(Card card) {			
		return card.value == 11;
		
	}
}
