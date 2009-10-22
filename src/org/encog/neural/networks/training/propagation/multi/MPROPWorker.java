package org.encog.neural.networks.training.propagation.multi;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.PropagationUtil;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagationMethod;
import org.encog.util.ErrorCalculation;

public class MPROPWorker extends Thread {

	private MultiPropagation owner;
	private BasicNetwork network;
	private long high;
	private long low;
	private ResilientPropagationMethod method;
	private PropagationUtil propagationUtil;
	private final ErrorCalculation errorCalculation = new ErrorCalculation();
	private double error;
	private boolean done;
	
	public MPROPWorker(BasicNetwork network, MultiPropagation owner, long low,long high)
	{
		this.network = network;
		this.owner = owner;
		this.low = low;
		this.high = high;
		this.method = new ResilientPropagationMethod(ResilientPropagation.DEFAULT_ZERO_TOLERANCE,ResilientPropagation.DEFAULT_MAX_STEP,ResilientPropagation.DEFAULT_INITIAL_UPDATE);
		this.propagationUtil = new PropagationUtil(network, method);
		this.errorCalculation.reset();
	}
	
	public void iteration()
	{	
		errorCalculation.reset();
		NeuralDataPair pair = owner.createPair();
		for(long l = this.low;l<=this.high;l++)
		{			
			 owner.getPair(l,pair);
			 NeuralData actual = this.propagationUtil.forwardPass(pair.getInput());
			 this.propagationUtil.backwardPass(pair.getIdeal());
			 errorCalculation.updateError(actual, pair.getIdeal());
		}
		this.setError(errorCalculation.calculateRMS());
		this.propagationUtil.getMethod().learn();
		this.owner.updateNetwork(this);
		System.out.println("Iteration " + this.getId());
	}
	
	public void run() {
		
		this.done = false;
		
		while(!done) {
			iteration();
		}				
	}

	public synchronized double getError() {
		return error;
	}

	public synchronized void setError(double error) {
		this.error = error;
	}

	public BasicNetwork getNetwork() {
		return network;
	}

	public void requestShutdown() {
		this.done = true;
	}
	
}
