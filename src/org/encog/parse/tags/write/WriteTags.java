package org.encog.parse.tags.write;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.encog.EncogError;
import org.encog.neural.NeuralNetworkError;
import org.encog.parse.ParseError;
import org.encog.parse.tags.TagConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to write out tags, such as XML or HTML.
 * 
 * @author jheaton
 */
public class WriteTags {

	/**
	 * The output stream to write to.
	 */
	private final OutputStream output;

	/**
	 * Stack to keep track of beginning and ending tags.
	 */
	private final Stack<String> tagStack;
	
	private final Map<String,String> attributes;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public WriteTags(final OutputStream output) {
		this.output = output;
		this.tagStack = new Stack<String>();
		this.attributes = new HashMap<String,String>();
	}

	public void addAttribute(final String name, final String value) {
		this.attributes.put(name, value);
	}

	public void addCDATA(final String text) {
		StringBuilder builder = new StringBuilder();
		builder.append('<');
		builder.append(TagConst.CDATA_BEGIN);
		builder.append(text);
		builder.append(TagConst.CDATA_END);
		builder.append('>');
		try {
			output.write(builder.toString().getBytes());
		} catch (IOException e) {
			throw new ParseError(e);
		}
	}

	public void addProperty(final String name, final double d) {
		beginTag(name);
		addText("" + d);
		endTag();
	}

	public void addProperty(final String name, final int i) {
		addProperty(name, "" + i);

	}

	public void addProperty(final String name, final String avaluelue) {
		beginTag(name);
		addText(avaluelue);
		endTag();
	}

	public void addText(final String text) {
		try {
			this.output.write(text.getBytes());
		} catch (IOException e) {
			throw new ParseError(e);
		}
	}

	public void beginDocument() {
	}

	public void beginTag(final String name) {
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(name);
		if( this.attributes.size()>0 )
		{
			for(String key: this.attributes.keySet())
			{
				String value = this.attributes.get(key);
				builder.append(' ');
				builder.append(key);
				builder.append('=');
				builder.append("\"");
				builder.append(value);
				builder.append("\"");
			}
		}
		builder.append(">");
		
		try {
			this.output.write(builder.toString().getBytes());
		} catch (IOException e) {
			throw new ParseError(e);
		}
		this.attributes.clear();
		this.tagStack.push(name);
	}

	public void close() {
		try {
			this.output.close();
		} catch (final IOException e) {
			throw new EncogError(e);
		}
	}

	public void endDocument() {
	}

	public void endTag() {
		if (this.tagStack.isEmpty()) {
			throw new NeuralNetworkError(
					"Can't create end tag, no beginning tag.");
		}
		final String tag = this.tagStack.pop();
		
		StringBuilder builder = new StringBuilder();
		builder.append("</");
		builder.append(tag);
		builder.append(">");
		
		try {
			this.output.write(builder.toString().getBytes());
		} catch (IOException e) {
			throw new ParseError(e);
		}

	}
}
