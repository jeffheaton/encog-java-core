package org.encog.ml.factory;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.method.FeedforwardFactory;
import org.encog.ml.factory.method.RBFNetworkFactory;
import org.encog.ml.factory.method.SVMFactory;

public class MLMethodFactory {
	
	public static final String TYPE_FEEDFORWARD = "feedforward";
	public static final String TYPE_RBFNETWORK = "rbfnetwork";
	public static final String TYPE_SVM = "svm";
	
	private final FeedforwardFactory feedforwardFactory = new FeedforwardFactory();
	private final SVMFactory svmFactory = new SVMFactory();
	private final RBFNetworkFactory rbfFactory = new RBFNetworkFactory();
	
	public MLMethod create(String methodType, String architecture, int input, int output)
	{
		if( TYPE_FEEDFORWARD.equals(methodType) ) {
			return feedforwardFactory.create(architecture,input,output);
		} else if( TYPE_RBFNETWORK.equals(methodType) ) {
			return rbfFactory.create(architecture,input,output);
		} else if( TYPE_SVM.equals(methodType) ) {
			return svmFactory.create(architecture,input,output);
		}
		throw new EncogError("Unknown method type: " + methodType);
	}
	
	
}
