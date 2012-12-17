package org.encog.util.datastruct;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.EncogError;
import org.encog.util.datastruct.StackInt;

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
