package org.encog.neural.persist.persistors;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.parse.ParseTemplate;
import org.encog.parse.recognize.Recognize;
import org.encog.parse.recognize.RecognizeElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParseTemplatePersistor implements Persistor {

	private ParseTemplate template;

	private void loadChar(RecognizeElement element, Element inputNode) {
		String value = inputNode.getAttribute("value");
		String from = inputNode.getAttribute("from");
		String to = inputNode.getAttribute("to");
		if (value.length() > 0) {
			element.add(value.charAt(0));
		} else {
			element.addRange(from.charAt(0), to.charAt(0));
		}
	}

	private void loadUnit(RecognizeElement element, Element inputNode) {
		String type = inputNode.getAttribute("type");
		String value = inputNode.getAttribute("value");
		element.setType(type);
		if ((value != null) && !value.equals("")) {
			element.addAcceptedSignal(type, value);
		} else {
			element.addAcceptedSignal(type, null);
		}
	}

	private void loadElement(Recognize recognize, Element inputNode) {
		String type = inputNode.getAttribute("type");
		RecognizeElement recognizeElement;

		if (type.equals("ALLOW_ONE"))
			recognizeElement = recognize
					.createElement(RecognizeElement.ALLOW_ONE);
		else if (type.equals("ALLOW_MULTIPLE"))
			recognizeElement = recognize
					.createElement(RecognizeElement.ALLOW_MULTIPLE);
		else
			recognizeElement = null;// ERROR

		for (Node child = inputNode.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;

			if (node.getNodeName().equals("char"))
				loadChar(recognizeElement, node);
			else if (node.getNodeName().equals("unit"))
				loadUnit(recognizeElement, node);

		}
	}

	private String getAttribute(Element inputNode, String attribute) {
		String result = inputNode.getAttribute(attribute);
		if ("".equals(result))
			result = null;
		return result;
	}

	private void loadRecognize(Element inputNode) {
		String id = getAttribute(inputNode, "id");
		String ignore = getAttribute(inputNode, "ignore");
		String recognizeClass = getAttribute(inputNode, "class");

		Recognize recognize = new Recognize(id);
		try {
			if (recognizeClass != null)
				recognize.setSignalClass(Class.forName(recognizeClass));
		} catch (ClassNotFoundException e) {
		}

		if ("true".equalsIgnoreCase(ignore))
			recognize.setIgnore(true);
		else
			recognize.setIgnore(false);

		for (Node child = inputNode.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;

			if (node.getNodeName().equals("element"))
				loadElement(recognize, node);
		}
		template.addRecognizer(recognize);
	}

	public EncogPersistedObject load(Element node) {
		this.template = new ParseTemplate();

		final String name = node.getAttribute("name");
		final String description = node.getAttribute("description");
		
		this.template.setName(name);
		this.template.setDescription(description);
		
		for (Node child = node.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node2 = (Element) child;

			if (node2.getNodeName().equals("recognize"))
				loadRecognize(node2);
		}

		return this.template;
	}

	public void save(EncogPersistedObject object, TransformerHandler hd) {
		// TODO Auto-generated method stub

	}

}
