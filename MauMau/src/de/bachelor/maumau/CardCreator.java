package de.bachelor.maumau;

import de.uniks.jism.interfaces.XMLEntityCreator;

public class CardCreator implements XMLEntityCreator {

	@Override
	public String[] getProperties() {
		return new String[]{Card.PROPERTY_ID,Card.PROPERTY_SUIT,Card.PROPERTY_VALUE,Card.PROPERTY_OWNER};
	}

	@Override
	public Object getSendableInstance(boolean arg0) {		
		return new Card();
	}

	@Override
	public Object getValue(Object object, String attributeName) {
		if(Card.PROPERTY_ID.equals(attributeName)){
			return ""+((Card)object).id;
		}
		if(Card.PROPERTY_SUIT.equals(attributeName)){
			return ""+((Card)object).suit;
		}
		if(Card.PROPERTY_VALUE.equals(attributeName)){
			return ""+((Card)object).value;
		}
		if(Card.PROPERTY_OWNER.equals(attributeName)){
			return ((Card)object).owner;
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attributeName, Object value, String typ) {
		if(Card.PROPERTY_SUIT.equals(attributeName)){
			((Card)entity).suit = (Integer.valueOf((String) value));
			return true;
		}
		if(Card.PROPERTY_ID.equals(attributeName)){
			((Card)entity).id = (Integer.valueOf((String) value));
			return true;
		}
		if(Card.PROPERTY_VALUE.equals(attributeName)){
			((Card)entity).value = (Integer.valueOf((String) value));
			return true;
		}
		if(Card.PROPERTY_OWNER.equals(attributeName)){
			((Card)entity).owner =(String) value;
			return true;
		}
		
		return false;
	}

	@Override
	public String getTag() {
		return "Card";
	}


}
