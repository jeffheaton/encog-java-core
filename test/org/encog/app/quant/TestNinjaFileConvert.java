package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.quant.ninja.NinjaFileConvert;
import org.encog.util.csv.CSVFormat;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNinjaFileConvert extends TestCase {
	
    public static final String INPUT_NAME = "test.csv";
    public static final String OUTPUT_NAME = "test2.csv";

    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("date,time,open,high,low,close,volume");
        }
        tw.println("20100101,000000,10,12,8,9,1000");
        tw.println("20100102,000000,9,17,7,15,1000");


        // close the stream
        tw.close();
    }

    public void testConvert() throws IOException
    {
        generateTestFileHeadings(true);
        NinjaFileConvert norm = new NinjaFileConvert();
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);
        norm.process(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("20100101 000000;10;12;8;9;1000",tr.readLine());
        Assert.assertEquals("20100102 000000;9;17;7;15;1000", tr.readLine());

        tr.close();

        (new File(INPUT_NAME)).delete();
        (new File(OUTPUT_NAME)).delete();
    }


}
