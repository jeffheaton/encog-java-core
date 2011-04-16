package org.encog.app.csv.temporal;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class TemporalWindowArray {

	public TemporalWindowArray(int inputWindow, int predictWindow) {
		this.inputWindow = inputWindow;
		this.predictWindow = predictWindow;
	}
	
    /**
     * The size of the input window.
     */
    private int inputWindow;

    /**
     * The size of the prediction window.
     */
    private int predictWindow;

    /**
     * @return The fields that are to be processed.
     */
    public TemporalWindowField[] getFields()
    {
            return this.fields;     
    }

    /**
     * The fields that are to be processed.
     */
    private TemporalWindowField[] fields;
    
	/**
	 * @return the inputWindow
	 */
	public int getInputWindow() {
		return inputWindow;
	}

	/**
	 * @param inputWindow the inputWindow to set
	 */
	public void setInputWindow(int inputWindow) {
		this.inputWindow = inputWindow;
	}

	/**
	 * @return the predictWindow
	 */
	public int getPredictWindow() {
		return predictWindow;
	}

	/**
	 * @param predictWindow the predictWindow to set
	 */
	public void setPredictWindow(int predictWindow) {
		this.predictWindow = predictWindow;
	}
	
    /**
     * Count the number of fields that are that are in the prediction.
     * @return The number of fields predicted.
     */
    public int countPredictFields()
    {
        int result = 0;

        for(TemporalWindowField field : this.fields)
        {
            if (field.getPredict())
                result++;
        }

        return result;
    }

    /**
     * Count the number of input fields, or fields used to predict.
     * @return The number of input fields.
     */
    public int countInputFields()
    {
        int result = 0;

        for (TemporalWindowField field : this.fields)
        {
            if (field.getInput())
                result++;
        }

        return result;
    }

	public void analyze(double[] array)
	{
		this.fields = new TemporalWindowField[1];
		this.fields[0] = new TemporalWindowField("0");
		this.fields[0].setAction(TemporalType.InputAndPredict);
	}
	
	public void analyze(double[][] array)
	{
		int length = array[0].length;
		this.fields = new TemporalWindowField[length];
		for(int i=0;i<length;i++)
		{
			this.fields[i] = new TemporalWindowField(""+i);
			this.fields[i].setAction(TemporalType.InputAndPredict);	
		}
	}

	public NeuralDataSet process(double[] data) {
		NeuralDataSet result = new BasicNeuralDataSet();
		
		int totalWindowSize = this.inputWindow+this.predictWindow;
		int stopPoint = data.length - totalWindowSize;
		
		for(int i=0;i<stopPoint;i++)
		{
			NeuralData inputData = new BasicNeuralData(this.inputWindow);
			NeuralData idealData = new BasicNeuralData(this.predictWindow);
			
			int index = i;
			
			// handle input window
			for(int j=0;j<this.inputWindow;j++)
			{
				inputData.setData(j, data[index++]);
			}
			
			// handle predict window
			for(int j=0;j<this.predictWindow;j++)
			{
				idealData.setData(j, data[index++]);
			}
			
			NeuralDataPair pair = new BasicNeuralDataPair(inputData,idealData);
			result.add(pair);
		}
		
		return result;
	}

}
