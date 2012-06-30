package evaluations;

import java.util.Arrays;
import java.util.List;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
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
	private static List<Integer> splits = Arrays.asList(1,3,10);
	private static List<Integer> dataSetSizes = Arrays.asList(10,30,100,300);
	private static List<Double> trainingErrors = Arrays.asList(0.3,0.1,0.03,0.01,0.003,0.001);
	private static int trainingSetSize = 260;
	
	public static void loop(EnsembleTrainFactory tf, String label, EnsembleMLMethodFactory mlfact, EnsembleAggregator agg) {
		for(Integer split : splits)
			for(Integer dataSetSize : dataSetSizes) {
				String fullLabel = "bagging," + label + "," + split + "," + dataSetSize;
				bagging = new BaggingET(split, dataSetSize, fullLabel, mlfact, tf, agg);
				for (double te: trainingErrors) {
					Evaluator ev = new Evaluator(bagging, dataLoader, te);
					ev.getResults(fullLabel+","+te);
				}
			}
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		dataLoader = new DataLoader(new IntMapper(outputs,0.3),trainingSetSize,readInputs,inputs,inputsReversed);
		dataLoader.readData(inputFile);
		EnsembleTrainFactory rpf = new ResilientPropagationFactory();
		MultiLayerPerceptronFactory mlpf = new MultiLayerPerceptronFactory();
		mlpf.setParameters(Arrays.asList(4,4), new ActivationSigmoid());
		MajorityVoting mv = new MajorityVoting();
		loop(rpf,"resprop,mpl{4,4},majorityvoting",mlpf,mv);
	}
}
