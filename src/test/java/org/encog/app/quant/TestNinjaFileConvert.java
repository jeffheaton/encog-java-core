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

import org.encog.app.quant.ninja.NinjaFileConvert;
import org.encog.util.csv.CSVFormat;

public class TestNinjaFileConvert extends TestCase {
	
    public static final File INPUT_NAME = new File("test.csv");
    public static final File OUTPUT_NAME = new File("test2.csv");

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

        INPUT_NAME.delete();
        OUTPUT_NAME.delete();
    }


}
