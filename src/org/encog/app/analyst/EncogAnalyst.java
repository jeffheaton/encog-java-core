package org.encog.app.analyst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.encog.app.analyst.analyze.PerformAnalysis;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class EncogAnalyst {
	
	public final static String ACTION_ANALYZE = "ANALYZE";

	public void analyze(File file, boolean headers, CSVFormat format)
	{
		PerformAnalysis a = new PerformAnalysis(file.toString(),true,CSVFormat.ENGLISH);
		a.process();
		
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

	}

	public void load(InputStream stream) {

	}

}
