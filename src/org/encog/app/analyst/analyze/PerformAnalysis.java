package org.encog.app.analyst.analyze;

import java.util.List;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.quant.normalize.ClassItem;
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
		
		String str = script.getProperties().getPropertyString(ScriptProperties.SETUP_CONFIG_allowedClasses);
		if( str==null ) {
			str = "";
		}
		
		boolean allowInt = str.contains("int");
		boolean allowReal = str.contains("real") || str.contains("double");
		boolean allowString = str.contains("string");
		
		// remove any classes that did not qualify
		for(AnalyzedField field: this.fields) {
			if( field.isClass() ) {
				if( !allowInt && field.isInteger() ) {
					field.setClass(false);
				}
				
				if( !allowString && (!field.isInteger() && !field.isReal()) ) {
					field.setClass(false);
				}
				
				if( !allowReal && field.isReal() && !field.isInteger() ) {
					field.setClass(false);
				}
				
				if( field.isInteger() && field.getClassMembers().size()<=2 ) {
					field.setClass(false);
				}
			}
		}
		
		// merge with existing
		if( target.getScript().getFields()!=null &&
				fields.length==target.getScript().getFields().length )
		{
			for(int i=0;i<fields.length;i++) {
				// copy the old field name
				this.fields[i].setName(target.getScript().getFields()[i].getName());
				
				if( this.fields[i].isClass() ) {
					List<AnalystClassItem> t = this.fields[i].getClassMembers();
					List<AnalystClassItem> s = target.getScript().getFields()[i].getClassMembers();
					
					if( s.size()==t.size())
					{
						for(int j=0;j<s.size();j++)
						{
							if( t.get(j).getCode().equals(s.get(j).getCode()))
								t.get(j).setName(s.get(j).getName());
						}
					}
				}
			}
		}
		
		// now copy the fields
		DataField[] df = new DataField[fields.length];
		
		for(int i=0;i<df.length;i++) {
			df[i] = this.fields[i].finalizeField();
		}
		
		target.getScript().setFields(df);

	}

}
