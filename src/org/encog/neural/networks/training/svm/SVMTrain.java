package org.encog.neural.networks.training.svm;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.neural.data.Indexable;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.svm.EncodeSVMProblem;
import org.encog.neural.networks.svm.SVMNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SVMTrain {
	
	private static final transient Logger LOGGER = LoggerFactory
	.getLogger(SVMTrain.class);
	
	private SVMNetwork network;
	private Indexable indexable;
	private svm_problem[] problem;
	
	public SVMTrain(BasicNetwork network, Indexable indexable)
	{
		this.network = (SVMNetwork)network;
		this.indexable = indexable;
		
		for(int i=0;i<this.network.getOutputCount();i++ )
		{
			this.problem = new svm_problem[this.network.getOutputCount()];
			this.problem[i] = EncodeSVMProblem.encode(indexable, i);
		}
	}
	
	public void iteration()
	{		
		for(int i=0;i<network.getOutputCount();i++ )
		{
			network.getModels()[i] = svm.svm_train(problem[i], network.getParams());
		}
	}

	public SVMNetwork getNetwork() {
		return network;
	}

	public Indexable getIndexable() {
		return indexable;
	}

	public svm_problem[] getProblem() {
		return problem;
	}
	
	
}
