/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.bot.browse.extract;

import java.util.Collection;

import org.encog.bot.browse.WebPage;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.encog.parse.Parse;
import org.encog.parse.signal.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extractor that is designed to extract all of the words from a web page.
 * 
 * @author jheaton
 * 
 */
public class ExtractWords extends BasicExtract {

	/**
	 * Used to parse the text found ont the web page.  This is how the
	 * text is broken into words.
	 */
	private final Parse parse;

	/**
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct the extractor for words.
	 */
	public ExtractWords() {
		this.parse = new Parse();
		this.parse.load();
	}

	/**
	 * Extract words from the specified WebPage.
	 * @param page The page to extract from.
	 */
	public void extract(final WebPage page) {

		for (final DataUnit unit : page.getData()) {
			if (unit instanceof TextDataUnit) {
				final TextDataUnit text = (TextDataUnit) unit;
				final Signal signal = this.parse.parse(text.getText());
				final Collection<Signal> list = signal.findByType("word");
				for (final Signal word : list) {
					distribute(word.toString());
				}
			}
		}
	}
}
