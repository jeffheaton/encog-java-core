package org.encog.persist.persistors;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class TrainingContinuationPersistor implements Persistor {

	public static final String TAG_ITEMS = "items";
	public static final String TAG_ITEM = "Item";
	public static final String ATTRIBUTE_NAME = "name";
	
	private TrainingContinuation current;
	
	@Override
	public EncogPersistedObject load(ReadXML in) {
		this.current = new TrainingContinuation();
		
		final String name = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_NAME);
		final String description = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);

		this.current.setName(name);
		this.current.setDescription(description);

		while (in.readToTag()) {
			if (in.is(TrainingContinuationPersistor.TAG_ITEMS, true)) {
				handleItems(in);
			}  else if (in.is(EncogPersistedCollection.TYPE_TRAINING_CONTINUATION, false)) {
				break;
			}
		}
		
		return this.current;
	}
	
	public void handleItems(ReadXML in) {
		while (in.readToTag()) {
			if (in.is(TrainingContinuationPersistor.TAG_ITEM, true)) {
				handleItem(in);
			}  else if (in.is(TrainingContinuationPersistor.TAG_ITEM, false)) {
				break;
			}
		}
	}
	
	public void handleItem(ReadXML in) {
		String name = in.getTag().getAttributeValue(TrainingContinuationPersistor.ATTRIBUTE_NAME);
		String str = in.readTextToTag();
		double[] list = NumberList.fromList(CSVFormat.EG_FORMAT, str);
		this.current.put(name,list);
		
	}
	
	public void saveItems(WriteXML out)
	{
		for(String key: this.current.getContents().keySet()) {
			out.addAttribute(ATTRIBUTE_NAME, key);
			out.beginTag(TAG_ITEM);
			double[] value = (double[])this.current.get(key);
			final StringBuilder result = new StringBuilder();
			NumberList
					.toList(CSVFormat.EG_FORMAT, result, value);
			out.addText(result.toString());
			out.endTag();
		}
	}

	@Override
	public void save(EncogPersistedObject obj, WriteXML out) {

		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_TRAINING_CONTINUATION,
				out, obj, true);
		this.current = (TrainingContinuation) obj;


		// save the layers
		out.beginTag(BasicNetworkPersistor.TAG_LAYERS);
		saveItems(out);
		out.endTag();

		out.endTag();		
	}

}
