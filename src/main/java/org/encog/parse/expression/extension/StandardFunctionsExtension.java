package org.encog.parse.expression.extension;

import java.util.List;

import org.encog.parse.expression.ExpressionError;
import org.encog.parse.expression.ExpressionHolder;
import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeFunction;
import org.encog.parse.expression.expvalue.EvaluateExpr;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class StandardFunctionsExtension implements ExpressionExtension {

	private ExpressionHolder owner;
	
	@Override
	public ExpressionTreeFunction factorFunction(ExpressionHolder theOwner,
			String theName, List<ExpressionTreeElement> theArgs) {
		
		if (theName.equals("abs")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.abs(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("acos")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.acos(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("asin")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.asin(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("atan")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.atan(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("atan2")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.atan2(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("cbrt")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cbrt(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("ceil")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cbrt(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("cos")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cos(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("cosh")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cosh(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("exp")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.exp(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("floor")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.floor(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("log")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.log(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("log10")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.log10(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("max")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.max(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("min")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.min(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("pow")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.pow(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("random")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.random());
				}
			};
		}
		else if (theName.equals("round")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.round(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("sin")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sin(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("sinh")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sinh(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("sqrt")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sqrt(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("tan")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.tan(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("tanh")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.tanh(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("toDegrees")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.toDegrees(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("toRadians")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.toRadians(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("length")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(this.getArgs().get(0).evaluate().toStringValue().length());
				}
			};
		}
		else if (theName.equals("format")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue( this.getOwner().getFormat().format(
							this.getArgs().get(0).evaluate().toFloatValue(),
							(int)this.getArgs().get(1).evaluate().toFloatValue()) );

				}
			};
		}
		else if (theName.equals("left")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					String str = this.getArgs().get(0).evaluate().toStringValue();
					int idx = (int)this.getArgs().get(1).evaluate().toFloatValue();					
					String result = str.substring(0,idx);
					
					return new ExpressionValue( result );

				}
			};
		}
		else if (theName.equals("cint")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toIntValue() );

				}
			};
		}
		else if (theName.equals("cfloat")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toFloatValue() );

				}
			};
		}
		else if (theName.equals("cstr")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toStringValue() );

				}
			};
		}
		else if (theName.equals("cbool")) {
			return new ExpressionTreeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toBooleanValue() );

				}
			};
		}
		else {
			return null;
		}
	
	}

}
