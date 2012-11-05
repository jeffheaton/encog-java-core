package org.encog.ml.prg;

import org.encog.ml.prg.expvalue.ExpressionValue;

public class KnownConstNode extends ProgramNode {
	
	private ExpressionValue constValue;
		
	public KnownConstNode(EncogProgram theOwner, String theName, ExpressionValue theValue) {		
		super(theOwner, theName, new ProgramNode[] {}, 0, 0);
		this.constValue = theValue;
	}

	@Override
	public ExpressionValue evaluate() {
		return this.constValue;
	}

}
