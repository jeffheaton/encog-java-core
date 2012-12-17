package org.encog.ml.prg.train.rewrite.algebraic;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.rewrite.RewriteRule;
import org.encog.ml.prg.util.MapProgram;
import org.encog.ml.prg.util.MappedNode;
import org.encog.ml.prg.util.TraverseProgram;
import org.encog.util.datastruct.WindowInt;

public class RewriteAlgebraic implements RewriteRule {
	
	public RewriteAlgebraic() {
	}

	
	@Override
	public boolean rewrite(EncogProgram program) {
		MapProgram map = new MapProgram(program);
		return rewriteNode(program,map,map.getRootNode());
	}
	
	
	
	private boolean rewriteNode(EncogProgram program, MapProgram map,
			MappedNode parentNode) {
		
		if( tryDoubleNeg(program,map,parentNode) ) {
			return true;
		}
		
		for(MappedNode childNode : parentNode.getChildren()) {
			rewriteNode(program,map,childNode);
		}
		
		return false;
		
	}


	private boolean tryDoubleNeg(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_NEG) {
			MappedNode child = parentNode.getChildren().get(0);
			if( child.getOpcode()==StandardExtensions.OPCODE_NEG ) {
				int t = parentNode.getIndex();
				program.deleteSubtree(t, parentNode.getSize()+child.getSize());
				return true;
			}
		}
			
		return false;
	}

	private boolean tryMinusMinus(EncogProgram program, TraverseProgram trav, WindowInt lastOpcode, WindowInt lastPos) {
		if( lastOpcode.getData()[0] == StandardExtensions.OPCODE_SUB && 
			isNumericConst(lastOpcode.getData()[1])  &&
			isNumericConst(lastOpcode.getData()[2]) ) {
				program.setProgramCounter(lastPos.getData()[0]);
				program.writeNode(StandardExtensions.OPCODE_ADD);
				return true;
			}
			
		return false;
	}
	
	private boolean isNumericConst(int opcode) {
		if( opcode==StandardExtensions.OPCODE_CONST_FLOAT || opcode==StandardExtensions.OPCODE_CONST_INT) {
			return true;
		}
		else return false;
	}
	
	
}
