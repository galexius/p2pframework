package com.example.firstapp;


public class Node {
	public double x;
	public double y;
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
}
