package bagging;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ensemble.bagging.Bagging;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

import helpers.DataLoader;
import helpers.DataMapper;
import helpers.EvaluationTechnique;

public class BaggingET implements EvaluationTechnique {

	private Bagging bagging;
	private int splits;
	private String label;
	private int dataSetSize;
	private EnsembleMLMethodFactory mlMethod;
	private EnsembleTrainFactory trainFactory;
	private EnsembleAggregator aggregator;
	private MLDataSet trainingSet;
	private MLDataSet testSet;
	
	public BaggingET(int splits, int dataSetSize, String label, EnsembleMLMethodFactory mlMethod, EnsembleTrainFactory trainFactory, EnsembleAggregator aggregator) {
		this.splits = splits;
		this.dataSetSize = dataSetSize;
		this.label = label;
		this.mlMethod = mlMethod;
		this.trainFactory = trainFactory;
		this.aggregator = aggregator;
	}
	
	@Override
	public double getError(BasicNeuralDataSet evalSet, DataMapper dataMapper) {
		int bad = 0;
		int evals = 0;
		for(int i = 0; i < evalSet.getRecordCount(); i++)
		{
			MLDataPair pair = evalSet.get(i);
			MLData output = bagging.compute(pair.getInput());
			ArrayList<String> result = dataMapper.unmap(output);
			ArrayList<String> expected = dataMapper.unmap(pair.getIdeal());
			if (!dataMapper.compare(result,expected,false))
				bad++;
			evals++;
		}
		return (double) bad / (double) evals;
	}

	@Override
	public int train(double trainToError) {
		return bagging.train(trainToError);
	}

	@Override
	public void init(DataLoader dataLoader) {
		bagging = new Bagging(splits,dataSetSize,mlMethod,trainFactory,aggregator);
		setTrainingSet(dataLoader.getTrainingSet());
		setTestSet(dataLoader.getTestSet());
		bagging.setTrainingData(trainingSet);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public MLDataSet getTrainingSet() {
		return trainingSet;
	}

	@Override
	public MLDataSet getTestSet() {
		return testSet;
	}

	@Override
	public void setTrainingSet(MLDataSet trainingSet) {
		this.trainingSet = trainingSet;
	}

	@Override
	public void setTestSet(MLDataSet testSet) {
		this.testSet = testSet;
	}

	
}
