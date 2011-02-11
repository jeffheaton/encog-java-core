package org.encog.app.analyst.analyze;

import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class PerformAnalysis {

	private String filename; 
	private boolean headers;
	private CSVFormat format;
	private AnalyzedField[] fields;
	
	public PerformAnalysis(String filename, boolean headers, CSVFormat format) {
		this.filename = filename;
		this.headers = headers;
		this.format = format;		
	}
	
	private void generateFieldsFromHeaders(ReadCSV csv)
	{
		this.fields = new AnalyzedField[csv.getColumnCount()];
		for(int i=0;i<this.fields.length;i++)
		{
			this.fields[i] = new AnalyzedField(csv.getColumnNames().get(i));
		}
	}
	
	private void generateFieldsFromCount(ReadCSV csv)
	{
		this.fields = new AnalyzedField[csv.getColumnCount()];
		for(int i=0;i<this.fields.length;i++)
		{
			this.fields[i] = new AnalyzedField("field:"+(i+1));
		}
	}
	
	private void generateFields(ReadCSV csv)
	{
		if( this.headers )
		{
			generateFieldsFromHeaders(csv);			
		}
		else
		{
			generateFieldsFromCount(csv);
		}
	}
	
	public void process()
	{
		ReadCSV csv = new ReadCSV(this.filename, this.headers, this.format);
				
		while(csv.next())
		{
			if( this.fields==null )
			{
				generateFields(csv);
			}
			
			for(int i=0;i<csv.getColumnCount();i++)
			{
				this.fields[i].analyze(csv.get(i));
			}
		}
		
		csv.close();
	}

}
