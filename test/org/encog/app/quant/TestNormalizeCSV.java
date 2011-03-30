package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.app.quant.normalize.NormalizeCSV;
import org.encog.util.csv.CSVFormat;

public class TestNormalizeCSV extends TestCase {

	public static final File INPUT_NAME = new File("test.csv");
	public static final File OUTPUT_NAME = new File("test2.csv");

    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("a,b,c,d,e");
        }
        tw.println("one,1,1,a,3");
        tw.println("two,2,2,a,4");
        tw.println("three,3,3,b,4");
        tw.println("four,3,4,b,6");
        tw.println("five,2,5,c,7");
        tw.println("six,1,6,c,8");

        // close the stream
        tw.close();
    }

    public void testNormCSVHeaders() throws IOException
    {    	
    	String[] cls = {"a","b","c"};
        generateTestFileHeadings(true);
        NormalizeCSV norm = new NormalizeCSV();
        norm.setPrecision(4);
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);
        norm.getStats().getStats()[2].makePassThrough();
        norm.getStats().getStats()[3].makeClass( NormalizationAction.Equilateral, cls, 1, -1 );
        norm.getStats().getStats()[4].setAction( NormalizationAction.Ignore);
        norm.normalize(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));
        Assert.assertEquals("\"a\",\"b\",\"c\",\"d-0\",\"d-1\"",tr.readLine());
        Assert.assertEquals("\"one\",-1,\"1\",-0.866,-0.5",tr.readLine());
        Assert.assertEquals("\"two\",0,\"2\",-0.866,-0.5",tr.readLine());
        tr.close();

        INPUT_NAME.delete();
		OUTPUT_NAME.delete();
    }

    public void testNormCSVNoHeaders() throws IOException
    {
        generateTestFileHeadings(false);
        NormalizeCSV norm = new NormalizeCSV();
        norm.analyze(INPUT_NAME, false, CSVFormat.ENGLISH);
        norm.setProduceOutputHeaders(false);
        norm.getStats().getStats()[2].makePassThrough();
        norm.getStats().getStats()[3].setAction( NormalizationAction.Ignore);
        norm.getStats().getStats()[4].setAction( NormalizationAction.Ignore);
        norm.normalize(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));
        Assert.assertEquals("\"one\",-1,\"1\"",tr.readLine());
        Assert.assertEquals("\"two\",0,\"2\"",tr.readLine());
        tr.close();

        INPUT_NAME.delete();
		OUTPUT_NAME.delete();
    }

	
}
