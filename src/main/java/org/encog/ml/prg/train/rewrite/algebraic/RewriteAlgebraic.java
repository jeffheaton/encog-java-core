package org.encog.ml.prg.train.rewrite.algebraic;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.rewrite.RewriteRule;

public class RewriteAlgebraic implements RewriteRule {

	private final List<AlgebraicRule> rules = new ArrayList<AlgebraicRule>();
	
	public RewriteAlgebraic() {
		defineRule("--a","a");
	}

	public void defineRule(String theFromExpression, String theToExpression) {
		EncogProgram fromExpression = new EncogProgram(theFromExpression);
		EncogProgram toExpression = new EncogProgram(theToExpression);
		this.rules.add(new AlgebraicRule(fromExpression,toExpression));
	}
	
	@Override
	public boolean rewrite(EncogProgram program) {
		
		for(AlgebraicRule rule: this.rules) {
			if( rule.attemptRewrite(program) ) {
				return true;
			}
		}
		
		return false;
		
	}
	
	
}
