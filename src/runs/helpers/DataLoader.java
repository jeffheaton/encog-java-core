package helpers;

import java.util.ArrayList;

import org.encog.NullStatusReportable;
import org.encog.ml.data.MLData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.csv.ReadCSV;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldMLDataSet;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageNeuralDataSet;

public class DataLoader {
	
	private static BasicNeuralDataSet _trainingSet;
	private static BasicNeuralDataSet _testSet;
	private static boolean _inputsReversed;
	private static DataMapper _dataMapper;

	public DataLoader(DataMapper dataMapper) {
		_dataMapper = dataMapper;
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

	private static long getTrainingSetSize() {
		return _trainingSet.getRecordCount();
	}

	private static DataMapper getMapper() {
		return _dataMapper;
	}

	private static int getReadInputs() {
		// TODO Auto-generated method stub
		return 0;
	}

	private static int getInputs() {
		// TODO Auto-generated method stub
		return 0;
	}
}
