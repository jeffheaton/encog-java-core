package org.encog.ca.visualize.basic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.encog.ca.universe.DiscreteCell;
import org.encog.ca.universe.Universe;
import org.encog.ca.universe.UniverseCell;
import org.encog.ca.visualize.CAVisualizer;

public class BasicCAVisualizer implements CAVisualizer {
	private Universe universe;
	
	public BasicCAVisualizer(Universe theUniverse) {
		this.universe = theUniverse;
	}
	
	private Image createImageLabel(int[] pixels, int width, int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		raster.setPixels(0, 0, width, height, pixels);
		return image;
	}
	
	public Image visualize()
	{
		int width = universe.getColumns();
		int height = universe.getRows();
		int imageSize = width * height;

		int[] pixels = new int[imageSize * 3];

		// Create Red Image

		int idx = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				UniverseCell cell = universe.get(row, col);
				
				if( cell instanceof DiscreteCell ) {
					if( cell.get(0)>0 ) {
						pixels[idx++] = 255;
						pixels[idx++] = 255;
						pixels[idx++] = 255;
					} else {
						pixels[idx++] = 0;
						pixels[idx++] = 0;
						pixels[idx++] = 0;
					}
				} else {
					for (int i = 0; i < 3; i++) {
						double d = (cell.get(i)+1.0)/2.0;
						pixels[idx++] = (int) (d * 255.0);
					}	
				}
			}
		}

		return createImageLabel(pixels, width, height);

	}
}
