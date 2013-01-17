package org.encog.mathutil;

public class IntPair {
	private int x;
	private int y;
	
	public IntPair(int theX, int theY) {
		this.x = theX;
		this.y = theY;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void add(int v) {
		this.x+=v;
		this.y+=v;
	}
	
	public void addX(int v) {
		this.x+=v;
	}
	
	public void addY(int v) {
		this.y+=v;
	}
	
	public void add(int addX, int addY) {
		this.x+=addX;
		this.y+=addY;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[IntPair:");
		result.append(this.x);
		result.append(";");
		result.append(this.y);
		result.append("]");
		return result.toString();
	}
	
	
}
