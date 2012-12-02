package org.encog.ml.prg.train.rewrite;

import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.util.TraverseProgram;

public class RewriteConstants implements RewriteRule {
	private EncogProgram program;

	@Override
	public boolean rewrite(EncogProgram theProgram) {
		this.program = theProgram;
		
		int rewriteStart = 0;
		int potentialRewrite = -1;
		boolean allConst = true;

		TraverseProgram trav = new TraverseProgram(this.program);
		trav.begin(0);
		do {
			// have we hit something variable
			if( trav.getTemplate().isVariableValue() ) {
				allConst = false;
			}
			
			// have we hit a node
			if( trav.getTemplate().getChildNodeCount()>0 ) {
				if( allConst ) {
					potentialRewrite = trav.getNextIndex();
				} else {
					if( potentialRewrite!=-1 ) {
						rewrite(rewriteStart,potentialRewrite);
						return true;
					} else {
						rewriteStart = trav.getCurrentIndex();
						allConst = true;
					}
				}
			}
		} while (trav.next());

		if( potentialRewrite!=-1 ) {
			rewrite(rewriteStart,potentialRewrite);
			return true;
		}
		
		return false;		
	}

	private void rewrite(int rewriteStart, int rewriteStop) {
		ExpressionValue c = program.evaluate(rewriteStart);
		program.deleteSubtree(rewriteStart, (rewriteStop-rewriteStart));
		ProgramExtensionTemplate temp = this.program.getConstTemplate(c);
		int sz = temp.getInstructionSize(this.program.getHeader());
		program.insert(rewriteStart, sz);
		program.setProgramCounter(rewriteStart);
		program.writeConstNode(c);
	}
}
