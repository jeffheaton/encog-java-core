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
import helpers.PerfResults;

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
	public double getMisclassification(BasicNeuralDataSet evalSet, DataMapper dataMapper) {
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
		double error = (double) bad / (double) evals;
		return error;
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

	@Override
	public PerfResults testPerformance(BasicNeuralDataSet evalSet, DataMapper dataMapper) {
		int outputs = evalSet.getIdealSize();
		long size = evalSet.getRecordCount();
		int tp[] = new int[outputs];
		int tn[] = new int[outputs];
		int fp[] = new int[outputs];
		int fn[] = new int[outputs];
		for(int i = 0; i < size; i++)
		{
			MLDataPair pair = evalSet.get(i);
			MLData output = bagging.compute(pair.getInput());
			for(int thisClass = 0; thisClass < outputs; thisClass++) {
				if (output.getData(thisClass) > 0.5) {
					if (pair.getIdeal().getData(thisClass) > 0.5) {
						tp[thisClass]++;
					} else {
						fp[thisClass]++;
					}
				} else {
					if (pair.getIdeal().getData(thisClass) < 0.5) {
						tn[thisClass]++;
					} else {
						fn[thisClass]++;						
					}
				}
			}
		}
		return new PerfResults(tp,fp,tn,fn,outputs);
	}
	
}
