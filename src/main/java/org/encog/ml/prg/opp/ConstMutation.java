package org.encog.ml.prg.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.tree.TreeNode;

public class ConstMutation implements EvolutionaryOperator {

	private double frequency;
	private double sigma;
	
	public ConstMutation(EncogProgramContext theContext, double theFrequency, double theSigma) {
		this.frequency = theFrequency;
		this.sigma = theSigma;
	}

	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return Returns the number of offspring produced.  In this case, one.
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * @return Returns the number of parents needed.  In this case, one.
	 */
	@Override
	public int parentsNeeded() {
		return 1;
	}

	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		EncogProgram program = (EncogProgram)parents[0];
		EncogProgramContext context = program.getContext();
		EncogProgram result = context.cloneProgram(program);
		mutateNode(rnd,result.getRootNode());
		offspring[0] = result;
	}
	
	private void mutateNode(Random rnd, ProgramNode node) {
		if( node.getTemplate()==StandardExtensions.EXTENSION_CONST_SUPPORT) {
			if( rnd.nextDouble()<this.frequency ) {
				ExpressionValue v = node.getData()[0];
				if( v.isFloat() ) {
					double adj = rnd.nextGaussian()*this.sigma;
					node.getData()[0] = new ExpressionValue(v.toFloatValue()+adj);
				}
			}
		}
		
		for(TreeNode n: node.getChildNodes()) {
			ProgramNode childNode = (ProgramNode)n;
			mutateNode(rnd, childNode);
		}
	}
}
