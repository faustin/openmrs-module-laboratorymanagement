package org.openmrs.module.laboratorymodule;



import org.openmrs.Concept;
import org.openmrs.Order;

public class EveryOrder {
	private  Concept parentConcept;
	private  java.util.List<Order>labOrders;
	public Concept getParentConcept() {
		return parentConcept;
	}
	public void setParentConcept(Concept parentConcept) {
		this.parentConcept = parentConcept;
	}
	public java.util.List<Order> getLabOrders() {
		return labOrders;
	}
	public void setLabOrders(java.util.List<Order> labOrders) {
		this.labOrders = labOrders;
	}
	
	
	
	
	
	
	
	
	
	

}
