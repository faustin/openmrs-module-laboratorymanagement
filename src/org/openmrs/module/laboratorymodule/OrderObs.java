package org.openmrs.module.laboratorymodule;

import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Order;

public class OrderObs {
	private Order order;
	List<Obs> obss;
	
	
	/**
	 * @return the obss
	 */
	public List<Obs> getObss() {
		return obss;
	}
	/**
	 * @param obss the obss to set
	 */
	public void setObss(List<Obs> obss) {
		this.obss = obss;
	}
	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
	
}
