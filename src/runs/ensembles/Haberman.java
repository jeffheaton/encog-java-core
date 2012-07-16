package ensembles;

import java.util.Arrays;
import java.util.List;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.Averaging;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ensemble.aggregator.MajorityVoting;
import org.encog.ensemble.ml.mlp.factory.MultiLayerPerceptronFactory;
import org.encog.ensemble.training.backpropagation.ResilientPropagationFactory;

import bagging.BaggingET;
import helpers.DataLoader;
import helpers.Evaluator;
import helpers.IntMapper;

public class Haberman {

	Evaluator ev;
	static BaggingET bagging;
	static DataLoader dataLoader;
	
	private static int outputs = 2;
	private static int inputs = 3;
	private static int readInputs = 1;
	private static boolean inputsReversed = true;
	private static String inputFile = "data/haberman.data";
	private static List<Integer> splits = Arrays.asList(1,3,10,30,100);
	private static List<Integer> dataSetSizes = Arrays.asList(30,100,300,1000);
	private static List<Double> trainingErrors = Arrays.asList(0.3,0.1,0.03);
	private static int trainingSetSize = 250;
	private static double activationThreshold = 0.3;
	
	public static void loop(EnsembleTrainFactory tf, EnsembleMLMethodFactory mlfact, EnsembleAggregator agg) {
		for(Integer dataSetSize : dataSetSizes)
		for(Integer split : splits)
		{
			String fullLabel = "bagging," + tf.toString() + "," + mlfact.toString() +
							   "," + agg.toString() + "," + split + "," + dataSetSize;
			bagging = new BaggingET(split, dataSetSize, fullLabel, mlfact, tf, agg);
			for (double te: trainingErrors) {
				Evaluator ev = new Evaluator(bagging, dataLoader, te);
				ev.getResults(fullLabel+","+te);
			}
		}
	}
	
	public static void main(String[] args) {
		//TODO: use args
		dataLoader = new DataLoader(new IntMapper(outputs,activationThreshold),trainingSetSize,readInputs,inputs,inputsReversed);
		dataLoader.readData(inputFile);
		EnsembleTrainFactory etf = new ResilientPropagationFactory();
		MultiLayerPerceptronFactory mlf = new MultiLayerPerceptronFactory();
		mlf.setParameters(Arrays.asList(100), new ActivationSigmoid());
		loop(etf,mlf,new Averaging());
	}
}
