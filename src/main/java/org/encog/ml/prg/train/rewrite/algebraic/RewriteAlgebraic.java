package org.encog.ml.prg.train.rewrite.algebraic;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.rewrite.RewriteRule;
import org.encog.ml.prg.util.MapProgram;
import org.encog.ml.prg.util.MappedNode;

public class RewriteAlgebraic implements RewriteRule {
	
	public RewriteAlgebraic() {
	}

	
	@Override
	public boolean rewrite(EncogProgram program) {
		MapProgram map = new MapProgram(program);
		boolean result = rewriteNode(program,map,map.getRootNode());
		program.validate();
		return result;
	}
	
	
	
	private boolean rewriteNode(EncogProgram program, MapProgram map,
			MappedNode parentNode) {
		
		if( tryDoubleNeg(program,map,parentNode) ) {
			return true;
		}
		
		if( tryMinusMinus(program,map,parentNode) ) {
			return true;
		}
		
		if( tryPlusMinus(program,map,parentNode) ) {
			return true;
		}
		
		if( tryPlusNeg(program,map,parentNode) ) {
			return true;
		}
		
		if( tryDoubleAdd(program,map,parentNode) ) {
			return true;
		}
		
		if( tryDoubleMinus(program,map,parentNode) ) {
			return true;
		}
		
		if( tryDoubleMul(program,map,parentNode) ) {
			return true;
		}
		
		if( tryDoubleDiv(program,map,parentNode) ) {
			return true;
		}
		
		for(MappedNode childNode : parentNode.getChildren()) {
			if( rewriteNode(program,map,childNode) ) {
				return true;
			}
		}
		
		return false;
		
	}


	private boolean tryDoubleNeg(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_NEG) {
			MappedNode child = parentNode.getChildren().get(0);
			if( child.getOpcode()==StandardExtensions.OPCODE_NEG ) {
				int t = child.getIndex();
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
					if( child2.getOpcode()==StandardExtensions.OPCODE_CONST_FLOAT) {
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
	
	private boolean tryPlusMinus(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_ADD ) {
			MappedNode child2 = parentNode.getChildren().get(1);
			
			if( child2.isNumericConst() ) {
				double d = child2.getConstValue().toFloatValue();
				if( d<0 ) {
					program.setProgramCounter(parentNode.getIndex());
					program.writeNode(StandardExtensions.OPCODE_SUB);
					program.setProgramCounter(child2.getIndex());
					if( child2.getOpcode()==StandardExtensions.OPCODE_CONST_FLOAT) {
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
			
			if( child2.getOpcode()==StandardExtensions.OPCODE_NEG ) {
				program.setProgramCounter(parentNode.getIndex());				
				program.writeNode(StandardExtensions.OPCODE_SUB);
				program.deleteSubtree(child2.getIndex(), child2.getSize());
				return true;
			}
		}
		
		return false;
	}
	

	private boolean tryDoubleAdd(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_ADD ) {
			MappedNode child1 = parentNode.getChildren().get(0);
			MappedNode child2 = parentNode.getChildren().get(1);
			
			if( child1.getOpcode()==StandardExtensions.OPCODE_VAR && child2.getOpcode()==StandardExtensions.OPCODE_VAR ) {
				if( child1.getParam2()==child2.getParam2() ) {
					int deleteStart = child1.getIndex();
					int deleteEnd = parentNode.getIndex()+parentNode.getSize();
					short v = child1.getParam2();
					program.deleteSubtree(deleteStart, deleteEnd-deleteStart);
					program.insert(deleteStart, 4);
					program.setProgramCounter(deleteStart);
					program.writeConstNode(2.0);
					program.writeNode(StandardExtensions.OPCODE_VAR,0,v);
					program.writeNode(StandardExtensions.OPCODE_MUL);
					return true;
				}				
			}
		}
		
		return false;
	}
	
	private boolean tryDoubleMinus(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_SUB ) {
			MappedNode child1 = parentNode.getChildren().get(0);
			MappedNode child2 = parentNode.getChildren().get(1);
			
			if( child1.getOpcode()==StandardExtensions.OPCODE_VAR && child2.getOpcode()==StandardExtensions.OPCODE_VAR ) {
				if( child1.getParam2()==child2.getParam2() ) {
					int deleteStart = child1.getIndex();
					int deleteEnd = parentNode.getIndex()+parentNode.getSize();
					program.deleteSubtree(deleteStart, deleteEnd-deleteStart);
					program.insert(deleteStart, 2);
					program.setProgramCounter(deleteStart);
					program.writeConstNode(0.0);
					return true;
				}				
			}
		}
		
		return false;
	}
	
	private boolean tryDoubleMul(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_MUL ) {
			MappedNode child1 = parentNode.getChildren().get(0);
			MappedNode child2 = parentNode.getChildren().get(1);
			
			if( child1.getOpcode()==StandardExtensions.OPCODE_VAR && child2.getOpcode()==StandardExtensions.OPCODE_VAR ) {
				if( child1.getParam2()==child2.getParam2() ) {
					int deleteStart = child1.getIndex();
					int deleteEnd = parentNode.getIndex()+parentNode.getSize();
					short v = child1.getParam2();
					program.deleteSubtree(deleteStart, deleteEnd-deleteStart);
					program.insert(deleteStart, 4);
					program.setProgramCounter(deleteStart);
					program.writeNode(StandardExtensions.OPCODE_VAR,0,v);
					program.writeConstNode(2.0);
					program.writeNode(StandardExtensions.OPCODE_POW);
					return true;
				}				
			}
		}
		
		return false;
	}
	
	private boolean tryDoubleDiv(EncogProgram program, MapProgram map, MappedNode parentNode) {
		if(parentNode.getOpcode() == StandardExtensions.OPCODE_DIV ) {
			MappedNode child1 = parentNode.getChildren().get(0);
			MappedNode child2 = parentNode.getChildren().get(1);
			
			if( child1.getOpcode()==StandardExtensions.OPCODE_VAR && child2.getOpcode()==StandardExtensions.OPCODE_VAR ) {
				if( child1.getParam2()==child2.getParam2() ) {
					int deleteStart = child1.getIndex();
					int deleteEnd = parentNode.getIndex()+parentNode.getSize();
					program.deleteSubtree(deleteStart, deleteEnd-deleteStart);
					program.insert(deleteStart, 2);
					program.setProgramCounter(deleteStart);
					program.writeConstNode(1.0);
					return true;
				}				
			}
		}
		
		return false;
	}

	
}
