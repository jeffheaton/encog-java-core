package org.encog.ml.prg.opp;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.generator.PrgGenerator;
import org.encog.ml.prg.generator.PrgGrowGenerator;

public class SubtreeMutation implements EvolutionaryOperator {

	private PrgGenerator generator;
	private final int maxDepth;
	
	public SubtreeMutation(EncogProgramContext theContext, int theMaxDepth) {
		this.generator = new PrgGrowGenerator(theContext, theMaxDepth);
		this.maxDepth = theMaxDepth;
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
	
	private ValueType determineValueType(ProgramNode node) {
		// total hack, will come up with something better soon.
		return node.getTemplate().getReturnValue().getPossibleTypes().iterator().next();
	}

	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		EncogProgram program = (EncogProgram)parents[0];
		EncogProgramContext context = program.getContext();
		EncogProgram result = context.cloneProgram(program);
		
		int index = rnd.nextInt(result.getRootNode().size());
		ProgramNode node = result.findNode(index);
		ValueType resultType = determineValueType(node);
		ProgramNode newInsert = this.generator.createNode(rnd, result, this.maxDepth, resultType);
		result.replaceNode(node,newInsert);
		
		offspring[0] = result;
	}

	public PrgGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(PrgGenerator generator) {
		this.generator = generator;
	}
	
	
}
