package org.encog.util.datastruct;

import org.encog.EncogError;

public class StackObject<T> {
	private Object[] stack;
	private int head = 0;
	
	public StackObject(int size) {
		this.stack = new Object[size];
	}
	
	public boolean isEmpty() {
		return this.head == 0;
	}
	
	public void push(T str) {
		if( this.head==this.stack.length) {
			throw new EncogError("Stack overflow");
		}
		this.stack[this.head++] = str;
	}
	
	@SuppressWarnings("unchecked")
	public T pop() {
		if( this.head==0 ) {
			throw new EncogError("Stack is empty");
		}
		return (T)this.stack[--this.head];
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
