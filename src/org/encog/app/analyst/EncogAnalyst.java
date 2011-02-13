package org.encog.app.analyst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.encog.app.analyst.analyze.AnalyzedField;
import org.encog.app.analyst.analyze.PerformAnalysis;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.EncogAnalystConfig;
import org.encog.app.analyst.script.WriteScriptFile;
import org.encog.app.analyst.script.normalize.NormalizedField;
import org.encog.app.quant.normalize.NormalizationDesired;
import org.encog.util.csv.CSVFormat;

public class EncogAnalyst {
	
	public final static String ACTION_ANALYZE = "ANALYZE";
		
	private AnalystScript script = new AnalystScript();

	public void analyze(File file, boolean headers, CSVFormat format)
	{
		script.getConfig().setFilename(EncogAnalystConfig.FILE_RAW,file.toString());
		PerformAnalysis a = new PerformAnalysis(script, file.toString(),headers,CSVFormat.ENGLISH);
		a.process(this);
		
	}
	
	public void clear()
	{
		
	}

	public void load(String filename)
	{
		load(new File(filename));
	}
	
	public void save(String filename)
	{
		save(new File(filename));
	}
	
	public void save(File file) {
		OutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			save(fos);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					throw new AnalystError(e);
				}
		}
	}

	public void load(File file) {
		InputStream fis = null;

		try {
			fis = new FileInputStream(file);
			load(fis);
		} catch (IOException ex) {
			throw new AnalystError(ex);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					throw new AnalystError(e);
				}
		}
	}

	public void save(OutputStream stream) {
		this.script.save(stream);
		
	}

	public void load(InputStream stream) {
		this.script.load(stream);
	}
	
	
	
	/**
	 * @return the script
	 */
	public AnalystScript getScript() {
		return script;
	}
	
	private void generateNormalizedFields() {
		NormalizedField[] norm = new NormalizedField[this.script.getFields().length];
		DataField[] dataFields = this.getScript().getFields();
		
		for(int i=0;i<this.script.getFields().length;i++)
		{
			DataField f = dataFields[i];
			NormalizationDesired action;
			
			if( f.isInteger() || f.isReal() && !f.isClass() ) {
				action = NormalizationDesired.Normalize;
				norm[i] = new NormalizedField(f.getName(),action,1,-1); 
			} else {				
				action = NormalizationDesired.PassThrough;
				norm[i] = new NormalizedField(f.getName(),action);
			}			
		}
		
		this.script.getNormalize().setNormalizedFields(norm);
		this.script.getNormalize().setSourceFile(EncogAnalystConfig.FILE_RAW);
		this.script.getNormalize().setTargetFile(EncogAnalystConfig.FILE_NORMALIZE);
	}
	
	public void wizard(File file, boolean b, CSVFormat english) {
		analyze(file, b, english);
		generateNormalizedFields();
	}

	public static void main(String[] args)
	{
		EncogAnalyst a = new EncogAnalyst();
	
		a.load("d:\\data\\iris.txt");
		a.wizard(
				new File("d:\\data\\iris_raw.csv"), 
				false, 
				CSVFormat.ENGLISH);
		a.save("d:\\data\\iris.txt");
		
/*
		a.analyze(
				new File("d:\\data\\forest.csv"), 
				false, 
				CSVFormat.ENGLISH);
		a.save("d:\\data\\forest.txt");*/
		
		System.out.println("Done");
	}

	

	

}
