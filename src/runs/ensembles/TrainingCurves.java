package ensembles;

import java.util.List;

import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.neural.data.basic.BasicNeuralDataSet;

import techniques.EvaluationTechnique;
import helpers.ArgParser;
import helpers.ArgParser.BadArgument;
import helpers.DataLoader;
import helpers.DataMapper;
import helpers.Evaluator;
import helpers.ProblemDescription;

public class TrainingCurves {

	Evaluator ev;
	static DataLoader dataLoader;
	static ProblemDescription problem;
	
	private static int trainingSetSize;
	private static double activationThreshold;
	private static EnsembleTrainFactory etf;
	private static List<EnsembleMLMethodFactory> mlfs;
	private static EnsembleAggregator agg;
	private static String etType;
	private static int maxIterations;
	
	public static void loop() {
		for(EnsembleMLMethodFactory mlf: mlfs)
		{
			EvaluationTechnique et = null;
			try {
				et = ArgParser.technique(etType,1,trainingSetSize,"",mlf,etf,agg);
			} catch (BadArgument e) {
				help();
			}
			DataMapper dataMapper = dataLoader.getMapper();
			BasicNeuralDataSet testSet = dataLoader.getTestSet();
			BasicNeuralDataSet trainingSet = dataLoader.getTrainingSet();
			et.init(dataLoader);
			for (int i=0; i < maxIterations; i++) {
				et.trainStep();
				double mse = et.trainError();
				double trainMisc = et.getMisclassification(testSet, dataMapper);
				double testMisc = et.getMisclassification(trainingSet, dataMapper);
				System.out.println(i + " " + mse + " " + trainMisc + " " + testMisc);
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length != 7) {
			help();
		} 
		try {
			etType = args[0];
			problem = ArgParser.problem(args[1]);
			trainingSetSize = ArgParser.intSingle(args[2]);
			activationThreshold = ArgParser.doubleSingle(args[3]);
			etf = ArgParser.ETF(args[4]);
			mlfs = ArgParser.MLFS(args[5]);
			agg = ArgParser.AGG("averaging");
			maxIterations = ArgParser.intSingle(args[6]);
		} catch (BadArgument e) {
			help();
		}
		
		try {
			dataLoader = problem.getDataLoader(activationThreshold,trainingSetSize);
		} catch (helpers.ProblemDescriptionLoader.BadArgument e) {
			System.err.println("Could not get dataLoader - perhaps the mapper_type property is wrong");
			e.printStackTrace();
		}
		loop();
		System.exit(0);
	}

	private static void help() {
		System.err.println("Usage: TrainingCurves <technique> <problem> <trainingSetSize> <activationThreshold> <training> <membertypes> <maxIterations>");
		System.exit(2);
	}
}
