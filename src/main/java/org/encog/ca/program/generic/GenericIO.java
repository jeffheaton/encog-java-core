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
package org.encog.ca.program.generic;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.encog.ca.CellularAutomataError;
import org.encog.ca.program.CAProgram;
import org.encog.ca.runner.CARunner;
import org.encog.ca.universe.Universe;
import org.encog.ca.visualize.basic.BasicCAVisualizer;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.file.FileUtil;
import org.encog.util.obj.SerializeObject;

public class GenericIO {
	
	public static void save(CARunner runner, File f) {
		try {
			EncogDirectoryPersistence.saveObject(
					new File(FileUtil.forceExtension(f.toString(), "eg")),
					runner.getUniverse());
			SerializeObject.save(
					new File(FileUtil.forceExtension(f.toString(), "bin")),
					(Serializable)runner.getPhysics());
			BasicCAVisualizer visualizer = new BasicCAVisualizer(
					runner.getUniverse());
			Image img = visualizer.visualize();
			ImageIO.write((RenderedImage) img, "png",
					new File(FileUtil.forceExtension(f.toString(), "png")));
		} catch (IOException ex) {
			throw new CellularAutomataError(ex);
		}

	}

	public static void load(File f, CARunner runner) {
		try {
			Universe universe = (Universe)EncogDirectoryPersistence.loadObject(
					new File(FileUtil.forceExtension(f.toString(), "eg")));
			CAProgram physics = (CAProgram)SerializeObject.load(
					new File(FileUtil.forceExtension(f.toString(), "bin")));
			physics.setSourceUniverse(universe);
			runner.init(universe,physics);
		} catch (IOException ex) {
			throw new CellularAutomataError(ex);
		} catch (ClassNotFoundException ex) {
			throw new CellularAutomataError(ex);
		}
	}
}
