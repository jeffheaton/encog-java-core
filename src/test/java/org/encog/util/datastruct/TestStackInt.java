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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.EncogError;

public class TestStackInt extends TestCase {
	
	public void testToString() {
		StackInt stack = new StackInt(5);
		stack.push(3);
		stack.push(4);
		Assert.assertEquals("[StackInt: 4 3 ]", stack.toString());
	}
	
	
	public void testStackAdd() {
		StackInt stack = new StackInt(5);
		stack.push(3);
		stack.push(4);
		stack.add();
		Assert.assertEquals(7, stack.pop());
	}
	
	public void testStackSub() {
		StackInt stack = new StackInt(5);
		stack.push(3);
		stack.push(4);
		Assert.assertEquals(-1, stack.sub());
		Assert.assertEquals(-1, stack.pop());
	}
	
	public void testStackMul() {
		StackInt stack = new StackInt(5);
		stack.push(3);
		stack.push(4);
		Assert.assertEquals(12, stack.mul());
		Assert.assertEquals(12, stack.pop());
	}
	
	public void testStackDiv() {
		StackInt stack = new StackInt(5);
		stack.push(3);
		stack.push(4);
		Assert.assertEquals(0, stack.div());
		Assert.assertEquals(0, stack.pop());
	}
	
	public void testStackMax() {
		StackInt stack = new StackInt(5);
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.push(4);
		Assert.assertEquals(4, stack.max(4));
		Assert.assertEquals(4, stack.pop());
	}
	
	public void testStackMin() {
		StackInt stack = new StackInt(5);
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.push(4);
		Assert.assertEquals(1, stack.min(4));
		Assert.assertEquals(1, stack.pop());
	}
	
	public void testUnderflow() {
		try {
			StackInt stack = new StackInt(5);
			stack.push(0);
			stack.push(0);
			stack.pop();
			stack.pop();
			stack.pop();
			Assert.assertFalse(true);
		} catch (EncogError e) {
			// error expected
		}
	}
	
	public void testOverflow() {
		try {
			StackInt stack = new StackInt(2);
			stack.push(0);
			stack.push(0);
			stack.push(0);
			Assert.assertFalse(true);
		} catch (EncogError e) {
			// error expected
		}
	}
	
	public void testSize() {
		StackInt stack = new StackInt(5);
		stack.push(0);
		stack.push(0);
		Assert.assertEquals(2,  stack.size());
	}
}
