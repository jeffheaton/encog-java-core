package org.encog.ml.prg.extension;

import org.encog.Encog;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.EvaluateExpr;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class StandardExtensions {
	
	/**
	 * Standard unary minus operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_VAR_SUPPORT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "#var";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			final EncogProgram owner = theOwner;
			return new ProgramNode(theOwner, theName, theArgs,1,0) {
				@Override
				public ExpressionValue evaluate() {
					int idx = this.getIntData()[0];
					ExpressionValue result = owner.getVariables().getVariable(idx);
					if( result==null ) {
						throw new ExpressionError("Variable has no value: " + owner.getVariables().getVariableName(idx));
					}
					return result;
				}
			};
		}
	};
	
	/**
	 * Standard unary minus operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_CONST_SUPPORT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "#const";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,1) {
				@Override
				public ExpressionValue evaluate() {
					return this.getExpressionData()[0];
				}
			};
		}
	};

	/**
	 * Standard unary minus operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_NEG = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "-";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(-this.getChildNodes().get(0)
							.evaluate().toFloatValue());
				}
			};
		}
	};

	/**
	 * Standard binary add operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_ADD = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "+";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.add(getChildNodes().get(0).evaluate(),
							getChildNodes().get(1).evaluate());
				}
			};
		}
	};

	/**
	 * Standard binary sub operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_SUB = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "-";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.sub(getChildNodes().get(0).evaluate(),
							getChildNodes().get(1).evaluate());
				}
			};
		}
	};

	/**
	 * Standard binary multiply operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_MUL = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "*";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.mul(getChildNodes().get(0).evaluate(),
							getChildNodes().get(1).evaluate());
				}
			};
		}
	};

	/**
	 * Standard binary multiply operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_DIV = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "/";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.div(getChildNodes().get(0).evaluate(),
							getChildNodes().get(1).evaluate());
				}
			};
		}
	};
	
	/**
	 * Standard binary power operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_POWER = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "^";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return EvaluateExpr.pow(getChildNodes().get(0).evaluate(), getChildNodes().get(1).evaluate());
				}
			};
		}
	};
	
	/**
	 * Standard boolean binary and operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_AND = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "&";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getChildNodes().get(0).evaluate().toBooleanValue() && getChildNodes().get(1).evaluate().toBooleanValue());
				}
			};
		}
	};
	
	/**
	 * Standard boolean binary or operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_OR = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "|";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getChildNodes().get(0).evaluate().toBooleanValue() || getChildNodes().get(1).evaluate().toBooleanValue());
				}
			};
		}
	};
	/**
	 * Standard boolean binary equal operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_EQUAL = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "=";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					double diff = Math.abs(getChildNodes().get(0).evaluate().toFloatValue() - getChildNodes().get(1).evaluate().toFloatValue());
					return new ExpressionValue( diff<Encog.DEFAULT_DOUBLE_EQUAL);
				}
			};
		}
	};
	/**
	 * Standard boolean binary greater than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_GREATER = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return ">";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getChildNodes().get(0).evaluate().toFloatValue() > getChildNodes().get(1).evaluate().toFloatValue());
				}
			};
		}
	};
	/**
	 * Standard boolean binary less than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_LESS = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "<";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getChildNodes().get(0).evaluate().toFloatValue() < getChildNodes().get(1).evaluate().toFloatValue());
				}
			};
		}
	};
	
	/**
	 * Standard numeric absolute value function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ABS = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "abs";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.abs(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};	
	
	/**
	 * Standard numeric acos function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ACOS = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "acos";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.acos(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};	
	
	/**
	 * Standard numeric asin function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ASIN = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "asin";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.asin(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric atan function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ATAN = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "atan";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.atan(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric atan2 function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ATAN2 = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "atan2";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.atan2(
							this.getChildNodes().get(0).evaluate().toFloatValue(),
							this.getChildNodes().get(1).evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric ceil function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CEIL = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "ceil";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.ceil(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric cos function.
	 */
	public static ProgramExtensionTemplate EXTENSION_COS = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "cos";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cos(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	

	/**
	 * Standard numeric cosh function.
	 */
	public static ProgramExtensionTemplate EXTENSION_COSH = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "cosh";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.cosh(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric exp function.
	 */
	public static ProgramExtensionTemplate EXTENSION_EXP = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "exp";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.exp(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric floor function.
	 */
	public static ProgramExtensionTemplate EXTENSION_FLOOR = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "floor";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.floor(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric log function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LOG = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "log";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.log(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric log10 function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LOG10 = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "log10";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.log10(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};

	/**
	 * Standard numeric max function.
	 */
	public static ProgramExtensionTemplate EXTENSION_MAX = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "max";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.max(
							this.getChildNodes().get(0).evaluate().toFloatValue(),
							this.getChildNodes().get(1).evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric max function.
	 */
	public static ProgramExtensionTemplate EXTENSION_MIN = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "min";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.min(
							this.getChildNodes().get(0).evaluate().toFloatValue(),
							this.getChildNodes().get(1).evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric pow function.
	 */
	public static ProgramExtensionTemplate EXTENSION_POW = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "pow";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.pow(
							this.getChildNodes().get(0).evaluate().toFloatValue(),
							this.getChildNodes().get(1).evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric random function.
	 */
	public static ProgramExtensionTemplate EXTENSION_RANDOM = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "rand";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.random());
				}
			};
		}
	};
	
	/**
	 * Standard numeric log10 function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ROUND = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "round";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.round(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric sin function.
	 */
	public static ProgramExtensionTemplate EXTENSION_SIN = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "sin";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sin(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric sinh function.
	 */
	public static ProgramExtensionTemplate EXTENSION_SINH = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "sin";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sinh(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric sqrt function.
	 */
	public static ProgramExtensionTemplate EXTENSION_SQRT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "sqrt";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.sqrt(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};

	/**
	 * Standard numeric tan function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TAN = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "tan";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.tanh(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	
	/**
	 * Standard numeric tanh function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TANH = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "tanh";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.tanh(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric toDegrees function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TODEG = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "toDegrees";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.toDegrees(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	/**
	 * Standard numeric toRadians function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TORAD = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "toRadians";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(Math.toRadians(this.getChildNodes().get(0)
							.evaluate().toFloatValue()));
				}
			};
		}
	};
	
	
	/**
	 * Standard string length function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LENGTH = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "length";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(this.getChildNodes().get(0).evaluate().toStringValue().length());
				}
			};
		}
	};
	
	/**
	 * Numeric formatting function.
	 */
	public static ProgramExtensionTemplate EXTENSION_FORMAT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "format";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue( this.getOwner().getContext().getFormat().format(
							this.getChildNodes().get(0).evaluate().toFloatValue(),
							(int)this.getChildNodes().get(1).evaluate().toFloatValue()) );

				}
			};
		}
	};
	
	/**
	 * String left function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LEFT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "left";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					String str = this.getChildNodes().get(0).evaluate().toStringValue();
					int idx = (int)this.getChildNodes().get(1).evaluate().toFloatValue();					
					String result = str.substring(0,idx);
					
					return new ExpressionValue( result );

				}
			};
		}
	};
	
	/**
	 * String right function.
	 */
	public static ProgramExtensionTemplate EXTENSION_RIGHT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "right";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					String str = this.getChildNodes().get(0).evaluate().toStringValue();
					int idx = (int)this.getChildNodes().get(1).evaluate().toFloatValue();					
					String result = str.substring(0,idx);
					
					return new ExpressionValue( result );

				}
			};
		}
	};
	
	/**
	 * Standard string cint function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CINT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "cint";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getChildNodes().get(0).evaluate().toIntValue() );

				}
			};
		}
	};

	/**
	 * Standard string cfloat function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CFLOAT = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "cfloat";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getChildNodes().get(0).evaluate().toFloatValue() );

				}
			};
		}
	};

	/**
	 * Standard string cstr function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CSTR = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "cstr";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getChildNodes().get(0).evaluate().toStringValue() );

				}
			};
		}
	};

	/**
	 * Standard string cbool function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CBOOL = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "cbool";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					;
					return new ExpressionValue( this.getChildNodes().get(0).evaluate().toBooleanValue() );

				}
			};
		}
	};

	/**
	 * Standard string iff function.
	 */
	public static ProgramExtensionTemplate EXTENSION_IFF = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "iff";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 3;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					boolean a = this.getChildNodes().get(0).evaluate().toBooleanValue();
					if( a ) {
						return this.getChildNodes().get(1).evaluate();	
					} else {
						return this.getChildNodes().get(2).evaluate();
					}
				}
			};	
		}
	};
	
	/**
	 * Standard string iff function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CLAMP = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "clamp";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 3;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					double value = this.getChildNodes().get(0).evaluate().toFloatValue();
					double min = this.getChildNodes().get(1).evaluate().toFloatValue();
					double max = this.getChildNodes().get(2).evaluate().toFloatValue();
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
	};

	
	/**
	 * Standard boolean binary greater than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_GREATER_EQUAL = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return ">=";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getChildNodes().get(0).evaluate().toFloatValue() >= getChildNodes().get(1).evaluate().toFloatValue());
				}
			};
		}
	};
	/**
	 * Standard boolean binary less than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_LESS_EQUAL = new BasicTemplate() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "<=";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getChildNodeCount() {
			return 2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProgramNode factorFunction(EncogProgram theOwner,
				String theName, ProgramNode[] theArgs) {
			return new ProgramNode(theOwner, theName, theArgs,0,0) {
				@Override
				public ExpressionValue evaluate() {
					return new ExpressionValue(getChildNodes().get(0).evaluate().toFloatValue() <= getChildNodes().get(1).evaluate().toFloatValue());
				}
			};
		}
	};
	
	
	public static void createNumericOperators(FunctionFactory factory) {
		factory.addExtension(EXTENSION_VAR_SUPPORT);
		factory.addExtension(EXTENSION_CONST_SUPPORT);
		factory.addExtension(EXTENSION_NEG);
		factory.addExtension(EXTENSION_ADD);
		factory.addExtension(EXTENSION_SUB);
		factory.addExtension(EXTENSION_MUL);
		factory.addExtension(EXTENSION_DIV);
		factory.addExtension(EXTENSION_POWER);
	}
	
	public static void createBooleanOperators(FunctionFactory factory) {
		factory.addExtension(EXTENSION_AND);
		factory.addExtension(EXTENSION_OR);
		factory.addExtension(EXTENSION_EQUAL);
		factory.addExtension(EXTENSION_LESS);
		factory.addExtension(EXTENSION_GREATER);
		factory.addExtension(EXTENSION_LESS_EQUAL);
		factory.addExtension(EXTENSION_GREATER_EQUAL);
		factory.addExtension(EXTENSION_IFF);
	}
	
	public static void createTrigFunctions(FunctionFactory factory) {
		factory.addExtension(EXTENSION_ACOS);
		factory.addExtension(EXTENSION_ASIN);
		factory.addExtension(EXTENSION_ATAN);
		factory.addExtension(EXTENSION_ATAN2);
		factory.addExtension(EXTENSION_COS);
		factory.addExtension(EXTENSION_COSH);
		factory.addExtension(EXTENSION_SIN);
		factory.addExtension(EXTENSION_SINH);
		factory.addExtension(EXTENSION_TAN);
		factory.addExtension(EXTENSION_TANH);
	}
	
	public static void createBasicFunctions(FunctionFactory factory) {
		factory.addExtension(EXTENSION_ABS);
		factory.addExtension(EXTENSION_CEIL);
		factory.addExtension(EXTENSION_EXP);
		factory.addExtension(EXTENSION_FLOOR);
		factory.addExtension(EXTENSION_LOG);
		factory.addExtension(EXTENSION_LOG10);
		factory.addExtension(EXTENSION_MAX);
		factory.addExtension(EXTENSION_MIN);
		factory.addExtension(EXTENSION_POW);
		factory.addExtension(EXTENSION_RANDOM);
		factory.addExtension(EXTENSION_ROUND);
		factory.addExtension(EXTENSION_SQRT);
		factory.addExtension(EXTENSION_CLAMP);
	}
	
	public static void createConversionFunctions(FunctionFactory factory) {
		factory.addExtension(EXTENSION_CINT);
		factory.addExtension(EXTENSION_CFLOAT);
		factory.addExtension(EXTENSION_CSTR);
		factory.addExtension(EXTENSION_CBOOL);
	}
	
	public static void createStringFunctions(FunctionFactory factory) {
		factory.addExtension(EXTENSION_LENGTH);
		factory.addExtension(EXTENSION_FORMAT);
		factory.addExtension(EXTENSION_LEFT);
		factory.addExtension(EXTENSION_RIGHT);
	}
	
	public static void createAll(FunctionFactory factory) {
		createNumericOperators(factory);
		createBooleanOperators(factory);
		createTrigFunctions(factory);
		createBasicFunctions(factory);
		createConversionFunctions(factory);
		createStringFunctions(factory);
		
		factory.addExtension(EXTENSION_TODEG);
		factory.addExtension(EXTENSION_TORAD);
	}
}
