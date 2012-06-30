package helpers;

public class Evaluator {

	private EvaluationTechnique technique;
	private DataLoader dataLoader;
	
	Evaluator(EvaluationTechnique technique, DataMapper mapper, int inputCols, int inputs, String dataFile, boolean inputsReversed, int trainingSetSize, double targetTrainingError) {
		this.setTechnique(technique);
		dataLoader = new DataLoader(mapper,inputCols,inputs,trainingSetSize,inputsReversed);
		dataLoader.readData(dataFile);
		this.technique.init(dataLoader);
		this.technique.train(targetTrainingError);
	}
	
	public Evaluator(EvaluationTechnique technique, DataLoader dataLoader, double targetTrainingError) {
		this.setTechnique(technique);
		this.dataLoader = dataLoader;
		this.technique.init(dataLoader);
		this.technique.train(targetTrainingError);
	}
	
	public void getResults (String prefix) {
		System.out.println(prefix + ",train," + (1 - this.technique.getError(this.dataLoader.getTrainingSet(),dataLoader.getMapper())));
		System.out.println(prefix + ",test," + (1 - this.technique.getError(this.dataLoader.getTestSet(),dataLoader.getMapper())));
	}

	public EvaluationTechnique getTechnique() {
		return technique;
	}

	public void setTechnique(EvaluationTechnique technique) {
		this.technique = technique;
	}
	
}
