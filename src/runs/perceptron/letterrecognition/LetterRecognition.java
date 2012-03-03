package perceptron.letterrecognition;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.train.MLTrain;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.csv.ReadCSV;
import org.encog.util.simple.EncogUtility;

public class LetterRecognition {

	private static int trainingSetSize = 10000;
	private static int outputs;
	private static int inputs;
	private static BasicNeuralDataSet trainingSet;
	private static BasicNeuralDataSet testSet;
	private static BasicNetwork network;

	/**
	 * @param args
	 */
	public static double[] map(String arg) {
		double [] retVal = new double[outputs];
		for (int i = 0; i < outputs; i++)
			retVal[i] = 0.0;
		int value = arg.charAt(0) - 'A';
		retVal[value] = 1;
		return retVal;
	}
	
	public static char unmap(int arg) {
		return (char) (arg + 'A');
	}
	
	public static void createNetwork () {
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,inputs));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,20));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,outputs));
		network.getStructure().finalizeStructure();
		
		//network = EncogUtility.simpleFeedForward(inputs, 5, 7, outputs, true);
		//Important: without proper randomizing the network doesn't train to convergence.
		(new ConsistentRandomizer(-1,1)).randomize(network);
	}
	
	public static void readData() {
		int total=0;
		System.out.println("importing dataset");
		ReadCSV csv = new ReadCSV("data/letter-recognition.data",false,',');
		inputs = 16;
		outputs = 'Z' - 'A' + 1;
		System.out.println("in: " + inputs + " out: " + outputs);
		trainingSet = new BasicNeuralDataSet();
		testSet = new BasicNeuralDataSet();
		while(csv.next())
		{
			BasicNeuralData inputSet = new BasicNeuralData(inputs);
			BasicNeuralData idealSet = new BasicNeuralData(outputs);
			//System.out.println("line has " + csv.getColumnCount() + " fields");
			double[] inputMap = map(csv.get(0));
			for(int i = 0; i < outputs; i++) {
				idealSet.setData(i,inputMap[i]);
			}
			for(int j = 1; j < inputs; j++) {
				inputSet.setData(j,csv.getDouble(j));
			}
			if (total < trainingSetSize) 
				trainingSet.add(inputSet, idealSet);
			else
				testSet.add(inputSet, idealSet);
			total++;
		}
		System.out.println("found " + total + " items");
		csv.close();
		
	}
	
	public static void main(String[] args) {
		readData();
		createNetwork();
		MLTrain train = new Backpropagation(network, trainingSet, 0.7, 0.9);
		train.iteration();
		double error1 = train.getError();

		for(int i=0;i<10;i++)
			train.iteration();
		
		double error2 = train.getError();
		
		double improve = (error1-error2)/error1;
		System.out.println("Improvement: " + improve);
	}

}
