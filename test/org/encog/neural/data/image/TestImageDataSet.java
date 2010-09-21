/*
 * Encog(tm) Unit Tests v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Iterator;

import javax.swing.JFrame;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.downsample.Downsample;
import org.encog.util.downsample.SimpleIntensityDownsample;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestImageDataSet extends TestCase {

	public void testImageDataSet()
	{
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
		ImageNeuralDataSet set = new ImageNeuralDataSet(downsample,true,-1,1);
		BasicNeuralData ideal = new BasicNeuralData(1);
		ImageNeuralData input = new ImageNeuralData(image);
		set.add(input,ideal);
		set.downsample(2,2);
		Iterator<NeuralDataPair> itr = set.iterator();
		NeuralDataPair pair = (NeuralDataPair)itr.next();
		NeuralData data = pair.getInput();
		double[] d = data.getData();
		Assert.assertEquals(d[0],-1.0, 0.1);
		Assert.assertEquals(d[5],1, 0.1);
		
		// just "flex" these for no exceptions
		input.toString();
		input.setImage(input.getImage());
	}
	
}
