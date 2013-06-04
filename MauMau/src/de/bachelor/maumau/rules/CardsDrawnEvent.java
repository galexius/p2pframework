package de.bachelor.maumau.rules;

import android.annotation.TargetApi;
import android.os.Build;
import de.bachelor.maumau.GameManager;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CardsDrawnEvent {
	
	private GameManager gameManager;
	private RuleEnforcer ruleEnforcer;
	public CardsDrawnEvent(RuleEnforcer ruleEnforcer, GameManager gameManager){
		this.ruleEnforcer = ruleEnforcer;
		this.gameManager = gameManager;
	}
	
	public void updateRuleEnforcer(){
		setupStandartRules();
	}

	private void setupStandartRules() {
		ruleEnforcer.removeAllExclusiveRules();
		
		ruleEnforcer.addExclusiveRule(new SameSuitRule(gameManager));
		ruleEnforcer.addExclusiveRule(new SameValueRule(gameManager));
		ruleEnforcer.addExclusiveRule(new JackAllowedRule(gameManager));
	}

	
	
}
