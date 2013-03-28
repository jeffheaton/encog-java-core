package org.encog.ml.prg.train.rewrite;

import org.encog.Encog;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.rules.RewriteRule;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class RewriteConstants implements RewriteRule {
	
	private boolean rewritten;

	public boolean rewrite(Genome g) {
		EncogProgram program = ((EncogProgram)g);
		this.rewritten = false;
		ProgramNode rootNode = program.getRootNode();
		ProgramNode rewrite = rewriteNode(rootNode);
		if (rewrite != null) {
			program.setRootNode(rewrite);
		}
		return this.rewritten;
	}

	private ProgramNode rewriteNode(ProgramNode node) {

		// first try to rewrite the child node
		ProgramNode rewrite = tryNodeRewrite(node);
		if (rewrite != null) {
			return rewrite;
		}

		// if we could not rewrite the entire node, rewrite as many children as
		// we can
		for (int i = 0; i < node.getChildNodes().size(); i++) {
			ProgramNode childNode = (ProgramNode)node.getChildNodes().get(i);
			rewrite = rewriteNode(childNode);
			if (rewrite != null) {
				node.getChildNodes().remove(i);
				node.getChildNodes().add(i, rewrite);
				this.rewritten = true;
			}
		}

		// we may have rewritten some children, but the parent was not
		// rewritten, so return null.
		return null;
	}

	private ProgramNode tryNodeRewrite(ProgramNode parentNode) {
		ProgramNode result = null;

		if (parentNode.isLeaf()) {
			return null;
		}

		if (parentNode.allConstDescendants()) {
			ExpressionValue v = parentNode.evaluate();
			double ck = v.toFloatValue();
			
			// do not rewrite if it produces a div by 0 or other bad result.
			if( Double.isNaN(ck) || Double.isInfinite(ck) ) {
				return result;
			}
			
			result = parentNode
					.getOwner()
					.getContext()
					.getFunctions()
					.factorFunction("#const", parentNode.getOwner(),
							new ProgramNode[] {});
			
			// is it an integer?
			if( Math.abs( ck- ((int)ck))<Encog.DEFAULT_DOUBLE_EQUAL) {
				result.getData()[0] = new ExpressionValue((int)ck);
			} else {
				result.getData()[0] = v;
			}
		}
		return result;
	}
}
