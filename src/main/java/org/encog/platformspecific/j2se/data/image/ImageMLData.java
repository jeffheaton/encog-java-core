/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.platformspecific.j2se.data.image;

import java.awt.Image;

import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.downsample.Downsample;

/**
 * An extension of the BasicNeuralData class that is designed to hold images for
 * input into a neural network. This class should only be used with the
 * ImageNeuralDataSet collection.
 *
 * This class provides the ability to associate images with the elements of a
 * dataset. These images will be downsampled to the resolution specified in the
 * ImageNeuralData set class that they are added to.
 *
 * @author jheaton
 *
 */
public class ImageMLData extends BasicMLData {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -4645971270240180450L;
	/**
	 * The image associated with this class.
	 */
	private Image image;

	/**
	 * Construct an object based on an image.
	 *
	 * @param theImage
	 *            The image to use.
	 */
	public ImageMLData(final Image theImage) {
		super(1);
		this.image = theImage;
	}

	/**
	 * Downsample, and copy, the image contents into the data of this object.
	 * Calling this method has no effect on the image, as the same image can be
	 * downsampled multiple times to different resolutions.
	 *
	 * @param downsampler
	 *            The downsampler object to use.
	 * @param findBounds
	 *            Should the bounds be located and cropped.
	 * @param height
	 *            The height to downsample to.
	 * @param width
	 *            The width to downsample to.
	 * @param hi
	 *            The high value to normalize to.
	 * @param lo
	 *            The low value to normalize to.
	 */
	public final void downsample(final Downsample downsampler,
			final boolean findBounds, final int height, final int width,
			final double hi, final double lo) {
		if (findBounds) {
			downsampler.findBounds();
		}
		final double[] sample = downsampler.downSample(this.image, height,
				width);

		for (int i = 0; i < sample.length; i++) {
			
	        sample[i] =  ((sample[i] - 0)
	                / (255 - 0))
	                * (hi - lo) + lo;
		}

		this.setData(sample);
	}

	/**
	 * @return the image
	 */
	public final Image getImage() {
		return this.image;
	}

	/**
	 * @param theImage
	 *            the image to set
	 */
	public final void setImage(final Image theImage) {
		this.image = theImage;
	}
}
