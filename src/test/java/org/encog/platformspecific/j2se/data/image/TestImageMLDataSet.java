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
package org.encog.platformspecific.j2se.data.image;

import java.io.File;

import javax.imageio.ImageIO;

import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.TempDir;
import org.encog.util.csv.CSVFormat;
import org.encog.util.downsample.Downsample;
import org.encog.util.downsample.SimpleIntensityDownsample;
import org.encog.util.file.FileUtil;
import org.encog.util.obj.SerializeObject;
import org.encog.util.simple.EncogUtility;
import org.junit.Test;

public class TestImageMLDataSet {
	
	public final TempDir TEMP_DIR = new TempDir();
	public final File IMG_A = TEMP_DIR.createFile("a.png");
	public final File IMG_B = TEMP_DIR.createFile("b.png");
	public final File IMG_C = TEMP_DIR.createFile("c.png");
	public final File FILE_CSV = TEMP_DIR.createFile("test.csv");
	
	@Test
	public void testSerialize() throws Exception {
		FileUtil.copyResource("org/encog/data/img/a.png", IMG_A);
		FileUtil.copyResource("org/encog/data/img/b.png", IMG_B);
		FileUtil.copyResource("org/encog/data/img/c.png", IMG_C);
		
		
		Downsample downsampler = new SimpleIntensityDownsample();
		ImageMLDataSet training = new ImageMLDataSet(downsampler, false, 1, -1);
		
		ImageMLData inputImage1 = new ImageMLData(ImageIO.read(IMG_A));
		ImageMLData inputImage2 = new ImageMLData(ImageIO.read(IMG_B));
		ImageMLData inputImage3 = new ImageMLData(ImageIO.read(IMG_C));
		
		BasicMLData idealImage1 = new BasicMLData(new double[]{1.0,0.0,0.0});
		BasicMLData idealImage2 = new BasicMLData(new double[]{0.0,1.0,0.0});
		BasicMLData idealImage3 = new BasicMLData(new double[]{0.0,0.0,1.0});
		
		training.add(inputImage1,idealImage1);
		training.add(inputImage2,idealImage2);
		training.add(inputImage3,idealImage3);
		
		training.downsample(10, 10);
		
		EncogUtility.saveCSV(FILE_CSV, CSVFormat.DECIMAL_POINT, training );
	}

}
