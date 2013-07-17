package de.bachelor.maumau.rules;

import de.bachelor.maumau.Card;
import de.bachelor.maumau.GameManager;

public class JackAllowedRule implements RuleInterface{

	public JackAllowedRule(GameManager gameManager){
	}
	
	@Override
	public boolean isAllowed(Card card) {			
		return card.value == 11;
		
	}
}
