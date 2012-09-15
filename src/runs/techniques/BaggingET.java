package techniques;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.bagging.Bagging;
import org.encog.ml.data.MLData;

import helpers.DataLoader;

public class BaggingET extends EvaluationTechnique {

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
	public void init(DataLoader dataLoader) {
		ensemble = new Bagging(splits,dataSetSize,mlMethod,trainFactory,aggregator);
		setTrainingSet(dataLoader.getTrainingSet());
		setTestSet(dataLoader.getTestSet());
		ensemble.setTrainingData(trainingSet);
	}

	@Override
	public MLData compute(MLData input) {
		return ensemble.compute(input);
	}

	@Override
	public void trainStep() {
		((Bagging) ensemble).trainStep();
	}

	@Override
	public double trainError() {
		return ensemble.getMember(0).getTraining().getError();
	}
	
}
