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
import org.encog.util.ImageSize;

/**
 * Downsample an image keeping the RGB colors.
 * 
 */
public class RGBDownsample implements Downsample {
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
	 * The current red average.
	 */
	private int currentRed;

	/**
	 * The current blue average.
	 */
	private int currentBlue;

	/**
	 * The current green average.
	 */
	private int currentGreen;

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
	public double[] downSample(final Image image, final int height,
			final int width) {

		processImage(image);

		final double[] result = new double[height * width * 3];

		final PixelGrabber grabber = new PixelGrabber(image, 0, 0,
				this.imageWidth, this.imageHeight, true);

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
				downSampleRegion(x, y);
				result[index++] = this.currentRed;
				result[index++] = this.currentGreen;
				result[index++] = this.currentBlue;
			}
		}

		return result;
	}

	/**
	 * Called to downsample a region of the image.
	 * 
	 * @param x
	 *            The x coordinate of the resulting downsample.
	 * @param y
	 *            The y coordinate of the resulting downsample.
	 */
	public void downSampleRegion(final int x, final int y) {
		final int startX = (int) (this.downSampleLeft + x * this.ratioX);
		final int startY = (int) (this.downSampleTop + y * this.ratioY);
		int endX = (int) (startX + this.ratioX);
		int endY = (int) (startY + this.ratioY);

		endX = Math.min(this.imageWidth, endX);
		endY = Math.min(this.imageHeight, endY);

		int redTotal = 0;
		int greenTotal = 0;
		int blueTotal = 0;

		int total = 0;

		for (int yy = startY; yy < endY; yy++) {
			for (int xx = startX; xx < endX; xx++) {
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

		this.currentRed = redTotal / total;
		this.currentGreen = greenTotal / total;
		this.currentBlue = blueTotal / total;
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

	public int getCurrentBlue() {
		return this.currentBlue;
	}

	public int getCurrentGreen() {
		return this.currentGreen;
	}

	public int getCurrentRed() {
		return this.currentRed;
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
	 * 
	 * @param image
	 *            The image to downsample.
	 */
	public void processImage(final Image image) {
		final ImageSize size = new ImageSize(image);
		this.imageHeight = size.getHeight();
		this.imageWidth = size.getWidth();
		this.downSampleLeft = 0;
		this.downSampleTop = 0;
		this.downSampleRight = this.imageWidth;
		this.downSampleBottom = this.imageHeight;

		this.ratioX = (double) (this.downSampleRight - this.downSampleLeft)
				/ (double) getImageWidth();
		this.ratioY = (double) (this.downSampleBottom - this.downSampleTop)
				/ (double) getImageHeight();
	}

	/**
	 * Set the current blue average.
	 * @param currentBlue The current blue average.
	 */
	public void setCurrentBlue(final int currentBlue) {
		this.currentBlue = currentBlue;
	}
	
	/**
	 * Set the current green average.
	 * @param currentGreen The current green average.
	 */
	public void setCurrentGreen(final int currentGreen) {
		this.currentGreen = currentGreen;
	}

	/**
	 * Set the current red average.
	 * @param currentRed The current red average.
	 */
	public void setCurrentRed(final int currentRed) {
		this.currentRed = currentRed;
	}

	/**
	 * Set the pixel map.
	 * @param pixelMap The pixel map.
	 */
	public void setPixelMap(final int[] pixelMap) {
		this.pixelMap = pixelMap;
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
