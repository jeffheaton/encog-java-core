/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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

public class SimpleIntensityDownsample implements Downsample {

	private Image image;
	private int pixelMap[];
	private double ratioX;
	private double ratioY;
	private int imageHeight;
	private int imageWidth;
	private int downSampleLeft;
	private int downSampleRight;
	private int downSampleTop;
	private int downSampleBottom;

	public SimpleIntensityDownsample(Image image) {
		processImage(image);
	}
	
	public void processImage(Image image) {
		this.image = image;
		ImageSize size = new ImageSize(image);
		this.imageHeight = size.getHeight();
		this.imageWidth = size.getWidth();
		this.downSampleLeft = 0;
		this.downSampleTop = 0;
		this.downSampleRight = this.imageWidth;
		this.downSampleBottom = this.imageHeight;
	}

	/**
	 * Called to downsample the image and store it in the down sample component.
	 */
	public double[] downSample(int height, int width) {

		double[] result = new double[height * width];

		final PixelGrabber grabber = new PixelGrabber(this.image, 0, 0,
				this.imageWidth, this.imageWidth, true);

		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
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
		final int startX = (int) (this.downSampleLeft + (x * this.ratioX));
		final int startY = (int) (this.downSampleTop + (y * this.ratioY));
		final int endX = (int) (startX + this.ratioX);
		final int endY = (int) (startY + this.ratioY);

		int redTotal = 0;
		int greenTotal = 0;
		int blueTotal = 0;

		int total = 0;

		for (int yy = startY; yy <= endY; yy++) {
			for (int xx = startX; xx <= endX; xx++) {
				final int loc = xx + (yy * this.imageWidth);
				int pixel = this.pixelMap[loc];
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				redTotal += red;
				greenTotal += green;
				blueTotal += blue;
				total++;
			}
		}

		return (redTotal+greenTotal+blueTotal)/(total*3);
	}

	/**
	 * This method is called to automatically crop the image so that whitespace
	 * is removed.
	 * 
	 * @param w
	 *            The width of the image.
	 * @param h
	 *            The height of the image
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
	 * This method is called internally to see if there are any pixels in the
	 * given scan line. This method is used to perform autocropping.
	 * 
	 * @param y
	 *            The horizontal line to scan.
	 * @return True if there were any pixels in this horizontal line.
	 */
	private boolean hLineClear(final int y) {
		for (int i = 0; i < this.imageWidth; i++) {
			if (this.pixelMap[(y * this.imageWidth) + i] != -1) {
				return false;
			}
		}
		return true;
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
			if (this.pixelMap[(i * this.imageWidth) + x] != -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @return the pixelMap
	 */
	public int[] getPixelMap() {
		return pixelMap;
	}

	/**
	 * @return the ratioX
	 */
	public double getRatioX() {
		return ratioX;
	}

	/**
	 * @return the ratioY
	 */
	public double getRatioY() {
		return ratioY;
	}

	/**
	 * @return the imageHeight
	 */
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 * @return the imageWidth
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 * @return the downSampleLeft
	 */
	public int getDownSampleLeft() {
		return downSampleLeft;
	}

	/**
	 * @return the downSampleRight
	 */
	public int getDownSampleRight() {
		return downSampleRight;
	}

	/**
	 * @return the downSampleTop
	 */
	public int getDownSampleTop() {
		return downSampleTop;
	}

	/**
	 * @return the downSampleBottom
	 */
	public int getDownSampleBottom() {
		return downSampleBottom;
	}
	
	
	
}
