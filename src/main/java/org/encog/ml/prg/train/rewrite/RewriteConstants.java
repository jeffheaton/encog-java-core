package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;

public class RewriteConstants implements RewriteRule {

	public void rewrite(EncogProgram program) {
		ProgramNode rootNode = program.getRootNode();
		ProgramNode rewrite = rewriteNode(rootNode);
		if (rewrite != null) {
			program.setRootNode(rewrite);
		}
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
			result = parentNode
					.getOwner()
					.getContext()
					.getFunctions()
					.factorFunction("#const", parentNode.getOwner(),
							new ProgramNode[] {});
			result.getExpressionData()[0] = parentNode.evaluate();
		}
		return result;
	}
}
