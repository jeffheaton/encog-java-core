package org.encog.ml.prg;

import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.util.csv.CSVFormat;

public class EncogProgramContext {
	
	private final CSVFormat format;
	private final FunctionFactory functions;
	
	public EncogProgramContext(CSVFormat theFormat, FunctionFactory theFunctions) {
		this.format = theFormat;
		this.functions = theFunctions;
	}
	
	public EncogProgramContext(CSVFormat format) {
		this(format, new FunctionFactory());
	}
	
	public EncogProgramContext() {
		this(CSVFormat.EG_FORMAT, new FunctionFactory());
	}

	public CSVFormat getFormat() {
		return format;
	}

	public FunctionFactory getFunctions() {
		return functions;
	}	
}
