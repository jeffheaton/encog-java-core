package org.encog.neural.networks.training.propagation.gradient;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

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
	
	private final BasicNetwork network;
	private NeuralDataSet training;
	private GradientUtil gradient;
	
	public GradientWorker(CalculateGradient owner, NeuralDataSet training,int low, int high) 
	{
		this.owner = owner;
		this.high = high;
		this.low = low;
		this.network = (BasicNetwork)owner.getNetwork().clone();
		this.training = training;
		this.gradient = new GradientUtil(network);
	}
	
	public void run() {
		double[] weights = this.owner.getWeights();
		NeuralDataPair pair = owner.createPair();
		
		if( training instanceof Indexable && this.high!=this.low )
		{
			Indexable t2 = (Indexable) training;
			gradient.reset(weights);
			for(int i=low;i<=high;i++) {	
				t2.getRecord(i, pair);
				gradient.calculate(pair.getInput(), pair.getIdeal());
			}
		}
		else
			gradient.calculate(training, weights);
	}
	
	public double[] getErrors() {
		return this.gradient.getErrors();
	}
	
	public int getCount() {
		return this.gradient.getCount();
	}
	
	public double getError()
	{
		return this.gradient.getError();
	}
}
