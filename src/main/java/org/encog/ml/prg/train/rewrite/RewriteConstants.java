package org.encog.ml.prg.train.rewrite;

import java.io.Serializable;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.util.TraverseProgram;

public class RewriteConstants implements RewriteRule, Serializable {
	
	public RewriteConstants() {
		
	}
	
	private boolean areAnyVariable(EncogProgram program, int rewriteStart, int rewriteStop) {
		TraverseProgram trav = new TraverseProgram(program);
		trav.begin(rewriteStart);
		while( trav.next() && trav.getFrameIndex()<rewriteStop ) {
			if( trav.getTemplate().isVariableValue() ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean rewrite(Genome genome) {
		EncogProgram program = (EncogProgram)genome;
		TraverseProgram trav = new TraverseProgram(program);
		trav.begin(0);
		while(trav.next()) {
			if( !trav.isLeaf() ) {
				int rewriteStop = trav.getFrameIndex();
				int rewriteStart = program.findNodeStart(rewriteStop);
				if( !areAnyVariable(program,rewriteStart,rewriteStop) ) {
					rewrite(program,rewriteStart,rewriteStop);
					return true;
				}
			}
		}
		return false;
	}

	private void rewrite(EncogProgram program, int rewriteStart, int rewriteStop) {
		int size = program.nextIndex(rewriteStop)-rewriteStart;
		ExpressionValue c = program.evaluate(rewriteStart, rewriteStop);
		program.deleteSubtree(rewriteStart, size);
		ProgramExtensionTemplate temp = program.getConstTemplate(c);
		int sz = temp.getInstructionSize(program.getHeader());
		program.insert(rewriteStart, sz);
		program.setProgramCounter(rewriteStart);
		program.writeConstNode(c);
	}
}
