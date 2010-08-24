package org.encog.script.javascript.objects;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.script.EncogScriptError;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.Function;
import sun.org.mozilla.javascript.internal.Scriptable;
import sun.org.mozilla.javascript.internal.ScriptableObject;

public class JSTrainingData extends ScriptableObject {

	private BasicNeuralDataSet data;
	private int inputCount;
	private int idealCount;
	
	@Override
	public String getClassName() {
		return "TrainingData";
	}
	
	public JSTrainingData()
	{
		
	}
	
	public JSTrainingData(int inputCount, int idealCount)
	{
		this.data = new BasicNeuralDataSet();
		this.inputCount = inputCount;
		this.idealCount = idealCount;
	}
	
	
	public int jsGet_inputCount()
	{
		return inputCount;
	}
	
	public int jsGet_idealCount()
	{
		return idealCount;
	}
	
	public int jsGet_count()
	{
		return (int)this.data.getRecordCount();
	}
	
	public static void jsFunction_define(Context cx, Scriptable thisObject, Object[] args, Function funObj)
	{
		JSTrainingData training = (JSTrainingData)thisObject;
		if( args.length!=(training.inputCount+training.idealCount) )
		{
			throw new EncogScriptError("Wrong number of parameters to define, must be " + (training.inputCount+training.idealCount) + ", because we have " + training.inputCount + " input and " + training.idealCount + " ideal.");
		}
		
		int index = 0;
		
		double[] inputData = new double[training.inputCount];
		double[] idealData = new double[training.idealCount];
		
		for(int i=0;i<training.inputCount;i++)
		{
			inputData[i] = Double.parseDouble(args[index++].toString());
		}
		
		for(int i=0;i<training.idealCount;i++)
		{
			idealData[i] = Double.parseDouble(args[index++].toString());
		}
		
		training.data.add(new BasicNeuralDataPair(new BasicNeuralData(inputData),new BasicNeuralData(idealData)));
		
	}

	public NeuralDataSet getData() {
		return data;
	}

}
