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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestReadCSV extends TestCase {
	
	public static final String INPUT_NAME = "test.csv";

	public void testCSVComma() throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter(INPUT_NAME));
		out.println("one,1");
		out.println("two,2");
		out.println("three,3");
		out.close();
		
		ReadCSV csv = new ReadCSV(INPUT_NAME,false,CSVFormat.EG_FORMAT);
		csv.next();
		Assert.assertEquals("one", csv.get(0));
		Assert.assertEquals("1", csv.get(1));
		csv.next();
		Assert.assertEquals("two", csv.get(0));
		Assert.assertEquals("2", csv.get(1));
		csv.next();
		Assert.assertEquals("three", csv.get(0));
		Assert.assertEquals("3", csv.get(1));
		Assert.assertFalse(csv.next());
		csv.close();
	}
	
	public void testCSVSpace() throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter(INPUT_NAME));
		out.println("one 1 \"test one two three\"");
		out.println("two\t2 \"test one two three\"");
		out.println("three  3  \"test one two three\"");
		out.close();
		
		ReadCSV csv = new ReadCSV(INPUT_NAME,false,new CSVFormat('.',' '));
		csv.next();
		Assert.assertEquals(3, csv.getColumnCount());
		Assert.assertEquals("one", csv.get(0));
		Assert.assertEquals("1", csv.get(1));
		Assert.assertEquals("test one two three", csv.get(2));
		csv.next();
		Assert.assertEquals("two", csv.get(0));
		Assert.assertEquals("2", csv.get(1));
		csv.next();
		Assert.assertEquals("three", csv.get(0));
		Assert.assertEquals("3", csv.get(1));
		Assert.assertFalse(csv.next());
		csv.close();
	}
}
