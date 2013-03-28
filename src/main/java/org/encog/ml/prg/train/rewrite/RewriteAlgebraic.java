package org.encog.ml.prg.train.rewrite;

import org.encog.Encog;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.RewriteRule;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.StandardExtensions;

public class RewriteAlgebraic implements RewriteRule {

	private boolean rewritten;

	private ProgramNode createNumericConst(final EncogProgram prg,
			final double v) {
		final ProgramNode result = prg.getFunctions().factorFunction("#const",
				prg, new ProgramNode[] {});
		result.getData()[0] = new ExpressionValue(v);
		return result;
	}

	private ProgramNode createNumericConst(final EncogProgram prg, final int v) {
		final ProgramNode result = prg.getFunctions().factorFunction("#const",
				prg, new ProgramNode[] {});
		result.getData()[0] = new ExpressionValue(v);
		return result;
	}

	private ProgramNode internalRewrite(final ProgramNode parent) {
		ProgramNode rewrittenParent = parent;

		rewrittenParent = tryDoubleNegative(rewrittenParent);
		rewrittenParent = tryMinusMinus(rewrittenParent);
		rewrittenParent = tryPlusNeg(rewrittenParent);
		rewrittenParent = tryVarOpVar(rewrittenParent);
		rewrittenParent = tryPowerZero(rewrittenParent);
		rewrittenParent = tryOnePower(rewrittenParent);
		rewrittenParent = tryZeroPlus(rewrittenParent);
		rewrittenParent = tryZeroDiv(rewrittenParent);
		rewrittenParent = tryZeroMul(rewrittenParent);
		rewrittenParent = tryMinusZero(rewrittenParent);

		// try children
		for (int i = 0; i < rewrittenParent.getChildNodes().size(); i++) {
			final ProgramNode childNode = (ProgramNode) rewrittenParent
					.getChildNodes().get(i);
			final ProgramNode rewriteChild = internalRewrite(childNode);
			if (childNode != rewriteChild) {
				rewrittenParent.getChildNodes().remove(i);
				rewrittenParent.getChildNodes().add(i, rewriteChild);
				this.rewritten = true;
			}
		}

		return rewrittenParent;
	}

	private boolean isConstValue(final ProgramNode node, final double v) {
		if (node.getTemplate() == StandardExtensions.EXTENSION_CONST_SUPPORT) {
			if (Math.abs(node.getData()[0].toFloatValue() - v) < Encog.DEFAULT_DOUBLE_EQUAL) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean rewrite(final Genome g) {
		this.rewritten = false;
		final EncogProgram program = (EncogProgram) g;
		final ProgramNode node = program.getRootNode();
		final ProgramNode rewrittenRoot = internalRewrite(node);
		if (rewrittenRoot != null) {
			program.setRootNode(rewrittenRoot);
		}
		return this.rewritten;
	}

	private ProgramNode tryDoubleNegative(final ProgramNode parent) {
		if (parent.getName().equals("-")) {
			final ProgramNode child = parent.getChildNode(0);
			if (child.getName().equals("-")) {
				final ProgramNode grandChild = child.getChildNode(0);
				this.rewritten = true;
				return grandChild;
			}
		}
		return parent;
	}

	private ProgramNode tryMinusMinus(ProgramNode parent) {
		if (parent.getName().equals("-") && parent.getChildNodes().size() == 2) {
			final ProgramNode child1 = parent.getChildNode(0);
			final ProgramNode child2 = parent.getChildNode(1);

			if (child2.getName().equals("#const")) {
				final ExpressionValue v = child2.getData()[0];
				if (v.isFloat()) {
					final double v2 = v.toFloatValue();
					if (v2 < 0) {
						child2.getData()[0] = new ExpressionValue(-v2);
						parent = parent
								.getOwner()
								.getContext()
								.getFunctions()
								.factorFunction("+", parent.getOwner(),
										new ProgramNode[] { child1, child2 });
					}
				} else if (v.isInt()) {
					final long v2 = v.toIntValue();
					if (v2 < 0) {
						child2.getData()[0] = new ExpressionValue(-v2);
						parent = parent
								.getOwner()
								.getContext()
								.getFunctions()
								.factorFunction("+", parent.getOwner(),
										new ProgramNode[] { child1, child2 });
					}
				}
			}
		}
		return parent;
	}

	private ProgramNode tryMinusZero(final ProgramNode parent) {
		if (parent.getTemplate() == StandardExtensions.EXTENSION_SUB) {
			final ProgramNode child2 = parent.getChildNode(1);
			if (isConstValue(child2, 0)) {
				final ProgramNode child1 = parent.getChildNode(0);
				return child1;
			}
		}
		return parent;
	}

	private ProgramNode tryOnePower(final ProgramNode parent) {
		if (parent.getTemplate() == StandardExtensions.EXTENSION_POWER
				|| parent.getTemplate() == StandardExtensions.EXTENSION_POWFN) {
			final ProgramNode child = parent.getChildNode(0);
			if (child.getTemplate() == StandardExtensions.EXTENSION_CONST_SUPPORT) {
				if (Math.abs(child.getData()[0].toFloatValue() - 1) < Encog.DEFAULT_DOUBLE_EQUAL) {
					this.rewritten = true;
					return createNumericConst(parent.getOwner(), 1);
				}
			}
		}

		return parent;
	}

	private ProgramNode tryPlusNeg(ProgramNode parent) {
		if (parent.getName().equals("+") && parent.getChildNodes().size() == 2) {
			final ProgramNode child1 = parent.getChildNode(0);
			final ProgramNode child2 = parent.getChildNode(1);

			if (child2.getName().equals("-")
					&& child2.getChildNodes().size() == 1) {
				parent = parent
						.getOwner()
						.getContext()
						.getFunctions()
						.factorFunction(
								"-",
								parent.getOwner(),
								new ProgramNode[] { child1,
										child2.getChildNode(0) });
			} else if (child2.getName().equals("#const")) {
				final ExpressionValue v = child2.getData()[0];
				if (v.isFloat()) {
					final double v2 = v.toFloatValue();
					if (v2 < 0) {
						child2.getData()[0] = new ExpressionValue(-v2);
						parent = parent
								.getOwner()
								.getContext()
								.getFunctions()
								.factorFunction("-", parent.getOwner(),
										new ProgramNode[] { child1, child2 });
					}
				} else if (v.isInt()) {
					final long v2 = v.toIntValue();
					if (v2 < 0) {
						child2.getData()[0] = new ExpressionValue(-v2);
						parent = parent
								.getOwner()
								.getContext()
								.getFunctions()
								.factorFunction("-", parent.getOwner(),
										new ProgramNode[] { child1, child2 });
					}
				}
			}
		}
		return parent;
	}

	private ProgramNode tryPowerZero(final ProgramNode parent) {
		if (parent.getTemplate() == StandardExtensions.EXTENSION_POWER
				|| parent.getTemplate() == StandardExtensions.EXTENSION_POWFN) {
			final ProgramNode child0 = parent.getChildNode(0);
			final ProgramNode child1 = parent.getChildNode(1);
			if (isConstValue(child1, 0)) {
				return createNumericConst(parent.getOwner(), 1);
			}
			if (isConstValue(child0, 0)) {
				return createNumericConst(parent.getOwner(), 0);
			}
		}

		return parent;
	}

	private ProgramNode tryVarOpVar(ProgramNode parent) {
		if (parent.getChildNodes().size() == 2
				&& parent.getName().length() == 1
				&& "+-*/".indexOf(parent.getName().charAt(0)) != -1) {
			final ProgramNode child1 = parent.getChildNode(0);
			final ProgramNode child2 = parent.getChildNode(1);

			if (child1.getName().equals("#var")
					&& child2.getName().equals("#var")) {
				if (child1.getData()[0].toIntValue() == child2.getData()[0]
						.toIntValue()) {
					switch (parent.getName().charAt(0)) {
					case '-':
						parent = createNumericConst(parent.getOwner(), 0);
						break;
					case '+':
						parent = parent
								.getOwner()
								.getFunctions()
								.factorFunction(
										"*",
										parent.getOwner(),
										new ProgramNode[] {
												createNumericConst(
														parent.getOwner(), 2),
												child1 });
						break;
					case '*':
						parent = parent
								.getOwner()
								.getFunctions()
								.factorFunction(
										"^",
										parent.getOwner(),
										new ProgramNode[] {
												child1,
												createNumericConst(
														parent.getOwner(), 2) });
						break;
					case '/':
						parent = createNumericConst(parent.getOwner(), 1);
						break;
					}
				}
			}
		}
		return parent;
	}

	private ProgramNode tryZeroDiv(final ProgramNode parent) {
		if (parent.getTemplate() == StandardExtensions.EXTENSION_DIV) {
			final ProgramNode child1 = parent.getChildNode(0);
			final ProgramNode child2 = parent.getChildNode(1);

			if (!isConstValue(child2, 0)) {
				if (isConstValue(child1, 0)) {
					this.rewritten = true;
					return this.createNumericConst(parent.getOwner(), 0);
				}
			}
		}

		return parent;
	}

	private ProgramNode tryZeroMul(final ProgramNode parent) {
		if (parent.getTemplate() == StandardExtensions.EXTENSION_MUL) {
			final ProgramNode child1 = parent.getChildNode(0);
			final ProgramNode child2 = parent.getChildNode(1);

			if (isConstValue(child1, 0) || isConstValue(child2, 0)) {
				this.rewritten = true;
				return this.createNumericConst(parent.getOwner(), 0);
			}
		}

		return parent;
	}

	private ProgramNode tryZeroPlus(final ProgramNode parent) {
		if (parent.getTemplate() == StandardExtensions.EXTENSION_ADD) {
			final ProgramNode child1 = parent.getChildNode(0);
			final ProgramNode child2 = parent.getChildNode(1);

			if (isConstValue(child1, 0)) {
				this.rewritten = true;
				return child2;
			}

			if (isConstValue(child2, 0)) {
				this.rewritten = true;
				return child1;
			}

		}

		return parent;
	}

}
