package org.encog.ml.prg.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;

public abstract class PrgAbstractGenerate implements PrgPopulationGenerator {
	private final EncogProgramContext context;
	private final CalculateScore scoreFunction;
	private final int maxDepth;
	private final List<ProgramExtensionTemplate> leafNodes = new ArrayList<ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> branchNodes = new ArrayList<ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> allNodes = new ArrayList<ProgramExtensionTemplate>();
	private boolean requireVariable = true;
	private boolean requireUnique = true;

	public PrgAbstractGenerate(final EncogProgramContext theContext,
			final CalculateScore theScoreFunction, final int theMaxDepth) {
		this.context = theContext;
		this.maxDepth = theMaxDepth;
		this.scoreFunction = theScoreFunction;

		for (final ProgramExtensionTemplate temp : this.context.getFunctions()
				.getOpCodes()) {
			this.allNodes.add(temp);
			if (temp.getChildNodeCount() == 0) {
				this.leafNodes.add(temp);
			} else {
				this.branchNodes.add(temp);
			}
		}
	}

	protected void createLeafNode(final Random random,
			final EncogProgram program) {
		final int opCode = random.nextInt(this.leafNodes.size());
		final ProgramExtensionTemplate temp = this.leafNodes.get(opCode);
		temp.randomize(random, program, 1.0);
	}

	@Override
	public void generate(final Random random, final Genome genome) {
		final EncogProgram program = (EncogProgram) genome;
		program.clear();
		createNode(random, program, 0, getMaxDepth());
	}

	@Override
	public void generate(final Random rnd, final Population pop) {
		final Set<String> populationContents = new HashSet<String>();
		final EPLHolder holder = ((PrgPopulation) pop).getHolder();

		pop.getSpecies().clear();
		final Species defaultSpecies = pop.createSpecies();

		for (int i = 0; i < pop.getPopulationSize(); i++) {
			final EncogProgram prg = new EncogProgram(getContext(),
					new EncogProgramVariables(), holder, i);
			defaultSpecies.add(prg);
			generateGenome(rnd, pop, prg, populationContents);
		}
	}

	private void generateGenome(final Random rnd, final Population pop,
			final EncogProgram prg, final Set<String> populationContents) {
		boolean done;
		String key = "";
		int maxTries = 100000;

		do {
			maxTries--;
			if (maxTries < 0) {
				throw new GeneticError(
						"Endless loop generating population.  You are likely creating a population size too small and requring uniqueness or variables.");
			}
			prg.clear();
			createNode(rnd, prg, 0, getMaxDepth());
			pop.rewrite(prg);

			done = true;

			// is the program unique?
			if (this.requireUnique) {
				key = prg.toBase64();
				if (populationContents.contains(key)) {
					done = false;
				}
			}

			// does the program contain a variable?
			if (done && !prg.hasVariable()) {
				done = false;
			}

			// does program produce a valid score?
			if (done && getScoreFunction() != null) {
				final double score = getScoreFunction().calculateScore(prg);

				if (Double.isInfinite(score) || Double.isNaN(score)) {
					done = false;
				} else {
					prg.setScore(score);
				}
			}

		} while (!done);

		if (this.requireUnique) {
			populationContents.add(key);
		}
	}

	/**
	 * @return the allNodes
	 */
	public List<ProgramExtensionTemplate> getAllNodes() {
		return this.allNodes;
	}

	/**
	 * @return the branchNodes
	 */
	public List<ProgramExtensionTemplate> getBranchNodes() {
		return this.branchNodes;
	}

	/**
	 * @return the context
	 */
	public EncogProgramContext getContext() {
		return this.context;
	}

	/**
	 * @return the leafNodes
	 */
	public List<ProgramExtensionTemplate> getLeafNodes() {
		return this.leafNodes;
	}

	/**
	 * @return the maxDepth
	 */
	@Override
	public int getMaxDepth() {
		return this.maxDepth;
	}

	/**
	 * @return the scoreFunction
	 */
	public CalculateScore getScoreFunction() {
		return this.scoreFunction;
	}

	/**
	 * @return the requireUnique
	 */
	public boolean isRequireUnique() {
		return this.requireUnique;
	}

	/**
	 * @return the requireVariable
	 */
	public boolean isRequireVariable() {
		return this.requireVariable;
	}

	/**
	 * @param requireUnique
	 *            the requireUnique to set
	 */
	public void setRequireUnique(final boolean requireUnique) {
		this.requireUnique = requireUnique;
	}

	/**
	 * @param requireVariable
	 *            the requireVariable to set
	 */
	public void setRequireVariable(final boolean requireVariable) {
		this.requireVariable = requireVariable;
	}

}
