package ensembles;

import java.util.List;

import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import techniques.EvaluationTechnique;
import helpers.ArgParser;
import helpers.ArgParser.BadArgument;
import helpers.DataLoader;
import helpers.Evaluator;
import helpers.ProblemDescription;

public class Test {

	Evaluator ev;
	static DataLoader dataLoader;
	static ProblemDescription problem;
	
	private static List<Integer> sizes;
	private static List<Integer> dataSetSizes;
	private static List<Double> trainingErrors;
	private static int trainingSetSize;
	private static double activationThreshold;
	private static EnsembleTrainFactory etf;
	private static List<EnsembleMLMethodFactory> mlfs;
	private static EnsembleAggregator agg;
	private static String etType;
	private static boolean verbose;
	
	public static void loop() {
		for(Integer dataSetSize : dataSetSizes)
		for(Integer size : sizes)
		for(EnsembleMLMethodFactory mlf: mlfs)
		{
			String fullLabel = etType + "," + etf.toString() + "," + mlf.toString() +
							   "," + agg.toString() + "," + size + "," + dataSetSize;
			EvaluationTechnique et = null;
			try {
				et = ArgParser.technique(etType,size,dataSetSize,fullLabel,mlf,etf,agg);
			} catch (BadArgument e) {
				help();
			}
			for (double te: trainingErrors) {
				Evaluator ev = new Evaluator(et, dataLoader, te, verbose);
				ev.getResults(fullLabel+","+te);
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length != 11) {
			help();
		} 
		try {
			etType = args[0];
			problem = ArgParser.problem(args[1]);
			sizes = ArgParser.intList(args[2]);
			dataSetSizes = ArgParser.intList(args[3]);
			trainingErrors = ArgParser.doubleList(args[4]);
			trainingSetSize = ArgParser.intSingle(args[5]);
			activationThreshold = ArgParser.doubleSingle(args[6]);
			etf = ArgParser.ETF(args[7]);
			mlfs = ArgParser.MLFS(args[8]);
			agg = ArgParser.AGG(args[9]);
			verbose = Boolean.parseBoolean(args[10]);
		} catch (BadArgument e) {
			help();
		}
		
		dataLoader = problem.getDataLoader(activationThreshold,trainingSetSize);
		loop(etf,mlf,agg);
		System.exit(0);
	}

	private static void help() {
		System.err.println("Usage: Test <technique> <problem> <sizes> <dataSetSizes> <trainingErrors> <trainingSetSize> <activationThreshold> <training> <memebertypes> <aggregator> <verbose>");
		System.exit(2);
	}
}
