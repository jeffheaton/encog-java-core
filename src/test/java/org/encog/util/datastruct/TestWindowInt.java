package org.encog.util.datastruct;

import org.junit.Assert;
import org.junit.Test;

public class TestWindowInt {
	
	@Test
	public void testSize() {
		WindowInt w = new WindowInt(5);
		Assert.assertEquals(5, w.size());
	}
	
	@Test
	public void testWindowAdd() {
		WindowInt w = new WindowInt(5);
		w.add(1);
		w.add(2);
		w.add(3);
		w.add(4);
		w.add(5);
		
		Assert.assertEquals(5,w.getData()[0]);
		Assert.assertEquals(4,w.getData()[1]);
		Assert.assertEquals(3,w.getData()[2]);
		Assert.assertEquals(2,w.getData()[3]);
		Assert.assertEquals(1,w.getData()[4]);
	}
	
	@Test
	public void testWindowShuft() {
		WindowInt w = new WindowInt(5);
		w.add(1);
		w.add(2);
		w.add(3);
		w.add(4);
		w.add(5);
		w.add(6);
		
		Assert.assertEquals(6,w.getData()[0]);
		Assert.assertEquals(5,w.getData()[1]);
		Assert.assertEquals(4,w.getData()[2]);
		Assert.assertEquals(3,w.getData()[3]);
		Assert.assertEquals(2,w.getData()[4]);
	}
}
