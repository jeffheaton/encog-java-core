package org.encog.persist.persistors.generic;

import org.encog.Encog;
import org.encog.mathutil.matrices.Matrix;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedActivationFunction;
import org.encog.persist.map.PersistedDoubleArray;
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
					out.beginTag(key);
					out.beginTag(PersistConst.LIST);
					out.addText(property.getString());
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
