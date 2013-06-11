package org.encog.ml.data.auto;

import org.encog.Encog;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestAutoFloatColumn extends TestCase {
	@Test
	public void testColumn() {
		float[] data = { 0.1f, 0.2f, 0.3f, 0.4f };
		AutoFloatColumn col = new AutoFloatColumn(data,0,10);	
		col.autoMinMax();
		Assert.assertEquals(0.1, col.getActualMin(), 0.0001);
		Assert.assertEquals(0.4, col.getActualMax(), 0.0001);
	}
}
