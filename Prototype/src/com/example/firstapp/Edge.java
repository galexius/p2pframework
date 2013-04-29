package com.example.firstapp;

import org.alljoyn.bus.annotation.Position;


public class Edge {
	@Position(0)
	private Node from;
	@Position(1)
	private Node to;

	public Edge(Node from, Node to){
		this.setFrom(from);
		this.setTo(to);
		
	}

	public Node getTo() {
		return to;
	}

	public void setTo(Node to) {
		this.to = to;
	}

	public Node getFrom() {
		return from;
	}

	public void setFrom(Node from) {
		this.from = from;
	}
}
