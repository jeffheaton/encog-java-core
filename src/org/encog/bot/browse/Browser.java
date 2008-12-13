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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.encog.bot.browse.range.Form;
import org.encog.bot.browse.range.FormElement;
import org.encog.bot.browse.range.Input;
import org.encog.bot.browse.range.Link;
import org.encog.bot.html.FormUtility;

public class Browser {

	protected WebPage currentPage;

	public void navigate(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		navigate(url, is);
		is.close();
	}

	public void navigate(URL url, InputStream is) throws IOException {
		LoadWebPage load = new LoadWebPage(url);
		currentPage = load.load(is);
	}

	public void navigate(String url) throws IOException {
		navigate(new URL(url));
	}

	public void navigate(Form form, Input submit) throws IOException {
		InputStream is;
		URLConnection connection = null;
		OutputStream os;
		URL targetURL;

		if (form.getMethod() == Form.Method.GET) {
			os = new ByteArrayOutputStream();
		} else {
			connection = form.getAction().getUrl().openConnection();
			os = connection.getOutputStream();
		}

		// add the parameters if present
		FormUtility formData = new FormUtility(os, null);
		for (FormElement element : form.getElements()) {
			if (element == submit || element.isAutoSend()) {
				String name = element.getName();
				String value = element.getValue();
				if (name != null) {
					if (value == null) {
						value = "";
					}
					formData.add(name,value);
				}
			}
		}

		// now execute the command
		if (form.getMethod() == Form.Method.GET) {
			String action = form.getAction().getUrl().toString();
			os.close();
			action += "?";
			action +=os.toString();
			targetURL = new URL(action);
			connection = targetURL.openConnection();
			is = connection.getInputStream();
		} else {
			targetURL = form.getAction().getUrl();
			os.close();
			is = connection.getInputStream();
		}

		navigate(targetURL, is);
		is.close();
	}

	public WebPage getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(WebPage currentPage) {
		this.currentPage = currentPage;
	}

	public void navigate(Link link) throws IOException {
		Address address = link.getTarget();

		if (address.getUrl() != null)
			navigate(address.getUrl());
		else
			navigate(address.getOriginal());

	}

	public void navigate(Form form) throws IOException {
		navigate(form, null);
	}

}
