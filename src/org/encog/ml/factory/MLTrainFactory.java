package org.encog.ml.factory;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.train.BackPropFactory;
import org.encog.ml.factory.train.LMAFactory;
import org.encog.ml.factory.train.RPROPFactory;
import org.encog.ml.factory.train.SCGFactory;
import org.encog.ml.factory.train.SVMFactory;
import org.encog.ml.factory.train.SVMSearchFactory;
import org.encog.neural.data.NeuralDataSet;

public class MLTrainFactory {
	public static final String TYPE_RPROP = "rprop";
	public static final String TYPE_BACKPROP = "backprop";
	public static final String TYPE_SCG = "scg";
	public static final String TYPE_LMA = "lma";
	public static final String TYPE_SVM = "svm-train";
	public static final String TYPE_SVM_SEARCH = "svm-search";
	
	private final BackPropFactory backpropFactory = new BackPropFactory();
	private final LMAFactory lmaFactory = new LMAFactory();
	private final RPROPFactory rpropFactory = new RPROPFactory();
	private final SVMFactory svmFactory = new SVMFactory();
	private final SVMSearchFactory svmSearchFactory = new SVMSearchFactory();
	private final SCGFactory scgFactory = new SCGFactory();
	
	public MLTrain create(MLMethod method, NeuralDataSet training, String type, String args) {
		if( TYPE_RPROP.equalsIgnoreCase(type) ) {
			return this.rpropFactory.create(method, training, args);
		} else if( TYPE_BACKPROP.equalsIgnoreCase(type) ) {
			return this.backpropFactory.create(method, training, args);
		} else if( TYPE_SCG.equalsIgnoreCase(type) ) {
			return this.scgFactory.create(method, training, args);
		} else if( TYPE_LMA.equalsIgnoreCase(type) ) {
			return this.lmaFactory.create(method, training, args);
		} else if( TYPE_SVM.equalsIgnoreCase(type) ) {
			return this.svmFactory.create(method, training, args);
		} else if( TYPE_SVM_SEARCH.equalsIgnoreCase(type) ) {
			return this.svmSearchFactory.create(method, training, args);
		} 
		else {
			throw new EncogError("Unknown training type: " + type);
		}
	}
}
