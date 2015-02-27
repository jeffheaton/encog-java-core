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
