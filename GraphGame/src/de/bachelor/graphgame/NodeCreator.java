package de.bachelor.graphgame;

import de.uniks.jism.interfaces.XMLEntityCreator;

public class NodeCreator implements XMLEntityCreator{

	@Override
	public String[] getProperties() {
		return new String[]{Node.PROPERTY_X,Node.PROPERTY_Y,Node.PROPERTY_ID, Node.PROPERTY_OWNER};
	}

	@Override
	public Object getSendableInstance(boolean arg0) {
		return new Node();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(Node.PROPERTY_X.equals(attribute)){
			return ""+((Node)entity).getX();
		}
		if(Node.PROPERTY_Y.equals(attribute)){
			return ""+((Node)entity).getY();
		}
		if(Node.PROPERTY_ID.equals(attribute)){
			return ""+((Node)entity).getId();
		}
		if(Node.PROPERTY_OWNER.equals(attribute)){
			return ((Node)entity).getOwner();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		Node node = (Node)entity;
		boolean valueSet = false;
		if(Node.PROPERTY_X.equals(attribute)){
			node.setX(Double.valueOf((String)value));
			return true;
		}
		if(Node.PROPERTY_Y.equals(attribute)){
			node.setY(Double.valueOf((String)value));
			return true;
		}
		if(Node.PROPERTY_ID.equals(attribute)){
			node.setid(Integer.valueOf((String)value));
			valueSet = true;
		}
		if(Node.PROPERTY_OWNER.equals(attribute)){
			node.setOwner((String)value);
			valueSet = true;
		}
		return valueSet;
	}

	@Override
	public String getTag() {
		return "Node";
	}

}
