package org.openmrs.module.laboratorymodule.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;

import com.itextpdf.text.DocumentException;

public interface LaboratoryService {

	public void saveOrderEncounter(Encounter encounter);

	public List<Obs> getAllpatientObs(int patientId);

	public List<Obs> getAllObsByLocation(int locationId);

	

	public List<EncounterType> getAllEncounterType();

	public List<Obs> getPatientTestBeforeDate(Date startDate, Date endDate);

	public List<Obs> getTestOfPatientBetweenTwoDate(int patientId,
			Date startDate, Date endDate);

	public List<Obs> getTestsBetweenTwoDateByLocation(int locationId,
			Date startDate, Date endDate);

	public boolean isFoundLabCode(String labCode);

	public List<Obs> getAllPatientLabtestByLocation(int patientId,
			int locationId, int conceptId, Date startDate, Date endDate);

	public List<Obs> getPatientLabtestBetweenTwoDate(int patientId,
			int conceptId, Date startDate, Date endDate);

	public List<Obs> getPatientLabtestByLocation(int patientId, int locationId,
			int conceptId);

	public List<Obs> getAllLabTest(Integer labConceptId);

	/**
	 * get List<obs> by Lab tests
	 * @param patientId
	 * @param cptIds(collection of Lab conceptIds )
	 * @param startDate
	 * @param endDate
	 * @return List<obs>
	 */
	public List<Obs> getLabExamsByExamType(int patientId,
			Collection<Integer> cptIds,Date startDate,Date endDate);

	public List<Obs> getLabExamsByExamTypeBetweenTwoDates(Date startDate,
			Date endDate, Integer conceptId);
	public List<Obs>getAllNegtiveLabExams(Date startDate,Date endDate,int conceptId);
	public List<Obs>getAllPositiveLabExams(Date startDate,Date endDate,int conceptId);

	public void exportToPDF(HttpServletRequest request,
			HttpServletResponse response, List<Object[]> listOflabtest,
			String filename, String title, int conceptId)
			throws DocumentException, IOException;

	public void exportPatientReportToPDF(HttpServletRequest request,
			HttpServletResponse response, Map<ConceptName, List<Object[]>> mappedLabExam,
			String filename, String title,int patientId) throws DocumentException, IOException;
/**
 * 
 * Auto generated method comment
 * 
 * @param patientId
 * @param cptsLst
 * @param startDate
 * @param enddate
 * @return
 */
	public List<Order> getLabOrders(int patientId, Collection<Integer> cptIds, Date startDate, Date enddate);
	public List<Order> getLabOrdersBetweentwoDate(int patientId	,Date startDate, Date enddate);
	public List<Order> getLabOrdersBetweentwoDate(Date startDate, Date enddate);
	public List<Order> getLabOrdersByLabCode(String  labCode);
	public List<Obs> getObsByLabOrder(int orderId);
	/**
	 * 
	 * @param patientId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Collection<Order> getPatientLabordersBetweendates(int patientId,Date startDate,Date endDate);
	public Collection<Obs> getObsByLabCode(String labCode);
	
	public List<Obs> getExistingOrderObs(Order order, Concept cpt);
	
	/**
	 * deletes the Obs linked to any order
	 * @param order
	 * @param cpt
	 */
	public void deleteExistingOrderObs(Order order,Concept cpt);
	
	/**
	 * Gets a list of all patient encounters occured on start date
	 * 
	 * @param patientId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Collection<Encounter> getPatientEncountersByDate(int patientId, Date startDate,EncounterType encounterType);
		

	
	} 
