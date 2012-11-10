package org.encog.ml.prg.train;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.train.rewrite.RewriteRule;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.parse.expression.common.RenderCommonExpression;

public class PrgPopulation {

	private CalculateScore scoreFunction;
	private final EncogProgramContext context;
	private EncogProgram[] members;
	private List<RewriteRule> rewriteRules = new ArrayList<RewriteRule>();
	private int maxDepth = 5;
	private int maxPopulation = 1000;
	
	public PrgPopulation(EncogProgramContext theContext, MLDataSet theTrainingSet, int theMaxPopulation) {
		this(theContext, new TrainingSetScore(theTrainingSet), theMaxPopulation);
	}
	
	public PrgPopulation(EncogProgramContext theContext, CalculateScore theScoreFunction, int theMaxPopulation) {
		this.context = theContext;
		this.maxPopulation = theMaxPopulation;
		this.members = new EncogProgram[this.maxPopulation];
		this.scoreFunction = theScoreFunction;
	}
	
	public void createRandomPopulation(int maxDepth) {
		CreateRandom rnd = new CreateRandom(context,this.maxDepth);
		
		for(int i=0;i<this.maxPopulation;i++) {
			
			boolean done = false;
			EncogProgram prg = null;
			do {
				prg = rnd.generate();
				double score = this.scoreFunction.calculateScore(prg);
				if( !Double.isInfinite(score) && !Double.isNaN(score) ) {
					prg.setScore(score);
					done = true;
				}
			} while(!done);
			
			rewrite(prg);
			this.members[i] = prg;
		}
	}

	
	public void dumpMembers() {
		
		RenderCommonExpression render = new RenderCommonExpression();
		
		int index = 0;
		for(EncogProgram prg: this.members) {
			System.out.println(index + ": Score " + prg.getScore() + " : " + render.render(prg));
			index++;
		}
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxPopulation() {
		return maxPopulation;
	}

	public void setMaxPopulation(int maxPopulation) {
		this.maxPopulation = maxPopulation;
	}

	public EncogProgramContext getContext() {
		return context;
	}

	public EncogProgram[] getMembers() {
		return members;
	}

	public void sort() {
		Arrays.sort(this.members);
	}
	
	public void addRewriteRule(RewriteRule rule) {
		this.rewriteRules.add(rule);
	}
	
	public void rewrite(EncogProgram prg) {
		for(RewriteRule rule: this.rewriteRules) {
			rule.rewrite(prg);
		}
	}

}
