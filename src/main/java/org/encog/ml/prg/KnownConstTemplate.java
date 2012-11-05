package org.encog.ml.prg;

import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.BasicTemplate;
import org.encog.ml.prg.extension.FunctionFactory;

public class KnownConstTemplate extends BasicTemplate {
	
	public static final KnownConstTemplate CONST_PI = new KnownConstTemplate("PI", Math.PI);
	public static final KnownConstTemplate CONST_E = new KnownConstTemplate("E", Math.E);
	
	public static void createAllConst(FunctionFactory factory) {
		factory.addExtension(CONST_PI);
		factory.addExtension(CONST_E);
	}
	
	private String name;
	private ExpressionValue value;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getChildNodeCount() {
		return 0;
	}

	@Override
	public ProgramNode factorFunction(EncogProgram theOwner, String theName,
			ProgramNode[] theArgs) {
		return new KnownConstNode(theOwner, theName, this.value);
	}
	
	public KnownConstTemplate(String theName, double theValue) {
		this.name = theName;
		this.value = new ExpressionValue(theValue);
	}

}
