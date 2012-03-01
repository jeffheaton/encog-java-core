package perceptron.letterrecognition;

import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.csv.ReadCSV;

public class LetterRecognition {

	/**
	 * @param args
	 */
	public static int map(String arg) {
		return arg.charAt(0) - 'A';
	}
	
	public static void main(String[] args) {
		int total=0;
		System.out.println("importing dataset");
		ReadCSV csv = new ReadCSV("data/letter-recognition.data",false,',');
		int columns = csv.getColumnCount();
		int ideal = 1;
		BasicNeuralData inputSet = new BasicNeuralData(columns);
		BasicNeuralData idealSet = new BasicNeuralData(ideal);
		final BasicNeuralDataPair pair = new BasicNeuralDataPair((NeuralData)inputSet,(NeuralData)idealSet);
		BasicNeuralDataSet set = new BasicNeuralDataSet();
		while(csv.next())
		{
			for(int i = 0; i < ideal; i++) {
				ideal.setData(i,map(csv.get(i)));
			}
			for(int j = 0; j < columns; j++) {
				ideal.setData(ideal + j,map(csv.get(ideal + j)));
			}
			total++;
		}
		System.out.println("found " + total + " items");
		csv.close();
	}

}
