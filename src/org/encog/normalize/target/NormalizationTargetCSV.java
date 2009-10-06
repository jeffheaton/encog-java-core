package org.encog.normalize.target;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.normalize.NormalizationError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.CommaList;

public class NormalizationTargetCSV implements NormalizationTarget {
	
	private File outputFile;
	private PrintWriter output;
	private CSVFormat format;
	
	public NormalizationTargetCSV(CSVFormat format, File file)
	{
		this.format = format;
		this.outputFile = file;
	}
	
	public NormalizationTargetCSV(File file)
	{
		this.format = CSVFormat.ENGLISH;
		this.outputFile = file;
	}

	public void write(double[] data, int inputCount) {
		StringBuilder result = new StringBuilder();
		CommaList.toCommas(format, result, data);
		this.output.println(result.toString());		
	}

	public void close() {
		this.output.close();		
	}

	public void open() {
		try
		{
		FileWriter outFile = new FileWriter(this.outputFile);
		output = new PrintWriter(outFile);
		}
		catch(IOException e)
		{
			throw( new NormalizationError(e));
		}
	}
	
	
}
