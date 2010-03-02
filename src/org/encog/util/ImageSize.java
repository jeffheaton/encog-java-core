/*
 * Encog(tm) Core v2.4
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

package org.encog.util;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.concurrent.Semaphore;

import org.encog.EncogError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class to determine the size of an image.
 * 
 * @author jheaton
 */
public class ImageSize implements ImageObserver {

	/**
	 * The width of the image.
	 */
	private int width;

	/**
	 * The height of the image.
	 */
	private int height;

	/**
	 * Wait for the values to be set.
	 */
	private final Semaphore wait;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Determine the size of an image.
	 * 
	 * @param image
	 *            The image to be sized.
	 */
	public ImageSize(final Image image) {
		this.wait = new Semaphore(0);
		this.width = image.getWidth(this);
		this.height = image.getHeight(this);
		if ((this.width == -1) || (this.height == -1)) {
			try {
				this.wait.acquire();
			} catch (final InterruptedException e) {
				if (this.logger.isErrorEnabled()) {
					this.logger.error("Exception", e);
				}
				throw new EncogError(e);
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

	/**
	 * The image has been updated.
	 * 
	 * @param img
	 *            The image.
	 * @param infoflags
	 *            Which data has been loaded.
	 * @param x
	 *            Not used.
	 * @param y
	 *            Not used.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return True if more data is still needed.
	 */
	public boolean imageUpdate(final Image img, final int infoflags,
			final int x, final int y, final int width, final int height) {
		final int c = ImageObserver.HEIGHT | ImageObserver.WIDTH;

		if ((infoflags & c) != c) {
			return true;
		}

		this.height = height;
		this.width = width;
		return false;
	}

}
