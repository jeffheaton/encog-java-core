package perceptron;

import java.util.ArrayList;

import org.encog.NullStatusReportable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.ReadCSV;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldMLDataSet;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageArray2D;
import org.encog.util.normalize.target.NormalizationStorageNeuralDataSet;


public class Tester {

	private static int _trainingSetSize;
	private static int _outputs;
	private static int _inputs;
	private static int _readInputs;
	protected static BasicNeuralDataSet _trainingSet;
	protected static BasicNeuralDataSet _testSet;
	protected static BasicNetwork _network;
	private static Mapper _mapper;
	private static boolean _inputsReversed = false;

	public Tester(int trainingSetSize, int inputs, int readInputs, int outputs, BasicNetwork network, Mapper mapper)
	{
		setTrainingSetSize(trainingSetSize);
		setInputs(inputs);
		setReadInputs(readInputs);
		setOutputs(outputs);
		_network = network;
		setMapper(mapper);
	}
	
	public static void setReversedInputs(){
		_inputsReversed = true;
	}
	
	public static double getExecutionError(BasicNeuralDataSet evalSet, boolean print) {
		int bad = 0;
		int evals = 0;
		for(int i = 0; i < evalSet.getRecordCount(); i++)
		{
			MLDataPair pair = evalSet.get(i);
			MLData output = _network.compute(pair.getInput());
			ArrayList<String> result = getMapper().unmap(output);
			ArrayList<String> expected = getMapper().unmap(pair.getIdeal());
			if (!getMapper().compare(result,expected,print))
				bad++;
			evals++;
		}
		return (double) bad / (double) evals;
	}
	public static void train(MLTrain train, double trainToError, boolean verbose)
	{
		double error = 1.0;
		int epoch = 0;
		do {
			train.iteration();
			error = getExecutionError(_trainingSet, false);
			if (verbose) System.out.println("Epoch " + epoch + ": " + error);
			epoch++;
		} while (error > trainToError);
		double trainerror = getExecutionError(_trainingSet, true);
		System.out.println("Training error: " + trainerror);
		double testerror = getExecutionError(_testSet, true);
		System.out.println("Test error: " + testerror);
	}

	public static void train(MLTrain train, double trainToError)
	{
		train(train, trainToError, false);
	}
	
	public static void readData(String inputFile) {
		int total=0;
		System.out.println("importing dataset");
		ReadCSV csv = new ReadCSV(inputFile,false,',');
		_trainingSet = new BasicNeuralDataSet();
		_testSet = new BasicNeuralDataSet();
		BasicNeuralDataSet _totalSet = new BasicNeuralDataSet();
		while(csv.next())
		{
			BasicNeuralData inputData = new BasicNeuralData(getInputs());
			ArrayList<String> readIn = new ArrayList<String>();
			MLData idealData;
			if(_inputsReversed) {
				for(int j = 0; j < getInputs(); j++) {
					inputData.setData(j,csv.getDouble(j));
				}
				for(int k = 0; k < getReadInputs(); k++)
					readIn.add(csv.get(k + getInputs()));
				idealData = getMapper().map(readIn);
			} else {
				for(int k = 0; k < getReadInputs(); k++)
					readIn.add(csv.get(k));
				idealData = getMapper().map(readIn);
				for(int j = 0; j < getInputs(); j++) {
					inputData.setData(j,csv.getDouble(j + getReadInputs()));
				}
			}
			_totalSet.add(inputData,idealData);
			total++;
		}
		BasicNeuralDataSet _normSet = new BasicNeuralDataSet();
		DataNormalization normalizer = new DataNormalization();
		normalizer.setReport(new NullStatusReportable());
		normalizer.setTarget(new NormalizationStorageNeuralDataSet(_normSet));
		InputField[] a = new InputField[getInputs()];
		for(int j = 0; j < getInputs(); j++) {
			normalizer.addInputField(a[j] = new InputFieldMLDataSet(false,_totalSet,j));
			normalizer.addOutputField(new OutputFieldRangeMapped(a[j],0.1,0.9));
		}
		normalizer.process();
		for (int i = 0; i < total; i++)
		{
			if (i < getTrainingSetSize()) 
				_trainingSet.add(_normSet.get(i).getInput(),_totalSet.get(i).getIdeal());
			else
				_testSet.add(_normSet.get(i).getInput(),_totalSet.get(i).getIdeal());
		}
		System.out.println("found " + total + " items");
		csv.close();
		
	}

	public Tester() {
		super();
	}

	public static int getOutputs() {
		return _outputs;
	}

	public static void setOutputs(int _outputs) {
		Tester._outputs = _outputs;
	}

	public static int getInputs() {
		return _inputs;
	}
	
	public static void setInputs(int _inputs) {
		Tester._inputs = _inputs;
	}

	public static Mapper getMapper() {
		return _mapper;
	}

	public static void setMapper(Mapper _mapper) {
		Tester._mapper = _mapper;
	}

	public static int getReadInputs() {
		return _readInputs;
	}

	public static void setReadInputs(int _readInputs) {
		Tester._readInputs = _readInputs;
	}

	public static int getTrainingSetSize() {
		return _trainingSetSize;
	}

	public static void setTrainingSetSize(int _trainingSetSize) {
		Tester._trainingSetSize = _trainingSetSize;
	}

}