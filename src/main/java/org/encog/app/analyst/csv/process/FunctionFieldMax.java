package org.encog.app.analyst.csv.process;

import java.util.List;

import org.encog.parse.expression.ExpressionHolder;
import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeFunction;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class FunctionFieldMax extends ExpressionTreeFunction {
	
	private ProcessExtension extension;

	public FunctionFieldMax(ProcessExtension theExtension, ExpressionHolder theOwner, List<ExpressionTreeElement> theArgs) {
		super(theOwner, "fieldmax", theArgs);
		this.extension = theExtension;
	}

	@Override
	public ExpressionValue evaluate() {
		String fieldName = this.getArgs().get(0).evaluate().toStringValue();
		int startIndex = (int)this.getArgs().get(1).evaluate().toIntValue();
		int stopIndex = (int)this.getArgs().get(2).evaluate().toIntValue();
		double value = Double.NEGATIVE_INFINITY;
		
		for(int i=startIndex;i<=stopIndex;i++) {
			String str = this.extension.getField(fieldName,this.extension.getBackwardWindowSize()+i);
			double d = extension.getFormat().parse(str);
			value = Math.max(d, value);
		}
		
		
		return new ExpressionValue(value);
	}

}
