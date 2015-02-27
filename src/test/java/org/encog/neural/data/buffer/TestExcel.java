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
package org.encog.neural.data.buffer;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.data.buffer.BinaryDataLoader;
import org.encog.ml.data.buffer.codec.ArrayDataCODEC;
import org.encog.ml.data.buffer.codec.ExcelCODEC;
import org.encog.neural.networks.XOR;


public class TestExcel extends TestCase {
	public void testXLSX() {
		final ArrayDataCODEC codec = new ArrayDataCODEC(XOR.XOR_INPUT,
				XOR.XOR_IDEAL);
		final BinaryDataLoader loader = new BinaryDataLoader(codec);
		loader.external2Binary(new File("encog.bin"));

		final ExcelCODEC codec2 = new ExcelCODEC(new File("encog.xlsx"));
		final BinaryDataLoader loader2 = new BinaryDataLoader(codec2);
		loader2.binary2External(new File("encog.bin"));

		final ExcelCODEC codec3 = new ExcelCODEC(new File("encog.xlsx"), 2, 1);
		final BinaryDataLoader loader3 = new BinaryDataLoader(codec3);
		loader3.external2Binary(new File("encog.bin"));

		final ArrayDataCODEC codec4 = new ArrayDataCODEC();
		final BinaryDataLoader loader4 = new BinaryDataLoader(codec4);
		loader4.binary2External(new File("encog.bin"));

		final double[][] input = codec4.getInput();
		final double[][] ideal = codec4.getIdeal();

		for (int i = 0; i < XOR.XOR_INPUT.length; i++) {
			for (int j = 0; j < XOR.XOR_INPUT[i].length; j++) {
				Assert.assertEquals(input[i][j], XOR.XOR_INPUT[i][j], 0.01);
			}

			for (int j = 0; j < XOR.XOR_IDEAL[i].length; j++) {
				Assert.assertEquals(ideal[i][j], XOR.XOR_IDEAL[i][j], 0.01);
			}
		}
	}

}
