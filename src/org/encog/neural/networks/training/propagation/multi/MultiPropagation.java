package org.encog.neural.networks.training.propagation.multi;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class MultiPropagation extends BasicTraining {
	
	private int threadCount;
	private MPROPWorker[] workers;
	private Indexable training;
	private BasicNetwork network;
	private boolean threadsStarted;
	private ResilientPropagation fallback;
	
	public MultiPropagation(BasicNetwork network, NeuralDataSet training, int threadCount)
	{
		if( !(training instanceof Indexable) ) {
			throw new TrainingError("Must use a training set that implements Indexable for multipropagation.");
		}
		
		this.threadCount = threadCount;
		this.training = (Indexable)training;
		this.network = network;
		this.threadsStarted = false;
		
		this.workers = new MPROPWorker[threadCount];
		
		long size = this.training.getRecordCount();
		long sizePerThread = size/this.threadCount;
		
		// should we fall back to RPROP?
		if( threadCount==1 || sizePerThread<1000)
		{
			this.fallback = new ResilientPropagation(network, training);
			return;
		}
		
		for(int i=0;i<this.threadCount;i++)
		{
			long low = i*sizePerThread;
			long high;
			
			// if this is the last record, then high to be the last item
			// in the training set.
			if( i==(this.threadCount-1) )
				high = size-1;
			else
				high = ((i+1)*sizePerThread)-1;
			
			BasicNetwork networkClone = (BasicNetwork)this.network.clone();
			this.workers[i] = new MPROPWorker(networkClone,this,low,high);			
		}
	}
	
	public MultiPropagation(BasicNetwork network, NeuralDataSet training) {
		this(network,training,3);
	}

	public void startThreads()
	{
		this.threadsStarted = true;
		for(int i=0;i<this.workers.length;i++)
		{
			this.workers[i].start();
		}
	}

	
	public BasicNetwork getNetwork() {
		if( this.fallback!=null ) {
			return this.fallback.getNetwork();
		}
		else
			return this.network;
	}

	
	public void iteration() {
		
		if( this.fallback!=null)
		{
			this.fallback.iteration();
			this.setError(this.fallback.getError());
			return;
		}
		
		if( !this.threadsStarted ) {
			this.startThreads();
		}
		
	}


	public void getPair(long l, NeuralDataPair pair) {
		this.training.getRecord(l, pair);
	}
	
	public NeuralDataPair createPair()
	{
		NeuralDataPair result;
		
		int idealSize = this.training.getIdealSize();
		int inputSize = this.training.getInputSize();

		if (idealSize > 0) {
			result = new BasicNeuralDataPair(new BasicNeuralData(
					(int)inputSize),
					new BasicNeuralData(
							(int)idealSize));
		} else {
			result = new BasicNeuralDataPair(new BasicNeuralData(
					(int)inputSize));
		}

		return result;
	}

}
