/*
 * Encog Artificial Intelligence Framework v1.x
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

public class ExtractWords extends BasicExtract {
	
	private Parse parse;
	final Logger logger = LoggerFactory.getLogger(ExtractWords.class);
	
	public ExtractWords()
	{
		parse = new Parse();
		parse.load();
	}
	
	public void extract(WebPage page)
	{

		for(DataUnit unit : page.getData() )
		{				
			if( unit instanceof TextDataUnit )
			{
				TextDataUnit text = (TextDataUnit)unit;
				Signal signal = parse.parse(text.getText());
				Collection<Signal> list = signal.findByType("word");
				for(Signal word: list)
				{
					distribute(word.toString());
				}
			}
		}
	}
}
