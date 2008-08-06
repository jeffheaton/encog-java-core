package org.encog.util;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

public class DownSample implements ImageObserver {

	private Image image;
	private int pixelMap[];
	private double ratioX;
	private double ratioY;
	private int downSampleLeft;
	private int downSampleRight;
	private int downSampleTop;
	private int downSampleBottom;
	
	
	//final double ratio = Math.exp(Math.log(getStopTemperature()
	//		/ getStartTemperature())
	//		/ (getCycles() - 1));

	public DownSample(Image image)
	{
		this.image = image;
	}
	
	
	/**
	 * Called to downsample the image and store it in the down sample component.
	 */
	public double[] downSample(int height, int width) {
		final int w = this.image.getWidth(this);
		final int h = this.image.getHeight(this);

		double[] result = new double[height*width];
		
		final PixelGrabber grabber = new PixelGrabber(this.image, 0, 0, w, h,
				true);
		try {			
			grabber.grabPixels();
			this.pixelMap = (int[]) grabber.getPixels();
			findBounds(w, h);

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

		} catch (final InterruptedException e) {
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
	protected double downSampleRegion(final int x, final int y) {
		final int w = this.image.getWidth(this);
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
				final int loc = xx + (yy * w);
				int pixel = this.pixelMap[loc];
				int red   = (pixel >> 16) & 0xff;
				int green = (pixel >>  8) & 0xff;
				int blue  = (pixel      ) & 0xff;
				
				redTotal+=red;
				greenTotal+=green;
				blueTotal+=blue;
				total++;

				if (this.pixelMap[loc] != -1) {
					return 1.0;
				}
			}
		}
		
		

		return 0.0;
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
	protected void findBounds(final int w, final int h) {
		// top line
		for (int y = 0; y < h; y++) {
			if (!hLineClear(y)) {
				this.downSampleTop = y;
				break;
			}

		}
		// bottom line
		for (int y = h - 1; y >= 0; y--) {
			if (!hLineClear(y)) {
				this.downSampleBottom = y;
				break;
			}
		}
		// left line
		for (int x = 0; x < w; x++) {
			if (!vLineClear(x)) {
				this.downSampleLeft = x;
				break;
			}
		}

		// right line
		for (int x = w - 1; x >= 0; x--) {
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
	protected boolean hLineClear(final int y) {
		final int w = this.image.getWidth(this);
		for (int i = 0; i < w; i++) {
			if (this.pixelMap[(y * w) + i] != -1) {
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
	protected boolean vLineClear(final int x) {
		final int w = this.image.getWidth(this);
		final int h = this.image.getHeight(this);
		for (int i = 0; i < h; i++) {
			if (this.pixelMap[(i * w) + x] != -1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return false;
	}

}
