package org.encog.ml.prg.train.rewrite.algebraic;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.util.TraverseProgram;

public class AlgebraicRule {
	
	private final EncogProgram fromExpression;
	private final EncogProgram toExpression;
	
	public AlgebraicRule(EncogProgram theFromExpression, EncogProgram theToExpression) {
		this.fromExpression = theFromExpression;
		this.toExpression = theToExpression;
	}

	/**
	 * @return the fromExpression
	 */
	public EncogProgram getFromExpression() {
		return fromExpression;
	}

	/**
	 * @return the toExpression
	 */
	public EncogProgram getToExpression() {
		return toExpression;
	}
	
	private boolean match(TraverseProgram argTrav) {
		TraverseProgram ruleTrav = new TraverseProgram(this.fromExpression);
		TraverseProgram prgTrav = new TraverseProgram(argTrav);
		
		return false;
	}
	
	private int search(EncogProgram program) {
		TraverseProgram trav = new TraverseProgram(program);
		trav.begin(0);
		while(trav.next()) {
			if( match(trav) ){
				
			}
		}
	
		return -1;
	}

	public boolean attemptRewrite(EncogProgram program) {
		search(program);
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}
