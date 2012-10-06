package org.encog.parse.expression.expvalue;

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
