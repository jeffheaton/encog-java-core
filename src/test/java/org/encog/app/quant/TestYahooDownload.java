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
import java.io.IOException;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.encog.app.quant.loader.yahoo.YahooDownload;
import org.encog.util.csv.CSVFormat;
import org.junit.Assert;

public class TestYahooDownload extends TestCase {

	public void testYahooDownloadError() throws IOException {
		try {
			YahooDownload yahoo = new YahooDownload();
			yahoo.setPercision(2);
			// load a non-sense ticker, should throw error
			yahoo.loadAllData("sdfhusdhfuish", new File("test.txt"), CSVFormat.ENGLISH,
					new GregorianCalendar(2000, 00, 01).getTime(),
					new GregorianCalendar(2000, 00, 10).getTime());

			// bad!
			Assert.assertTrue(false);
		} catch (QuantError e) {
			// good!
		}
	}

	public void testYahooDownloadCSV() throws IOException {
		YahooDownload yahoo = new YahooDownload();
		yahoo.setPercision(2);
		yahoo.loadAllData("yhoo", new File("test.txt"), CSVFormat.ENGLISH,
				new GregorianCalendar(2000, 00, 01).getTime(),
				new GregorianCalendar(2000, 00, 10).getTime());
		BufferedReader tr = new BufferedReader(new FileReader("test.txt"));

		Assert.assertEquals(
				"date,time,open price,high price,low price,close price,volume,adjusted price",
				tr.readLine());
		Assert.assertEquals(
				"20000110,0,432.5,451.25,420,436.06,61022400,109.02",
				tr.readLine());
		Assert.assertEquals("20000107,0,366.75,408,363,407.25,48999600,101.81",
				tr.readLine());
		Assert.assertEquals("20000106,0,406.25,413,361,368.19,71301200,92.05",
				tr.readLine());
		Assert.assertEquals(
				"20000105,0,430.5,431.12,402,410.5,83194800,102.62",
				tr.readLine());
		tr.close();
	}

}
