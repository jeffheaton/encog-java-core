package org.encog.util.csv;

public class ParseNumberList {

	private enum State
	{
		predelimiter,
		prenumber,
		integerpart,
		fractionpart,
		postnumber,
		postdelimiter
	}
	
	private final CSVFormat format;
	private final String line;
	private int current;
	private final StringBuilder integerPart = new StringBuilder();
	private final StringBuilder fractionPart = new StringBuilder();
	private State state;
	private boolean hadDelimiter;
	
	public ParseNumberList(CSVFormat format, String line)
	{
		this.format = format;
		this.line = line;
		this.current = 0;
	}
	
	
	
	public CSVFormat getFormat() {
		return format;
	}



	public String getLine() {
		return line;
	}

	public boolean moreDoubles()
	{
		return(current<line.length());
	}
	
	public void processDigit(char ch)
	{
		switch(this.state)
		{
			case predelimiter:
			case prenumber:
				this.state = State.integerpart;
				integerPart.setLength(0);
				integerPart.append(ch);
				break;
			case integerpart:
				integerPart.append(ch);
				break;
			case fractionpart:
				fractionPart.append(ch);
				break;
			case postnumber:
				throw new CSVError("Error looking for delimiter on: " + this.line);
		}
	}
	
	public void processWhitespace()
	{
		switch(this.state)
		{
		case prenumber:
		case postnumber:
			break;
		case integerpart:
			this.state = State.postnumber;
			break;
		case fractionpart:
			this.state = State.postnumber;
			break;
		}
	}
	
	private void processNegative()
	{
		if( this.state==State.prenumber || this.state==State.predelimiter)
		{
			this.state=State.integerpart;
			integerPart.setLength(0);
			this.integerPart.append('-');
		}
		else
		{
			throw new CSVError("Unexpected negative on:" + this.line);
		}
	}
	
	private void processThousands()
	{
		if( this.state!=State.integerpart)
			throw new CSVError("Invalid location of thousands separator on: " + this.line);
	}
	
	private void processDecimal() {
		if( this.state==State.integerpart || this.state==State.prenumber)
		{
			this.fractionPart.setLength(0);
			this.state=State.fractionpart;
		}
		else
		{
			throw new CSVError("Unexpected decimal on: " + line);
		}
		
	}
	
	private void processDelimiter() {
		
	}

	public double nextDouble()
	{
		if( !moreDoubles() )
			throw new CSVError("Attempting to parse beyond end of string.");
		
		this.state = State.predelimiter;
		this.fractionPart.setLength(0);
		this.integerPart.setLength(0);
		this.hadDelimiter = false;
		
		
		while( current < line.length() )
		{
			char ch = line.charAt(current++);
			
			if( Character.isDigit(ch) )
			{
				processDigit(ch);
			}
			else if( ch==this.format.getThousands() )
			{
				processThousands();
			}
			else if( ch=='-' )
			{
				processNegative();
			}
			else if( ch==format.getDecimal() )
			{
				processDecimal();
			}
			else if( ch==format.getStringDelimiter() )
			{
				processDelimiter();
			}
			else if( ch==format.getSeparator() )
			{
				break;
			}
			else if( Character.isWhitespace(ch))
			{
				processWhitespace();
			}
			else
			{
				throw new CSVError("Invalid character: "+ch);
			}
		}
		
		// now convert the integer and fraction parts into an actual number
		double integerNumber = 0;
		double fractionNumber = 0;
		
		if( integerPart.length()>0)
			integerNumber = Integer.parseInt(integerPart.toString());
		if( fractionPart.length()>0)
			fractionNumber = (double)(Integer.parseInt(fractionPart.toString()))/(10.0*fractionPart.length());
		return integerNumber+fractionNumber;
		
	}

}
