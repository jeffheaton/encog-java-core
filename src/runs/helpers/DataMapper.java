package helpers;

import java.util.ArrayList;

import org.encog.ml.data.MLData;

public interface DataMapper {
	public MLData map(ArrayList<String> readIn);
	public ArrayList<String> unmap (MLData dataSet);
	public boolean compare(ArrayList<String> result, ArrayList<String> expected, boolean print);
}
