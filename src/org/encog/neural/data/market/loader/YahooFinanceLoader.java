package org.encog.neural.data.market.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.encog.bot.html.FormUtility;
import org.encog.neural.data.market.MarketDataType;
import org.encog.neural.data.market.TickerSymbol;
import org.encog.util.ReadCSV;

public class YahooFinanceLoader implements MarketLoader {

	public Collection<LoadedMarketData> load(TickerSymbol ticker,
			Set<MarketDataType> dataNeeded, Date from, Date to) {
		try
		{
			Collection<LoadedMarketData> result  = new ArrayList<LoadedMarketData>();
			URL url = buildURL(ticker,from,to);
			InputStream is = url.openStream();
			ReadCSV csv = new ReadCSV(is,true,',');
			
			while(csv.next())
			{
				final Date date = csv.getDate("date");
				final double adjClose = csv.getDouble("adj close");
				final double open = csv.getDouble("open");
				final double close = csv.getDouble("close");
				final double high = csv.getDouble("high");
				final double low = csv.getDouble("low");
				final double volume = csv.getDouble("volume");
				
				LoadedMarketData data = new LoadedMarketData(date, ticker);
				data.setData(MarketDataType.ADJUSTED_CLOSE, adjClose);
				data.setData(MarketDataType.OPEN, open);
				data.setData(MarketDataType.CLOSE, close);
				data.setData(MarketDataType.HIGH, high);
				data.setData(MarketDataType.LOW, low);
				data.setData(MarketDataType.OPEN, open);
				data.setData(MarketDataType.VOLUME, volume);
				result.add(data);
			} 
			
			csv.close();
			is.close();
			return result;
		}
		catch(IOException e)
		{
			throw new LoaderError(e);
		} catch (ParseException e) {
			throw new LoaderError(e);
		}
	}
	
	private URL buildURL(TickerSymbol ticker,Date from,Date to) throws IOException
	{
		// process the dates
		Calendar calendarFrom = Calendar.getInstance();
		calendarFrom.setTime(from);
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(to);
		
		// construct the URL
		OutputStream os = new ByteArrayOutputStream();
		FormUtility form = new FormUtility(os,null);
		form.add("s",ticker.getSymbol().toUpperCase());
		form.add("a", ""+calendarFrom.get(Calendar.MONTH));
		form.add("b", ""+calendarFrom.get(Calendar.DAY_OF_MONTH));
		form.add("c", ""+calendarFrom.get(Calendar.YEAR));
		form.add("d", ""+calendarTo.get(Calendar.MONTH));
		form.add("e", ""+calendarTo.get(Calendar.DAY_OF_MONTH));
		form.add("f", ""+calendarTo.get(Calendar.YEAR));
		form.add("g", "d");
		form.add("ignore", ".csv");
		os.close();
		String str = "http://ichart.finance.yahoo.com/table.csv?"+os.toString();		
		return new URL(str);
	}
}
