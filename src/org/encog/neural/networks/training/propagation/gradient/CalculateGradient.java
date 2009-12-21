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
	private double[] errors;
	private double error;
	private int count;
	
	public CalculateGradient(BasicNetwork network) {
		this(network,1);
	}
	
	public CalculateGradient(final BasicNetwork network, final int threads) {				
		
		if( threads!=0 ) {
			this.network = network;	
			this.threadCount = threads;			
		}
		else {
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
			final long workPerThread = recordCount / threads;

			if (workPerThread < 100) {
				num = Math.max(1, (int) (recordCount / 100));
			}
			
			this.threadCount = num;
		}
	}
	
	public void calculate(NeuralDataSet training, double[] weights)
	{
		this.training = training;
		this.weights = weights;
		this.errors = new double[this.weights.length];
		
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
		for(int i=0;i<this.errors.length;i++) {
			this.errors[i] = 0;
			for(int j=0;j<this.threadCount;j++) {
				this.errors[i]+=this.workers[j].getErrors()[i];
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
	

	public double[] getErrors() {
		return this.errors;
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
