package org.openmrs.module.laboratorymodule;

import java.util.List;

import org.openmrs.Order;

public class OrderObs {
	private Order order;
	List<Object[]> obss;
	/**
	 * @return the obss
	 */
	public List<Object[]> getObss() {
		return obss;
	}
	/**
	 * @param obss the obss to set
	 */
	public void setObss(List<Object[]> obss) {
		this.obss = obss;
	}
	private Object[]orderStatus;
	
	
	/**
	 * @return the orderStatus
	 */
	public Object[] getOrderStatus() {
		return orderStatus;
	}
	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(Object[] orderStatus) {
		this.orderStatus = orderStatus;
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
