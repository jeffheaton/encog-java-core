package org.encog.ml.factory;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.train.BackPropFactory;
import org.encog.ml.factory.train.ClusterSOMFactory;
import org.encog.ml.factory.train.LMAFactory;
import org.encog.ml.factory.train.NeighborhoodSOMFactory;
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
	public static final String TYPE_SOM_NEIGHBORHOOD = "som-neighborhood";
	public static final String TYPE_SOM_CLUSTER = "som-cluster";
	
	public static final String PROPERTY_LEARNING_RATE = "LR";
	public static final String PROPERTY_LEARNING_MOMENTUM = "MOM";
	public static final String PROPERTY_INITIAL_UPDATE = "INIT_UPDATE";
	public static final String PROPERTY_MAX_STEP = "MAX_STEP";
	public static final String PROPERTY_BAYESIAN_REGULARIZATION = "BAYES_REG";
	public static final String PROPERTY_GAMMA = "GAMMA";
	public static final String PROPERTY_C = "C";
	public static final String PROPERTY_PROPERTY_NEIGHBORHOOD = "NEIGHBORHOOD";	
	public static final String PROPERTY_ITERATIONS = "ITERATIONS";
	public static final String PROPERTY_START_LEARNING_RATE = "START_LR";
	public static final String PROPERTY_END_LEARNING_RATE = "END_LR";
	public static final String PROPERTY_START_RADIUS = "START_RADIUS";
	public static final String PROPERTY_END_RADIUS = "END_RADIUS";	
	public static final String PROPERTY_NEIGHBORHOOD = "NEIGHBORHOOD";
	public static final String PROPERTY_RBF_TYPE = "RBF_TYPE";
	public static final String PROPERTY_DIMENSIONS = "DIM";
	
	private final BackPropFactory backpropFactory = new BackPropFactory();
	private final LMAFactory lmaFactory = new LMAFactory();
	private final RPROPFactory rpropFactory = new RPROPFactory();
	private final SVMFactory svmFactory = new SVMFactory();
	private final SVMSearchFactory svmSearchFactory = new SVMSearchFactory();
	private final SCGFactory scgFactory = new SCGFactory();
	private final NeighborhoodSOMFactory neighborhoodFactory = new NeighborhoodSOMFactory();
	private final ClusterSOMFactory somClusterFactory = new ClusterSOMFactory();
	
	public MLTrain create(MLMethod method, NeuralDataSet training, String type, String args) {
		
		String args2 = args;
		
		if( args2==null ) {
			args2="";
		}
		
		if( TYPE_RPROP.equalsIgnoreCase(type) ) {
			return this.rpropFactory.create(method, training, args2);
		} else if( TYPE_BACKPROP.equalsIgnoreCase(type) ) {
			return this.backpropFactory.create(method, training, args2);
		} else if( TYPE_SCG.equalsIgnoreCase(type) ) {
			return this.scgFactory.create(method, training, args2);
		} else if( TYPE_LMA.equalsIgnoreCase(type) ) {
			return this.lmaFactory.create(method, training, args2);
		} else if( TYPE_SVM.equalsIgnoreCase(type) ) {
			return this.svmFactory.create(method, training, args2);
		} else if( TYPE_SVM_SEARCH.equalsIgnoreCase(type) ) {
			return this.svmSearchFactory.create(method, training, args2);
		} else if( TYPE_SOM_NEIGHBORHOOD.equalsIgnoreCase(type) ) {
			return this.neighborhoodFactory.create(method, training, args2);
		} else if( TYPE_SOM_CLUSTER.equalsIgnoreCase(type)) {
			return this.somClusterFactory.create(method, training, args2);
		}
		else {
			throw new EncogError("Unknown training type: " + type);
		}
	}
}
