package org.encog.persist.persistors;

import org.encog.neural.networks.BasicNetwork;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.Persistor;
import org.encog.persist.map.PersistConst;
import org.encog.persist.persistors.generic.GenericPersistor;

public class BasicNetworkPersistor implements Persistor {

	public final GenericPersistor fallback = new GenericPersistor(BasicNetwork.class);

	@Override
	public EncogPersistedObject load(ReadXML in) {
		if( !in.getTag().getAttributes().containsKey(PersistConst.VERSION) )
		{
			throw new PersistError("You are attempting to read a resource from an Encog 2.x saved EG file.  Please use the class EncogUpgrade in Encog 2.5 to save this resource to compatible format.");
		}
		return fallback.load(in);
	}

	@Override
	public void save(EncogPersistedObject obj, WriteXML out) {
		fallback.save(obj, out);		
	}
	
}
