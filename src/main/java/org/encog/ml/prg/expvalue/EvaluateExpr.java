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
package org.encog.ml.prg.expvalue;


public class EvaluateExpr {
	private EvaluateExpr() {
		
	}
	
	public static ExpressionValue add(ExpressionValue a, ExpressionValue b) {
		if( a.isString() || b.isString() ) {
			return new ExpressionValue(a.toStringValue() + b.toStringValue());
		} if( a.isInt() && b.isInt() ) {
			return new ExpressionValue(a.toIntValue() + b.toIntValue());
		} else {
			return new ExpressionValue(a.toFloatValue() + b.toFloatValue());
		}
	}
	
	public static ExpressionValue sub(ExpressionValue a, ExpressionValue b) {
		if( a.isInt() && b.isInt() ) {
			return new ExpressionValue(a.toIntValue() - b.toIntValue());
		}
		else
		return new ExpressionValue(a.toFloatValue() - b.toFloatValue());
	}
	
	public static ExpressionValue mul(ExpressionValue a, ExpressionValue b) {
		if( a.isInt() && b.isInt() ) {
			return new ExpressionValue(a.toIntValue() * b.toIntValue());
		}
		return new ExpressionValue(a.toFloatValue() * b.toFloatValue());
	}
	
	public static ExpressionValue div(ExpressionValue a, ExpressionValue b) {
		if( a.isInt() && b.isInt() ) {
			return new ExpressionValue(a.toIntValue() / b.toIntValue());
		}
		return new ExpressionValue(a.toFloatValue() / b.toFloatValue());
	}
	
	public static ExpressionValue pow(ExpressionValue a, ExpressionValue b) {
		if( a.isInt() && b.isInt() ) {
			return new ExpressionValue(Math.pow(a.toIntValue(),b.toIntValue()));
		}
		return new ExpressionValue(Math.pow(a.toFloatValue(), b.toFloatValue()));
	}	
}
