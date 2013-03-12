package org.encog.ml.prg.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.generator.PrgGrowGenerator;

public class SubtreeMutation implements EvolutionaryOperator {

	private PrgGrowGenerator rnd;
	
	public SubtreeMutation(EncogProgramContext theContext, int theMaxDepth) {
		this.rnd = new PrgGrowGenerator(theContext, theMaxDepth);
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
		
		int index = rnd.nextInt(result.getRootNode().size());
		ProgramNode node = result.findNode(index);
		ProgramNode newInsert = this.rnd.generate(rnd, result);
		result.replaceNode(node,newInsert);
		
		offspring[0] = result;
	}
}
