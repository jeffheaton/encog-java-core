/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.prg.train.ZeroEvalScoreFunction;
import org.encog.util.concurrency.MultiThreadable;

/**
 * The abstract base for Full and Grow program generation.
 */
public abstract class AbstractPrgGenerator implements PrgGenerator,
		MultiThreadable {
	/**
	 * An optional scoring function.
	 */
	private CalculateScore score = new ZeroEvalScoreFunction();

	/**
	 * The program context to use.
	 */
	private final EncogProgramContext context;

	/**
	 * The maximum depth to generate to.
	 */
	private final int maxDepth;

	/**
	 * The minimum const to generate.
	 */
	private double minConst = -10;

	/**
	 * The maximum const to generate.
	 */
	private double maxConst = 10;

	/**
	 * True, if the program has enums.
	 */
	private final boolean hasEnum;

	/**
	 * The actual number of threads to use.
	 */
	private int actualThreads;

	/**
	 * The number of threads to use.
	 */
	private int threads;

	/**
	 * The contents of this population, stored in rendered form. This prevents
	 * duplicates.
	 */
	private final Set<String> contents = new HashSet<String>();

	/**
	 * A random number generator factory.
	 */
	private RandomFactory randomFactory = new BasicRandomFactory();

	/**
	 * The maximum number of allowed generation errors.
	 */
	private int maxGenerationErrors = 500;

	/**
	 * Construct the generator.
	 * 
	 * @param theContext
	 *            The context that is to be used for generation.
	 * @param theMaxDepth
	 *            The maximum depth to generate to.
	 */
	public AbstractPrgGenerator(final EncogProgramContext theContext,
			final int theMaxDepth) {
		if (theContext.getFunctions().size() == 0) {
			throw new EncogError("There are no opcodes defined");
		}

		this.context = theContext;
		this.maxDepth = theMaxDepth;
		this.hasEnum = this.context.hasEnum();
	}

	/**
	 * Add a population member from one of the threads.
	 * 
	 * @param population
	 *            The population to add to.
	 * @param prg
	 *            The program to add.
	 */
	public void addPopulationMember(final PrgPopulation population,
			final EncogProgram prg) {
		synchronized (this) {
			final Species defaultSpecies = population.getSpecies().get(0);
			prg.setSpecies(defaultSpecies);
			defaultSpecies.add(prg);
			this.contents.add(prg.dumpAsCommonExpression());
		}
	}

	/**
	 * Attempt to create a genome. Cycle the specified number of times if an
	 * error occurs.
	 * 
	 * @param rnd The random number generator.
	 * @param pop The population.
	 * @return The generated genome.
	 */
	public EncogProgram attemptCreateGenome(final Random rnd,
			final Population pop) {
		boolean done = false;
		EncogProgram result = null;
		int tries = this.maxGenerationErrors;

		while (!done) {
			result = generate(rnd);
			result.setPopulation(pop);

			double s;
			try {
				tries--;
				s = this.score.calculateScore(result);
			} catch (final EARuntimeError e) {
				s = Double.NaN;
			}

			if (tries < 0) {
				throw new EncogError("Could not generate a valid genome after "
						+ this.maxGenerationErrors + " tries.");
			} else if (!Double.isNaN(s) && !Double.isInfinite(s)
					&& !this.contents.contains(result.dumpAsCommonExpression())) {
				done = true;
			}
		}

		return result;
	}

	/**
	 * Create a random note according to the specified paramaters.
	 * @param rnd A random number generator.
	 * @param program The program to generate for.
	 * @param depthRemaining The depth remaining to generate.
	 * @param types The types to generate.
	 * @param includeTerminal Should we include terminal nodes.
	 * @param includeFunction Should we include function nodes.
	 * @return The generated program node.
	 */
	public ProgramNode createRandomNode(final Random rnd,
			final EncogProgram program, final int depthRemaining,
			final List<ValueType> types, final boolean includeTerminal,
			final boolean includeFunction) {

		// if we've hit the max depth, then create a terminal nodes, so it stops
		// here
		if (depthRemaining == 0) {
			return createTerminalNode(rnd, program, types);
		}

		// choose which opcode set we might create the node from
		final List<ProgramExtensionTemplate> opcodeSet = getContext()
				.getFunctions().findOpcodes(types, getContext(),
						includeTerminal, includeFunction);

		// choose a random opcode
		final ProgramExtensionTemplate temp = generateRandomOpcode(rnd,
				opcodeSet);
		if (temp == null) {
			throw new EACompileError(
					"Trying to generate a random opcode when no opcodes exist.");
		}

		// create the child nodes
		final int childNodeCount = temp.getChildNodeCount();
		final ProgramNode[] children = new ProgramNode[childNodeCount];

		if (temp.getNodeType().isOperator() && children.length >= 2) {

			// for an operator of size 2 or greater make sure all children are
			// the same time
			final List<ValueType> childTypes = temp.getParams().get(0)
					.determineArgumentTypes(types);
			final ValueType selectedType = childTypes.get(rnd
					.nextInt(childTypes.size()));
			childTypes.clear();
			childTypes.add(selectedType);

			// now create the children of a common type
			for (int i = 0; i < children.length; i++) {
				children[i] = createNode(rnd, program, depthRemaining - 1,
						childTypes);
			}
		} else {

			// otherwise, let the children have their own types
			for (int i = 0; i < children.length; i++) {
				final List<ValueType> childTypes = temp.getParams().get(i)
						.determineArgumentTypes(types);
				children[i] = createNode(rnd, program, depthRemaining - 1,
						childTypes);
			}
		}

		// now actually create the node
		final ProgramNode result = new ProgramNode(program, temp, children);
		temp.randomize(rnd, types, result, getMinConst(), getMaxConst());
		return result;
	}

	/**
	 * Create a terminal node.
	 * @param rnd A random number generator.
	 * @param program The program to generate for.
	 * @param types The types that we might generate.
	 * @return The terminal program node.
	 */
	public ProgramNode createTerminalNode(final Random rnd,
			final EncogProgram program, final List<ValueType> types) {
		final ProgramExtensionTemplate temp = generateRandomOpcode(
				rnd,
				getContext().getFunctions().findOpcodes(types, this.context,
						true, false));
		if (temp == null) {
			throw new EACompileError("No opcodes exist for the type: "
					+ types.toString());
		}
		final ProgramNode result = new ProgramNode(program, temp,
				new ProgramNode[] {});

		temp.randomize(rnd, types, result, this.minConst, this.maxConst);
		return result;
	}

	public int determineMaxDepth(final Random rnd) {
		return this.maxDepth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EncogProgram generate(final Random rnd) {
		final EncogProgram program = new EncogProgram(this.context);
		final List<ValueType> types = new ArrayList<ValueType>();
		types.add(this.context.getResult().getVariableType());
		program.setRootNode(createNode(rnd, program, determineMaxDepth(rnd),
				types));
		return program;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Generate a random opcode.
	 * @param rnd Random number generator.
	 * @param opcodes The opcodes to choose from.
	 * @return The selected opcode.
	 */
	public ProgramExtensionTemplate generateRandomOpcode(final Random rnd,
			final List<ProgramExtensionTemplate> opcodes) {
		final int maxOpCode = opcodes.size();

		if (maxOpCode == 0) {
			return null;
		}

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
	 * @return the maxGenerationErrors
	 */
	@Override
	public int getMaxGenerationErrors() {
		return this.maxGenerationErrors;
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
	 * @param maxGenerationErrors
	 *            the maxGenerationErrors to set
	 */
	@Override
	public void setMaxGenerationErrors(final int maxGenerationErrors) {
		this.maxGenerationErrors = maxGenerationErrors;
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

}
