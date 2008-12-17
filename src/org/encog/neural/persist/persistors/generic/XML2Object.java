package org.encog.neural.persist.persistors.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.neural.networks.Layer;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XML2Object {

	public EncogPersistedObject load(Element node, EncogPersistedObject target) {

		try {
			for (Node child = node.getFirstChild(); child != null; child = child
					.getNextSibling()) {
				if (!(child instanceof Element)) {
					continue;
				}
				final Element element = (Element) child;
				// System.out.println(element.getNodeValue());

				Field field = target.getClass().getDeclaredField(
						element.getNodeName());
				field.setAccessible(true);
				String value = XMLUtil.getElementValue(element);
				Class<?> type = field.getType();
				if (type == long.class) {
					field.setLong(target, Long.parseLong(value));
				} else if (type == int.class) {
					field.setInt(target, Integer.parseInt(value));
				} else if (type == short.class) {
					field.setShort(target, Short.parseShort(value));
				} else if (type == String.class) {
					field.set(target, value);
				} else if (type == List.class) {
					field.set(target, loadList(element));
				}

			}
			return target;
		} catch (NoSuchFieldException e) {
			throw new EncogError(e);
		} catch (NumberFormatException e) {
			throw new EncogError(e);
		} catch (IllegalArgumentException e) {
			throw new EncogError(e);
		} catch (IllegalAccessException e) {
			throw new EncogError(e);
		}
	}

	public List<Object> loadList(Element element) {
		List<Object> result = new ArrayList<Object>();

		for (Node child = element.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element)) {
				continue;
			}
			final Element e = (Element) child;
			String value = XMLUtil.getElementValue(e);
			
			if( e.getNodeName().equals("S"))
			{
				result.add(value);
			}
		}

		return result;
	}
}
