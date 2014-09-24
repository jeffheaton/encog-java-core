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
package org.encog.ca.universe.basic;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.encog.ca.CellularAutomataError;
import org.encog.ca.universe.UniverseCell;
import org.encog.ca.universe.UniverseCellFactory;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogPersistor;
import org.encog.persist.EncogReadHelper;
import org.encog.persist.EncogWriteHelper;
import org.encog.persist.PersistConst;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class PersistBasicUniverse implements EncogPersistor {

	@Override
	public String getPersistClassString() {
		return "BasicUniverse";
	}

	@Override
	public Object read(InputStream is) {
		int rows=0; 
		int cols=0;
		int elementCount=0;
		int size=0;
		double min=0;
		double max=0;
		Map<String, String> objParams=null;
		BasicUniverse result = null;
		final EncogReadHelper in = new EncogReadHelper(is);
		EncogFileSection section;

		while ((section = in.readNextSection()) != null) {
			if (section.getSectionName().equals("BasicUniverse")
					&& section.getSubSectionName().equals("PARAMS")) {
				objParams = section.parseParams();				
			}
			if (section.getSectionName().equals("BasicUniverse")
					&& section.getSubSectionName().equals("UNIVERSE-PARAM")) {
				final Map<String, String> params = section.parseParams();
				cols = EncogFileSection.parseInt(params,PersistConst.COLS);
				rows = EncogFileSection.parseInt(params,PersistConst.ROWS);				
			} else if (section.getSectionName().equals("BasicUniverse")
					&& section.getSubSectionName().equals("UNIVERSE-CELLS")) {
				final Map<String, String> params = section.parseParams();
				min = EncogFileSection.parseDouble(params,PersistConst.MIN);
				max = EncogFileSection.parseDouble(params,PersistConst.MAX);
				elementCount = EncogFileSection.parseInt(params,BasicUniverse.ELEMENT_COUNT);
				size = EncogFileSection.parseInt(params,PersistConst.SIZE);
				
			} else if (section.getSectionName().equals("BasicUniverse")
					&& section.getSubSectionName().equals("UNIVERSE")) {
				
				// first create the universe
				BasicCellFactory factory;
				
				if( elementCount==-1 ) {
					factory = new BasicCellFactory(size,min,max);
				} else {
					factory = new BasicCellFactory(size,elementCount);
				}
				
				result = new BasicUniverse(rows,cols,factory);
				result.getProperties().putAll(objParams);
				
				// now fill the universe
				int ec = 1;
				if( elementCount!=-1 ) {
					ec = elementCount;
				}
				int row = 0;
				for(String line : section.getLines() ) {
					double[] d = NumberList.fromList(CSVFormat.EG_FORMAT, line);
					int idx = 0;
					for(int col=0;col<cols;col++) {
						UniverseCell cell = result.get(row, col);
						for(int i=0;i<size;i++) {
							cell.set(i, d[idx++]);
						}
					}
					row++;
				}
			}
		}

		return result;
	}

	@Override
	public void save(OutputStream os, Object obj) {
		final EncogWriteHelper out = new EncogWriteHelper(os);
		final BasicUniverse universe = (BasicUniverse) obj;
		out.addSection("BasicUniverse");
		out.addSubSection("PARAMS");
		out.addProperties(universe.getProperties());
		out.addSubSection("UNIVERSE-PARAM");
		out.writeProperty(PersistConst.ROWS, universe.getRows());
		out.writeProperty(PersistConst.COLS, universe.getColumns());
		out.addSubSection("UNIVERSE-CELLS");
		UniverseCellFactory factory = universe.getCellFactory();
		String className = factory.getClass().getSimpleName();
		out.writeProperty(PersistConst.TYPE, className);
		if( factory instanceof BasicCellFactory ) {
			BasicCellFactory factory2 = (BasicCellFactory)factory;
			out.writeProperty(BasicUniverse.ELEMENT_COUNT, factory2.getElementCount());
			out.writeProperty(PersistConst.MAX, factory2.getMax());
			out.writeProperty(PersistConst.MIN, factory2.getMin());
			out.writeProperty(PersistConst.SIZE, factory2.size());
		} else {
			throw new CellularAutomataError("Unknown cell factory: " + className);
		}
		out.addSubSection("UNIVERSE");
		for(int row = 0; row<universe.getRows(); row++) {			
			for(int col = 0; col<universe.getColumns(); col++) {
				UniverseCell cell = universe.get(row, col);
				for(int i=0;i<cell.size();i++) {
					out.addColumn(cell.get(i));	
				}
				
			}
			out.writeLine();
		}
	

		out.flush();

	}

	@Override
	public int getFileVersion() {
		return 0;
	}

}
