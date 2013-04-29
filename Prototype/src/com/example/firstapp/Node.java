package com.example.firstapp;

import org.alljoyn.bus.annotation.Position;

public class Node {
	@Position(0)
	public float x;
	@Position(1)
	public float y;
	@Position(2)
	private int id;
	@Position(3)
	private String owner ="";
	
	public Node(){};
	
    public Node(float x, float y,int id) {
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
