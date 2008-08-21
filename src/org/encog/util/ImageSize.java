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

package org.encog.util;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.concurrent.Semaphore;

import org.encog.EncogError;

public class ImageSize implements ImageObserver {
	
	private int width;
	private int height; 
	private Semaphore wait;
	
	public ImageSize(Image image)
	{
		this.wait = new Semaphore(0);
		this.width = image.getWidth(this);
		this.height = image.getHeight(this);
		if( this.width ==-1 || this.height==-1 )
		{
			try {
				wait.acquire();
			} catch (InterruptedException e) {
				throw new EncogError(e);
			}
		}
		
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y,
			int width, int height) {
		int c = (ImageObserver.HEIGHT|ImageObserver.WIDTH);
		
		if( (infoflags & c) != c )
		{
			return true;
		}
		
		this.height = height;
		this.width = width;
		return false;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	
}
