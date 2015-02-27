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

import org.encog.app.quant.indicators.MovingAverage;
import org.encog.app.quant.indicators.ProcessIndicators;
import org.encog.app.quant.indicators.predictive.BestClose;
import org.encog.util.csv.CSVFormat;

public class TestProcessIndicators extends TestCase {

    public final static File INPUT_NAME = new File("test.csv");
    public final static File OUTPUT_NAME = new File("test2.csv");

    public void generateTestFileHeadings(boolean header) throws IOException
    {
    	PrintWriter tw = new PrintWriter(new FileWriter(INPUT_NAME));

        if (header)
        {
            tw.println("date,close");
        }
        tw.println("20100101,1");
        tw.println("20100102,2");
        tw.println("20100103,3");
        tw.println("20100104,4");
        tw.println("20100105,5");
        tw.println("20100106,6");
        tw.println("20100107,7");
        tw.println("20100108,8");
        tw.println("20100109,9");
        tw.println("20100110,10");

        // close the stream
        tw.close();
    }

    public void testIndicatorsHeaders() throws IOException
    {
        generateTestFileHeadings(true);
        ProcessIndicators norm = new ProcessIndicators();
        norm.analyze(INPUT_NAME, true, CSVFormat.ENGLISH);
        norm.addColumn(new MovingAverage(3, true));
        norm.addColumn(new BestClose(3,true));
        norm.getColumns().get(0).setOutput(true);
        norm.process(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("\"date\",\"close\",\"MovAvg\",\"PredictBestClose\"", tr.readLine());
        Assert.assertEquals("20100103,3,2,6", tr.readLine());
        Assert.assertEquals("20100104,4,3,7", tr.readLine());
        Assert.assertEquals("20100105,5,4,8", tr.readLine());
        Assert.assertEquals("20100106,6,5,9", tr.readLine());
        Assert.assertEquals("20100107,7,6,10", tr.readLine());

        tr.close();

        INPUT_NAME.delete();
        OUTPUT_NAME.delete();
    }

    public void TestIndicatorsNoHeaders() throws IOException
    {
        generateTestFileHeadings(false);
        ProcessIndicators norm = new ProcessIndicators();
        norm.analyze(INPUT_NAME, false, CSVFormat.ENGLISH);
        norm.addColumn(new MovingAverage(3, true));
        norm.addColumn(new BestClose(3, true));
        norm.getColumns().get(0).setOutput( true );
        norm.renameColumn(1, "close");
        norm.process(OUTPUT_NAME);

        BufferedReader tr = new BufferedReader(new FileReader(OUTPUT_NAME));

        Assert.assertEquals("20100103,3,2,6", tr.readLine());
        Assert.assertEquals("20100104,4,3,7", tr.readLine());
        Assert.assertEquals("20100105,5,4,8", tr.readLine());
        Assert.assertEquals("20100106,6,5,9", tr.readLine());
        Assert.assertEquals("20100107,7,6,10", tr.readLine());

        tr.close();

        INPUT_NAME.delete();
        OUTPUT_NAME.delete();
    }
	
	
}
