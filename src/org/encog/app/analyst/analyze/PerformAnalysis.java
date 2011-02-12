package org.encog.app.analyst.analyze;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class PerformAnalysis {

	private String filename; 
	private boolean headers;
	private CSVFormat format;
	private AnalyzedField[] fields;
	private AnalystScript script;
	
	public PerformAnalysis(AnalystScript script,String filename, boolean headers, CSVFormat format) {
		this.filename = filename;
		this.headers = headers;
		this.format = format;	
		this.script = script;
	}
	
	private void generateFieldsFromHeaders(ReadCSV csv)
	{
		this.fields = new AnalyzedField[csv.getColumnCount()];
		for(int i=0;i<this.fields.length;i++)
		{
			this.fields[i] = new AnalyzedField(this.script, csv.getColumnNames().get(i));
		}
	}
	
	private void generateFieldsFromCount(ReadCSV csv)
	{
		this.fields = new AnalyzedField[csv.getColumnCount()];
		for(int i=0;i<this.fields.length;i++)
		{
			this.fields[i] = new AnalyzedField(this.script, "field:"+(i+1));
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
	
	public void process(EncogAnalyst target)
	{
		ReadCSV csv = new ReadCSV(this.filename, this.headers, this.format);
				
		// pass one, calculate the min/max
		while(csv.next())
		{
			if( this.fields==null )
			{
				generateFields(csv);
			}
			
			for(int i=0;i<csv.getColumnCount();i++)
			{
				this.fields[i].analyze1(csv.get(i));
			}
		}
		
		for(AnalyzedField field: this.fields)
		{
			field.completePass1();
		}
		
		csv.close();
	
		// pass two, standard deviation
		csv = new ReadCSV(this.filename, this.headers, this.format);
		while(csv.next())
		{
			for(int i=0;i<csv.getColumnCount();i++)
			{
				this.fields[i].analyze2(csv.get(i));
			}
		}
		
		for(AnalyzedField field: this.fields)
		{
			field.completePass2();
		}

		
		csv.close();
		
		target.getScript().setFields(this.fields);
	}

}
