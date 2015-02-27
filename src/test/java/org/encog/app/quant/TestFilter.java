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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.app.analyst.csv.filter.FilterCSV;
import org.encog.util.csv.CSVFormat;

public class TestFilter extends TestCase {

    public final static File INPUT_NAME = new File("test.csv");
    public final static File OUTPUT_NAME = new File("test2.csv");

    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("a,b");
        }
        tw.println("one,1");
        tw.println("two,1");
        tw.println("three,1");
        tw.println("four,2");
        tw.println("five,1");
        tw.println("six,1");

        // close the stream
        tw.close();
    }

    public void testFilterCSVHeaders() throws IOException
    {
        generateTestFileHeadings(true);
        FilterCSV norm = new FilterCSV();
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);
        norm.exclude(1, "1");
        norm.process(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));
        Assert.assertEquals("\"a\",\"b\"", tr.readLine());
        Assert.assertEquals("four,2", tr.readLine());
        tr.close();

        INPUT_NAME.delete();
        OUTPUT_NAME.delete();
    }

    public void TestFilterCSVNoHeaders() throws IOException
    {
        generateTestFileHeadings(false);
        FilterCSV norm = new FilterCSV();
        norm.analyze(INPUT_NAME, false, CSVFormat.ENGLISH);
        norm.exclude(1, "1");
        norm.process(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));
        Assert.assertEquals("four,2", tr.readLine());
        tr.close();

        INPUT_NAME.delete();
        OUTPUT_NAME.delete();
    }
	
}
