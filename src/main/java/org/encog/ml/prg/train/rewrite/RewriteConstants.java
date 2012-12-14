package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.util.TraverseProgram;

public class RewriteConstants implements RewriteRule {
	private EncogProgram program;
	
	public RewriteConstants() {
		
	}

	@Override
	public boolean rewrite(EncogProgram theProgram) {
		this.program = theProgram;
		
		int rewriteStart = 0;
		int potentialRewrite = -1;
		boolean allConst = true;


		
		return false;		
	}

	private void rewrite(int rewriteStart, int rewriteStop) {
		/*ExpressionValue c = program.evaluate(rewriteStart);
		program.deleteSubtree(rewriteStart, (rewriteStop-rewriteStart));
		ProgramExtensionTemplate temp = this.program.getConstTemplate(c);
		int sz = temp.getInstructionSize(this.program.getHeader());
		program.insert(rewriteStart, sz);
		program.setProgramCounter(rewriteStart);
		program.writeConstNode(c);*/
	}
}
