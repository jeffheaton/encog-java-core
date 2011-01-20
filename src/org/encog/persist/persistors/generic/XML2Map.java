package org.encog.persist.persistors.generic;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.matrices.Matrix;
import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class XML2Map {
	
	public PersistedObject load(ReadXML in)
	{		
		String type = in.getTag().getName();
		PersistedObject result = new PersistedObject();
		result.clear(type);
		loadObject(in,result);
		return result;
	}
	
	private void loadObject(ReadXML in, PersistedObject po)
	{
		// handle attributes
		for( String key: in.getTag().getAttributes().keySet() )
		{
			String value = in.getTag().getAttributeValue(key);
			po.setProperty(key, value, true);
		}
		
		String objectName = in.getTag().getName();
		// handle properties
		while (in.readToTag()) {
			if( in.getTag().getType()==Type.BEGIN ) {
				String name = in.getTag().getName();
				String str = in.readTextToTag();
				if( in.getTag().getName().equals(PersistConst.LIST)) {
					List<PersistedObject> list = inputList(in);
					po.setProperty(name, list);
				} else if( in.getTag().getName().equals(PersistConst.DATA)) {
					str = in.readTextToTag();
					double[] d = NumberList.fromList(CSVFormat.ENGLISH, str);
					po.setProperty(name, d);
				} else if( in.getTag().getName().equals(PersistConst.IDATA)) {
					str = in.readTextToTag();
					int[] d = NumberList.fromListInt(CSVFormat.ENGLISH, str);
					po.setProperty(name, d);
				} else if( in.getTag().getName().equals(PersistConst.MATRIX)) { 
					str = in.readTextToTag();
					po.setProperty(name, inputMatrix(str));
				} else if( in.getTag().getName().equals(PersistConst.ACTIVATION_TYPE)) { 
					in.readToTag();
					String t = in.getTag().getName();
					po.setProperty(name, ActivationPersistUtil.loadActivation(t, in));
				} else {
					po.setProperty(name, str, false);
				}
			} else if( in.getTag().getType()==Type.END ) {
				if( in.getTag().getName().equals(objectName))
					break;
			}
		}
		
	}
	
	private List<PersistedObject> inputList(ReadXML in) {
		List<PersistedObject> result = new ArrayList<PersistedObject>();
		String objectName = in.getTag().getName();
		// handle properties
		while (in.readToTag()) {
			if( in.getTag().getType()==Type.BEGIN ) {
				String type = in.getTag().getName();
				PersistedObject po = new PersistedObject();
				po.clear(type);
				loadObject(in,po);
				result.add( po );
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
