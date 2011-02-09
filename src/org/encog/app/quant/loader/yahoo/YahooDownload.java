package org.encog.app.quant.loader.yahoo;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import org.encog.app.quant.loader.LoaderError;
import org.encog.app.quant.loader.MarketLoader;
import org.encog.neural.data.market.TickerSymbol;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.http.FormUtility;
import org.encog.util.time.NumericDateUtil;

public class YahooDownload implements MarketLoader {

    public static final String INDEX_DJIA = "^dji";
    public static final String INDEX_SP500 = "^gspc";
    public static final String INDEX_NASDAQ = "^ixic";

    public int percision;

    public YahooDownload()
    {
        setPercision( 10 );
    }


	/**
	 * This method builds a URL to load data from Yahoo Finance for a neural
	 * network to train with.
	 * 
	 * @param ticker
	 *            The ticker symbol to access.
	 * @param from
	 *            The beginning date.
	 * @param to
	 *            The ending date.
	 * @return The UEL
	 * @throws IOException
	 *             An error accessing the data.
	 */
	private URL buildURL(final String ticker, final Date from,
			final Date to) throws IOException {
		// process the dates
		final Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(from);
		final Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(to);

		// construct the URL
		final OutputStream os = new ByteArrayOutputStream();
		final FormUtility form = new FormUtility(os, null);
		form.add("s", ticker.toUpperCase());
		form.add("a", "" + calendarFrom.get(Calendar.MONTH));
		form.add("b", "" + calendarFrom.get(Calendar.DAY_OF_MONTH));
		form.add("c", "" + calendarFrom.get(Calendar.YEAR));
		form.add("d", "" + calendarTo.get(Calendar.MONTH));
		form.add("e", "" + calendarTo.get(Calendar.DAY_OF_MONTH));
		form.add("f", "" + calendarTo.get(Calendar.YEAR));
		form.add("g", "d");
		form.add("ignore", ".csv");
		os.close();
		final String str = "http://ichart.finance.yahoo.com/table.csv?"
				+ os.toString();
		return new URL(str);
	}

    public void loadAllData(String ticker, String output, CSVFormat outputFormat, Date from,
             Date to)
    {
    	try
    	{
		final URL url = buildURL(ticker, from, to);
		final InputStream is = url.openStream();
		final ReadCSV csv = new ReadCSV(is, true, CSVFormat.ENGLISH);
		
		PrintWriter tw = new PrintWriter(new FileWriter(output));
        tw.println("date,time,open price,high price,low price,close price,volume,adjusted price");

		while (csv.next()) {
			final Date date = csv.getDate("date");
			final double adjClose = csv.getDouble("adj close");
			final double open = csv.getDouble("open");
			final double close = csv.getDouble("close");
			final double high = csv.getDouble("high");
			final double low = csv.getDouble("low");
			final double volume = csv.getDouble("volume");

			NumberFormat df = DecimalFormat.getInstance();
			df.setGroupingUsed(false);
			
            StringBuilder line = new StringBuilder();
            line.append(NumericDateUtil.date2Long(date));
            line.append(outputFormat.getSeparator());
            line.append(NumericDateUtil.time2Int(date));
            line.append(outputFormat.getSeparator());
            line.append(outputFormat.format(open,percision));
            line.append(outputFormat.getSeparator());
            line.append(outputFormat.format(high, percision));
            line.append(outputFormat.getSeparator());
            line.append(outputFormat.format(low, percision));
            line.append(outputFormat.getSeparator());
            line.append(outputFormat.format(close, percision));
            line.append(outputFormat.getSeparator());
            line.append(df.format(volume));
            line.append(outputFormat.getSeparator());
            line.append(outputFormat.format(adjClose, percision));
            tw.println(line.toString());
        }

        tw.close();
    	}
    	catch(IOException ex)
    	{
			throw new LoaderError(ex);
    	}
    }


	/**
	 * @return the percision
	 */
	public int getPercision() {
		return percision;
	}


	/**
	 * @param percision the percision to set
	 */
	public void setPercision(int percision) {
		this.percision = percision;
	}

    
	
}
