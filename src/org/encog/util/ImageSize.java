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
