package org.encog.script.javascript.objects;

import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.script.EncogScriptError;
import org.encog.util.simple.EncogUtility;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.ScriptableObject;

public class JSNeuralNetwork extends ScriptableObject {

	private BasicNetwork network;
	
	@Override
	public String getClassName() {
		return "NeuralNetwork";
	}
	
	public void jsFunction_createFeedForward(int input, int hidden1, int hidden2, int output, String activation)
	{
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(input);
		pattern.setOutputNeurons(output);
		
		if( hidden1>0 )
			pattern.addHiddenLayer(hidden1);
		if( hidden2>0 )
			pattern.addHiddenLayer(hidden2);
		
		if( activation.equalsIgnoreCase("sigmoid") )
			pattern.setActivationFunction(new ActivationSigmoid());
		else if( activation.equalsIgnoreCase("tanh") )
			pattern.setActivationFunction(new ActivationSigmoid());
		else if( activation.equalsIgnoreCase("linear") )
			pattern.setActivationFunction(new ActivationSigmoid());
		else
			throw new EncogScriptError("Uknown activation type: " + activation);
		
		this.network = pattern.generate();
	}
	
	public void jsFunction_evaluate(JSTrainingData data)
	{
		Object obj = ScriptableObject.getProperty(this.getParentScope(),"console");
		JSEncogConsole console = (JSEncogConsole)Context.jsToJava(obj, JSEncogConsole.class);
		
		for (final NeuralDataPair pair : data.getData()) {
			final NeuralData output = network.compute(pair.getInput());
			console.println("Input="
					+ EncogUtility.formatNeuralData(pair.getInput())
					+ ", Actual=" + EncogUtility.formatNeuralData(output)
					+ ", Ideal="
					+ EncogUtility.formatNeuralData(pair.getIdeal()));

		}
	}

	public BasicNetwork getNetwork() {
		return network;
	}
}
