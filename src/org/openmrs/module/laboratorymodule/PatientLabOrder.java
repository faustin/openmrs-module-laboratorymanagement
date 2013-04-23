package org.openmrs.module.laboratorymodule;

import org.openmrs.Concept;
import org.openmrs.Order;

@SuppressWarnings({ "unused", "unused" })
public class PatientLabOrder {
	private Concept grandFatherConcept;
	private java.util.List<EveryOrder> labOrders;
	
	public Concept getGrandFatherConcept() {
		return grandFatherConcept;
	}
	public void setGrandFatherConcept(Concept grandFatherConcept) {
		this.grandFatherConcept = grandFatherConcept;
	}
	public java.util.List<EveryOrder> getLabOrders() {
		return labOrders;
	}
	public void setLabOrders(java.util.List<EveryOrder> labOrders) {
		this.labOrders = labOrders;
	}
	
	

}
