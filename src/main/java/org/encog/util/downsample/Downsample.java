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
package org.encog.util.downsample;

import java.awt.Image;

/**
 * Utility to downsample an image.
 * 
 * @author jheaton
 * 
 */
public interface Downsample {

	/**
	 * Downsample the image to the specified height and width.
	 * @param image
	 * 			The image to downsample.
	 * @param height
	 *            The height to downsample to.
	 * @param width
	 *            The width to downsample to.
	 * @return The downsampled image.
	 */
	double[] downSample(Image image, int height, int width);

	/**
	 * Find the bounds around the image to exclude whitespace.
	 */
	void findBounds();

	/**
	 * @return Get the bottom boundary of the image.
	 */
	int getDownSampleBottom();

	/**
	 * @return The left boundary of the image.
	 */
	int getDownSampleLeft();

	/**
	 * @return Get the right boundary of the image.
	 */
	int getDownSampleRight();

	/**
	 * @return Get the top boundary of the image.
	 */
	int getDownSampleTop();


	/**
	 * @return The height of the image.
	 */
	int getImageHeight();

	/**
	 * @return The width of the image.
	 */
	int getImageWidth();

	/**
	 * @return The image pixel map.
	 */
	int[] getPixelMap();

	/**
	 * @return The x-ratio of the downsample.
	 */
	double getRatioX();

	/**
	 * @return The y-ratio of the downsample.
	 */
	double getRatioY();

	/**
	 * Process the specified image.
	 * 
	 * @param image
	 *            The image to process.
	 */
	void processImage(Image image);
}
