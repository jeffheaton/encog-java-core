/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
