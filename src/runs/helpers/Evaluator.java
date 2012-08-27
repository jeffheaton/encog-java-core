package helpers;

import org.encog.neural.data.basic.BasicNeuralDataSet;

import techniques.EvaluationTechnique;

public class Evaluator {

	private EvaluationTechnique technique;
	private DataLoader dataLoader;
	
	Evaluator(EvaluationTechnique technique, DataMapper mapper, int inputCols, int inputs, String dataFile, boolean inputsReversed, int trainingSetSize, double targetTrainingError) {
		this.setTechnique(technique);
		dataLoader = new DataLoader(mapper,inputCols,inputs,trainingSetSize,inputsReversed);
		dataLoader.readData(dataFile);
		this.technique.init(dataLoader);
		this.technique.train(targetTrainingError,false);
	}
	
	public Evaluator(EvaluationTechnique technique, DataLoader dataLoader, double targetTrainingError, boolean verbose) {
		this.setTechnique(technique);
		this.dataLoader = dataLoader;
		this.technique.init(dataLoader);
		this.technique.train(targetTrainingError,verbose);
	}
	
	public void makeLine(String type, String prefix, BasicNeuralDataSet dataSet) {
		DataMapper dataMapper = dataLoader.getMapper();
		PerfResults perf = this.technique.testPerformance(dataSet, dataMapper,false);
		System.out.println(prefix + "," + type + "," + 
				(this.technique.getMisclassification(dataSet,dataMapper)) + "," +
				(perf.getAccuracy(PerfResults.AveragingMethod.MICRO)) + "," +
				(perf.getPrecision(PerfResults.AveragingMethod.MICRO)) + "," +
				(perf.getRecall(PerfResults.AveragingMethod.MICRO)) + "," +
				(perf.FScore(1.0, PerfResults.AveragingMethod.MICRO)) + "," +
				(perf.getAccuracy(PerfResults.AveragingMethod.MACRO)) + "," +
				(perf.getPrecision(PerfResults.AveragingMethod.MACRO)) + "," +
				(perf.getRecall(PerfResults.AveragingMethod.MACRO)) + "," +
				(perf.FScore(1.0, PerfResults.AveragingMethod.MACRO))
		);
	}
	
	public void getResults (String prefix) {
		makeLine(prefix,"train",this.dataLoader.getTrainingSet());
		makeLine(prefix,"test",this.dataLoader.getTestSet());
	}

	public EvaluationTechnique getTechnique() {
		return technique;
	}

	public void setTechnique(EvaluationTechnique technique) {
		this.technique = technique;
	}
	
}
