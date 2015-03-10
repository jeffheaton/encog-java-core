/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
import java.awt.image.BufferedImage;

import junit.framework.Assert;

import org.junit.Test;

public class TestRGBDownsample {
	@Test
	public void testSameSize() {
		Image image = new BufferedImage(320,200,BufferedImage.TYPE_INT_RGB);
		RGBDownsample ds = new RGBDownsample();
		double[] d = ds.downSample(image, 200, 320);
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
