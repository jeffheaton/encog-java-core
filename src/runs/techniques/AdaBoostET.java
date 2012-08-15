package techniques;

import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.adaboost.AdaBoost;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ml.data.MLData;

import helpers.DataLoader;

public class AdaBoostET extends EvaluationTechnique {

	private AdaBoost boosting;
	private int T;
	private int dataSetSize;

	public AdaBoostET(int T, int dataSetSize, String label, EnsembleMLMethodFactory mlMethod, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator) {
		this.T = T;
		this.dataSetSize = dataSetSize;
		this.label = label;
		this.mlMethod = mlMethod;
		this.trainFactory = trainFactory;
		this.aggregator = aggregator;
	}

	@Override
	public void train(double trainToError, boolean verbose) {
		boosting.train(trainToError,verbose);
	}

	@Override
	public void init(DataLoader dataLoader) {
		boosting = new AdaBoost(T,dataSetSize,mlMethod,trainFactory,aggregator);
		setTrainingSet(dataLoader.getTrainingSet());
		setTestSet(dataLoader.getTestSet());
		boosting.setTrainingData(trainingSet);
	}

	@Override
	public MLData compute(MLData input) {
		return boosting.compute(input);
	}

	@Override
	public void trainStep() {
		System.err.println("Can't to this in Boosting");
	}

	@Override
	public double trainError() {
		return 0;
	}
	
}
