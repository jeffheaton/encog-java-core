package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.encog.app.quant.temporal.TemporalType;
import org.encog.app.quant.temporal.TemporalWindowCSV;
import org.encog.util.csv.CSVFormat;
import org.junit.Assert;

public class TestTemporalWindowCSV extends TestCase {

	public static final String INPUT_NAME = "test.csv";
    public static final String OUTPUT_NAME = "test2.csv";


    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("a,b");
        }

        tw.println("100,1");
        tw.println("200,2");
        tw.println("300,3");
        tw.println("400,4");
        tw.println("500,5");
        tw.println("600,6");
        tw.println("700,7");
        tw.println("800,8");
        tw.println("900,9");
        tw.println("1000,10");

        // close the stream
        tw.close();
    }

    public void testTemp() throws IOException
    {
        generateTestFileHeadings(true);
        TemporalWindowCSV norm = new TemporalWindowCSV();
        norm.setInputWindow( 5);
        norm.setPredictWindow( 1);
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);
        norm.getFields()[0].setAction( TemporalType.PassThrough );
        norm.getFields()[1].setAction( TemporalType.InputAndPredict );
        norm.process(OUTPUT_NAME);
       
        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("a,Input:b(t),Input:b(t-1),Input:b(t-2),Input:b(t-3),Input:b(t-4),Predict:b(t+1)",tr.readLine());
        Assert.assertEquals("\"600\",1,2,3,4,5,6",tr.readLine());
        Assert.assertEquals("\"700\",2,3,4,5,6,7",tr.readLine());
        Assert.assertEquals("\"800\",3,4,5,6,7,8",tr.readLine());
        Assert.assertEquals("\"900\",4,5,6,7,8,9",tr.readLine());
        Assert.assertEquals("\"1000\",5,6,7,8,9,10",tr.readLine());
        Assert.assertNull(tr.readLine());

        tr.close();

        (new File("test.csv")).delete();
        (new File("test2.csv")).delete();
    }
    
    public void testTempNoHeaders() throws IOException
    {
        generateTestFileHeadings(false);
        TemporalWindowCSV norm = new TemporalWindowCSV();
        norm.setInputWindow( 5);
        norm.setPredictWindow( 1);
        norm.analyze(INPUT_NAME, false, CSVFormat.ENGLISH);
        norm.getFields()[0].setAction( TemporalType.PassThrough );
        norm.getFields()[1].setAction( TemporalType.InputAndPredict );
        norm.process(OUTPUT_NAME);
       
        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("\"600\",1,2,3,4,5,6",tr.readLine());
        Assert.assertEquals("\"700\",2,3,4,5,6,7",tr.readLine());
        Assert.assertEquals("\"800\",3,4,5,6,7,8",tr.readLine());
        Assert.assertEquals("\"900\",4,5,6,7,8,9",tr.readLine());
        Assert.assertEquals("\"1000\",5,6,7,8,9,10",tr.readLine());
        Assert.assertNull(tr.readLine());

        tr.close();

        (new File("test.csv")).delete();
        (new File("test2.csv")).delete();
    }

	
}
