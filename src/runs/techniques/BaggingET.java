package techniques;

import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ensemble.bagging.Bagging;
import org.encog.ml.data.MLData;

import helpers.DataLoader;

public class BaggingET extends EvaluationTechnique {

	private Bagging bagging;
	private int splits;
	private int dataSetSize;

	public BaggingET(int splits, int dataSetSize, String label, EnsembleMLMethodFactory mlMethod, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator) {
		this.splits = splits;
		this.dataSetSize = dataSetSize;
		this.label = label;
		this.mlMethod = mlMethod;
		this.trainFactory = trainFactory;
		this.aggregator = aggregator;
	}

	@Override
	public int train(double trainToError, boolean verbose) {
		return bagging.train(trainToError,verbose);
	}

	@Override
	public void init(DataLoader dataLoader) {
		bagging = new Bagging(splits,dataSetSize,mlMethod,trainFactory,aggregator);
		setTrainingSet(dataLoader.getTrainingSet());
		setTestSet(dataLoader.getTestSet());
		bagging.setTrainingData(trainingSet);
	}

	@Override
	public MLData compute(MLData input) {
		return bagging.compute(input);
	}
	
}
