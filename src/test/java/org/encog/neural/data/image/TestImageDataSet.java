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
package org.encog.neural.data.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.util.Iterator;

import javax.swing.JFrame;

import junit.framework.TestCase;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.platformspecific.j2se.data.image.ImageMLData;
import org.encog.platformspecific.j2se.data.image.ImageMLDataSet;
import org.encog.util.downsample.Downsample;
import org.encog.util.downsample.SimpleIntensityDownsample;

public class TestImageDataSet extends TestCase {

	public void testImageDataSet()
	{
		try {
		JFrame frame = new JFrame();
		frame.setVisible(true);
		Image image = frame.createImage(10, 10);
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,10,10);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 5, 5);
		g.dispose();
		
		Downsample downsample = new SimpleIntensityDownsample();
		ImageMLDataSet set = new ImageMLDataSet(downsample,true,-1,1);
		BasicMLData ideal = new BasicMLData(1);
		ImageMLData input = new ImageMLData(image);
		set.add(input,ideal);
		set.downsample(2,2);
		Iterator<MLDataPair> itr = set.iterator();
		MLDataPair pair = (MLDataPair)itr.next();
		MLData data = pair.getInput();
		double[] d = data.getData();
		//Assert.assertEquals(d[0],-1.0, 0.1);
		//Assert.assertEquals(d[5],1, 0.1);
		
		// just "flex" these for no exceptions
		input.toString();
		input.setImage(input.getImage());
		}
		catch( HeadlessException ex) {
			// ignore if we are running headless (i.e. the build server)
		}
	}
	
}
