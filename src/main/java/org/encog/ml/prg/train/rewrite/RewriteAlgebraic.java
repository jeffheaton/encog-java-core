package org.encog.ml.prg.train.rewrite;

import org.encog.Encog;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.RewriteRule;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.StandardExtensions;

public class RewriteAlgebraic implements RewriteRule {

	private boolean rewritten;
	
	@Override
	public boolean rewrite(Genome g) {
		this.rewritten = false;
		EncogProgram program = ((EncogProgram)g);
		ProgramNode node = program.getRootNode();
		ProgramNode rewrittenRoot = internalRewrite(node);
		if( rewrittenRoot!=null ) {
			program.setRootNode(rewrittenRoot);
		}
		return this.rewritten;
	}
	
	private ProgramNode tryPowerZero(ProgramNode parent) {
		if( parent.getTemplate()==StandardExtensions.EXTENSION_POWER || parent.getTemplate()==StandardExtensions.EXTENSION_POWFN ) {
			ProgramNode child = parent.getChildNode(1);
			if( child.getTemplate()==StandardExtensions.EXTENSION_CONST_SUPPORT ) {
				if( Math.abs(child.getData()[0].toFloatValue())<Encog.DEFAULT_DOUBLE_EQUAL) {
					this.rewritten = true;
					return createNumericConst(parent.getOwner(),1);
				}
			}
		}

		return parent;
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
				ExpressionValue v = child2.getData()[0];
				if( v.isFloat() ) {
					double v2 = v.toFloatValue();
					if( v2<0 ) {
						child2.getData()[0].setValue(-v2);
						parent = parent.getOwner().getContext().getFunctions().factorFunction("+", parent.getOwner(), new ProgramNode[] 
								{child1,child2} );
					}
				}
				else if( v.isInt() ) {
					long v2 = v.toIntValue();
					if( v2<0 ) {
						child2.getData()[0].setValue(-v2);
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
				ExpressionValue v = child2.getData()[0];
				if( v.isFloat() ) {
					double v2 = v.toFloatValue();
					if( v2<0 ) {
						child2.getData()[0].setValue(-v2);
						parent = parent.getOwner().getContext().getFunctions().factorFunction("-", parent.getOwner(), new ProgramNode[] 
								{child1,child2} );
					}
				}
				else if( v.isInt() ) {
					long v2 = v.toIntValue();
					if( v2<0 ) {
						child2.getData()[0].setValue(-v2);
						parent = parent.getOwner().getContext().getFunctions().factorFunction("-", parent.getOwner(), new ProgramNode[] 
								{child1,child2} );
					}
				}
			}
		}
		return parent;
	}
	
	private ProgramNode createNumericConst(EncogProgram prg, int v) {
		ProgramNode result = prg.getFunctions().factorFunction("#const", prg, new ProgramNode[] {} );
		result.getData()[0] = new ExpressionValue(v);
		return result;
	}
	
	private ProgramNode createNumericConst(EncogProgram prg, double v) {
		ProgramNode result = prg.getFunctions().factorFunction("#const", prg, new ProgramNode[] {} );
		result.getData()[0] = new ExpressionValue(v);
		return result;
	}
	
	private ProgramNode tryVarOpVar(ProgramNode parent) {
		if( parent.getChildNodes().size()==2 && parent.getName().length()==1 && "+-*/".indexOf(parent.getName().charAt(0))!=-1 ) {
			ProgramNode child1 = parent.getChildNode(0);
			ProgramNode child2 = parent.getChildNode(1);
			
			if( child1.getName().equals("#var") && child2.getName().equals("#var") ) {
				if( child1.getData()[0].toIntValue()==child2.getData()[0].toIntValue()) {
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
	
	
	
	private ProgramNode internalRewrite(final ProgramNode parent) {
		ProgramNode rewrittenParent = parent;
		
		rewrittenParent = tryDoubleNegative(rewrittenParent);
		rewrittenParent = tryMinusMinus(rewrittenParent);
		rewrittenParent = tryPlusNeg(rewrittenParent);
		rewrittenParent = tryVarOpVar(rewrittenParent);
		rewrittenParent = tryPowerZero(rewrittenParent);
		
		// try children
		for (int i = 0; i < rewrittenParent.getChildNodes().size(); i++) {
			ProgramNode childNode = (ProgramNode)rewrittenParent.getChildNodes().get(i);
			ProgramNode rewriteChild = internalRewrite(childNode);
			if (childNode!=rewriteChild) {
				rewrittenParent.getChildNodes().remove(i);
				rewrittenParent.getChildNodes().add(i, rewriteChild);
				this.rewritten = true;
			}
		}
		
		return rewrittenParent;
	}

}
