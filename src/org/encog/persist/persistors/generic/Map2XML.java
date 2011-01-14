package org.encog.persist.persistors.generic;

import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;
import org.encog.persist.map.PersistedProperty;
import org.encog.persist.map.PersistedValue;
import org.encog.persist.map.PersistedValueArray;

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
			}
		}		
		
		// close the opening tag
		out.endTag();
	}
}
