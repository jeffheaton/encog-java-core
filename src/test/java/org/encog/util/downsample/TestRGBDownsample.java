package org.encog.util.downsample;

import java.awt.Image;
import java.awt.image.BufferedImage;

import junit.framework.Assert;

import org.junit.Test;

public class TestRGBDownsample {
	@Test
	public void testSameSize() {
		Image image = new BufferedImage(320,200,BufferedImage.TYPE_INT_RGB);
		RGBDownsample ds = new RGBDownsample();
		double[] d = ds.downSample(image, 320, 200);
		Assert.assertEquals(192000, d.length);
	}
	
	@Test
	public void testDifferentSize() {
		Image image = new BufferedImage(320,200,BufferedImage.TYPE_INT_RGB);
		RGBDownsample ds = new RGBDownsample();
		double[] d = ds.downSample(image, 160, 100);
		Assert.assertEquals(48000, d.length);
	}
}
