package org.encog.ml.genetic;

import org.encog.ml.genetic.genome.Genome;
import org.encog.util.concurrency.EngineConcurrency;
import org.encog.util.concurrency.TaskGroup;

public class BasicGeneticAlgorithm extends GeneticAlgorithm {

	/**
	 * Is this the first iteration.
	 */
	private boolean first = true;

	/**
	 * Modify the weight matrix and bias values based on the last call to
	 * calcError.
	 * 
	 * @throws NeuralNetworkException
	 */
	@Override
	public final void iteration() {

		if (this.first) {
			getPopulation().claim(this);
			this.first = false;
		}

		final int countToMate = (int) (getPopulation().getPopulationSize() * getPercentToMate());
		final int offspringCount = countToMate * 2;
		int offspringIndex = getPopulation().getPopulationSize()
				- offspringCount;
		final int matingPopulationSize = (int) (getPopulation()
				.getPopulationSize() * getMatingPopulation());

		final TaskGroup group = EngineConcurrency.getInstance()
				.createTaskGroup();

		// mate and form the next generation
		for (int i = 0; i < countToMate; i++) {
			final Genome mother = getPopulation().getGenomes().get(i);
			final int fatherInt = (int) (Math.random() * matingPopulationSize);
			final Genome father = getPopulation().getGenomes().get(fatherInt);
			final Genome child1 = getPopulation().getGenomes().get(
					offspringIndex);
			final Genome child2 = getPopulation().getGenomes().get(
					offspringIndex + 1);

			final MateWorker worker = new MateWorker(mother, father, child1,
					child2);

			EngineConcurrency.getInstance().processTask(worker, group);

			offspringIndex += 2;
		}

		group.waitForComplete();

		// sort the next generation
		getPopulation().sort();
	}

}
