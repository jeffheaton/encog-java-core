package org.encog.app.analyst.script;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.encog.Encog;
import org.encog.util.csv.CSVFormat;

public class WriteScriptFile {
	
	public final static char QUOTE = '\"';
	public final static char COMMA = ',';
	
	private PrintWriter out;
	private StringBuilder line = new StringBuilder();
	private String currentSection;

	public WriteScriptFile(OutputStream stream)
	{
		this.out = new PrintWriter(stream);		
	}
	
	public void writeLine()
	{
		this.out.println(line.toString());
		line.setLength(0);
	}
	
	public void addColumn(String str)
	{
		if( line.length()>0 )
		{
			line.append(COMMA);
		}
		
		line.append(QUOTE);
		line.append(str);
		line.append(QUOTE);
	}
	
	public void addColumn(boolean b)
	{
		if( line.length()>0 )
		{
			line.append(COMMA);
		}
		
		line.append(b?1:0);
	}
	
	public void addColumn(int i)
	{
		if( line.length()>0 )
		{
			line.append(COMMA);
		}
		
		line.append(i);
	}
	
	public void addColumn(double d)
	{
		if( line.length()>0 )
		{
			line.append(COMMA);
		}
		
		line.append(CSVFormat.ENGLISH.format(d, Encog.DEFAULT_PRECISION));
	}

	public void flush()
	{
		out.flush();
	}

	public void addColumns(List<String> cols) {
		for(String str: cols) {
			addColumn(str);
		}
		
	}

	public void addSection(String str) {
		this.currentSection = str;
		out.println("**"+str);		
	}
	
	public void addSubSection(String str) {
		out.println("**"+this.currentSection+":"+str);		
	}

	public void writeProperty(String name, int value) {
		out.println(name+"="+value);		
	}

	public void writeProperty(String name, String value) {
		out.println(name+"="+value);	
		
	}
	
	
	
}
