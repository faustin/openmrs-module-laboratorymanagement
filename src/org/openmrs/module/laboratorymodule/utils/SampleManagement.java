package org.openmrs.module.laboratorymodule.utils;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.User;

/**
 * @author Faustin
 * 
 */
public class SampleManagement {
private	Concept specimenConcept;
private Date collectionDate;
private List<Order>oderList;
private User   creator;
private boolean voided = false;
private Date dateVoided;
private User voidedBy;
/**
 * @return the specimenConcept
 */
public Concept getSpecimenConcept() {
	return specimenConcept;
}
/**
 * @param specimenConcept the specimenConcept to set
 */
public void setSpecimenConcept(Concept specimenConcept) {
	this.specimenConcept = specimenConcept;
}
/**
 * @return the collectionDate
 */
public Date getCollectionDate() {
	return collectionDate;
}
/**
 * @param collectionDate the collectionDate to set
 */
public void setCollectionDate(Date collectionDate) {
	this.collectionDate = collectionDate;
}
/**
 * @return the oderList
 */
public List<Order> getOderList() {
	return oderList;
}
/**
 * @param oderList the oderList to set
 */
public void setOderList(List<Order> oderList) {
	this.oderList = oderList;
}
/**
 * @return the creator
 */
public User getCreator() {
	return creator;
}
/**
 * @param creator the creator to set
 */
public void setCreator(User creator) {
	this.creator = creator;
}
/**
 * @return the voided
 */
public boolean isVoided() {
	return voided;
}
/**
 * @param voided the voided to set
 */
public void setVoided(boolean voided) {
	this.voided = voided;
}
/**
 * @return the dateVoided
 */
public Date getDateVoided() {
	return dateVoided;
}
/**
 * @param dateVoided the dateVoided to set
 */
public void setDateVoided(Date dateVoided) {
	this.dateVoided = dateVoided;
}
/**
 * @return the voidedBy
 */
public User getVoidedBy() {
	return voidedBy;
}
/**
 * @param voidedBy the voidedBy to set
 */
public void setVoidedBy(User voidedBy) {
	this.voidedBy = voidedBy;
}


	

}
