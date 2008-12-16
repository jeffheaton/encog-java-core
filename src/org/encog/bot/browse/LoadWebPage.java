/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.bot.browse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.encog.bot.browse.range.Div;
import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Form;
import org.encog.bot.browse.range.HierarchyElement;
import org.encog.bot.browse.range.Input;
import org.encog.bot.browse.range.Link;
import org.encog.bot.browse.range.Span;
import org.encog.bot.dataunit.CodeDataUnit;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TagDataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.encog.bot.html.HTMLTag;
import org.encog.bot.html.ParseHTML;

public class LoadWebPage {

	protected WebPage page;
	protected URL base;
	protected Form lastForm;
	protected HierarchyElement lastHierarchyElement;

	public LoadWebPage(URL base) {
		this.base = base;
	}

	public WebPage load(String str) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes());
		WebPage result = load(bis);
		bis.close();
		return result;
	}

	protected void loadDataUnits(InputStream is) throws IOException {
		StringBuilder text = new StringBuilder();
		int ch;
		ParseHTML parse = new ParseHTML(is);
		boolean style = false;
		boolean script = false;

		while ((ch = parse.read()) != -1) {
			if (ch == 0) {
				
				if( style==true )
					createCodeDataUnit(text.toString());
				else if( script==true )
					createCodeDataUnit(text.toString());
				else
					createTextDataUnit(text.toString());
				style = false;
				script = false;
				
				text.setLength(0);
				createTagDataUnit(parse.getTag());
				if( parse.getTag().getName().equalsIgnoreCase("style") )
				{
					style = true;
				}
				else if( parse.getTag().getName().equalsIgnoreCase("script") )
				{
					script = true;
				}
			} else {
				text.append((char) ch);
			}
		}

		createTextDataUnit(text.toString());
	}

	protected int findEndTag(int index, HTMLTag tag) {
		int depth = 0;

		if (tag.getType() != HTMLTag.Type.BOTH) {
			while (index < page.getDataSize()) {
				DataUnit du = page.getDataUnit(index);

				if (du instanceof TagDataUnit) {
					HTMLTag nextTag = ((TagDataUnit) du).getTag();
					if (tag.getName().equalsIgnoreCase(nextTag.getName())) {
						if (nextTag.getType() == HTMLTag.Type.END) {
							if (depth == 0)
								return index;
							else
								depth--;
						} else if (nextTag.getType() == HTMLTag.Type.BEGIN)
							depth++;
					}
				}
				index++;
			}
			return -1;
		} else {
			return index;
		}

	}

	protected void loadLink(int index, HTMLTag tag) {
		Link link = new Link();
		String href = tag.getAttributeValue("href");

		if (href != null) {
			link.setTarget(new Address(base, href));
			link.setBegin(index);
			link.setEnd(findEndTag(index + 1, tag));
			page.addContent(link);
		}
	}

	protected void loadTitle(int index, HTMLTag tag) {
		DocumentRange title = new DocumentRange();
		title.setBegin(index);
		title.setEnd(findEndTag(index + 1, tag));
		this.page.setTitle(title);
	}

	protected void loadForm(int index, HTMLTag tag) {
		String method = tag.getAttributeValue("method");
		String action = tag.getAttributeValue("action");

		Form form = new Form();
		form.setBegin(index);
		form.setEnd(findEndTag(index + 1, tag));

		if (method == null || method.equalsIgnoreCase("GET"))
			form.setMethod(Form.Method.GET);
		else
			form.setMethod(Form.Method.POST);

		if (action == null)
			form.setAction(new Address(base));
		else
			form.setAction(new Address(this.base, action));

		this.page.addContent(form);
		this.lastForm = form;
	}

	protected void loadInput(int index, HTMLTag tag) {
		String type = tag.getAttributeValue("type");
		String name = tag.getAttributeValue("name");
		String value = tag.getAttributeValue("value");

		Input input = new Input();
		input.setType(type);
		input.setName(name);
		input.setValue(value);

		if (lastForm != null)
			lastForm.addElement(input);
		else
			this.page.addContent(input);
	}

	protected void loadContents() {
		for (int index = 0; index < page.getDataSize(); index++) {
			DataUnit du = page.getDataUnit(index);
			if (du instanceof TagDataUnit) {
				HTMLTag tag = ((TagDataUnit) du).getTag();

				if (tag.getType() != HTMLTag.Type.END) {
					if (tag.getName().equalsIgnoreCase("a")) {
						loadLink(index, tag);
					} else if (tag.getName().equalsIgnoreCase("title")) {
						loadTitle(index, tag);
					} else if (tag.getName().equalsIgnoreCase("form")) {
						loadForm(index, tag);
					} else if (tag.getName().equalsIgnoreCase("input")) {
						loadInput(index, tag);
					}

				}

				if (tag.getType() == HTMLTag.Type.BEGIN) {
					if (tag.getName().equalsIgnoreCase("div")) {
						loadDiv(index, tag);
					} else if (tag.getName().equalsIgnoreCase("span")) {
						loadSpan(index, tag);
					}
				}

				if (tag.getType() == HTMLTag.Type.END) {
					if (tag.getName().equalsIgnoreCase("div")) {
						if (this.lastHierarchyElement != null)
							this.lastHierarchyElement = this.lastHierarchyElement
									.getParent();
					} else if (tag.getName().equalsIgnoreCase("span")) {
						if (this.lastHierarchyElement != null)
							this.lastHierarchyElement = this.lastHierarchyElement
									.getParent();
					}
				}
			}
		}
	}

	private void loadSpan(int index, HTMLTag tag) {
		Span span = new Span();
		String classAttribute = tag.getAttributeValue("class");
		String idAttribute = tag.getAttributeValue("id");

		span.setIdAttribute(idAttribute);
		span.setClassAttribute(classAttribute);
		span.setBegin(index);
		span.setEnd(findEndTag(index + 1, tag));
		addHierarchyElement(span);
	}

	private void loadDiv(int index, HTMLTag tag) {
		Div div = new Div();
		String classAttribute = tag.getAttributeValue("class");
		String idAttribute = tag.getAttributeValue("id");

		div.setIdAttribute(idAttribute);
		div.setClassAttribute(classAttribute);
		div.setBegin(index);
		div.setEnd(findEndTag(index + 1, tag));
		addHierarchyElement(div);
	}
	
	private void addHierarchyElement(HierarchyElement element)
	{
		if( this.lastHierarchyElement==null )
			this.page.addContent(element);
		else
			this.lastHierarchyElement.addElement(element);
		this.lastHierarchyElement = element;
	}

	public WebPage load(InputStream is) throws IOException {
		page = new WebPage();

		loadDataUnits(is);
		loadContents();

		return page;
	}

	private void createTextDataUnit(String str) {
		if (str.trim().length() > 0) {
			TextDataUnit d = new TextDataUnit();
			d.setText(str);
			page.addDataUnit(d);
		}
	}
	
	private void createCodeDataUnit(String str) {
		if (str.trim().length() > 0) {
			CodeDataUnit d = new CodeDataUnit();
			d.setCode(str);
			page.addDataUnit(d);
		}
	}

	private void createTagDataUnit(HTMLTag tag) {
		TagDataUnit d = new TagDataUnit();
		d.setTag(tag);

		page.addDataUnit(d);
	}

}
