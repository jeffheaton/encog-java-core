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
package org.encog.parse.expression.extension;

import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.NodeFunction;
import org.encog.parse.expression.ExpressionError;
import org.encog.parse.expression.expvalue.EvaluateExpr;
import org.encog.parse.expression.expvalue.ExpressionValue;

public class StandardFunctionsExtension implements ExpressionExtension {

	private EncogProgram owner;
	
	@Override
	public NodeFunction factorFunction(EncogProgram theOwner,
			String theName, List<ProgramNode> theArgs) {
		
		if (theName.equals("abs")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.abs(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("acos")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.acos(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("asin")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.asin(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("atan")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.atan(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("atan2")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.atan2(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("cbrt")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cbrt(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("ceil")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cbrt(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		} 
		else if (theName.equals("cos")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cos(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("cosh")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cosh(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("exp")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.exp(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("floor")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.floor(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("log")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.log(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("log10")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.log10(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("max")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.max(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("min")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.min(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("pow")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.pow(
							this.getArgs().get(0).evaluate().toFloatValue(),
							this.getArgs().get(1).evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("random")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.random());
				}
			};
		}
		else if (theName.equals("round")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.round(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("sin")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sin(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("sinh")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sinh(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("sqrt")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sqrt(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("tan")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.tan(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("tanh")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.tanh(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("toDegrees")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.toDegrees(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("toRadians")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.toRadians(this.getArgs().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
		else if (theName.equals("length")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(this.getArgs().get(0).evaluate().toStringValue().length());
				}
			};
		}
		else if (theName.equals("format")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue( this.getOwner().getFormat().format(
							this.getArgs().get(0).evaluate().toFloatValue(),
							(int)this.getArgs().get(1).evaluate().toFloatValue()) );

				}
			};
		}
		else if (theName.equals("left")) {
			return new NodeFunction(theOwner, theName, theArgs) {
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
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toIntValue() );

				}
			};
		}
		else if (theName.equals("cfloat")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toFloatValue() );

				}
			};
		}
		else if (theName.equals("cstr")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toStringValue() );

				}
			};
		}
		else if (theName.equals("cbool")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getArgs().get(0).evaluate().toBooleanValue() );

				}
			};
		}
		else if (theName.equals("iff")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					boolean a = this.getArgs().get(0).evaluate().toBooleanValue();
					if( a ) {
						return this.getArgs().get(1).evaluate();	
					} else {
						return this.getArgs().get(2).evaluate();
					}
					
					

				}
			};			
		}
		else if (theName.equals("clamp")) {
			return new NodeFunction(theOwner, theName, theArgs) {
				@Override
				public ExpressionValue evaluate() {
					double value = this.getArgs().get(0).evaluate().toFloatValue();
					double min = this.getArgs().get(1).evaluate().toFloatValue();
					double max = this.getArgs().get(2).evaluate().toFloatValue();
					if( value<min ) {
						return new ExpressionValue(min);
					} else if( value>max ) {
						return new ExpressionValue(max);
					} else {
						return new ExpressionValue(value);
					}
				}
			};			
		}
		else {
			return null;
		}
	
	}

}
