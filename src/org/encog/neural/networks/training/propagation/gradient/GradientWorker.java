package org.encog.neural.networks.training.propagation.gradient;

public class GradientWorker implements Runnable {

	/**
	 * The high index point in the training data to be used by this individual
	 * worker.
	 */
	private final int high;

	/**
	 * The low index point in the training data to be used by this individual
	 * worker.
	 */
	private final int low;
	
	private final CalculateGradient owner;
	
	public GradientWorker(CalculateGradient owner, int high, int low) 
	{
		this.owner = owner;
		this.high = high;
		this.low = low;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	

}
