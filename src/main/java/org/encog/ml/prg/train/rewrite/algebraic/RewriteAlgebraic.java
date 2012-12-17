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
		
		if( tryMinusMinus(program,map,parentNode) ) {
			return true;
		}
		
		if( tryPlusNeg(program,map,parentNode) ) {
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

	private boolean tryMinusMinus(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_SUB ) {
			MappedNode child2 = parentNode.getChildren().get(1);
			
			if( child2.isNumericConst() ) {
				double d = child2.getConstValue().toFloatValue();
				if( d<0 ) {
					program.setProgramCounter(parentNode.getIndex());
					program.writeNode(StandardExtensions.OPCODE_ADD);
					program.setProgramCounter(child2.getIndex());
					if( child2.getOpcode()==StandardExtensions.OPCODE_CFLOAT) {
						program.writeConstNode(-d);
					} else {
						program.writeConstNode((int)-d);
					}
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean tryPlusNeg(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_ADD ) {
			MappedNode child2 = parentNode.getChildren().get(1);
			
			if( child2.isNumericConst() ) {
				double d = child2.getConstValue().toFloatValue();
				if( d<0 ) {
					program.setProgramCounter(parentNode.getIndex());
					program.writeNode(StandardExtensions.OPCODE_SUB);
					program.setProgramCounter(child2.getIndex());
					if( child2.getOpcode()==StandardExtensions.OPCODE_CFLOAT) {
						program.writeConstNode(-d);
					} else {
						program.writeConstNode((int)-d);
					}
					return true;
				}
			}
		}
		
		return false;
	}

	
}
