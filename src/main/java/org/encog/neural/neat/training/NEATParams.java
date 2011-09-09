package org.encog.neural.neat.training;

public class NEATParams {
	/**
	 * The activation mutation rate.
	 */
	public double activationMutationRate = 0.1;

	/**
	 * The likelyhood of adding a link.
	 */
	public double chanceAddLink = 0.07;

	/**
	 * The likelyhood of adding a node.
	 */
	public double chanceAddNode = 0.04;

	/**
	 * THe likelyhood of adding a recurrent link.
	 */
	public double chanceAddRecurrentLink = 0.05;

	/**
	 * The compatibility threshold for a species.
	 */
	public double compatibilityThreshold = 0.26;

	/**
	 * The crossover rate.
	 */
	public double crossoverRate = 0.7;

	/**
	 * The max activation perturbation.
	 */
	public double maxActivationPerturbation = 0.1;

	/**
	 * The maximum number of species.
	 */
	public int maxNumberOfSpecies = 0;

	/**
	 * The maximum number of neurons.
	 */
	public double maxPermittedNeurons = 100;

	/**
	 * The maximum weight perturbation.
	 */
	public double maxWeightPerturbation = 0.5;

	/**
	 * The mutation rate.
	 */
	public double mutationRate = 0.2;

	/**
	 * The number of link add attempts.
	 */
	public int numAddLinkAttempts = 5;

	/**
	 * The number of generations allowed with no improvement.
	 */
	public int numGensAllowedNoImprovement = 15;

	/**
	 * The number of tries to find a looped link.
	 */
	public int numTrysToFindLoopedLink = 5;

	/**
	 * The number of tries to find an old link.
	 */
	public int numTrysToFindOldLink = 5;

	/**
	 * The probability that the weight will be totally replaced.
	 */
	public double probabilityWeightReplaced = 0.1;

}
