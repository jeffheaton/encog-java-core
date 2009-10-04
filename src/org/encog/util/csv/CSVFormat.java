package org.encog.util.csv;

import java.text.NumberFormat;
import java.util.Locale;

public class CSVFormat {
	
	public static final CSVFormat DECIMAL_POINT = new CSVFormat('.',',');
	public static final CSVFormat DECIMAL_COMMA = new CSVFormat(',',';');
	public static final CSVFormat ENGLISH = DECIMAL_POINT;
	public static final CSVFormat NONENGLISH = DECIMAL_COMMA;
	public static final CSVFormat EG_FORMAT = DECIMAL_POINT;
	
	private final char decimal;
	private final char separator;
	private final NumberFormat numberFormatter;
	
	public CSVFormat(char decimal,char separator) {
		super();
		this.decimal = decimal;
		this.separator = separator;
		
		if( decimal=='.' )
		{
			this.numberFormatter = NumberFormat.getInstance(Locale.US);
		}
		else if( decimal==',' )
		{
			this.numberFormatter = NumberFormat.getInstance(Locale.FRANCE);
		}
		else
		{
			this.numberFormatter = NumberFormat.getInstance();
		}
	}

	public char getDecimal() {
		return decimal;
	}

	public char getSeparator() {
		return separator;
	}	
	
	public double parse(String str)
	{
		try
		{
			return this.numberFormatter.parse(str).doubleValue();
		}
		catch(Exception e)
		{
			throw new CSVError(e);
		}
	}
	
	public String format(double d, int digits)
	{
		this.numberFormatter.setMaximumFractionDigits(digits);
		return this.numberFormatter.format(d);
	}
	
	public NumberFormat getNumberFormatter() {
		return numberFormatter;
	}

	public static char getDecimalCharacter()
	{
		NumberFormat nf = NumberFormat.getInstance();
		String str = nf.format(0.5);
		
		// there is PROBABLY a better way to do this, but I could not find it.
		// Basically we want to know the decimal separator for the current
		// locale.  So we get the default number formatter and loop until we
		// find the fractional char for 0.5.  Which may be "0,5" in some areas.
		for(int i=0;i<str.length();i++)
		{
			char ch = str.charAt(i);
			if( !Character.isDigit(ch))
				return ch;
		}
		
		// for some reason, we failed to find it.  This should never happen.
		// But if it does, just return a decimal point.
		return '.';
	}
}
