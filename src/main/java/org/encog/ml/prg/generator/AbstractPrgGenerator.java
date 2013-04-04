package org.encog.ml.prg.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.EncogError;
import org.encog.mathutil.randomize.factory.BasicRandomFactory;
import org.encog.mathutil.randomize.factory.RandomFactory;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.ea.exception.EARuntimeError;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;
import org.encog.ml.genetic.GeneticError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.prg.train.ZeroEvalScoreFunction;
import org.encog.util.concurrency.MultiThreadable;

public abstract class AbstractPrgGenerator implements PrgGenerator,
		MultiThreadable {
	private CalculateScore score = new ZeroEvalScoreFunction();
	private final EncogProgramContext context;
	private final int maxDepth;
	private double minConst = -10;
	private double maxConst = 10;
	private final boolean hasEnum;
	private int actualThreads;
	private int threads;
	private final Set<String> contents = new HashSet<String>();
	private RandomFactory randomFactory = new BasicRandomFactory();

	public AbstractPrgGenerator(final EncogProgramContext theContext,
			final int theMaxDepth) {
		if (theContext.getFunctions().size() == 0) {
			throw new EncogError("There are no opcodes defined");
		}

		this.context = theContext;
		this.maxDepth = theMaxDepth;
		this.hasEnum = this.context.hasEnum();
	}

	public void addPopulationMember(final PrgPopulation population,
			final EncogProgram prg) {
		synchronized (this) {
			final Species defaultSpecies = population.getSpecies().get(0);
			prg.setSpecies(defaultSpecies);
			defaultSpecies.add(prg);
			this.contents.add(prg.dumpAsCommonExpression());
		}
	}

	public EncogProgram attemptCreateGenome(final Random rnd,
			final Population pop) {
		boolean done = false;
		EncogProgram result = null;
		final int tries = 0;

		while (!done) {
			result = generate(rnd);
			result.setPopulation(pop);

			double s;
			try {
				// tries++;
				s = this.score.calculateScore(result);
			} catch (final EARuntimeError e) {
				s = Double.NaN;
			}

			if (tries > 100) {
				done = true;
			} else if (!Double.isNaN(s) && !Double.isInfinite(s)
					&& !this.contents.contains(result.dumpAsCommonExpression())) {
				done = true;
			}
		}

		return result;
	}

	public ProgramNode createLeafNode(final Random rnd,
			final EncogProgram program) {
		final ProgramExtensionTemplate temp = generateRandomOpcode(rnd,
				this.getContext().getFunctions().getTerminalSet());
		final ProgramNode result = new ProgramNode(program, temp,
				new ProgramNode[] {});
		
		temp.randomize(rnd, result, this.minConst, this.maxConst);
		return result;
	}

	@Override
	public EncogProgram generate(final Random rnd) {
		final EncogProgram program = new EncogProgram(this.context);
		program.setRootNode(createNode(rnd, program, determineMaxDepth(rnd)));
		return program;
	}

	@Override
	public void generate(final Random rnd, final Population pop) {
		// prepare population
		this.contents.clear();
		pop.getSpecies().clear();
		final Species defaultSpecies = pop.createSpecies();

		// determine thread usage
		if (this.score.requireSingleThreaded()) {
			this.actualThreads = 1;
		} else if (this.threads == 0) {
			this.actualThreads = Runtime.getRuntime().availableProcessors();
		} else {
			this.actualThreads = this.threads;
		}

		// start up
		ExecutorService taskExecutor = null;

		if (this.threads == 1) {
			taskExecutor = Executors.newSingleThreadScheduledExecutor();
		} else {
			taskExecutor = Executors.newFixedThreadPool(this.actualThreads);
		}

		for (int i = 0; i < pop.getPopulationSize(); i++) {
			taskExecutor.execute(new GenerateWorker(this, (PrgPopulation) pop));
		}

		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (final InterruptedException e) {
			throw new GeneticError(e);
		}

		// just pick a leader, for the default species.
		defaultSpecies.setLeader(defaultSpecies.getMembers().get(0));
	}

	public ProgramExtensionTemplate generateRandomOpcode(final Random rnd,
			final List<ProgramExtensionTemplate> opcodes) {
		final int maxOpCode = opcodes.size();
		int tries = 10000;

		ProgramExtensionTemplate result = null;

		while (result == null) {
			final int opcode = rnd.nextInt(maxOpCode);
			result = opcodes.get(opcode);
			tries--;
			if (tries < 0) {
				throw new EACompileError(
						"Could not generate an opcode.  Make sure you have valid opcodes defined.");
			}
		}
		return result;
	}

	/**
	 * @return the context
	 */
	public EncogProgramContext getContext() {
		return this.context;
	}

	/**
	 * @return the maxConst
	 */
	public double getMaxConst() {
		return this.maxConst;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return this.maxDepth;
	}

	/**
	 * @return the minConst
	 */
	public double getMinConst() {
		return this.minConst;
	}

	/**
	 * @return the randomFactory
	 */
	public RandomFactory getRandomFactory() {
		return this.randomFactory;
	}

	/**
	 * @return the score
	 */
	public CalculateScore getScore() {
		return this.score;
	}

	/**
	 * @return The desired number of threads.
	 */
	@Override
	public int getThreadCount() {
		return this.threads;
	}

	/**
	 * @return the hasEnum
	 */
	public boolean isHasEnum() {
		return this.hasEnum;
	}

	/**
	 * @param maxConst
	 *            the maxConst to set
	 */
	public void setMaxConst(final double maxConst) {
		this.maxConst = maxConst;
	}

	/**
	 * @param minConst
	 *            the minConst to set
	 */
	public void setMinConst(final double minConst) {
		this.minConst = minConst;
	}

	/**
	 * @param randomFactory
	 *            the randomFactory to set
	 */
	public void setRandomFactory(final RandomFactory randomFactory) {
		this.randomFactory = randomFactory;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(final CalculateScore score) {
		this.score = score;
	}

	/**
	 * @param numThreads
	 *            The desired thread count.
	 */
	@Override
	public void setThreadCount(final int numThreads) {
		this.threads = numThreads;
	}

	public int determineMaxDepth(Random rnd) {
		return this.maxDepth;
	}
}
