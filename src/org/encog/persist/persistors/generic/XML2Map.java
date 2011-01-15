package org.encog.persist.persistors.generic;

import org.encog.mathutil.matrices.Matrix;
import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;
import org.encog.persist.persistors.BasicLayerPersistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class XML2Map {
	
	public PersistedObject load(ReadXML in)
	{		
		String type = in.getTag().getName();
		PersistedObject result = new PersistedObject();
		result.clear(type);
		
		// handle attributes
		for( String key: in.getTag().getAttributes().keySet() )
		{
			String value = in.getTag().getAttributeValue(key);
			result.setProperty(key, value, true);
		}
		
		String objectName = in.getTag().getName();
		// handle properties
		while (in.readToTag()) {
			if( in.getTag().getType()==Type.BEGIN ) {
				String name = in.getTag().getName();
				String str = in.readTextToTag();
				if( in.getTag().getName().equals(PersistConst.LIST)) {
					str = in.readTextToTag();
					result.setPropertyList(name,str);
				} else if( in.getTag().getName().equals(PersistConst.DATA)) {
					str = in.readTextToTag();
					double[] d = NumberList.fromList(CSVFormat.ENGLISH, str);
					result.setProperty(name, d);
				} else if( in.getTag().getName().equals(PersistConst.MATRIX)) { 
					str = in.readTextToTag();
					result.setProperty(name, inputMatrix(str));
				} else {
					result.setProperty(name, str, false);
				}
			} else if( in.getTag().getType()==Type.END ) {
				if( in.getTag().getName().equals(objectName))
					break;
			}
		}
		
		return result;
	}
	
	private Matrix inputMatrix(String line)
	{
		double[] d = NumberList.fromList(CSVFormat.EG_FORMAT, line);
		int rows = (int)d[0];
		int cols = (int)d[1];
		
		Matrix result = new Matrix(rows,cols);
		
		int index = 2;
		for(int r = 0;r<rows;r++)
		{
			for(int c = 0; c< cols; c++)
			{
				result.set(r,c,d[index++]);
			}
		}
		
		return result;
	}
}
