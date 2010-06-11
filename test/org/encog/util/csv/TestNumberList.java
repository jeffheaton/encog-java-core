/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

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
