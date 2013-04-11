package org.encog.ml.prg.train.rewrite;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.rules.RewriteRule;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.StandardExtensions;

public class RewriteBoolean implements RewriteRule {
	
	private boolean rewritten;
	
	private boolean isTrue(ProgramNode node) {
		if( node.getTemplate()==StandardExtensions.EXTENSION_CONST_SUPPORT ) {
			ExpressionValue v = node.evaluate();
			if( v.isBoolean() ) {
				if( v.toBooleanValue() ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isFalse(ProgramNode node) {
		if( node.getTemplate()==StandardExtensions.EXTENSION_CONST_SUPPORT ) {
			ExpressionValue v = node.evaluate();
			if( v.isBoolean() ) {
				if( !v.toBooleanValue() ) {
					return true;
				}
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
	
	private ProgramNode internalRewrite(final ProgramNode parent) {
		ProgramNode rewrittenParent = parent;

		rewrittenParent = tryAnd(rewrittenParent);

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
	
	private ProgramNode tryAnd(final ProgramNode parent) {
		if (parent.getTemplate()==StandardExtensions.EXTENSION_AND ) {
			final ProgramNode child1 = parent.getChildNode(0);
			final ProgramNode child2 = parent.getChildNode(1);
			
			if ( isTrue(child1) && child2.getTemplate()!=StandardExtensions.EXTENSION_CONST_SUPPORT) {
				this.rewritten = true;
				return child2;
			}
			
			if ( isTrue(child2) && child1.getTemplate()!=StandardExtensions.EXTENSION_CONST_SUPPORT) {
				this.rewritten = true;
				return child1;
			}
		}
		return parent;
	}
}
