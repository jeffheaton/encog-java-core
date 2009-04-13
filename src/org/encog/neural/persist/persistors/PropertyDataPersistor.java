package org.encog.neural.persist.persistors;

import org.encog.neural.data.PropertyData;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;

public class PropertyDataPersistor implements Persistor {

	public final static String TAG_PROPERTIES = "properties";
	public final static String TAG_PROPERTY = "Property";
	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_VALUE = "value";
	
	public EncogPersistedObject load(ReadXML in) {
		
		String name = in.getTag().getAttributes().get(EncogPersistedCollection.ATTRIBUTE_NAME);
		String description = in.getTag().getAttributes().get(EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);
		
		PropertyData result = new PropertyData();
		
		result.setName(name);
		result.setDescription(description);
		
		while( in.readToTag() )
		{
			if( in.is(TAG_PROPERTIES,true) )
			{
				handleProperties(in);
			}
			else if( in.is(TAG_PROPERTIES,false) )
			{
				break;
			}
			
		}
		
		return result;
	}

	private void handleProperties(ReadXML in) {
		while( in.readToTag() )
		{
			if( in.is(TAG_PROPERTIES,true) )
			{
				handleProperty(in);
			}
			else if( in.is(TAG_PROPERTIES,false) )
			{
				break;
			}
			
		}
		
	}

	private void handleProperty(ReadXML in) {
		// TODO Auto-generated method stub
		
	}

	public void save(EncogPersistedObject obj, WriteXML out) {
		
		PropertyData pData = (PropertyData)obj;
		
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_PROPERTY, out, obj, true);
		out.beginTag(TAG_PROPERTIES);
		for(String key: pData.getData().keySet())
		{
			out.addAttribute(ATTRIBUTE_NAME,key);
			out.addAttribute(ATTRIBUTE_VALUE, pData.get(key));
			out.beginTag(TAG_PROPERTY);
			out.endTag();
		}
		out.endTag();
		out.endTag();
		
	}

}
