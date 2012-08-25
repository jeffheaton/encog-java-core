package techniques;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.bagging.Bagging;
import org.encog.ensemble.stacking.Stacking;
import org.encog.ml.data.MLData;

import helpers.DataLoader;

public class StackingET extends EvaluationTechnique {

	private Stacking stacking;
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
	public void train(double trainToError, boolean verbose) {
		stacking.train(trainToError,verbose);
	}

	@Override
	public void init(DataLoader dataLoader) {
		stacking = new Stacking(splits,dataSetSize,mlMethod,trainFactory,aggregator);
		setTrainingSet(dataLoader.getTrainingSet());
		setTestSet(dataLoader.getTestSet());
		stacking.setTrainingData(trainingSet);
	}

	@Override
	public MLData compute(MLData input) {
		return stacking.compute(input);
	}

	@Override
	public void trainStep() {
		stacking.trainStep();
	}

	@Override
	public double trainError() {
		return stacking.getMember(0).getTraining().getError();
	}
	
}
