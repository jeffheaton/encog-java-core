package helpers;

import java.io.IOException;
import java.util.Properties;

public class ProblemDescriptionLoader implements ProblemDescription {
	public static class BadArgument extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4633079187826478261L;
		
	}
	public enum MapperType {
		INT,
		LETTER,
	}
	
	boolean loaded=false;
	private static int outputs;
	private static int inputs;
	private static int readInputs;
	private static boolean inputsReversed;
	private static String inputFile;
	private static MapperType mapperType;
	private static String label;
	
	public ProblemDescriptionLoader(String file) throws BadArgument {
		this.fromProblemDescriptionFile(file);
	}
	
	public boolean isReady() {
		return loaded;
	}
	
	public void fromProblemDescriptionFile(String file) throws BadArgument {
		Properties descFile = new Properties();
		try {
			descFile.load(this.getClass().getClassLoader().getResourceAsStream(file));
			outputs=Integer.parseInt(descFile.getProperty("outputs"));
			inputs=Integer.parseInt(descFile.getProperty("inputs"));
			readInputs=Integer.parseInt(descFile.getProperty("read_inputs"));
			inputsReversed=Boolean.parseBoolean(descFile.getProperty("inputs_reversed"));
			inputFile=descFile.getProperty("data_file");
			mapperType=MapperType.valueOf(descFile.getProperty("mapper_type").toUpperCase());
			label=descFile.getProperty("label");
			loaded=true;
		} catch (IOException e) {
			System.err.println("Could not load config file: " + file);
			throw new BadArgument();
		}
	}

	public DataMapper makeMapper(MapperType how, double activationThreshold) throws BadArgument {
		switch(how) {
			case INT: return new IntMapper(outputs,activationThreshold);
			case LETTER: return new LetterMapper(outputs,activationThreshold,0.0);
			default: throw new BadArgument();
		}
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public DataLoader getDataLoader(double activationThreshold, int trainingSetSize) throws BadArgument {
		if (! loaded)
			throw new BadArgument();
		DataLoader dataLoader = new DataLoader(makeMapper(mapperType,activationThreshold),trainingSetSize,readInputs,inputs,inputsReversed);
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
}
