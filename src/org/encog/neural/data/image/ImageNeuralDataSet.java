package org.encog.neural.data.image;

import java.awt.Image;
import java.util.ConcurrentModificationException;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.downsample.Downsample;

public class ImageNeuralDataSet extends BasicNeuralDataSet {

	public static final String MUST_USE_IMAGE = "This data set only supports ImageNeuralData or Image objects.";
	private Class<Downsample> downsampler;
	private int height;
	private int width;
	private boolean findBounds;
	
	public ImageNeuralDataSet(Class<Downsample> downsampler, boolean findBounds)
	{
		this.downsampler = downsampler;
		this.height = -1;
		this.width = -1;
	}
	
	
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
	
	public void downsample(int height,int width)
	{
		this.height = height;
		this.width = width;
		
		for(NeuralDataPair pair: this)
		{
			if( pair.getInput() instanceof ImageNeuralData )
			{
				throw new NeuralNetworkError("Invalid class type found in ImageNeuralDataSet, only ImageNeuralData items are allowed.");
			}
			
			Downsample downsample;
			try {
				downsample = this.downsampler.newInstance();
				ImageNeuralData input = (ImageNeuralData)pair.getInput();
				input.downsample(downsample,this.findBounds,height,width);
			} catch (InstantiationException e) {
				throw new NeuralNetworkError(e);
			} catch (IllegalAccessException e) {
				throw new NeuralNetworkError(e);
			}
			
		}
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}	
}
