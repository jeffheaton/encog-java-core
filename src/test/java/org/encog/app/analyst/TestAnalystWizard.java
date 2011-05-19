/*
 * Encog(tm) Core Unit Tests v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.app.analyst;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.app.analyst.report.AnalystReport;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.util.TempDir;
import org.encog.util.file.FileUtil;

public class TestAnalystWizard extends TestCase {
	public final TempDir TEMP_DIR = new TempDir();
	
	public void testIrisWizard() throws Exception {
		File rawFile = TEMP_DIR.createFile("iris_raw.csv");
		FileUtil.copyResource("org/encog/data/iris.csv", rawFile);
		
		File analystFile = TEMP_DIR.createFile("iris.ega");
		
		EncogAnalyst encog = new EncogAnalyst();
		encog.setMaxIteration(1);
		AnalystWizard wiz = new AnalystWizard(encog);		
		wiz.setGoal(AnalystGoal.Classification);
		wiz.wizard(rawFile, true, AnalystFileFormat.DECPNT_COMMA);
		

		encog.executeTask("task-full");
		
		encog.save(analystFile);
		encog.load(analystFile);
		
		AnalystReport report = new AnalystReport(encog);
		report.produceReport(TEMP_DIR.createFile("report.html"));
		
		Assert.assertEquals(5,encog.getScript().getNormalize().getNormalizedFields().size());
		Assert.assertEquals(4.3,encog.getScript().getFields()[0].getMin(),0.001);
		Assert.assertEquals(7.9,encog.getScript().getFields()[0].getMax(),0.001);
		Assert.assertEquals(5.84333,encog.getScript().getFields()[0].getMean(),0.001);
		Assert.assertEquals(encog.getScript().getFields()[0].isClass(),false);
		Assert.assertEquals(encog.getScript().getFields()[0].isReal(),true);
		Assert.assertEquals(encog.getScript().getFields()[0].isInteger(),false);
		Assert.assertEquals(encog.getScript().getFields()[0].isComplete(),true);
		Assert.assertEquals(-3.38833, encog.getScript().getNormalize().getNormalizedFields().get(0).normalize(0.001),0.001);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
