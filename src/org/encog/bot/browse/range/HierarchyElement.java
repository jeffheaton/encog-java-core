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
package org.encog.bot.browse.range;

import java.util.ArrayList;
import java.util.List;

public class HierarchyElement  extends DocumentRange {
	private String idAttribute;
	private String classAttribute;
	private List<HierarchyElement> elements = new ArrayList<HierarchyElement>();
	private HierarchyElement parent;
	
	/**
	 * @return the idAttribute
	 */
	public String getIdAttribute() {
		return idAttribute;
	}
	/**
	 * @param idAttribute the idAttribute to set
	 */
	public void setIdAttribute(String idAttribute) {
		this.idAttribute = idAttribute;
	}
	/**
	 * @return the elements
	 */
	public List<HierarchyElement> getElements() {
		return elements;
	}
	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<HierarchyElement> elements) {
		this.elements = elements;
	}
	/**
	 * @return the classAttribute
	 */
	public String getClassAttribute() {
		return classAttribute;
	}
	/**
	 * @param classAttribute the classAttribute to set
	 */
	public void setClassAttribute(String classAttribute) {
		this.classAttribute = classAttribute;
	}
	/**
	 * @return the parent
	 */
	public HierarchyElement getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(HierarchyElement parent) {
		this.parent = parent;
	}
	
	public void addElement(HierarchyElement element)
	{
		this.elements.add(element);
		element.setParent(this);
	}
	
}
