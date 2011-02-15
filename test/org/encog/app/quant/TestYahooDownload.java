package org.encog.app.quant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.GregorianCalendar;

import org.encog.app.quant.loader.yahoo.YahooDownload;
import org.encog.util.csv.CSVFormat;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestYahooDownload extends TestCase {

	public void testYahooDownloadError() throws IOException {
		try {
			YahooDownload yahoo = new YahooDownload();
			yahoo.setPercision(2);
			// load a non-sense ticker, should throw error
			yahoo.loadAllData("sdfhusdhfuish", "test.txt", CSVFormat.ENGLISH,
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
		yahoo.loadAllData("yhoo", "test.txt", CSVFormat.ENGLISH,
				new GregorianCalendar(2000, 00, 01).getTime(),
				new GregorianCalendar(2000, 00, 10).getTime());
		BufferedReader tr = new BufferedReader(new FileReader("test.txt"));

		Assert.assertEquals(
				"date,time,open price,high price,low price,close price,volume,adjusted price",
				tr.readLine());
		Assert.assertEquals(
				"20000110,0,432.5,451.25,420,436.06,61022400,109.01",
				tr.readLine());
		Assert.assertEquals("20000107,0,366.75,408,363,407.25,48999600,101.81",
				tr.readLine());
		Assert.assertEquals("20000106,0,406.25,413,361,368.19,71301200,92.05",
				tr.readLine());
		Assert.assertEquals(
				"20000105,0,430.5,431.13,402,410.5,83194800,102.62",
				tr.readLine());
		tr.close();
	}

}
