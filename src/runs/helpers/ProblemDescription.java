package helpers;

public interface ProblemDescription {
	
	public DataLoader getDataLoader(double activationThreshold, int trainingSetSize);
	public int getOutputs();
	public int getInputs();
	public int getReadInputs();
	public boolean areInputsReversed();
	public String getInputFile();
	
}
