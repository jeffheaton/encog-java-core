package org.encog.ml;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * Defines a training method for a machine learning method.  Most MLMethod 
 * objects need to be trained in some way before they are ready for use.
 *
 */
public interface MLTrain {
	TrainingImplementationType getImplementationType();
	
	/**
	 * @return True if training can progress no further.
	 */
	boolean isTrainingDone();
	
	/**
	 * @return The training data to use.
	 */
	NeuralDataSet getTraining();

	/**
	 * Perform one iteration of training.
	 */
	void iteration();
	
	/**
	 * Get the current error percent from the training.
	 * 
	 * @return The current error.
	 */
	double getError();

	/**
	 * Should be called once training is complete and no more iterations are
	 * needed. Calling iteration again will simply begin the training again, and
	 * require finishTraining to be called once the new training session is
	 * complete.
	 * 
	 * It is particularly important to call finishTraining for multithreaded
	 * training techniques.
	 */
	void finishTraining();
	
	/**
	 * Perform a number of training iterations.
	 * @param count The number of iterations to perform.
	 */
	void iteration(int count);
	
	/**
	 * @return The current training iteration.
	 */
	int getIteration();
	
	public boolean canContinue();
	
	/**
	 * Pause the training to continue later.
	 * 
	 * @return A training continuation object.
	 */
	public TrainingContinuation pause();

	/**
	 * Resume training.
	 * 
	 * @param state
	 *            The training continuation object to use to continue.
	 */
	public void resume(final TrainingContinuation state);




}
