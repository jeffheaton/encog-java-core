package helpers;

import helpers.datasets.HabermanPS;
import helpers.datasets.LandsatPS;
import helpers.datasets.LetterRecognitionPS;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.EnsembleMLMethodFactory;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.Averaging;
import org.encog.ensemble.aggregator.EnsembleAggregator;
import org.encog.ensemble.aggregator.MajorityVoting;
import org.encog.ensemble.ml.mlp.factory.MultiLayerPerceptronFactory;
import org.encog.ensemble.training.BackpropagationFactory;
import org.encog.ensemble.training.ResilientPropagationFactory;
import org.encog.ensemble.training.ScaledConjugateGradientFactory;

import techniques.AdaBoostET;
import techniques.BaggingET;
import techniques.EvaluationTechnique;

public class ArgParser {
	
	public static class BadArgument extends Exception {
		/**
		 * Means a bad argument has been passed to an argparser. Null is evil, exceptions are good.
		 */
		private static final long serialVersionUID = 3153481788294315535L;
	}
	
	public enum TrainFactories {
		BACKPROP,
		RPROP,
		SCG,
	}
	
	public enum MLMethodFactories {
		MLP,
	}
	
	public enum Activations {
		SIGMOID,
	}
	
	public enum Aggregators {
		MAJORITYVOTING,
		AVERAGING,
	}
	
	public enum Problems {
		UCI_HABERMAN,
		UCI_LETTERRECOGNITION,
		STATLOG_LANDSAT,
	}
	
	public enum Techniques {
		BAGGING,
		ADABOOST,
		SINGLE
	}

	public static List<Integer> intList(String string) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (String value: string.split(",")) {
			res.add(Integer.parseInt(value));
		}
		return res;
	}

	public static List<Double> doubleList(String string) {
		ArrayList<Double> res = new ArrayList<Double>();
		for (String value: string.split(",")) {
			res.add(Double.parseDouble(value));
		}
		return res;
	}

	public static int intSingle(String string) {
		return Integer.parseInt(string);
	}

	public static double doubleSingle(String string) {
		return Double.parseDouble(string);
	}

	public static EnsembleTrainFactory ETF(String string) throws BadArgument {
		switch (TrainFactories.valueOf(string.toUpperCase())) {
			case BACKPROP: return new BackpropagationFactory();
			case RPROP: return new ResilientPropagationFactory();
			case SCG: return new ScaledConjugateGradientFactory();
			default: throw new BadArgument();
		}
	}

	public static EnsembleMLMethodFactory MLF(String string) throws BadArgument {
		String values[] = string.split(":");
		switch (MLMethodFactories.valueOf(values[0].toUpperCase())) {
			case MLP:
				MultiLayerPerceptronFactory mlp = new MultiLayerPerceptronFactory();
				mlp.setParameters(intList(values[1]), activation(values[2]));
				return mlp;
			default: throw new BadArgument();
		}
	}

	public static ArrayList<EnsembleMLMethodFactory> MLFS(String string) throws BadArgument {
		ArrayList<EnsembleMLMethodFactory> res = new ArrayList<EnsembleMLMethodFactory>();
		String values[] = string.split("_");
		for (String value: values) {
			res.add(MLF(value));
		}
		return res;
	}
	private static ActivationFunction activation(String string) throws BadArgument {
		switch (Activations.valueOf(string.toUpperCase())) {
			case SIGMOID: return new ActivationSigmoid();
			default: throw new BadArgument();
		}
	}

	public static EnsembleAggregator AGG(String string) throws BadArgument {
		switch (Aggregators.valueOf(string.toUpperCase())) {
			case AVERAGING: return new Averaging();
			case MAJORITYVOTING: return new MajorityVoting();
			default: throw new BadArgument();
		}
	}

	public static ProblemDescription problem(String string) throws BadArgument {
		switch (Problems.valueOf(string.toUpperCase())) {
			case UCI_HABERMAN: return new HabermanPS();
			case UCI_LETTERRECOGNITION: return new LetterRecognitionPS();
			case STATLOG_LANDSAT: return new LandsatPS();
			default: throw new BadArgument();
		}
	}

	public static EvaluationTechnique technique(String etType, Integer size,
			Integer dataSetSize, String fullLabel, EnsembleMLMethodFactory mlf,
			EnsembleTrainFactory etf, EnsembleAggregator agg) throws BadArgument {
		switch (Techniques.valueOf(etType.toUpperCase())) {
			case BAGGING: return new BaggingET(size,dataSetSize,fullLabel,mlf,etf,agg);
			case ADABOOST: return new AdaBoostET(size,dataSetSize,fullLabel,mlf,etf,agg);
			default: throw new BadArgument();
		}
	}

}
