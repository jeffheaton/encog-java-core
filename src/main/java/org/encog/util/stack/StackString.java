package org.encog.util.stack;

import org.encog.EncogError;

public class StackString {
	private String[] stack;
	private int head = 0;
	
	public StackString(int size) {
		this.stack = new String[size];
	}
	
	public boolean isEmpty() {
		return this.head == 0;
	}
	
	public void push(String str) {
		if( this.head==this.stack.length) {
			throw new EncogError("Stack overflow");
		}
		this.stack[this.head++] = str;
	}
	
	public String pop() {
		if( this.head==0 ) {
			throw new EncogError("Stack is empty");
		}
		return this.stack[--this.head];
	}
	
	public int size() {
		return this.head;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[StackString: ");
		for(int i=head-1;i>=0;i--) {
			result.append(this.stack[i]);
			result.append(" ");
		}
		result.append("]");
		return result.toString();
	}
	
}
