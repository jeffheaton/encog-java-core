package org.encog.neural.networks.training.svm;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.neural.data.Indexable;
import org.encog.neural.networks.SVMNetwork;

public class SVMTrain {
	
	private SVMNetwork network;
	private Indexable indexable;
	private svm_problem[] problem;
	
	public SVMTrain(SVMNetwork network, Indexable indexable)
	{
		this.network = network;
		this.indexable = indexable;
		
		for(int i=0;i<network.getOutputCount();i++ )
		{
			this.problem = new svm_problem[network.getOutputCount()];
			this.problem[i] = EncodeSVMProblem.encode(indexable, i);
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
