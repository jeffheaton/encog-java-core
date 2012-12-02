package org.encog.util.stack;

import org.encog.EncogError;

public class StackInt {
	private int[] stack;
	private int head = 0;
	
	public StackInt(int size) {
		this.stack = new int[size];
	}
	
	public boolean isEmpty() {
		return this.head == 0;
	}
	
	public void push(int i) {
		if( this.head==this.stack.length) {
			throw new EncogError("Stack overflow");
		}
		this.stack[this.head++] = i;
	}
	
	public int pop() {
		if( this.head==0 ) {
			throw new EncogError("Stack is empty");
		}
		return this.stack[--this.head];
	}
	
	public int add() {
		int a = pop();
		int b = pop();
		int result = b+a;
		push(result);
		return result;
	}
	
	public int sub() {
		int a = pop();
		int b = pop();
		int result = b-a;
		push(result);
		return result;
	}
	
	public int mul() {
		int a = pop();
		int b = pop();
		int result = b*a;
		push(result);
		return result;
	}
	
	public int div() {
		int a = pop();
		int b = pop();
		int result = b/a;
		push(result);
		return result;
	}
	
	public int size() {
		return this.head;
	}
	
	public int max(int size) {
		if( size()<size ) {
			throw new EncogError("Not enough data on stack.");
		}
		
		int result = Integer.MIN_VALUE;
		for(int i=0;i<size;i++) {
			result = Math.max(result, pop());
		}
		push(result);
		return result;
	}
	
	public int min(int size) {
		if( size()<size ) {
			throw new EncogError("Not enough data on stack.");
		}
		
		int result = Integer.MAX_VALUE;
		for(int i=0;i<size;i++) {
			result = Math.min(result, pop());
		}
		
		push(result);
		return result;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[StackInt: ");
		for(int i=head-1;i>=0;i--) {
			result.append(this.stack[i]);
			result.append(" ");
		}
		result.append("]");
		return result.toString();
	}
	
}
