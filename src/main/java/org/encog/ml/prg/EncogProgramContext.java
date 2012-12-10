package org.encog.ml.prg;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.epl.EPLHolderFactory;
import org.encog.ml.prg.epl.bytearray.ByteArrayHolderFactory;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.GeneticTrainingParams;
import org.encog.util.csv.CSVFormat;

public class EncogProgramContext {

	private final CSVFormat format;
	private final FunctionFactory functions;
	private final List<String> definedVariables = new ArrayList<String>();
	private EPLHolderFactory holderFactory = new ByteArrayHolderFactory();
	private GeneticTrainingParams params = new GeneticTrainingParams();
	//private EPLHolderFactory holderFactory = new BufferedHolderFactory();
	
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
	
	/**
	 * @return the params
	 */
	public GeneticTrainingParams getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(GeneticTrainingParams params) {
		this.params = params;
	}

	public void loadAllFunctions() {
		StandardExtensions.createAll(getFunctions());
	}

	public EncogProgram createProgram(String str) {
		EncogProgram result = new EncogProgram(this);
		result.compileExpression(str);
		return result;
	}

	public EncogProgram cloneProgram(EncogProgram prg) {
		return new EncogProgram(prg);
	}

	/**
	 * @return the holderFactory
	 */
	public EPLHolderFactory getHolderFactory() {
		return holderFactory;
	}

	/**
	 * @param holderFactory the holderFactory to set
	 */
	public void setHolderFactory(EPLHolderFactory holderFactory) {
		this.holderFactory = holderFactory;
	}
	
	
	
}
