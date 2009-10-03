package org.encog.util.csv;

public class CSVFormat {
	
	public static final CSVFormat SIMPLE_DECIMAL_POINT = new CSVFormat('.',(char)0,',',(char)0);
	public static final CSVFormat STRING_DECIMAL_POINT = new CSVFormat('.',(char)0,',','\"');
	public static final CSVFormat SIMPLE_DECIMAL_COMMA = new CSVFormat(',',(char)0,';',(char)0);
	public static final CSVFormat STRING_DECIMAL_COMMA = new CSVFormat(',',(char)0,';','\"');
	public static final CSVFormat ENGLISH = SIMPLE_DECIMAL_POINT;
	
	private final char decimal;
	private final char thousands;
	private final char separator;
	private final char stringDelimiter;
	
	public CSVFormat(char decimal, char thousands, char separator,char stringDelimiter) {
		super();
		this.decimal = decimal;
		this.thousands = thousands;
		this.separator = separator;
		this.stringDelimiter = stringDelimiter;
	}

	public char getDecimal() {
		return decimal;
	}

	public char getThousands() {
		return thousands;
	}

	public char getSeparator() {
		return separator;
	}

	public char getStringDelimiter() {
		return stringDelimiter;
	}
	
	
	
}
