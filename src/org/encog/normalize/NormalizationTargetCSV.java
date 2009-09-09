package org.encog.normalize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.util.ReadCSV;

public class NormalizationTargetCSV implements NormalizationTarget {
	
	private File outputFile;
	private PrintWriter output;
	
	public NormalizationTargetCSV(File file)
	{

			this.outputFile = file;

		
	}

	public void write(double[] data, int inputCount) {
		StringBuilder result = new StringBuilder();
		ReadCSV.toCommas(result, data);
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
