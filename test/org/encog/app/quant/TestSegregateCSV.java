package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.app.quant.segregate.SegregateCSV;
import org.encog.app.quant.segregate.SegregateTargetPercent;
import org.encog.util.csv.CSVFormat;

public class TestSegregateCSV extends TestCase {

	public static final File INPUT_NAME = new File("test.csv");
    public static final File OUTPUT1_NAME = new File("test2.csv");
    public static final File OUTPUT2_NAME = new File("test3.csv");

    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("a,b");
        }
        tw.println("one,1");
        tw.println("two,2");
        tw.println("three,3");
        tw.println("four,4");

        // close the stream
        tw.close();
    }

    public void testFilterCSVHeaders() throws IOException
    {
        generateTestFileHeadings(true);
        SegregateCSV norm = new SegregateCSV();
        norm.getTargets().add(new SegregateTargetPercent(OUTPUT1_NAME, 75));
        norm.getTargets().add(new SegregateTargetPercent(OUTPUT2_NAME, 25));
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);            
        norm.process();

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT1_NAME));
        Assert.assertEquals("\"a\",\"b\"",tr.readLine());
        Assert.assertEquals("one,1",tr.readLine());
        Assert.assertEquals("two,2",tr.readLine());
        Assert.assertEquals("three,3", tr.readLine());
        Assert.assertNull(tr.readLine());
        tr.close();

        tr = new BufferedReader(new FileReader(OUTPUT2_NAME));
        Assert.assertEquals("\"a\",\"b\"", tr.readLine());
        Assert.assertEquals("four,4", tr.readLine());
        Assert.assertNull(tr.readLine());
        tr.close();

        INPUT_NAME.delete();
        OUTPUT1_NAME.delete();
        OUTPUT2_NAME.delete();
    }

    public void testFilterCSVNoHeaders() throws IOException
    {
        generateTestFileHeadings(false);
        SegregateCSV norm = new SegregateCSV();
        norm.getTargets().add(new SegregateTargetPercent(OUTPUT1_NAME, 75));
        norm.getTargets().add(new SegregateTargetPercent(OUTPUT2_NAME, 25));
        norm.analyze(INPUT_NAME, false, CSVFormat.ENGLISH);
        norm.setProduceOutputHeaders(false);
        norm.process();

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT1_NAME));
        Assert.assertEquals("one,1", tr.readLine());
        Assert.assertEquals("two,2", tr.readLine());
        Assert.assertEquals("three,3", tr.readLine());
        Assert.assertNull(tr.readLine());
        tr.close();

        tr = new BufferedReader(new FileReader(OUTPUT2_NAME));
        Assert.assertEquals("four,4", tr.readLine());
        Assert.assertNull(tr.readLine());
        tr.close();

        INPUT_NAME.delete();
        OUTPUT1_NAME.delete();
        OUTPUT2_NAME.delete();
    }
	
}
