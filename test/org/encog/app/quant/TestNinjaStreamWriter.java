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
