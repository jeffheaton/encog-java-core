package org.encog.neural.data.image;

import java.awt.Image;
import java.util.ConcurrentModificationException;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class ImageNeuralDataSet extends BasicNeuralDataSet {

	public static final String MUST_USE_IMAGE = "This data set only supports ImageNeuralData or Image objects.";
	
	
	public void add(NeuralData data) {
		if( !(data instanceof ImageNeuralData) )
			throw new NeuralNetworkError(ImageNeuralDataSet.MUST_USE_IMAGE);
		else
			super.add(data);
	}

	public void add(NeuralData inputData,NeuralData idealData)
	{
		if( !(inputData instanceof ImageNeuralData) )
			throw new NeuralNetworkError(ImageNeuralDataSet.MUST_USE_IMAGE);
		else
			super.add(inputData,idealData);
	}

	public void add(NeuralDataPair inputData) {
		if( !(inputData.getInput() instanceof ImageNeuralData) )
			throw new NeuralNetworkError(ImageNeuralDataSet.MUST_USE_IMAGE);
		else
			super.add(inputData);
	}
	
	
	
}
