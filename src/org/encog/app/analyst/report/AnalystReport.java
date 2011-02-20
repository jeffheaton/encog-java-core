package org.encog.app.analyst.report;

import java.io.File;
import java.io.IOException;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.DataField;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.app.quant.normalize.NormalizedField;
import org.encog.engine.util.Format;
import org.encog.util.HTMLReport;
import org.encog.util.file.FileUtil;

public class AnalystReport {

	private EncogAnalyst analyst;

	public AnalystReport(EncogAnalyst analyst) {
		this.analyst = analyst;
	}

	public String produceReport() {
		HTMLReport report = new HTMLReport();

		report.beginHTML();
		report.title("Encog Analyst Report");
		report.beginBody();

		report.h1("Field Ranges");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Class?");
		report.header("Complete?");
		report.header("Int?");
		report.header("Real?");
		report.header("Max");
		report.header("Min");
		report.header("Mean");
		report.header("Standard Deviation");
		report.endRow();
		
		for( DataField df: this.analyst.getScript().getFields() ) {
			report.beginRow();
			report.cell(df.getName());
			report.cell(df.isClass()?"Yes":"No");
			report.cell(df.isComplete()?"Yes":"No");
			report.cell(df.isInteger()?"Yes":"No");
			report.cell(df.isReal()?"Yes":"No");
			report.cell(Format.formatDouble(df.getMax(),5));
			report.cell(Format.formatDouble(df.getMin(),5));
			report.cell(Format.formatDouble(df.getMean(),5));
			report.cell(Format.formatDouble(df.getStandardDeviation(),5));
			report.endRow();
			
			for( AnalystClassItem item : df.getClassMembers() ) {
				report.beginRow();
				report.cell("&nbsp;");
				report.cell(item.getName() + " => " + item.getCode(),8);
				report.endRow();
			}
			
		}
		
		report.endTable();
		
		
		report.h1("Normalization");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Action");
		report.header("High");
		report.header("Low");
		report.endRow();
		
		for(NormalizedField item : this.analyst.getScript().getNormalize().getNormalizedFields() )
		{
			report.beginRow();
			report.cell(item.getName());
			report.cell(item.getAction().toString());
			report.cell(Format.formatDouble(item.getNormalizedHigh(), 5));
			report.cell(Format.formatDouble(item.getNormalizedLow(), 5));
			report.endRow();
		}
		
		report.endTable();
		
		report.h1("Machine Learning");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Value");
		report.endRow();
		
		report.tablePair("Type",this.analyst.getScript().getMachineLearning().getMLType());
		report.tablePair("Architecture",this.analyst.getScript().getMachineLearning().getMLArchitecture());
		report.tablePair("Resource File",this.analyst.getScript().getMachineLearning().getResourceFile());
		report.tablePair("Resource Name",this.analyst.getScript().getMachineLearning().getResourceName());
		report.endTable();
		
		
		report.h1("Files");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Filename");
		report.endRow();
		for( String key : this.analyst.getScript().getConfig().getFilenames().keySet() )
		{
			String value = this.analyst.getScript().getConfig().getFilenames().get(key);
			report.beginRow();
			report.cell(key);
			report.cell(value);
			report.endRow();
		}
		report.endTable();
		
		report.endBody();
		report.endHTML();

		return report.toString();
	}
	
	

	public void produceReport(File filename) {
		try {
			String str = produceReport();
			FileUtil.writeFileAsString(filename, str);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		}
	}
}
