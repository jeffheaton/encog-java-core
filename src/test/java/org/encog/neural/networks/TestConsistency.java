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
package org.encog.neural.networks;

import junit.framework.TestCase;

import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.benchmark.EncoderTrainingFactory;
import org.encog.util.simple.EncogUtility;
import org.junit.Assert;

public class TestConsistency extends TestCase {

	public static final double[] EXPECTED_WEIGHTS1 = { 0.008012107263322008,1.3830172071769407,-0.027657273609111438,0.3926920473512011,-0.5591917997643333,-0.03508764590487992,-0.8339860696052167,0.1371821074024733,0.6804152092361858,0.9587552253200567,-0.9363149724379914,-0.28898946379986346,1.0572222265035895,0.3146739685034085,-0.8752594385878787,0.4819077576654748,0.7108891944426319,0.7165167879211988,-0.49437671786974574,-0.5433328356252362,-0.563603612348345,0.559330141185627 };
	public static final double[] EXPECTED_WEIGHTS2 = { 0.040412107263322006,1.6318492071769406,0.058742726390888546,0.43589204735120113,-0.5159917997643333,0.008112354095120074,-0.8555860696052167,0.07497410740247332,0.7668152092361858,0.9911552253200567,-0.8643149724379915,-0.26738946379986345,1.0788222265035896,0.3470739685034085,-0.8302594385878788,1.1248619976654748,0.7984891944426319,0.6841167879211988,-0.6059767178697457,-0.6729328356252361,-0.720851612348345,0.551830141185627 };
	
	public void testRPROPConsistency() {
		MLDataSet training = EncoderTrainingFactory.generateTraining(4, false);
		BasicNetwork network = EncogUtility.simpleFeedForward(4, 2, 0, 4, true);
		(new ConsistentRandomizer(-1,1,50)).randomize(network);
		ResilientPropagation rprop = new ResilientPropagation(network,training);
		for(int i=0;i<5;i++) {
			rprop.iteration();
		}
		Assert.assertArrayEquals(EXPECTED_WEIGHTS1, network.getFlat().getWeights(),0.0001);
		for(int i=0;i<5;i++) {
			rprop.iteration();
		}
		Assert.assertArrayEquals(EXPECTED_WEIGHTS2, network.getFlat().getWeights(),0.0001);	
		
		double e = network.calculateError(training);
		Assert.assertEquals(0.0767386807494191, e, 0.00001);
		
		
	}
	
	public void testFileConsistency() {
		MLDataSet training = EncoderTrainingFactory.generateTraining(4, false);
		BasicNetwork network = (BasicNetwork)EncogDirectoryPersistence.loadResourceObject("org/encog/data/encodernet.eg");
		double e = network.calculateError(training);
		Assert.assertEquals(0.0767386807494191, e, 0.00001);
	}
	
}
