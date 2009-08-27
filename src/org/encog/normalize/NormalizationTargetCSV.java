package org.encog.normalize;

import java.io.File;

public class NormalizationTargetCSV implements NormalizationTarget {
	
	private File output;
	
	public NormalizationTargetCSV(File output)
	{
		this.output = output;
	}
}
