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
