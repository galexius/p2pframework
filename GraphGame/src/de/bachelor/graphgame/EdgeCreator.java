package de.bachelor.graphgame;

import de.uniks.jism.interfaces.XMLEntityCreator;

public class EdgeCreator implements XMLEntityCreator {

	@Override
	public String[] getProperties() {
		return new String[]{Edge.PROPERTY_SRC, Edge.PROPERTY_DEST};
	}

	@Override
	public Object getSendableInstance(boolean arg0) {		
		return new Edge();
	}

	@Override
	public Object getValue(Object arg0, String arg1) {
		if(Edge.PROPERTY_DEST.equals(arg1)){
			return ""+((Edge)arg0).getDest();
		}
		if(Edge.PROPERTY_SRC.equals(arg1)){
			return ""+((Edge)arg0).getSrc();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		if(Edge.PROPERTY_DEST.equals(attribute)){
			((Edge)entity).setDest(Integer.valueOf((String) value));
			return true;
		}
		if(Edge.PROPERTY_SRC.equals(attribute)){
			((Edge)entity).setSrc(Integer.valueOf((String) value));
			return true;
		}
		return false;
	}

	@Override
	public String getTag() {
		return "Edge";
	}


}
