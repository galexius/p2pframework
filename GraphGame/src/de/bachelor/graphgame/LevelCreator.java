package de.bachelor.graphgame;

import de.uniks.jism.interfaces.XMLEntityCreator;

public class LevelCreator implements XMLEntityCreator {

	@Override
	public String[] getProperties() {
		return new String[]{Level.PROPERTY_ID};
	}

	@Override
	public Object getSendableInstance(boolean arg0) {		
		return new Level();
	}

	@Override
	public Object getValue(Object arg0, String arg1) {
		if(Level.PROPERTY_ID.equals(arg1)){
			return ((Level)arg0).getId();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		if(Level.PROPERTY_ID.equals(attribute)){
			((Level)entity).setId(Integer.valueOf((String) value));
			return true;
		}
		if(Level.PROPERTY_NODE.equals(attribute)){
			((Level)entity).addToNodeList((Node) value);
			return true;
		}
		if(Level.PROPERTY_EDGE.equals(attribute)){
			((Level)entity).addToEdgeList((Edge) value);
			return true;
		}
		return false;
	}

	@Override
	public String getTag() {
		return "Level";
	}


}
