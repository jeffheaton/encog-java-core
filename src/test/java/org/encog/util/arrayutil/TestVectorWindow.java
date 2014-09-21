package org.encog.util.arrayutil;

import org.encog.Encog;
import org.encog.EncogError;
import org.junit.Assert;
import org.junit.Test;

public class TestVectorWindow {
	@Test(expected=EncogError.class)
	public void testIncomplete() {
		double[] o = new double[1];
		VectorWindow window = new VectorWindow(3);
		window.add(new double[] {1});
		window.copyWindow(o, 0);
	}
	
	@Test
	public void testComplete() {
		final double[] expect = {1.0, 2.0, 3.0};
		double[] o = new double[3];
		VectorWindow window = new VectorWindow(3);
		window.add(new double[] {1});
		window.add(new double[] {2});
		window.add(new double[] {3});
		window.copyWindow(o, 0);
		Assert.assertArrayEquals(expect,o,Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	@Test
	public void testCompleteScroll() {
		final double[] expect = {2.0, 3.0, 4.0};
		double[] o = new double[3];
		VectorWindow window = new VectorWindow(3);
		window.add(new double[] {1});
		window.add(new double[] {2});
		window.add(new double[] {3});
		window.add(new double[] {4});
		window.copyWindow(o, 0);
		Assert.assertArrayEquals(expect,o,Encog.DEFAULT_DOUBLE_EQUAL);
	}

}
