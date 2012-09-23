package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.List;

import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionTreeFunction extends ExpressionTreeElement {

	private final String name;
	private final List<ExpressionTreeElement> args = new ArrayList<ExpressionTreeElement>();
	private final ExpressionHolder owner;
	
	public ExpressionTreeFunction(ExpressionHolder theOwner, String theName, List<ExpressionTreeElement> theArgs) {
		this.owner = theOwner;
		this.name = theName;
		this.args.addAll(theArgs);
	}
	
	
	
	public String getName() {
		return name;
	}



	public List<ExpressionTreeElement> getArgs() {
		return args;
	}



	public ExpressionHolder getOwner() {
		return owner;
	}



	@Override
	public ExpressionValue evaluate() {
		
		if( name.equals("abs") ) {
			return new ExpressionValue(Math.abs(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("acos") ) {
			return new ExpressionValue(Math.acos(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("asin") ) {
			return new ExpressionValue(Math.asin(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("atan") ) {
			return new ExpressionValue(Math.atan(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("atan2") ) {
			return new ExpressionValue(Math.atan2(this.args.get(0).evaluate().toFloatValue(),
					this.args.get(1).evaluate().toFloatValue()));
		}
		else if( name.equals("cbrt") ) {
			return new ExpressionValue(Math.cbrt(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("ceil") ) {
			return new ExpressionValue(Math.ceil(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("cos") ) {
			return new ExpressionValue(Math.cos(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("cosh") ) {
			return new ExpressionValue(Math.cosh(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("exp") ) {
			return new ExpressionValue(Math.exp(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("floor") ) {
			return new ExpressionValue(Math.floor(this.args.get(0).evaluate().toFloatValue()));
		} 
		else if( name.equals("log") ) {
			return new ExpressionValue(Math.log(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("log10") ) {
			return new ExpressionValue(Math.log10(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("max") ) {
			return new ExpressionValue(Math.max(this.args.get(0).evaluate().toFloatValue(),
					this.args.get(1).evaluate().toFloatValue()));
		}
		else if( name.equals("min") ) {
			return new ExpressionValue(Math.min(this.args.get(0).evaluate().toFloatValue(),
					this.args.get(1).evaluate().toFloatValue()));
		}
		else if( name.equals("pow") ) {
			return new ExpressionValue(Math.pow(this.args.get(0).evaluate().toFloatValue(),
					this.args.get(1).evaluate().toFloatValue()));
		}
		else if( name.equals("random") ) {
			return new ExpressionValue(Math.random());
		}
		else if( name.equals("round") ) {
			return new ExpressionValue(Math.round(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("sin") ) {
			return new ExpressionValue(Math.sin(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("sinh") ) {
			return new ExpressionValue(Math.sinh(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("sqrt") ) {
			return new ExpressionValue(Math.sqrt(this.args.get(0).evaluate().toFloatValue()));
		}  
		else if( name.equals("tan") ) {
			return new ExpressionValue(Math.tan(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("tanh") ) {
			return new ExpressionValue(Math.tanh(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("toDegrees") ) {
			return new ExpressionValue(Math.toDegrees(this.args.get(0).evaluate().toFloatValue()));
		}
		else if( name.equals("toRadians") ) {
			return new ExpressionValue(Math.toRadians(this.args.get(0).evaluate().toFloatValue()));
		}
		else {
			throw new ExpressionError("Undefined function: " + this.name);
		}
	}

}
