/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
