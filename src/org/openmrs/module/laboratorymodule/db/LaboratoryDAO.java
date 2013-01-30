package org.openmrs.module.laboratorymodule.db;

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
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.DocumentException;

@Transactional
public interface LaboratoryDAO {

	public void saveOrderEncounter(Encounter encounter);

	public List<Encounter> getAllEncountersByProvider(int providerId);

	public List<Obs> getAllpatientObs(int patientId);

	public List<Obs> getAllObsByLocation(int locationId);

	public void getSampleCodeByPatient(int testobsId, int newObsId);

	public List<EncounterType> getAllEncounterType();

	public List<Obs> getPatientTestBeforeDate(Date startDate, Date endDate);

	public boolean isFoundLabCode(String labCode);

	public List<Obs> getTestOfPatientBetweenTwoDate(int patientId,
			Date startDate, Date endDate);
	public List<Obs> getTestsBetweenTwoDateByLocation(int locationId,
			Date startDate, Date endDate);
	public List<Obs>getAllPatientLabtestByLocation(int patientId,int locationId,int conceptId, Date startDate,Date endDate);

	public List<Obs> getPatientLabtestBetweenTwoDate(int patientId,
			int conceptId, Date startDate, Date endDate);

	public List<Obs> getPatientLabtestByLocation(int patientId, int locationId,
			int conceptId);
	public void exportToPDF(HttpServletRequest request,
			HttpServletResponse response, List<Object[]>listOflabtest,
			 String filename,String title,int conceptId ) throws DocumentException, IOException;

	public List<Obs> getAllLabTest(Integer labConceptId);

	public List<Obs> getLabExamsByExamType(int patientId,
			Collection<Integer> cptIds,Date startDate,Date endDate);

	public List<Obs> getLabExamsByExamTypeBetweenTwoDates(Date startDate,Date endDate,Integer conceptId);

	public void exportPatientReportToPDF(HttpServletRequest request,
			HttpServletResponse response,Map<ConceptName, List<Object[]>> mappedLabExam,
			String filename, String title,int patientId) throws DocumentException, IOException;

	public List<Obs> getAllNegtiveLabExams(Date startDate, Date endDate,
			int conceptId);

	public List<Obs> getAllPositiveLabExams(Date startDate, Date endDate,
			int conceptId);

	public List<Order> getLabOrders(int patientId, Collection<Integer> cptIds,
			Date startDate, Date enddate);

	public List<Order> getLabOrdersBetweentwoDate(int patientId,
			Date startDate, Date enddate);
	public List<Order> getLabOrdersBetweentwoDate(Date startDate, Date enddate);
	public List<Order> getLabOrdersByLabCode(String labCode);

	public Collection<Order> getPatientLabordersBetweendates(int patientId,
			Date startDate, Date endDate);

	/**
	 * Gets all obs with the given lab code
	 * @param labCode the lab code (i.e. accession number)
	 * @return the list of obs
	 */
	public List<Obs> getObsByLabCode(String labCode);
	
	/**
	 * Gets obs that are attached to the given order
	 * @param order the lab order
	 * @param cpt the concept of the obs (may be null)
	 * @return the list of obs
	 */
	public List<Obs> getObsByLabOrder(Order order, Concept cpt);
}
