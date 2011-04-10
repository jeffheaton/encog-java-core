package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.BasicTrainSOM;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodFunction;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodRBF1D;

public class NeighborhoodSOMFactory {
	
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		
		double learningRate = 0.7; 

		try {
			if( args.containsKey("LEARNINGRATE")) {
				learningRate = Double.parseDouble(args.get("LEARNINGRATE"));
			}
		} catch(NumberFormatException ex) {
			throw new AnalystError("Invalid SOM learning rate, must be valid numeric.");
		}
		
		//NeighborhoodFunction nf = new NeighborhoodRBF(RBFEnum.Gaussian,10,10);
		NeighborhoodFunction nf = new NeighborhoodRBF1D(RBFEnum.Gaussian);
		return new BasicTrainSOM((SOM)method, learningRate, training, nf);
	}
}
