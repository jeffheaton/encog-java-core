package org.encog.neural.data.image;

import java.awt.Image;

import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.downsample.Downsample;

public class ImageNeuralData extends BasicNeuralData {
	
	private Image image;
	
	public ImageNeuralData(Image image)
	{
		super(1);
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	public void downsample(Downsample downsampler, boolean findBounds, int height, int width) {
		if( findBounds ) {
			downsampler.findBounds();
		}
		double sample[] = downsampler.downSample(height, width);
		this.setData(sample);
	}
	
	
}
