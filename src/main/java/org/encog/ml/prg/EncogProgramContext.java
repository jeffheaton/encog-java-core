package org.encog.ml.prg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.epl.EPLHolderFactory;
import org.encog.ml.prg.epl.bytearray.ByteArrayHolderFactory;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.GeneticTrainingParams;
import org.encog.util.csv.CSVFormat;

public class EncogProgramContext implements Serializable {

	private final CSVFormat format;
	private final FunctionFactory functions;
	private final List<String> definedVariables = new ArrayList<String>();
	private EPLHolderFactory holderFactory = new ByteArrayHolderFactory();
	private GeneticTrainingParams params = new GeneticTrainingParams();

	// private EPLHolderFactory holderFactory = new BufferedHolderFactory();

	public EncogProgramContext() {
		this(CSVFormat.EG_FORMAT, new FunctionFactory());
	}

	public EncogProgramContext(final CSVFormat format) {
		this(format, new FunctionFactory());
	}

	public EncogProgramContext(final CSVFormat theFormat,
			final FunctionFactory theFunctions) {
		this.format = theFormat;
		this.functions = theFunctions;
	}

	public EncogProgram cloneProgram(final EncogProgram prg) {
		return new EncogProgram(prg);
	}

	public EncogProgram createProgram(final String str) {
		final EncogProgram result = new EncogProgram(this);
		result.compileExpression(str);
		return result;
	}

	public void defineVariable(final String v) {
		if (!this.definedVariables.contains(v)) {
			this.definedVariables.add(v);
		}
	}

	public List<String> getDefinedVariables() {
		return this.definedVariables;
	}

	public CSVFormat getFormat() {
		return this.format;
	}

	public FunctionFactory getFunctions() {
		return this.functions;
	}

	/**
	 * @return the holderFactory
	 */
	public EPLHolderFactory getHolderFactory() {
		return this.holderFactory;
	}

	/**
	 * @return the params
	 */
	public GeneticTrainingParams getParams() {
		return this.params;
	}

	public void loadAllFunctions() {
		StandardExtensions.createAll(getFunctions());
	}

	/**
	 * @param holderFactory
	 *            the holderFactory to set
	 */
	public void setHolderFactory(final EPLHolderFactory holderFactory) {
		this.holderFactory = holderFactory;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(final GeneticTrainingParams params) {
		this.params = params;
	}

}
