package de.bachelor.graphgame;



public class Edge {
	public static final String PROPERTY_SRC="src";
	public static final String PROPERTY_DEST="dest";
	
	private int src;
	private int dest;

	public Edge(){
		
	}
	
	public Edge(int from, int to){
		this.setSrc(from);
		this.setDest(to);
		
	}

	public int getDest() {
		return dest;
	}

	public void setDest(int to) {
		this.dest = to;
	}

	public int getSrc() {
		return src;
	}

	public void setSrc(int from) {
		this.src = from;
	}
}
