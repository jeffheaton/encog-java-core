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
