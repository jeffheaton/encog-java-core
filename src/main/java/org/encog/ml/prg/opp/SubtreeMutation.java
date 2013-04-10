package org.encog.ml.prg.opp;

import java.util.ArrayList;
import java.util.List;
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
	 * @return Returns the number of offspring produced. In this case, one.
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * @return Returns the number of parents needed. In this case, one.
	 */
	@Override
	public int parentsNeeded() {
		return 1;
	}

	private List<ValueType> determineValueType(ProgramNode node) {
		List<ValueType> result = new ArrayList<ValueType>();
		result.addAll(node.getTemplate().getReturnValue().getPossibleTypes());
		return result;
	}

	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		EncogProgram program = (EncogProgram) parents[0];
		EncogProgramContext context = program.getContext();
		EncogProgram result = context.cloneProgram(program);

		List<ValueType> types = new ArrayList<ValueType>();
		types.add(context.getResult().getVariableType());
		int[] globalIndex = new int[1];
		globalIndex[0] = rnd.nextInt(result.getRootNode().size());
		findNode(rnd, result, result.getRootNode(), types, globalIndex);

		offspring[0] = result;
	}

	private void findNode(Random rnd, EncogProgram result,
			ProgramNode parentNode, List<ValueType> types, int[] globalIndex) {
		if (globalIndex[0] == 0) {
			globalIndex[0]--;

			ProgramNode newInsert = this.generator.createNode(rnd, result,
					this.maxDepth, types);
			result.replaceNode(parentNode, newInsert);
		} else {
			globalIndex[0]--;
			for(int i=0; i<parentNode.getTemplate().getChildNodeCount();i++) {
				ProgramNode childNode = parentNode.getChildNode(i);
				List<ValueType> childTypes = parentNode.getTemplate().getParams().get(i).determineArgumentTypes(types);
				findNode(rnd,result,childNode,childTypes,globalIndex);
			}
		}
	}

	public PrgGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(PrgGenerator generator) {
		this.generator = generator;
	}

}
