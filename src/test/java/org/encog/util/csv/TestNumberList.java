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
