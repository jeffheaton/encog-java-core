/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
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
