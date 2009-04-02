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
public class SimpleIntensityDownsample implements Downsample {

	/**
	 * The image to downsample.
	 */
	private Image image;
	
	/**
	 * The pixel map from the image.
	 */
	private int[] pixelMap;
	
	/**
	 * The downsample x-ratio.
	 */
	private double ratioX;
	
	/**
	 * The downsample y-ratio.
	 */
	private double ratioY;
	
	/**
	 * The image height.
	 */
	private int imageHeight;
	
	/**
	 * The image width.
	 */
	private int imageWidth;
	
	/** 
	 * The left boundary of the downsample.
	 */
	private int downSampleLeft;
	
	/**
	 * The right boundary of the downsample.
	 */
	private int downSampleRight;
	
	/** 
	 * The top boundary of the downsample.
	 */
	private int downSampleTop;
	
	/**
	 * The bottom boundary of the downsample.
	 */
	private int downSampleBottom;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct the downsample utility for the specified image.
	 * @param image The image to downsample.
	 */
	public SimpleIntensityDownsample(final Image image) {
		processImage(image);
	}

	/**
	 * Called to downsample the image and store it in the down sample component.
	 * @param height The height to downsample to.
	 * @param width THe width to downsample to.
	 * @return The downsampled image.
	 */
	public double[] downSample(final int height, final int width) {

		final double[] result = new double[height * width];

		final PixelGrabber grabber = new PixelGrabber(this.image, 0, 0,
				this.imageWidth, this.imageWidth, true);

		try {
			grabber.grabPixels();
		} catch (final InterruptedException e) {
			throw new EncogError(e);
		}

		this.pixelMap = (int[]) grabber.getPixels();

		// now downsample

		this.ratioX = (double) (this.downSampleRight - this.downSampleLeft)
				/ (double) width;
		this.ratioY = (double) (this.downSampleBottom - this.downSampleTop)
				/ (double) height;

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result[index++] = downSampleRegion(x, y);
			}
		}

		return result;
	}

	/**
	 * Called to downsample a quadrant of the image.
	 * 
	 * @param x
	 *            The x coordinate of the resulting downsample.
	 * @param y
	 *            The y coordinate of the resulting downsample.
	 * @return Returns true if there were ANY pixels in the specified quadrant.
	 */
	private double downSampleRegion(final int x, final int y) {
		final int startX = (int) (this.downSampleLeft + x * this.ratioX);
		final int startY = (int) (this.downSampleTop + y * this.ratioY);
		final int endX = (int) (startX + this.ratioX);
		final int endY = (int) (startY + this.ratioY);

		int redTotal = 0;
		int greenTotal = 0;
		int blueTotal = 0;

		int total = 0;

		for (int yy = startY; yy <= endY; yy++) {
			for (int xx = startX; xx <= endX; xx++) {
				final int loc = xx + yy * this.imageWidth;
				final int pixel = this.pixelMap[loc];
				final int red = pixel >> 16 & 0xff;
				final int green = pixel >> 8 & 0xff;
				final int blue = pixel & 0xff;

				redTotal += red;
				greenTotal += green;
				blueTotal += blue;
				total++;
			}
		}

		return (redTotal + greenTotal + blueTotal) / (total * 3);
	}

	/**
	 * This method is called to automatically crop the image so that whitespace
	 * is removed.
	 */
	public void findBounds() {
		// top line
		for (int y = 0; y < this.imageHeight; y++) {
			if (!hLineClear(y)) {
				this.downSampleTop = y;
				break;
			}

		}
		// bottom line
		for (int y = this.imageHeight - 1; y >= 0; y--) {
			if (!hLineClear(y)) {
				this.downSampleBottom = y;
				break;
			}
		}
		// left line
		for (int x = 0; x < this.imageWidth; x++) {
			if (!vLineClear(x)) {
				this.downSampleLeft = x;
				break;
			}
		}

		// right line
		for (int x = this.imageWidth - 1; x >= 0; x--) {
			if (!vLineClear(x)) {
				this.downSampleRight = x;
				break;
			}
		}
	}

	/**
	 * @return the downSampleBottom
	 */
	public int getDownSampleBottom() {
		return this.downSampleBottom;
	}

	/**
	 * @return the downSampleLeft
	 */
	public int getDownSampleLeft() {
		return this.downSampleLeft;
	}

	/**
	 * @return the downSampleRight
	 */
	public int getDownSampleRight() {
		return this.downSampleRight;
	}

	/**
	 * @return the downSampleTop
	 */
	public int getDownSampleTop() {
		return this.downSampleTop;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return this.image;
	}

	/**
	 * @return the imageHeight
	 */
	public int getImageHeight() {
		return this.imageHeight;
	}

	/**
	 * @return the imageWidth
	 */
	public int getImageWidth() {
		return this.imageWidth;
	}

	/**
	 * @return the pixelMap
	 */
	public int[] getPixelMap() {
		return this.pixelMap;
	}

	/**
	 * @return the ratioX
	 */
	public double getRatioX() {
		return this.ratioX;
	}

	/**
	 * @return the ratioY
	 */
	public double getRatioY() {
		return this.ratioY;
	}

	/**
	 * This method is called internally to see if there are any pixels in the
	 * given scan line. This method is used to perform autocropping.
	 * 
	 * @param y
	 *            The horizontal line to scan.
	 * @return True if there were any pixels in this horizontal line.
	 */
	private boolean hLineClear(final int y) {
		for (int i = 0; i < this.imageWidth; i++) {
			if (this.pixelMap[y * this.imageWidth + i] != -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Process the image and prepare it to be downsampled.
	 * @param image The image to downsample.
	 */
	public void processImage(final Image image) {
		this.image = image;
		final ImageSize size = new ImageSize(image);
		this.imageHeight = size.getHeight();
		this.imageWidth = size.getWidth();
		this.downSampleLeft = 0;
		this.downSampleTop = 0;
		this.downSampleRight = this.imageWidth;
		this.downSampleBottom = this.imageHeight;
	}

	/**
	 * This method is called to determine ....
	 * 
	 * @param x
	 *            The vertical line to scan.
	 * @return True if there are any pixels in the specified vertical line.
	 */
	private boolean vLineClear(final int x) {
		for (int i = 0; i < this.imageHeight; i++) {
			if (this.pixelMap[i * this.imageWidth + x] != -1) {
				return false;
			}
		}
		return true;
	}

}
