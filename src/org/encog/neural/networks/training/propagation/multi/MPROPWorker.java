package org.encog.neural.networks.training.propagation.multi;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.PropagationUtil;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagationMethod;

public class MPROPWorker extends Thread {

	private MultiPropagation owner;
	private BasicNetwork network;
	private long high;
	private long low;
	private ResilientPropagationMethod method;
	private PropagationUtil propagationUtil;
	
	public MPROPWorker(BasicNetwork network, MultiPropagation owner, long low,long high)
	{
		this.network = network;
		this.owner = owner;
		this.low = low;
		this.high = high;
		this.method = new ResilientPropagationMethod(ResilientPropagation.DEFAULT_ZERO_TOLERANCE,ResilientPropagation.DEFAULT_MAX_STEP);
		this.propagationUtil = new PropagationUtil(network, method);
	}
	
	
	
	public void iteration()
	{
		NeuralDataPair pair = owner.createPair();
		for(long l = this.low;l<=this.high;l++)
		{
			 owner.getPair(l,pair);
			 this.propagationUtil.forwardPass(pair.getInput());
			 this.propagationUtil.backwardPass(pair.getIdeal());
		}
		
		this.propagationUtil.getMethod().learn();
	}
	
	public void run() {
		for(;;) {
			iteration();
		}
		
	}
	
	

}
