package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.EncogError;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.BasicTrainSOM;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodBubble;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodFunction;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodRBF;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodRBF1D;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodSingle;
import org.encog.util.ParamsHolder;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class NeighborhoodSOMFactory {
	
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		if( !(method instanceof SOM) ) {
			throw new EncogError("Neighborhood training cannot be used on a method of type: " + method.getClass().getName() );
		}
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		ParamsHolder holder = new ParamsHolder(args);
		
		double learningRate = holder.getDouble(MLTrainFactory.PROPERTY_LEARNING_RATE, false, 0.7);
		String neighborhoodStr = holder.getString(MLTrainFactory.PROPERTY_NEIGHBORHOOD, false, "rbf");
		String rbfTypeStr = holder.getString(MLTrainFactory.PROPERTY_RBF_TYPE, false, "gaussian");
		
		RBFEnum t;
		
		if( rbfTypeStr.equalsIgnoreCase("Gaussian"))
			t = RBFEnum.Gaussian;
		else if( rbfTypeStr.equalsIgnoreCase("Multiquadric"))
			t = RBFEnum.Multiquadric;
		else if( rbfTypeStr.equalsIgnoreCase("InverseMultiquadric"))
			t = RBFEnum.InverseMultiquadric;
		else if( rbfTypeStr.equalsIgnoreCase("MexicanHat"))
			t = RBFEnum.MexicanHat;
		else 
			t = RBFEnum.Gaussian;
		
		NeighborhoodFunction nf = null;
		
		if( neighborhoodStr.equalsIgnoreCase("bubble")) {
			nf = new NeighborhoodBubble(1);
		} else if( neighborhoodStr.equalsIgnoreCase("rbf")) {
			String str = holder.getString(MLTrainFactory.PROPERTY_DIMENSIONS, true, null);
			int[] size = NumberList.fromListInt(CSVFormat.EG_FORMAT, str);			
			nf = new NeighborhoodRBF(size,t);
		} else if( neighborhoodStr.equalsIgnoreCase("rbf1d")) {
			nf = new NeighborhoodRBF1D(t);	
		} if( neighborhoodStr.equalsIgnoreCase("single")) {
			nf = new NeighborhoodSingle();
		}
		
		BasicTrainSOM result = new BasicTrainSOM((SOM)method, learningRate, training, nf);
		
		if( args.containsKey(MLTrainFactory.PROPERTY_ITERATIONS) ) {
			int plannedIterations = holder.getInt(MLTrainFactory.PROPERTY_ITERATIONS, false, 1000);
			double startRate = holder.getDouble(MLTrainFactory.PROPERTY_START_LEARNING_RATE, false, 0.05);
			double endRate = holder.getDouble(MLTrainFactory.PROPERTY_END_LEARNING_RATE, false, 0.05);
			double startRadius = holder.getDouble(MLTrainFactory.PROPERTY_START_RADIUS, false, 10);
			double endRadius = holder.getDouble(MLTrainFactory.PROPERTY_END_RADIUS, false, 1);
			result.setAutoDecay(plannedIterations, startRate, endRate, startRadius, endRadius);
		}
		
		return result;
	}
}
