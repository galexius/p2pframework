package de.bachelor.graphgame;


public class Node {
	public static final String PROPERTY_X="x";
	public static final String PROPERTY_Y="y";
	public static final String PROPERTY_ID="id";
	public static final String PROPERTY_OWNER = "owner";
	
	private double x;
	private double y;
	private int id;
	private String owner ="";
	
	public Node(){};
	
    public Node(double x, double y,int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
    
    public int getId(){
    	return id;
    }

    public void setid(int id){
    	this.id = id;
    }

	@Override
    public String toString() {
        return x + ", " + y;
    }

	public void setOwner(String owner){
		this.owner = owner;		
	}

	public String getOwner() {
		return owner;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public double getX(){
		return x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public double getY(){
		return y;
	}
}
