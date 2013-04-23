package org.openmrs.module.laboratorymodule;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Order;

public class LabOrder {
	private Concept parentConcept;
	private List<Concept> childrenConcept;
	// List<Order> labOrderslist;
	
	/**
	 * @return the parentConcept
	 */
	public Concept getParentConcept() {
		return parentConcept;
	}
	/**
	 * @param parentConcept the parentConcept to set
	 */
	public void setParentConcept(Concept parentConcept) {
		this.parentConcept = parentConcept;
	}
	/**
	 * @return the childrenConcept
	 */
	public List<Concept> getChildrenConcept() {
		return childrenConcept;
	}
	/**
	 * @param childrenConcept the childrenConcept to set
	 */
	public void setChildrenConcept(List<Concept> childrenConcept) {
		this.childrenConcept = childrenConcept;
	}
}
