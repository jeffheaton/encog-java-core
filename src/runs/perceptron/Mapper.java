package perceptron;

import java.util.ArrayList;

import org.encog.ml.data.MLData;

public interface Mapper {
	public MLData map (ArrayList<String> data);
	public ArrayList<String> unmap (MLData dataSet);
	public boolean compare(ArrayList<String> result, ArrayList<String> expected, boolean print);
}
