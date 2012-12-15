package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.util.TraverseProgram;

public class RewriteConstants implements RewriteRule {
	private EncogProgram program;
	
	public RewriteConstants() {
		
	}
	
	private boolean areAnyVariable(int rewriteStart, int rewriteStop) {
		TraverseProgram trav = new TraverseProgram(this.program);
		trav.begin(rewriteStart);
		while( trav.next() && trav.getFrameIndex()<rewriteStop ) {
			if( trav.getTemplate().isVariableValue() ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean rewrite(EncogProgram theProgram) {
		this.program = theProgram;
		TraverseProgram trav = new TraverseProgram(theProgram);
		trav.begin(0);
		while(trav.next()) {
			if( !trav.isLeaf() ) {
				int rewriteStop = trav.getFrameIndex();
				int rewriteStart = this.program.findNodeStart(rewriteStop);
				if( !areAnyVariable(rewriteStart,rewriteStop) ) {
					rewrite(rewriteStart,rewriteStop);
					return true;
				}
			}
		}
		return false;
	}

	private void rewrite(int rewriteStart, int rewriteStop) {
		int size = program.nextIndex(rewriteStop)-rewriteStart;
		ExpressionValue c = program.evaluate(rewriteStart, rewriteStop);
		program.deleteSubtree(rewriteStart, size);
		ProgramExtensionTemplate temp = this.program.getConstTemplate(c);
		int sz = temp.getInstructionSize(this.program.getHeader());
		program.insert(rewriteStart, sz);
		program.setProgramCounter(rewriteStart);
		program.writeConstNode(c);
	}
}
