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
package org.encog.util;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.concurrent.Semaphore;

import org.encog.EncogError;

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
