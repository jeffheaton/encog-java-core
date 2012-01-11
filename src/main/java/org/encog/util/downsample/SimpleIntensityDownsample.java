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
import java.awt.image.PixelGrabber;

import org.encog.EncogError;

/**
 * Downsample an image using a simple intensity scale. Color information is
 * discarded.
 * 
 * @author jheaton
 */
public class SimpleIntensityDownsample extends RGBDownsample {

	/**
	 * Called to downsample the image and store it in the down sample component.
	 * 
	 * @param image
	 *            The image to downsample.
	 * @param height
	 *            The height to downsample to.
	 * @param width
	 *            THe width to downsample to.
	 * @return The downsampled image.
	 */
	@Override
	public double[] downSample(final Image image, final int height,
			final int width) {

		processImage(image);

		final double[] result = new double[height * width * 3];

		final PixelGrabber grabber = new PixelGrabber(image, 0, 0,
				getImageWidth(), getImageHeight(), true);

		try {
			grabber.grabPixels();
		} catch (final InterruptedException e) {
			throw new EncogError(e);
		}

		setPixelMap((int[]) grabber.getPixels());

		// now downsample

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				downSampleRegion(x, y);
				result[index++] = (getCurrentRed() + getCurrentBlue() 
						+ getCurrentGreen()) / 3;
			}
		}

		return result;
	}

}
