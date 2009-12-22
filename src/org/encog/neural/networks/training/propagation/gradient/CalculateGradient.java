/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.networks.training.propagation.gradient;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingError;

public class CalculateGradient {
	
	/**
	 * How many threads are being used to train the network.
	 */
	private int threadCount;
	
	
	/**
	 * The training set to be used. This must be an indexable training set to
	 * that it can be divided by the threads.
	 */
	private Indexable indexed;
	
	private NeuralDataSet training;
	
	

	/**
	 * The workers to be used, one for each thread.
	 */
	private GradientWorker[] workers;
	
	private Thread[] threads;
	
	private BasicNetwork network;
	
	private double[] weights;	
	private double[] gradients;
	private double error;
	private int count;
	
	public CalculateGradient(BasicNetwork network, final NeuralDataSet training) {
		this(network,training,1);
	}
	
	public CalculateGradient(final BasicNetwork network, final NeuralDataSet training, final int threads) {				
		this.training = training;
		this.network = network;
		
		if( threads!=0 || !(this.training instanceof Indexable) ) {
			this.network = network;	
			this.threadCount = threads;			
		}
		else {
			this.indexed = (Indexable)this.training;
			int num = Runtime.getRuntime().availableProcessors();

			// if there is more than one processor, use processor count +1
			if (num != 1) {
				num++;
			}
			// if there is a single processor, just use one thread

			// Now see how big the training sets are going to be.
			// We want at least 100 training elements in each.
			// This method will likely be further "tuned" in future versions.

			final long recordCount = this.indexed.getRecordCount();
			final long workPerThread = recordCount / num;

			if (workPerThread < 100) {
				num = Math.max(1, (int) (recordCount / 100));
			}
			
			this.threadCount = num;
		}
	}
	
	public void calculate(double[] weights)
	{
		this.weights = weights;
		this.gradients = new double[this.weights.length];
		
		if( this.threadCount==1 ) {
			createWorkersSingleThreaded(training);
			runWorkersSingleThreaded();
		}
		else {
			if( !(training instanceof Indexable) ) {
				throw new TrainingError("Must use indexable training set for multithreaded.");
			}
			
			createWorkersMultiThreaded((Indexable)training);
			runWorkersMultiThreaded();
		}
				
		aggregate();
		determineError();
		
	}
	
	private void runWorkersSingleThreaded()
	{
		this.workers[0].run();
	}
	
	private void runWorkersMultiThreaded()
	{
		// start the workers
		for(int i=0;i<this.threadCount;i++) {
			this.threads[i].start();
		}
		
		// wait for all workers to finish
		for(int i=0;i<this.threadCount;i++) {
			try {
				this.threads[i].join();
			} catch (InterruptedException e) {
			}
		}
	}
	
	private void createWorkersMultiThreaded(Indexable training)
	{		
		this.indexed = training;
		// setup the workers
		this.workers = new GradientWorker[threadCount];
		this.threads = new Thread[threadCount];

		final int size = (int)this.indexed.getRecordCount();
		final int sizePerThread = size / this.threadCount;

		// create the workers
		for (int i = 0; i < this.threadCount; i++) {
			final int low = i * sizePerThread;
			int high;

			// if this is the last record, then high to be the last item
			// in the training set.
			if (i == (this.threadCount - 1)) {
				high = size - 1;
			} else {
				high = ((i + 1) * sizePerThread) - 1;
			}

			final Indexable trainingClone = this.indexed.openAdditional();
			this.workers[i] = new GradientWorker(this, trainingClone,
					 low, high);
			this.threads[i] = new Thread(this.workers[i]);
		}
	}
	
	private void createWorkersSingleThreaded(NeuralDataSet training)
	{		
		// setup the workers
		this.workers = new GradientWorker[threadCount];
		this.workers[0] = new GradientWorker(this, training,
				 0, 0);
	}
	
	private void aggregate()
	{
		for(int i=0;i<this.gradients.length;i++) {
			this.gradients[i] = 0;
			for(int j=0;j<this.threadCount;j++) {
				this.gradients[i]+=this.workers[j].getErrors()[i];
			}
		}
		
		this.count = 0;
		for(int i=0;i<this.threadCount;i++) {
			this.count+=this.workers[i].getCount();
		}
	}
	
	private void determineError() {
		double totalError = 0;
		for(int i=0;i<this.threadCount;i++) {
			totalError+=this.workers[i].getError();
		}
		this.error = (totalError/this.threadCount);
	}
	

	public double[] getGradients() {
		return this.gradients;
	}

	public double getError() {
		return this.error;
	}

	public BasicNetwork getNetwork() {
		return network;
	}	
	
	public double[] getWeights()
	{
		return this.weights;
	}
	
	/**
	 * Create a new neural data pair object of the correct size for the neural
	 * network that is being trained. This object will be passed to the getPair
	 * method to allow the neural data pair objects to be copied to it.
	 * 
	 * @return A new neural data pair object.
	 */
	public NeuralDataPair createPair() {
		NeuralDataPair result;

		final int idealSize = this.training.getIdealSize();
		final int inputSize = this.training.getInputSize();

		if (idealSize > 0) {
			result = new BasicNeuralDataPair(new BasicNeuralData(inputSize),
					new BasicNeuralData(idealSize));
		} else {
			result = new BasicNeuralDataPair(new BasicNeuralData(inputSize));
		}

		return result;
	}
	
	public int getCount()
	{
		return this.count;
	}
}
