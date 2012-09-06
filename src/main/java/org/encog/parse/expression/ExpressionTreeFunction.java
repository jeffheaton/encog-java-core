package org.encog.parse.expression;

import java.util.ArrayList;
import java.util.List;

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
	public double evaluate() {
		
		if( name.equals("abs") ) {
			return Math.abs(this.args.get(0).evaluate());
		}
		else if( name.equals("acos") ) {
			return Math.acos(this.args.get(0).evaluate());
		}
		else if( name.equals("asin") ) {
			return Math.asin(this.args.get(0).evaluate());
		}
		else if( name.equals("atan") ) {
			return Math.atan(this.args.get(0).evaluate());
		}
		else if( name.equals("atan2") ) {
			return Math.atan2(this.args.get(0).evaluate(),
					this.args.get(1).evaluate());
		}
		else if( name.equals("cbrt") ) {
			return Math.cbrt(this.args.get(0).evaluate());
		}
		else if( name.equals("ceil") ) {
			return Math.ceil(this.args.get(0).evaluate());
		}
		else if( name.equals("cos") ) {
			return Math.cos(this.args.get(0).evaluate());
		}
		else if( name.equals("cosh") ) {
			return Math.cosh(this.args.get(0).evaluate());
		}
		else if( name.equals("exp") ) {
			return Math.exp(this.args.get(0).evaluate());
		}
		else if( name.equals("floor") ) {
			return Math.floor(this.args.get(0).evaluate());
		} 
		else if( name.equals("log") ) {
			return Math.log(this.args.get(0).evaluate());
		}
		else if( name.equals("log10") ) {
			return Math.log10(this.args.get(0).evaluate());
		}
		else if( name.equals("max") ) {
			return Math.max(this.args.get(0).evaluate(),
					this.args.get(1).evaluate());
		}
		else if( name.equals("min") ) {
			return Math.min(this.args.get(0).evaluate(),
					this.args.get(1).evaluate());
		}
		else if( name.equals("pow") ) {
			return Math.pow(this.args.get(0).evaluate(),
					this.args.get(1).evaluate());
		}
		else if( name.equals("random") ) {
			return Math.random();
		}
		else if( name.equals("round") ) {
			return Math.round(this.args.get(0).evaluate());
		}
		else if( name.equals("sin") ) {
			return Math.sin(this.args.get(0).evaluate());
		}
		else if( name.equals("sinh") ) {
			return Math.sinh(this.args.get(0).evaluate());
		}
		else if( name.equals("sqrt") ) {
			return Math.sqrt(this.args.get(0).evaluate());
		}  
		else if( name.equals("tan") ) {
			return Math.tan(this.args.get(0).evaluate());
		}
		else if( name.equals("tanh") ) {
			return Math.tanh(this.args.get(0).evaluate());
		}
		else if( name.equals("toDegrees") ) {
			return Math.toDegrees(this.args.get(0).evaluate());
		}
		else if( name.equals("toRadians") ) {
			return Math.toRadians(this.args.get(0).evaluate());
		}
		else {
			throw new ExpressionError("Undefined function: " + this.name);
		}
	}

}
