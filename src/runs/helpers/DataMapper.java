package helpers;

import java.util.ArrayList;

import org.encog.ml.data.MLData;

interface DataMapper {

	public MLData map(ArrayList<String> readIn);

}
