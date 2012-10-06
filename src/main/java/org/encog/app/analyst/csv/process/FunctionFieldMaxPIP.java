package org.encog.app.analyst.csv.process;

import java.util.List;

import org.encog.parse.expression.ExpressionHolder;
import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeFunction;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class FunctionFieldMaxPIP extends ExpressionTreeFunction {
	
	private ProcessExtension extension;

	public FunctionFieldMaxPIP(ProcessExtension theExtension, ExpressionHolder theOwner, List<ExpressionTreeElement> theArgs) {
		super(theOwner, "fieldmaxpip", theArgs);
		this.extension = theExtension;
	}

	@Override
	public ExpressionValue evaluate() {
		String fieldName = this.getArgs().get(0).evaluate().toStringValue();
		int startIndex = (int)this.getArgs().get(1).evaluate().toIntValue();
		int stopIndex = (int)this.getArgs().get(2).evaluate().toIntValue();
		int value = Integer.MIN_VALUE;
		
		String str = this.extension.getField(fieldName,this.extension.getBackwardWindowSize());
		double quoteNow = extension.getFormat().parse(str);
		
		for(int i=startIndex;i<=stopIndex;i++) {
			str = this.extension.getField(fieldName,this.extension.getBackwardWindowSize()+i);
			double d = extension.getFormat().parse(str)-quoteNow;
			d/=0.0001;
			d=Math.round(d);
			value = Math.max((int)d, value);
		}
		
		
		return new ExpressionValue(value);
	}

}
