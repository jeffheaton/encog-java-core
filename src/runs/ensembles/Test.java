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
	private static EnsembleMLMethodFactory mlf;
	private static EnsembleAggregator agg;
	private static String etType;
	
	public static void loop(EnsembleTrainFactory etf, EnsembleMLMethodFactory mlf, EnsembleAggregator agg) {
		for(Integer dataSetSize : dataSetSizes)
		for(Integer size : sizes)
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
				Evaluator ev = new Evaluator(et, dataLoader, te);
				ev.getResults(fullLabel+","+te);
			}
		}
	}
	
	public static int main(String[] args) {
		if (args.length != 10) {
			help();
		} 
		try {
			etType = args[1];
			problem = ArgParser.problem(args[2]);
			sizes = ArgParser.intList(args[3]);
			dataSetSizes = ArgParser.intList(args[4]);
			trainingErrors = ArgParser.doubleList(args[5]);
			trainingSetSize = ArgParser.intSingle(args[6]);
			activationThreshold = ArgParser.doubleSingle(args[7]);
			etf = ArgParser.ETF(args[8]);
			mlf = ArgParser.MLF(args[9]);
			agg = ArgParser.AGG(args[10]);
		} catch (BadArgument e) {
			help();
		}
		
		dataLoader = problem.getDataLoader(activationThreshold,trainingSetSize);
		loop(etf,mlf,agg);
		return 0;
	}

	private static void help() {
		System.err.println("Usage: Test <technique> <problem> <sizes> <dataSetSizes> <trainingErrors> <trainingSetSize> <activationThreshold> <training> <memebertype> <aggregator>");
		System.exit(2);
	}
}
