package org.openmrs.module.laboratorymodule;

import java.util.List;

import org.openmrs.Concept;

public class LabOrderParent {
	private Concept grandFatherConcept;
	private List<LabOrder> labTests;

	/**
	 * @return the labTests
	 */
	public List<LabOrder> getLabTests() {
		return labTests;
	}

	/**
	 * @param labTests the labTests to set
	 */
	public void setLabTests(List<LabOrder> labTests) {
		this.labTests = labTests;
	}

	/**
	 * @return the grandFatherConcept
	 */
	public Concept getGrandFatherConcept() {
		return grandFatherConcept;
	}

	/**
	 * @param grandFatherConcept the grandFatherConcept to set
	 */
	public void setGrandFatherConcept(Concept grandFatherConcept) {
		this.grandFatherConcept = grandFatherConcept;
	}
}
