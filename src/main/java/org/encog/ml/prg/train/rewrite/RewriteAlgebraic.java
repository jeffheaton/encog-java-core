package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class RewriteAlgebraic implements RewriteRule {

	@Override
	public boolean rewrite(EncogProgram program) {
		// TODO Auto-generated method stub
		return false;
	}

/*	private boolean rewritten;
	
	@Override
	public boolean rewrite(EncogProgram program) {
		this.rewritten = false;
		ProgramNode node = program.getRootNode();
		ProgramNode rewrittenRoot = internalRewrite(node);
		if( rewrittenRoot!=null ) {
			program.setRootNode(rewrittenRoot);
		}
		return this.rewritten;
	}
	
	private ProgramNode tryDoubleNegative(ProgramNode parent) {
		if( parent.getName().equals("-") ) {
			ProgramNode child = parent.getChildNode(0);
			if( child.getName().equals("-") ) {
				ProgramNode grandChild = child.getChildNode(0);	
				this.rewritten = true;
				return grandChild;
			}
		}
		return parent;
	}
	
	private ProgramNode tryMinusMinus(ProgramNode parent) {
		if( parent.getName().equals("-") && parent.getChildNodes().size()==2 ) {
			ProgramNode child1 = parent.getChildNode(0);
			ProgramNode child2 = parent.getChildNode(1);
			
			if( child2.getName().equals("#const") ) {
				ExpressionValue v = child2.getExpressionData()[0];
				if( v.isFloat() ) {
					double v2 = v.toFloatValue();
					if( v2<0 ) {
						child2.getExpressionData()[0].setValue(-v2);
						parent = parent.getOwner().getContext().getFunctions().factorFunction("+", parent.getOwner(), new ProgramNode[] 
								{child1,child2} );
					}
				}
				else if( v.isInt() ) {
					long v2 = v.toIntValue();
					if( v2<0 ) {
						child2.getExpressionData()[0].setValue(-v2);
						parent = parent.getOwner().getContext().getFunctions().factorFunction("+", parent.getOwner(), new ProgramNode[] 
								{child1,child2} );
					}
				}
			}
		}
		return parent;
	}
	
	private ProgramNode tryPlusNeg(ProgramNode parent) {
		if( parent.getName().equals("+") && parent.getChildNodes().size()==2 ) {
			ProgramNode child1 = parent.getChildNode(0);
			ProgramNode child2 = parent.getChildNode(1);
			
			if( child2.getName().equals("-") && child2.getChildNodes().size()==1 ) {
				parent = parent.getOwner().getContext().getFunctions().factorFunction("-", parent.getOwner(), new ProgramNode[] 
						{child1,child2.getChildNode(0)} );
			} else if( child2.getName().equals("#const") ) {
				ExpressionValue v = child2.getExpressionData()[0];
				if( v.isFloat() ) {
					double v2 = v.toFloatValue();
					if( v2<0 ) {
						child2.getExpressionData()[0].setValue(-v2);
						parent = parent.getOwner().getContext().getFunctions().factorFunction("-", parent.getOwner(), new ProgramNode[] 
								{child1,child2} );
					}
				}
				else if( v.isInt() ) {
					long v2 = v.toIntValue();
					if( v2<0 ) {
						child2.getExpressionData()[0].setValue(-v2);
						parent = parent.getOwner().getContext().getFunctions().factorFunction("-", parent.getOwner(), new ProgramNode[] 
								{child1,child2} );
					}
				}
			}
		}
		return parent;
	}
	
	private ProgramNode createNumericConst(EncogProgram prg, double v) {
		ProgramNode result = prg.getFunctions().factorFunction("#const", prg, new ProgramNode[] {} );
		result.getExpressionData()[0] = new ExpressionValue(v);
		return result;
	}
	
	private ProgramNode tryVarOpVar(ProgramNode parent) {
		if( parent.getChildNodes().size()==2 && parent.getName().length()==1 && "/+-*".indexOf(parent.getName().charAt(0))!=-1 ) {
			ProgramNode child1 = parent.getChildNode(0);
			ProgramNode child2 = parent.getChildNode(1);
			
			if( child1.getName().equals("#var") && child2.getName().equals("#var") ) {
				if( child1.getIntData()[0]==child2.getIntData()[0]) {
					switch(parent.getName().charAt(0)) {
						case '-':
							parent = createNumericConst(parent.getOwner(),0);
							break;
						case '+':
							parent = parent.getOwner().getFunctions().factorFunction("*", parent.getOwner(), new ProgramNode[] 
									{
								createNumericConst(parent.getOwner(),2),
								child1
									} );
							break;
						case '*':
							parent = parent.getOwner().getFunctions().factorFunction("^", parent.getOwner(), new ProgramNode[] 
									{
								child1,
								createNumericConst(parent.getOwner(),2)
									} );
							break;
						case '/':
							parent = createNumericConst(parent.getOwner(),1);
							break;
					}
				}				
			}
		}
		return parent;
	}
	
	
	
	private ProgramNode internalRewrite(ProgramNode parent) {
		ProgramNode rewritten = parent;
		
		rewritten = tryDoubleNegative(rewritten);
		rewritten = tryMinusMinus(rewritten);
		rewritten = tryPlusNeg(rewritten);
		rewritten = tryVarOpVar(rewritten);
		
		// try children
		for (int i = 0; i < parent.getChildNodes().size(); i++) {
			ProgramNode childNode = (ProgramNode)parent.getChildNodes().get(i);
			ProgramNode rewriteChild = internalRewrite(childNode);
			if (childNode!=rewriteChild) {
				parent.getChildNodes().remove(i);
				parent.getChildNodes().add(i, rewriteChild);
				this.rewritten = true;
			}
		}
		
		return rewritten;
	}*/

}
