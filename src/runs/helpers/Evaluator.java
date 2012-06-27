package helpers;

import org.encog.ml.data.MLDataSet;

public class Evaluator {

	private EvaluationTechnique technique;
	private DataLoader dataLoader;
	
	Evaluator(EvaluationTechnique technique, DataMapper mapper, int inputCols, int inputs, String dataFile, double targetTrainingError) {
		this.setTechnique(technique);
		dataLoader = new DataLoader(mapper,inputCols,inputs);
		dataLoader.readData(dataFile);
		this.technique.init();
		this.technique.train(targetTrainingError);
		System.out.println("Training " + this.technique.getLabel() + " to " + targetTrainingError + " error.");
	}
	
	public void getResults () {
		System.out.println("Training set accuracy: " + (1 - this.technique.getCVError(this.dataLoader.getTrainingSet())));
		System.out.println("Test set accuracy: " + (1 - this.technique.getCVError(this.dataLoader.getTestSet())));
	}

	public EvaluationTechnique getTechnique() {
		return technique;
	}

	public void setTechnique(EvaluationTechnique technique) {
		this.technique = technique;
	}
	
}
