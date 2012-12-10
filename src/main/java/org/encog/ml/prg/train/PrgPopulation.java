package org.encog.ml.prg.train;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.train.rewrite.RewriteRule;
import org.encog.parse.expression.common.RenderCommonExpression;

public class PrgPopulation {

	private final EncogProgramContext context;
	private EncogProgram[] members;
	private List<RewriteRule> rewriteRules = new ArrayList<RewriteRule>();
	private EPLHolder holder;
	
	public PrgPopulation(EncogProgramContext theContext) {
		GeneticTrainingParams params = theContext.getParams();
		this.holder = theContext.getHolderFactory().factor(params.getPopulationSize(), params.getMaxIndividualSize());
		this.context = theContext;
		this.members = new EncogProgram[params.getPopulationSize()];
	}
		
	public void dumpMembers() {
		
		RenderCommonExpression render = new RenderCommonExpression();
		
		int index = 0;
		for(EncogProgram prg: this.members) {
			System.out.println(index + ": Score " + prg.getScore() + " : " + render.render(prg));
			index++;
		}
	}

	public EncogProgramContext getContext() {
		return context;
	}

	public EncogProgram[] getMembers() {
		return members;
	}
	
	public void addRewriteRule(RewriteRule rule) {
		this.rewriteRules.add(rule);
	}
	
	public void rewrite(EncogProgram prg) {
		
		boolean done = false;
		
		while(!done) {
			done = true;
			
			for(RewriteRule rule: this.rewriteRules) {
				if( rule.rewrite(prg) ) {
					done = false;
				}
			}
		}
	}

	public int size() {
		return this.members.length;
	}

	/**
	 * @return the holder
	 */
	public EPLHolder getHolder() {
		return holder;
	}

	public EncogProgram createProgram() {
		EPLHolder newHolder = this.context.getHolderFactory().factor(1, this.context.getParams().getMaxIndividualSize());
		EncogProgram result = new EncogProgram(this.context, new EncogProgramVariables(), newHolder,0);
		return result;
	}
	
	

}
