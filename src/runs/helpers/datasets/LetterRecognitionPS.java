package helpers.datasets;

import helpers.DataLoader;
import helpers.LetterMapper;
import helpers.ProblemDescription;

public class LetterRecognitionPS implements ProblemDescription {

	private static int outputs = 'Z' - 'A' + 1;
	private static int inputs = 16;
	private static int readInputs = 1;
	private static boolean inputsReversed = false;
	private static String inputFile = "data/letter-recognition.data";
	private static double lowBound = 0.0;

	@Override
	public DataLoader getDataLoader(double activationThreshold, int trainingSetSize) {
		DataLoader dataLoader = new DataLoader(new LetterMapper(outputs,activationThreshold,lowBound),trainingSetSize,readInputs,inputs,inputsReversed);
		dataLoader.readData(inputFile);
		return dataLoader;
	}

	@Override
	public int getOutputs() {
		return outputs;
	}

	@Override
	public int getInputs() {
		return inputs;
	}

	@Override
	public int getReadInputs() {
		return readInputs;
	}

	@Override
	public boolean areInputsReversed() {
		return inputsReversed;
	}

	@Override
	public String getInputFile() {
		return inputFile;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

}
