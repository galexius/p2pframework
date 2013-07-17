package de.bachelor.maumau.rules;

import java.util.ArrayList;

import de.bachelor.maumau.Card;

public class RuleEnforcer {

	//All these rules need to pass
	private ArrayList<RuleInterface> inclusiveRules = new ArrayList<RuleInterface>();
	//At least one rule of these needs to pass 
	private ArrayList<RuleInterface> exclusiveRules = new ArrayList<RuleInterface>();
	
	public boolean allRulesPassed(Card card){		
		for (RuleInterface rule: inclusiveRules) {
			if(!rule.isAllowed(card)) return false;
		}
		boolean exclusiveRulesPassed = false;
		if(exclusiveRules.isEmpty()){
			return true;
		}
		for (RuleInterface exclusiveRule : exclusiveRules) {
			if(exclusiveRule.isAllowed(card)) return true;
		}
		return exclusiveRulesPassed;		
	}

	public ArrayList<RuleInterface> getInclusiveRules() {
		return inclusiveRules;
	}
	public ArrayList<RuleInterface> getExclusiveRules() {
		return exclusiveRules;
	}

	public void addInclusiveRule(RuleInterface rule) {
		if(!inclusiveRules.contains(rule)){
			inclusiveRules.add(rule);
		}
	}
	public void removeInclusiveRule(RuleInterface rule) {
		if(inclusiveRules.contains(rule)){
			inclusiveRules.remove(rule);
		}
	}
	public void addExclusiveRule(RuleInterface rule) {
		if(!exclusiveRules.contains(rule)){
			exclusiveRules.add(rule);
		}
	}
	public void removeExclusiveRule(RuleInterface rule) {
		if(exclusiveRules.contains(rule)){
			exclusiveRules.remove(rule);
		}
	}
	
	public void removeAllExclusiveRules(){
		exclusiveRules.clear();
	}
	public void removeAllInclusiveRules(){
		inclusiveRules.clear();
	}
	
}
