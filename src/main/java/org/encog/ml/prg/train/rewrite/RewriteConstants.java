package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;

public class RewriteConstants {
	
	public void rewrite(EncogProgram program) {
		ProgramNode rootNode = program.getRootNode();
		rewriteNode(rootNode);
	}
	
	private void rewriteNode(ProgramNode node) {
		
	}
	
	
	private ProgramNode tryNodeRewrite(ProgramNode parentNode) {
		ProgramNode result = null;
		
		if( parentNode.allLeafChildren() ) {
			if( parentNode.allConstChildren() ) {
				result = parentNode.getOwner().getContext().getFunctions().factorFunction("#const", parentNode.getOwner(), new ProgramNode[] {} );
				result.getExpressionData()[0] = parentNode.evaluate();
			}
		}
		return result;
	}
}
