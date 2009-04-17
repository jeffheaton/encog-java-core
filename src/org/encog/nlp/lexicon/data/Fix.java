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
package org.encog.nlp.lexicon.data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * Note: This class is part of the Encog Natural Language Processing(NLP)
 * package.  This package is still under heavy construction, and will not 
 * be considered stable until Encog 3.0.
 * 
 * @author jheaton
 *
 */
@Entity
@Table(name="lexicon_fix",
    uniqueConstraints = {@UniqueConstraint(columnNames={"text","pre"})} )
public class Fix extends DataObject {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -1836858648292387040L;
	private String text;
	private boolean pre;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	@Transient
	final transient private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ManyToOne
	private WordType wordType;
	
	@Override
	public void validate() {
		// TODO Auto-generated method stu
	}

	public String getText() {
		return text;
	}

	public void setText(String value) {
		this.text = value;
	}

	public boolean isPre() {
		return pre;
	}

	public void setPre(boolean pre) {
		this.pre = pre;
	}

	public int length() {
		return this.text.length();
	}

	public boolean isPost() {
		return !isPre();
	}
	
	
	
	public WordType getWordType() {
		return wordType;
	}

	public void setWordType(WordType wordType) {
		this.wordType = wordType;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Fix:");
		if( this.pre)
		{
			result.append("p:");
		}
		else
		{
			result.append("s:");
		}
		result.append(this.text);
		result.append("]");
		return result.toString();
	}
}
