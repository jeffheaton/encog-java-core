/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ml.prg.extension;

import java.util.List;
import java.util.Random;

import org.encog.EncogError;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.VariableMapping;
import org.encog.ml.prg.expvalue.EvaluateExpr;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.expvalue.ValueType;

/**
 * This class defines the standard opcodes for Encog programs.
 * 
 * The operator precedence is based on the following article.
 * 
 * http://en.wikipedia.org/wiki/Operators_in_C_and_C%2B%2B#Operator_precedence
 * 
 */
public class StandardExtensions {

	/**
	 * Variable support.
	 */
	public static ProgramExtensionTemplate EXTENSION_VAR_SUPPORT = new BasicTemplate(
			ProgramExtensionTemplate.NO_PREC, "#var():{*}", NodeType.Leaf,
			true, 1) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			final int idx = (int) actual.getData()[0].toIntValue();
			final ExpressionValue result = actual.getOwner().getVariables()
					.getVariable(idx);
			if (result == null) {
				throw new EARuntimeError("Variable has no value: "
						+ actual.getOwner().getVariables().getVariableName(idx));
			}
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isPossibleReturnType(final EncogProgramContext context,
				final ValueType rtn) {
			if (!super.isPossibleReturnType(context, rtn)) {
				return false;
			}
			for (final VariableMapping mapping : context.getDefinedVariables()) {
				if (mapping.getVariableType() == rtn) {
					return true;
				}
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void randomize(final Random rnd,
				final List<ValueType> desiredTypes, final ProgramNode actual,
				final double minValue, final double maxValue) {

			final int variableIndex = actual.getOwner().selectRandomVariable(
					rnd, desiredTypes);
			if (variableIndex == -1) {
				throw new EncogError("Can't find any variables of type "
						+ desiredTypes.toString() + " to generate.");
			}
			actual.getData()[0] = new ExpressionValue(variableIndex);
		}

	};

	/**
	 * Numeric const.
	 */
	public static ProgramExtensionTemplate EXTENSION_CONST_SUPPORT = new BasicTemplate(
			ProgramExtensionTemplate.NO_PREC, "#const():{*}", NodeType.Leaf,
			false, 1) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return actual.getData()[0];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void randomize(final Random rnd,
				final List<ValueType> desiredType, final ProgramNode actual,
				final double minValue, final double maxValue) {
			final ValueType pickedType = desiredType.get(rnd
					.nextInt(desiredType.size()));
			final EncogProgramContext context = actual.getOwner().getContext();
			switch (pickedType) {
			case floatingType:
				actual.getData()[0] = new ExpressionValue(
						RangeRandomizer.randomize(rnd, minValue, maxValue));
				break;
			case stringType:
				// this will be added later
				break;
			case booleanType:
				actual.getData()[0] = new ExpressionValue(rnd.nextBoolean());
				break;
			case intType:
				actual.getData()[0] = new ExpressionValue(
						(int) RangeRandomizer
								.randomize(rnd, minValue, maxValue));
				break;
			case enumType:
				final int enumType = rnd.nextInt(context.getMaxEnumType() + 1);
				final int enumCount = context.getEnumCount(enumType);
				final int enumIndex = rnd.nextInt(enumCount);
				actual.getData()[0] = new ExpressionValue(enumType, enumIndex);
				break;
			}
		}
	};

	/**
	 * Standard unary minus operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_NEG = new BasicTemplate(3,
			"-({f,i}):{f,i}", NodeType.Unary, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(-actual.getChildNode(0).evaluate()
					.toFloatValue());
		}
	};

	/**
	 * Standard binary add operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_ADD = new BasicTemplate(6,
			"+({f,i,s}{f,i,s}):{f,i,s}", NodeType.OperatorLeft, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.add(actual.getChildNode(0).evaluate(), actual
					.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard binary sub operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_SUB = new BasicTemplate(6,
			"-({f,i}{f,i}):{f,i}", NodeType.OperatorLeft, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.sub(actual.getChildNode(0).evaluate(), actual
					.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard binary multiply operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_MUL = new BasicTemplate(5,
			"*({f,i}{f,i}):{f,i}", NodeType.OperatorLeft, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.mul(actual.getChildNode(0).evaluate(), actual
					.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard binary div operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_DIV = new BasicTemplate(5,
			"/({f,i}{f,i}):{f,i}", NodeType.OperatorLeft, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.div(actual.getChildNode(0).evaluate(), actual
					.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard binary protected div operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_PDIV = new BasicTemplate(
			5, "%({f,i}{f,i}):{f,i}", NodeType.OperatorLeft, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.protectedDiv(actual.getChildNode(0).evaluate(),
					actual.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard binary power operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_POWER = new BasicTemplate(
			1, "^({f,i}{f,i}):{f,i}", NodeType.OperatorRight, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.pow(actual.getChildNode(0).evaluate(), actual
					.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard boolean binary and operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_AND = new BasicTemplate(
			10, "&({b}{b}):{b}", NodeType.OperatorLeft, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toBooleanValue()
					&& actual.getChildNode(1).evaluate().toBooleanValue());
		}
	};

	/**
	 * Standard boolean binary and operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_NOT = new BasicTemplate(3,
			"!({b}):{b}", NodeType.Unary, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(!actual.getChildNode(0).evaluate()
					.toBooleanValue());
		}
	};

	/**
	 * Standard boolean binary or operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_OR = new BasicTemplate(12,
			"|({b}{b}):{b}", NodeType.OperatorLeft, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toBooleanValue()
					|| actual.getChildNode(1).evaluate().toBooleanValue());
		}
	};

	/**
	 * Standard boolean binary equal operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_EQUAL = new BasicTemplate(
			9, "=({*}{*}):{b}", NodeType.OperatorRight, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.equ(actual.getChildNode(0).evaluate(), actual
					.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard boolean not equal operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_NOT_EQUAL = new BasicTemplate(
			9, "<>({*}{*}):{b}", NodeType.OperatorRight, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return EvaluateExpr.notequ(actual.getChildNode(0).evaluate(),
					actual.getChildNode(1).evaluate());
		}
	};

	/**
	 * Standard boolean binary greater than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_GT = new BasicTemplate(8,
			">({i,f}{i,f}):{b}", NodeType.OperatorRight, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toFloatValue() > actual.getChildNode(1).evaluate()
					.toFloatValue());
		}
	};
	/**
	 * Standard boolean binary less than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_LT = new BasicTemplate(8,
			"<({i,f}{i,f}):{b}", NodeType.OperatorRight, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toFloatValue() < actual.getChildNode(1).evaluate()
					.toFloatValue());
		}
	};

	/**
	 * Standard boolean binary greater than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_GTE = new BasicTemplate(8,
			">=({i,f}{i,f}):{b}", NodeType.OperatorRight, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toFloatValue() >= actual.getChildNode(1).evaluate()
					.toFloatValue());
		}
	};

	/**
	 * Standard boolean binary less than operator.
	 */
	public static ProgramExtensionTemplate EXTENSION_LTE = new BasicTemplate(8,
			"<=({i,f}{i,f}):{b}", NodeType.OperatorRight, false, 0) {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toFloatValue() <= actual.getChildNode(1).evaluate()
					.toFloatValue());
		}
	};

	/**
	 * Standard numeric absolute value function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ABS = new BasicTemplate(
			"abs({f,i}):{f,i}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.abs(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric acos function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ACOS = new BasicTemplate(
			"acos({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.abs(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric asin function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ASIN = new BasicTemplate(
			"asin({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.asin(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric atan function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ATAN = new BasicTemplate(
			"atan({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.atan(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric atan2 function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ATAN2 = new BasicTemplate(
			"atan2({f}{f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.atan2(actual.getChildNode(0)
					.evaluate().toFloatValue(), actual.getChildNode(1)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric ceil function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CEIL = new BasicTemplate(
			"ceil({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.ceil(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric cos function.
	 */
	public static ProgramExtensionTemplate EXTENSION_COS = new BasicTemplate(
			"cos({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.cos(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric cosh function.
	 */
	public static ProgramExtensionTemplate EXTENSION_COSH = new BasicTemplate(
			"cosh({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.cosh(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric exp function.
	 */
	public static ProgramExtensionTemplate EXTENSION_EXP = new BasicTemplate(
			"exp({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.exp(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric floor function.
	 */
	public static ProgramExtensionTemplate EXTENSION_FLOOR = new BasicTemplate(
			"floor({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.floor(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric log function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LOG = new BasicTemplate(
			"log({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.log(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric log10 function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LOG10 = new BasicTemplate(
			"log10({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.log10(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric max function.
	 */
	public static ProgramExtensionTemplate EXTENSION_MAX = new BasicTemplate(
			"max({f,s,i}({f,s,i}):{f,s,i}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.max(actual.getChildNode(0)
					.evaluate().toFloatValue(), actual.getChildNode(1)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric max function.
	 */
	public static ProgramExtensionTemplate EXTENSION_MIN = new BasicTemplate(
			"min({f,s,i}({f,s,i}):{f,s,i}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.min(actual.getChildNode(0)
					.evaluate().toFloatValue(), actual.getChildNode(1)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric pow function.
	 */
	public static ProgramExtensionTemplate EXTENSION_POWFN = new BasicTemplate(
			"pow({f}{f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.pow(actual.getChildNode(0)
					.evaluate().toFloatValue(), actual.getChildNode(1)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric random function.
	 */
	public static ProgramExtensionTemplate EXTENSION_RANDOM = new BasicTemplate(
			"rand():{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.random());
		}
	};

	/**
	 * Standard numeric log10 function.
	 */
	public static ProgramExtensionTemplate EXTENSION_ROUND = new BasicTemplate(
			"round({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.round(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric sin function.
	 */
	public static ProgramExtensionTemplate EXTENSION_SIN = new BasicTemplate(
			"sin({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.sin(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric sinh function.
	 */
	public static ProgramExtensionTemplate EXTENSION_SINH = new BasicTemplate(
			"sinh({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.sinh(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric sqrt function.
	 */
	public static ProgramExtensionTemplate EXTENSION_SQRT = new BasicTemplate(
			"sqrt({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.sqrt(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric tan function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TAN = new BasicTemplate(
			"tan({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.tan(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric tanh function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TANH = new BasicTemplate(
			"tanh({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.tanh(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric toDegrees function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TODEG = new BasicTemplate(
			"todeg({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.toDegrees(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard numeric toRadians function.
	 */
	public static ProgramExtensionTemplate EXTENSION_TORAD = new BasicTemplate(
			"torad({f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(Math.toRadians(actual.getChildNode(0)
					.evaluate().toFloatValue()));
		}
	};

	/**
	 * Standard string length function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LENGTH = new BasicTemplate(
			"length({s}):{i}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toStringValue().length());
		}
	};

	/**
	 * Numeric formatting function.
	 */
	public static ProgramExtensionTemplate EXTENSION_FORMAT = new BasicTemplate(
			"format({f}{i}):{s}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual
					.getOwner()
					.getContext()
					.getFormat()
					.format(actual.getChildNode(0).evaluate().toFloatValue(),
							(int) actual.getChildNode(1).evaluate()
									.toFloatValue()));
		}
	};

	/**
	 * String left function.
	 */
	public static ProgramExtensionTemplate EXTENSION_LEFT = new BasicTemplate(
			"left({s}{i}):{s}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			final String str = actual.getChildNode(0).evaluate()
					.toStringValue();
			final int idx = (int) actual.getChildNode(1).evaluate()
					.toFloatValue();
			final String result = str.substring(0, idx);

			return new ExpressionValue(result);
		}

	};

	/**
	 * String right function.
	 */
	public static ProgramExtensionTemplate EXTENSION_RIGHT = new BasicTemplate(
			"right({s}{i}):{s}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			final String str = actual.getChildNode(0).evaluate()
					.toStringValue();
			final int idx = (int) actual.getChildNode(1).evaluate()
					.toFloatValue();
			final String result = str.substring(idx);

			return new ExpressionValue(result);
		}
	};

	/**
	 * Standard string cint function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CINT = new BasicTemplate(
			"cint({f}):{i}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toIntValue());
		}
	};

	/**
	 * Standard string cfloat function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CFLOAT = new BasicTemplate(
			"cfloat({i}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toFloatValue());
		}
	};

	/**
	 * Standard string cstr function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CSTR = new BasicTemplate(
			"cstr({*}):{s}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toStringValue());
		}
	};

	/**
	 * Standard string cbool function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CBOOL = new BasicTemplate(
			"cbool({i,f}):{b}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			return new ExpressionValue(actual.getChildNode(0).evaluate()
					.toBooleanValue());
		}
	};

	/**
	 * Standard string iff function.
	 */
	public static ProgramExtensionTemplate EXTENSION_IFF = new BasicTemplate(
			"iff({b}:{*}:{*}):{*}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			final boolean a = actual.getChildNode(0).evaluate()
					.toBooleanValue();
			if (a) {
				return actual.getChildNode(1).evaluate();
			} else {
				return actual.getChildNode(2).evaluate();
			}
		}
	};

	/**
	 * Standard string clamp function.
	 */
	public static ProgramExtensionTemplate EXTENSION_CLAMP = new BasicTemplate(
			"clamp({f}{f}{f}):{f}") {
		/**
		 * The serial id.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ExpressionValue evaluate(final ProgramNode actual) {
			final double value = actual.getChildNode(0).evaluate()
					.toFloatValue();
			final double min = actual.getChildNode(1).evaluate().toFloatValue();
			final double max = actual.getChildNode(2).evaluate().toFloatValue();
			if (value < min) {
				return new ExpressionValue(min);
			} else if (value > max) {
				return new ExpressionValue(max);
			} else {
				return new ExpressionValue(value);
			}
		}
	};

	/**
	 * Add all known opcodes to a context.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 */
	public static void createAll(final EncogProgramContext context) {
		final FunctionFactory factory = context.getFunctions();
		for (final ProgramExtensionTemplate temp : EncogOpcodeRegistry.INSTANCE
				.findAllOpcodes()) {
			factory.addExtension(temp);
		}
	}

	/**
	 * Add the opcodes for basic operations to a context.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 */
	public static void createBasicFunctions(final EncogProgramContext context) {
		final FunctionFactory factory = context.getFunctions();
		factory.addExtension(EXTENSION_ABS);
		factory.addExtension(EXTENSION_CEIL);
		factory.addExtension(EXTENSION_EXP);
		factory.addExtension(EXTENSION_FLOOR);
		factory.addExtension(EXTENSION_LOG);
		factory.addExtension(EXTENSION_LOG10);
		factory.addExtension(EXTENSION_MAX);
		factory.addExtension(EXTENSION_MIN);
		factory.addExtension(EXTENSION_POWFN);
		factory.addExtension(EXTENSION_RANDOM);
		factory.addExtension(EXTENSION_ROUND);
		factory.addExtension(EXTENSION_SQRT);
		factory.addExtension(EXTENSION_CLAMP);
	}

	/**
	 * Add the opcodes for boolean operations to a context.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 */
	public static void createBooleanOperators(final EncogProgramContext context) {
		final FunctionFactory factory = context.getFunctions();
		factory.addExtension(EXTENSION_AND);
		factory.addExtension(EXTENSION_OR);
		factory.addExtension(EXTENSION_EQUAL);
		factory.addExtension(EXTENSION_LT);
		factory.addExtension(EXTENSION_GT);
		factory.addExtension(EXTENSION_LTE);
		factory.addExtension(EXTENSION_GTE);
		factory.addExtension(EXTENSION_IFF);
		factory.addExtension(EXTENSION_NOT_EQUAL);
		factory.addExtension(EXTENSION_NOT);
	}

	/**
	 * Add the opcodes for type conversion operations to a context.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 */
	public static void createConversionFunctions(
			final EncogProgramContext context) {
		final FunctionFactory factory = context.getFunctions();
		factory.addExtension(EXTENSION_CINT);
		factory.addExtension(EXTENSION_CFLOAT);
		factory.addExtension(EXTENSION_CSTR);
		factory.addExtension(EXTENSION_CBOOL);
	}

	/**
	 * Add the opcodes for numeric operations to a context, do not use protected
	 * division.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 */
	public static void createNumericOperators(final EncogProgramContext context) {
		createNumericOperators(context, false);
	}

	/**
	 * Add the opcodes for numeric operations to a context.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 * @param protectedDiv
	 *            Should protected division be used.
	 */
	public static void createNumericOperators(
			final EncogProgramContext context, final boolean protectedDiv) {
		final FunctionFactory factory = context.getFunctions();
		factory.addExtension(EXTENSION_VAR_SUPPORT);
		factory.addExtension(EXTENSION_CONST_SUPPORT);
		factory.addExtension(EXTENSION_NEG);
		factory.addExtension(EXTENSION_ADD);
		factory.addExtension(EXTENSION_SUB);
		factory.addExtension(EXTENSION_MUL);
		if (protectedDiv) {
			factory.addExtension(EXTENSION_PDIV);
		} else {
			factory.addExtension(EXTENSION_DIV);
		}
		factory.addExtension(EXTENSION_POWER);
	}

	/**
	 * Add the opcodes for string operations to a context.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 */
	public static void createStringFunctions(final EncogProgramContext context) {
		final FunctionFactory factory = context.getFunctions();
		factory.addExtension(EXTENSION_LENGTH);
		factory.addExtension(EXTENSION_FORMAT);
		factory.addExtension(EXTENSION_LEFT);
		factory.addExtension(EXTENSION_RIGHT);
	}

	/**
	 * Add the opcodes for trig functions operations to a context.
	 * 
	 * @param context
	 *            The context to add the opcodes to.
	 */
	public static void createTrigFunctions(final EncogProgramContext context) {
		final FunctionFactory factory = context.getFunctions();
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
}
