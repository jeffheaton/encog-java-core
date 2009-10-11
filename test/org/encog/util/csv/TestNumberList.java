package org.encog.util.csv;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNumberList extends TestCase {
	public void testToList() throws Exception
	{
		StringBuilder result = new StringBuilder();
		double[] doubleData = { 0.5,10000,10.5 };
		NumberList.toList(CSVFormat.DECIMAL_POINT, result, doubleData);
		Assert.assertEquals("0.5,10000,10.5", result.toString());
		result.setLength(0);
		NumberList.toList(CSVFormat.DECIMAL_COMMA, result, doubleData);
		Assert.assertEquals("0,5;10000;10,5", result.toString());
	}
	
	public void testFromList() throws Exception
	{
		double[] d = NumberList.fromList(CSVFormat.DECIMAL_POINT, "1,2.5,3000");
		Assert.assertEquals(3, d.length);
		Assert.assertEquals(1, d[0], 0.1);
		Assert.assertEquals(2.5, d[1], 0.1);
		Assert.assertEquals(3000, d[2], 0.1);
		double[] d2 = NumberList.fromList(CSVFormat.DECIMAL_COMMA, "1;2,5;3000");
		Assert.assertEquals(3, d2.length);
		Assert.assertEquals(1, d2[0], 0.1);
		Assert.assertEquals(2.5, d2[1], 0.1);
		Assert.assertEquals(3000, d2[2], 0.1);
	}
	
}
