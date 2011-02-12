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
import org.encog.app.analyst.script.WriteScriptFile;
import org.encog.util.csv.CSVFormat;

public class EncogAnalyst {
	
	public final static String ACTION_ANALYZE = "ANALYZE";
		
	private AnalystScript script = new AnalystScript();

	public void analyze(File file, boolean headers, CSVFormat format)
	{
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

	public static void main(String[] args)
	{
		EncogAnalyst a = new EncogAnalyst();
	
		a.load("d:\\data\\iris.txt");
		a.analyze(
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
