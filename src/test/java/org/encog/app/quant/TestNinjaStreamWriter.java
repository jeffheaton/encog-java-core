/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.encog.app.quant.ninja.NinjaStreamWriter;
import org.encog.util.csv.CSVFormat;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNinjaStreamWriter extends TestCase {

	public void testWrite() throws IOException {
		NinjaStreamWriter nsw = new NinjaStreamWriter();

		nsw.open("test.csv", true, CSVFormat.ENGLISH);

		nsw.beginBar(new GregorianCalendar(2010, 00, 01).getTime());
		nsw.storeColumn("close", 10);
		nsw.endBar();

		nsw.beginBar(new GregorianCalendar(2010, 00, 02).getTime());
		nsw.storeColumn("close", 11);
		nsw.endBar();

		nsw.close();

		BufferedReader tr = new BufferedReader(new FileReader("test.csv"));

		Assert.assertEquals("date,time,\"close\"", tr.readLine());
		Assert.assertEquals("20100101,0,10", tr.readLine());
		Assert.assertEquals("20100102,0,11", tr.readLine());

		tr.close();
	}

}
