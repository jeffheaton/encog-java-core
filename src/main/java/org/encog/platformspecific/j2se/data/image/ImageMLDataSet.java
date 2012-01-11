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

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.NeuralNetworkError;
import org.encog.util.downsample.Downsample;

/**
 * Store a collection of images for training with a neural network. This class
 * collects and then downsamples images for use with a neural network. This is a
 * memory based class, so large datasets can run out of memory.
 * 
 * @author jheaton
 */
public class ImageMLDataSet extends BasicMLDataSet {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 3368190842312829906L;

	/**
	 * Error message to inform the caller that only ImageNeuralData objects can
	 * be used with this collection.
	 */
	public static final String MUST_USE_IMAGE 
	= "This data set only supports ImageNeuralData or Image objects.";

	/**
	 * The downsampler to use.
	 */
	private final Downsample downsampler;

	/**
	 * The height to downsample to.
	 */
	private int height;

	/**
	 * The width to downsample to.
	 */
	private int width;

	/**
	 * Should the bounds be found and cropped.
	 */
	private final boolean findBounds;

	/**
	 * The high value to normalize to.
	 */
	private final double hi;

	/**
	 * The low value to normalize to.
	 */
	private final double lo;

	/**
	 * Construct this class with the specified downsampler.
	 * 
	 * @param theDownsampler
	 *            The downsampler to use.
	 * @param theFindBounds
	 *            Should the bounds be found and clipped.
	 * @param theHi
	 *            The high value to normalize to.
	 * @param theLo
	 *            The low value to normalize to.
	 */
	public ImageMLDataSet(final Downsample theDownsampler,
			final boolean theFindBounds, 
			final double theHi, final double theLo) {
		this.downsampler = theDownsampler;
		this.findBounds = theFindBounds;
		this.height = -1;
		this.width = -1;
		this.hi = theHi;
		this.lo = theLo;
	}

	/**
	 * Downsample all images and generate training data.
	 * 
	 * @param theHeight
	 *            The height to downsample to.
	 * @param theWidth
	 *            the width to downsample to.
	 */
	public final void downsample(final int theHeight, final int theWidth) {
		this.height = theHeight;
		this.width = theWidth;

		for (final MLDataPair pair : this) {
			if (!(pair.getInput() instanceof ImageMLData)) {
				throw new NeuralNetworkError(
						"Invalid class type found in ImageNeuralDataSet, only "
								+ "ImageNeuralData items are allowed.");
			}

			final ImageMLData input = (ImageMLData) pair.getInput();
			input.downsample(this.downsampler, this.findBounds, height, width,
					this.hi, this.lo);

		}
	}

	/**
	 * @return the height
	 */
	public final int getHeight() {
		return this.height;
	}

	/**
	 * @return the width
	 */
	public final int getWidth() {
		return this.width;
	}
}
