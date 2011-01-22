/*
 * Encog(tm) Core v3.0 - Java Version
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
package org.encog.persist.persistors.generic;

import org.encog.Encog;
import org.encog.mathutil.matrices.Matrix;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedActivationFunction;
import org.encog.persist.map.PersistedDoubleArray;
import org.encog.persist.map.PersistedIntArray;
import org.encog.persist.map.PersistedMatrix;
import org.encog.persist.map.PersistedObject;
import org.encog.persist.map.PersistedProperty;
import org.encog.persist.map.PersistedValue;
import org.encog.persist.map.PersistedValueArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class Map2XML {
	
	public void save(PersistedObject obj, WriteXML out)
	{
		// first write out the attributes
		for( String key : obj.getData().keySet() )
		{
			PersistedProperty property = obj.getData().get(key);
			
			if( property.isAttribute() ) {
				out.addAttribute(key, property.getString());
			}
		}
		
		// write the opening tag
		out.beginTag(obj.getObjectType());
		
		// now deal with actual properties
		for( String key : obj.getData().keySet() )
		{
			PersistedProperty property = obj.getData().get(key);
			
			if( !property.isAttribute() ) {
				if( property instanceof PersistedValue )
				{
					out.addProperty(key, property.getString());
				}
				else if( property instanceof PersistedValueArray )
				{
					PersistedValueArray pva = (PersistedValueArray)property;
					out.beginTag(key);
					out.beginTag(PersistConst.LIST);
					for(PersistedObject po: pva.getList())
					{
						save(po,out);
					}
					out.endTag();
					out.endTag();
				}
				else if( property instanceof PersistedDoubleArray )
				{
					out.beginTag(key);
					out.beginTag(PersistConst.DATA);
					out.addText(property.getString());
					out.endTag();
					out.endTag();
				}
				else if( property instanceof PersistedIntArray )
				{
					out.beginTag(key);
					out.beginTag(PersistConst.IDATA);
					out.addText(property.getString());
					out.endTag();
					out.endTag();
				}
				else if( property instanceof PersistedMatrix )
				{
					PersistedMatrix pm = (PersistedMatrix)property;
					out.beginTag(key);
					out.beginTag(PersistConst.MATRIX);
					out.addText(outputMatrix(pm.getMatrix()));
					out.endTag();
					out.endTag();
				}
				else if( property instanceof PersistedActivationFunction )
				{
					PersistedActivationFunction pa = (PersistedActivationFunction)property;
					out.beginTag(key);
					out.beginTag(PersistConst.ACTIVATION_TYPE);
					ActivationPersistUtil.saveActivationFunction(pa.getActivationFunction(), out);
					out.endTag();
					out.endTag();
				}
			}
		}		
		
		// close the opening tag
		out.endTag();
	}
	
	private String outputMatrix(Matrix matrix)
	{
		StringBuilder result = new StringBuilder();
		result.append(matrix.getRows());
		result.append(',');
		result.append(matrix.getCols());		
		
		for(int row=0;row<matrix.getRows();row++)
		{
			for(int col=0;col<matrix.getCols();col++)
			{
				result.append(',');
				result.append(CSVFormat.EG_FORMAT.format(matrix.get(row, col), Encog.DEFAULT_PRECISION));
			}
		}
		
		return result.toString();
	}
}
