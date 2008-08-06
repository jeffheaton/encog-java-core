package org.encog.neural.data.image;

import java.awt.Image;

import org.encog.neural.data.basic.BasicNeuralData;

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
	
	
}
