package org.encog.ml.prg.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.CalculateGenomeScore;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;

public abstract class PrgAbstractGenerate implements PrgPopulationGenerator {
	private final EncogProgramContext context;
	private final CalculateGenomeScore scoreFunction;
	private final int maxDepth;
	private final List<ProgramExtensionTemplate> leafNodes = new ArrayList<ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> branchNodes = new ArrayList<ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> allNodes = new ArrayList<ProgramExtensionTemplate>();
	
	public PrgAbstractGenerate(EncogProgramContext theContext, CalculateGenomeScore theScoreFunction, int theMaxDepth) {
		this.context = theContext;
		this.maxDepth = theMaxDepth;
		this.scoreFunction = theScoreFunction;
		
		for(ProgramExtensionTemplate temp : this.context.getFunctions().getOpCodes() ) {
			this.allNodes.add(temp);
			if( temp.getChildNodeCount()==0 ) {
				this.leafNodes.add(temp);
			} else {
				this.branchNodes.add(temp);
			}
		}
	}
	
	public void generate(Random random, Genome genome) {
		EncogProgram program = (EncogProgram)genome;
		program.clear();
		createNode(random, program,0);
	}
	
	protected void createLeafNode(Random random, EncogProgram program) {
		int opCode = random.nextInt(this.leafNodes.size());
		ProgramExtensionTemplate temp = this.leafNodes.get(opCode);
		temp.randomize(random, program, 1.0);
	}
		
	@Override
	public void generate(Random rnd, Population pop) {
		EPLHolder holder = ((PrgPopulation) pop).getHolder();

		pop.getGenomes().clear();
		for (int i = 0; i < pop.getPopulationSize(); i++) {
			EncogProgram prg = new EncogProgram(getContext(),
					new EncogProgramVariables(), holder, i);
			pop.getGenomes().add(prg);

			boolean done = false;
			do {
				prg.clear();
				this.createNode(rnd, prg, 0);
				if (getScoreFunction() != null) {
					double score = getScoreFunction().calculateScore(prg);
					if (!Double.isInfinite(score) && !Double.isNaN(score)) {
						prg.setScore(score);
						done = true;
					}
				} else {
					done = true;
				}
			} while (!done);

			pop.rewrite(prg);
		}
	}

	/**
	 * @return the context
	 */
	public EncogProgramContext getContext() {
		return context;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @return the leafNodes
	 */
	public List<ProgramExtensionTemplate> getLeafNodes() {
		return leafNodes;
	}

	/**
	 * @return the branchNodes
	 */
	public List<ProgramExtensionTemplate> getBranchNodes() {
		return branchNodes;
	}

	/**
	 * @return the allNodes
	 */
	public List<ProgramExtensionTemplate> getAllNodes() {
		return allNodes;
	}

	/**
	 * @return the scoreFunction
	 */
	public CalculateGenomeScore getScoreFunction() {
		return scoreFunction;
	}
	
	
	
}
