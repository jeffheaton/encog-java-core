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
package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.encog.app.analyst.csv.sort.SortCSV;
import org.encog.app.analyst.csv.sort.SortType;
import org.encog.app.analyst.csv.sort.SortedField;
import org.encog.util.csv.CSVFormat;
import org.junit.Assert;

public class TestSort extends TestCase {

	public static final File INPUT_NAME = new File("test.csv");
    public static final File OUTPUT_NAME = new File("test2.csv");


    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("a,b");
        }

        tw.println("five,5");
        tw.println("four,4");
        tw.println("two,2");
        tw.println("three,3");                      
        tw.println("six,6");
        tw.println("one,1");

        // close the stream
        tw.close();
    }

    public void testSortHeaders() throws IOException
    {
        generateTestFileHeadings(true);
        SortCSV norm = new SortCSV();
        norm.getSortOrder().add(new SortedField(1,SortType.SortString,true));
        norm.process(INPUT_NAME,OUTPUT_NAME,true,CSVFormat.ENGLISH);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("\"a\",\"b\"", tr.readLine());
        Assert.assertEquals("\"one\",1", tr.readLine());
        Assert.assertEquals("\"two\",2", tr.readLine());
        Assert.assertEquals("\"three\",3", tr.readLine());
        Assert.assertEquals("\"four\",4", tr.readLine());
        Assert.assertEquals("\"five\",5", tr.readLine());
        Assert.assertEquals("\"six\",6", tr.readLine());
        Assert.assertNull(tr.readLine());


        tr.close();

        (new File("test.csv")).delete();
        (new File("test2.csv")).delete();
    }
    
    public void testSortNoHeaders() throws IOException
    {
        generateTestFileHeadings(false);
        SortCSV norm = new SortCSV();
        norm.getSortOrder().add(new SortedField(1,SortType.SortInteger,true));
        norm.setProduceOutputHeaders(false);
        norm.process(INPUT_NAME,OUTPUT_NAME,false,CSVFormat.ENGLISH);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("\"one\",1", tr.readLine());
        Assert.assertEquals("\"two\",2", tr.readLine());
        Assert.assertEquals("\"three\",3", tr.readLine());
        Assert.assertEquals("\"four\",4", tr.readLine());
        Assert.assertEquals("\"five\",5", tr.readLine());
        Assert.assertEquals("\"six\",6", tr.readLine());
        Assert.assertNull(tr.readLine());


        tr.close();

        (new File("test.csv")).delete();
        (new File("test2.csv")).delete();
    }


	
	
}
