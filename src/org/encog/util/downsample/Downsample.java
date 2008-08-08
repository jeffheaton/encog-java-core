package org.encog.util.downsample;

import java.awt.Image;

public interface Downsample {

	public double[] downSample(int height, int width);
	public void processImage(Image image);
	public void findBounds();
	public Image getImage();
	public int[] getPixelMap();
	public double getRatioX();
	public double getRatioY();
	public int getImageHeight();
	public int getImageWidth();
	public int getDownSampleLeft();
	public int getDownSampleRight();
	public int getDownSampleTop();
	public int getDownSampleBottom();
}
