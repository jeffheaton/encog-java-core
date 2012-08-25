package techniques;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.stacking.Stacking;

import helpers.DataLoader;

public class StackingET extends EvaluationTechnique {

	private int splits;
	private int dataSetSize;

	public StackingET(int splits, int dataSetSize, String label, EnsembleMLMethodFactory mlMethod, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator) {
		this.splits = splits;
		this.dataSetSize = dataSetSize;
		this.label = label;
		this.mlMethod = mlMethod;
		this.trainFactory = trainFactory;
		this.aggregator = aggregator;
	}

	@Override
	public void init(DataLoader dataLoader) {
		ensemble = new Stacking(splits,dataSetSize,mlMethod,trainFactory,aggregator);
		setTrainingSet(dataLoader.getTrainingSet());
		setTestSet(dataLoader.getTestSet());
		ensemble.setTrainingData(trainingSet);
	}
	
}
