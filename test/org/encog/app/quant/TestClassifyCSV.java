package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.quant.classify.ClassifyCSV;
import org.encog.app.quant.classify.ClassifyMethod;
import org.encog.util.csv.CSVFormat;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestClassifyCSV extends TestCase {

	public static final String INPUT_NAME = "test.csv";
	public static final String OUTPUT_NAME = "test2.csv";

    public void generateTestFile(boolean header) throws IOException
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


    public void testTheClassifyCSV() throws IOException
    {
        internalTest(true, ClassifyMethod.SingleField);
        internalTest(false, ClassifyMethod.OneOf);
        internalTest(false, ClassifyMethod.Equilateral);
    }


    public void testKeepOrig() throws IOException
    {
        generateTestFile(true);
        ClassifyCSV norm = new ClassifyCSV();
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);
        norm.addTarget(1, ClassifyMethod.SingleField, -1, "org");
        norm.process(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("\"a\",\"org\",\"b\"",tr.readLine());
        Assert.assertEquals("one,1,0",tr.readLine());
        Assert.assertEquals("two,2,1",tr.readLine());
        Assert.assertEquals("three,3,2", tr.readLine());
        tr.close();

		(new File(INPUT_NAME)).delete();
		(new File(OUTPUT_NAME)).delete();
    }

    public void internalTest(boolean headers, ClassifyMethod method) throws IOException
    {
        generateTestFile(headers);
        ClassifyCSV norm = new ClassifyCSV();
        norm.setPrecision(4);
        norm.analyze(INPUT_NAME, headers, CSVFormat.ENGLISH);
        norm.addTarget(1,method,-1,null);
        norm.setProduceOutputHeaders(headers);
        norm.process(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));


        switch( method )
        {
            case SingleField:
                if( headers )
                    Assert.assertEquals("\"a\",\"b\"",tr.readLine());

                Assert.assertEquals("one,0", tr.readLine());
                Assert.assertEquals("two,1",tr.readLine());
                Assert.assertEquals("three,2",tr.readLine());
                Assert.assertEquals("four,3",tr.readLine());
                break;
            case Equilateral:
                if( headers )
            		Assert.assertEquals("\"a\",\"b-0\",\"b-1\",\"b-2\"",tr.readLine());
                Assert.assertEquals("one,-0.8165,-0.4714,-0.3333",tr.readLine());
                Assert.assertEquals("two,0.8165,-0.4714,-0.3333",tr.readLine());
                Assert.assertEquals("three,0,0.9428,-0.3333",tr.readLine());
                Assert.assertEquals("four,0,0,1", tr.readLine());
                break;
            case OneOf:
            	if( headers)
            		Assert.assertEquals("\"a\",\"b-0\",\"b-1\",\"b-2\",\"b-3\"",tr.readLine());
                Assert.assertEquals("one,1.0,-1.0,-1.0,-1.0", tr.readLine());
                Assert.assertEquals("two,-1.0,1.0,-1.0,-1.0", tr.readLine());
                Assert.assertEquals("three,-1.0,-1.0,1.0,-1.0", tr.readLine());
                Assert.assertEquals("four,-1.0,-1.0,-1.0,1.0", tr.readLine());
                break;
        }

        tr.close();

        (new File(INPUT_NAME)).delete();
		(new File(OUTPUT_NAME)).delete();
    }

}
