/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.neural.data.image;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.downsample.Downsample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store a collection of images for training with a neural network. This class
 * collects and then downsamples images for use with a neural network. This is a
 * memory based class, so large datasets can run out of memory.
 * 
 * @author jheaton
 */
public class ImageNeuralDataSet extends BasicNeuralDataSet {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 3368190842312829906L;

	/**
	 * Error message to inform the caller that only ImageNeuralData objects can
	 * be used with this collection.
	 */
	public static final String MUST_USE_IMAGE = 
		"This data set only supports ImageNeuralData or Image objects.";

	/**
	 * The downsampler to use.
	 */
	private final Class<Downsample> downsampler;

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
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct this class with the specified downsampler.
	 * 
	 * @param downsampler
	 *            The downsampler to use.
	 * @param findBounds
	 *            Should the bounds be found and clipped.
	 */
	public ImageNeuralDataSet(final Class<Downsample> downsampler,
			final boolean findBounds) {
		this.downsampler = downsampler;
		this.findBounds = findBounds;
		this.height = -1;
		this.width = -1;
	}

	/**
	 * Add the specified data, must be an ImageNeuralData class.
	 * 
	 * @param data
	 *            The data The object to add.
	 */
	@Override
	public void add(final NeuralData data) {
		if (!(data instanceof ImageNeuralData)) {
			throw new NeuralNetworkError(ImageNeuralDataSet.MUST_USE_IMAGE);
		}

		super.add(data);
	}

	/**
	 * Add the specified input and ideal object to the collection.
	 * 
	 * @param inputData
	 *            The image to train with.
	 * @param idealData
	 *            The expected otuput form this image.
	 */
	@Override
	public void add(final NeuralData inputData, final NeuralData idealData) {
		if (!(inputData instanceof ImageNeuralData)) {
			throw new NeuralNetworkError(ImageNeuralDataSet.MUST_USE_IMAGE);
		}

		super.add(inputData, idealData);
	}

	/**
	 * Add input and expected output. This is used for supervised training.
	 * 
	 * @param inputData
	 *            The input data to train on.
	 */
	@Override
	public void add(final NeuralDataPair inputData) {
		if (!(inputData.getInput() instanceof ImageNeuralData)) {
			throw new NeuralNetworkError(ImageNeuralDataSet.MUST_USE_IMAGE);
		}

		super.add(inputData);
	}

	/**
	 * Downsample all images and generate training data.
	 * 
	 * @param height
	 *            The height to downsample to.
	 * @param width
	 *            the width to downsample to.
	 */
	public void downsample(final int height, final int width) {
		this.height = height;
		this.width = width;

		for (final NeuralDataPair pair : this) {
			if (pair.getInput() instanceof ImageNeuralData) {
				throw new NeuralNetworkError(
						"Invalid class type found in ImageNeuralDataSet, only "
								+ "ImageNeuralData items are allowed.");
			}

			Downsample downsample;
			try {
				downsample = this.downsampler.newInstance();
				final ImageNeuralData input = (ImageNeuralData) pair.getInput();
				input.downsample(downsample, this.findBounds, height, width);
			} catch (final InstantiationException e) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("Exception", e);
				}
				throw new NeuralNetworkError(e);
			} catch (final IllegalAccessException e) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("Exception", e);
				}
				throw new NeuralNetworkError(e);
			}

		}
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return this.width;
	}
}
