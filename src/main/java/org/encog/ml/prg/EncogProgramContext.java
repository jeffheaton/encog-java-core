package org.encog.ml.prg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.util.csv.CSVFormat;

public class EncogProgramContext {

	private final CSVFormat format;
	private final FunctionFactory functions;
	private final List<String> definedVariables = new ArrayList<String>();
	private final Map<String,Double> config = new HashMap<String,Double>();
	private double constMin = -10;
	private double constMax = 10;
	
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
	
	public void defineVariable(String v) {
		if( this.definedVariables.contains(v) ) {
			throw new ExpressionError("Variable " + v + " already defined.");
		}
		definedVariables.add(v);
		
	}

	public List<String> getDefinedVariables() {
		return this.definedVariables;
	}

	public Map<String, Double> getConfig() {
		return config;
	}

	public double getConstMin() {
		return constMin;
	}

	public void setConstMin(double constMin) {
		this.constMin = constMin;
	}

	public double getConstMax() {
		return constMax;
	}

	public void setConstMax(double constMax) {
		this.constMax = constMax;
	}
	
	
	
	
}
