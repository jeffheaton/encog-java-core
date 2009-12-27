/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.util.downsample;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import org.encog.EncogError;
import org.encog.util.ImageSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Downsample an image using a simple intensity scale. Color information is
 * discarded.
 * 
 * @author jheaton
 */
public class SimpleIntensityDownsample extends RGBDownsample {

	
	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	
	/**
	 * Called to downsample the image and store it in the down sample component.
	 * 
	 * @param height
	 *            The height to downsample to.
	 * @param width
	 *            THe width to downsample to.
	 * @return The downsampled image.
	 */
	public double[] downSample(Image image, final int height, final int width) {

		processImage(image);
		
		final double[] result = new double[height * width * 3];

		final PixelGrabber grabber = new PixelGrabber(image, 0, 0,
				getImageWidth(), getImageHeight(), true);

		try {
			grabber.grabPixels();
		} catch (final InterruptedException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}
			throw new EncogError(e);
		}

		this.setPixelMap( (int[]) grabber.getPixels() );

		// now downsample

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				downSampleRegion(x, y);
				result[index++] = ( getCurrentRed() + getCurrentBlue() + getCurrentGreen() )/3;
			}
		}

		return result;
	}


}
