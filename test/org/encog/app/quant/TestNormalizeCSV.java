package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.quant.normalize.NormalizationDesired;
import org.encog.app.quant.normalize.NormalizeCSV;
import org.encog.util.csv.CSVFormat;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNormalizeCSV extends TestCase {

	public static final String INPUT_NAME = "test.csv";
	public static final String OUTPUT_NAME = "test2.csv";

    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("a,b,c,d,e");
        }
        tw.println("one,1,1,2,3");
        tw.println("two,2,2,3,4");
        tw.println("three,3,3,4,5");
        tw.println("four,3,4,5,6");
        tw.println("five,2,5,6,7");
        tw.println("six,1,6,7,8");

        // close the stream
        tw.close();
    }

    public void testNormCSVHeaders() throws IOException
    {
        generateTestFileHeadings(true);
        NormalizeCSV norm = new NormalizeCSV();
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);
        norm.getStats().getStats()[2].makePassThrough();
        norm.getStats().getStats()[3].setAction( NormalizationDesired.Ignore);
        norm.getStats().getStats()[4].setAction( NormalizationDesired.Ignore);
        norm.normalize(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));
        Assert.assertEquals("\"a\",\"b\",\"c\"",tr.readLine());
        Assert.assertEquals("\"one\",-1,\"1\"",tr.readLine());
        Assert.assertEquals("\"two\",0,\"2\"",tr.readLine());
        tr.close();

        (new File(INPUT_NAME)).delete();
		(new File(OUTPUT_NAME)).delete();
    }

    public void testNormCSVNoHeaders() throws IOException
    {
        generateTestFileHeadings(false);
        NormalizeCSV norm = new NormalizeCSV();
        norm.analyze(INPUT_NAME, false, CSVFormat.ENGLISH);
        norm.setProduceOutputHeaders(false);
        norm.getStats().getStats()[2].makePassThrough();
        norm.getStats().getStats()[3].setAction( NormalizationDesired.Ignore);
        norm.getStats().getStats()[4].setAction( NormalizationDesired.Ignore);
        norm.normalize(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));
        Assert.assertEquals("\"one\",-1,\"1\"",tr.readLine());
        Assert.assertEquals("\"two\",0,\"2\"",tr.readLine());
        tr.close();

        (new File(INPUT_NAME)).delete();
		(new File(OUTPUT_NAME)).delete();
    }

	
}
