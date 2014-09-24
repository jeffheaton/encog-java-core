/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.datastruct;

import org.encog.EncogError;

/**
 * An integer stack.
 */
public class StackInt {
	/**
	 * The stack.
	 */
	private int[] stack;
	
	/**
	 * The head.
	 */
	private int head = 0;
	
	/**
	 * Construct a new stack.
	 * @param size The size of the stack.
	 */
	public StackInt(int size) {
		this.stack = new int[size];
	}
	
	/**
	 * @return True, if the stack is empty.
	 */
	public boolean isEmpty() {
		return this.head == 0;
	}
	
	/**
	 * Push an int onto the stack.
	 * @param i The value to push onto the stack.
	 */
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
