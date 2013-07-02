package org.openmrs.module.laboratorymodule.advice;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;

import org.openmrs.ConceptSet;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Obs;

import org.openmrs.api.context.Context;

import org.openmrs.module.laboratorymodule.service.LaboratoryService;



public class LaboratoryMgt {
	protected final Log log = LogFactory.getLog(getClass());

	static List<Obs> patientObservations;
	

	public static Date getPreviousMidnight(Date date) {
		// Initialize with date if it was specified
		Calendar cal = new GregorianCalendar();
		if (date != null)
			cal.setTime(date);

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date addDaysToDate(Date date, int days) {
		// Initialize with date if it was specified
		Calendar cal = new GregorianCalendar();
		if (date != null)
			cal.setTime(date);

		cal.add(Calendar.DAY_OF_WEEK, days);
		return cal.getTime();
	}

	public static Date getDateParameter(HttpServletRequest request,
			String name, Date def) throws java.text.ParseException {
		String strDate = request.getParameter(name);
		

		if (strDate != null) {
			try {
				return Context.getDateFormat().parse(strDate);
			} catch (Exception e) {
				// TODO: handle exception

			}

		}

		return def;
	}


	public static Collection<Concept> getAllRequiredLabConcepts(
			Collection<ConceptSet> setMemebers) {
		Collection<Concept> concepts = new ArrayList<Concept>();
		for (ConceptSet setMemeber : setMemebers) {
			concepts.add(setMemeber.getConcept());

		}

		return concepts;

	}

	/**
	 * get all tests with results
	 * @param List<Obs> patientObservations 
	 * @return List<Obs>
	 */
	public static List<Obs> getAllTestWithResult(List<Obs> patientObservations) {		
		
		List<Obs> testWithResult = new ArrayList<Obs>();
		//run through  all patient Lab observations and take only the Lab obs whose 
		//values either coded,numeric or text is not null
		for (Obs onePatientObs : patientObservations) {
			if ((onePatientObs.getValueCoded() != null)
					|| (onePatientObs.getValueNumeric() != null)
					|| (onePatientObs.getValueText() != null)
					|| (onePatientObs.getValueDatetime() != null)

			) {			

				testWithResult.add(onePatientObs);

			}

		}

		return testWithResult;
	}

	public static List<Obs> getAllPendingtests(List<Obs> patientObservations) {
		List<Obs> pendingTests = new ArrayList<Obs>();
		for (Obs onePatientObs : patientObservations) {

			if (onePatientObs.getConcept().getDatatype().isNumeric()
					&& (onePatientObs.getValueNumeric() == null)) {
				pendingTests.add(onePatientObs);

			}
			if (onePatientObs.getConcept().getDatatype().isCoded()
					&& (onePatientObs.getValueCoded() == null)) {
				pendingTests.add(onePatientObs);

			}
			if (onePatientObs.getConcept().getDatatype().isText()
					&& (onePatientObs.getValueText() == null)) {
				pendingTests.add(onePatientObs);

			}
			if (onePatientObs.getConcept().getDatatype().isDate()
					&& (onePatientObs.getValueDatetime() == null)) {
				pendingTests.add(onePatientObs);

			}

		}

		return pendingTests;

	}

	public static Map<Integer, Integer> getAlllabTestSizeByyear(
			String patientIdstr, String locationIdstr, int conceptId,
			Date startDate, Date endDate) {
		// Date declaration
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		// Map that holds the year(as key) and number of patients (as value)
		List<Obs> testTakenBetweenTwoDates = null;
		LaboratoryService labService = Context
				.getService(LaboratoryService.class);
		TreeMap<Integer, Integer> numberOfLabTestByYear = new TreeMap<Integer, Integer>();
		int startMonths = startDate.getMonth() + 1;
		int endMonth = endDate.getMonth() + 1;

		int startYear = startDate.getYear() + 1900;
		int endYear = endDate.getYear() + 1900;

		if (startYear == endYear) {

			for (int months = startMonths; months <= endMonth; months = months + 1) {

				try {
					// define the start date and and date of each year
					// populate the map

					if (months == startMonths) {
						// populate the map
						Date startDat = startDate;
						Date endDat = getLastDayOfMonth(startYear, months - 1);
						List<Obs> allTestWithResult = getAllLabTestByCriteria(
								patientIdstr, locationIdstr, conceptId,
								startDat, endDat);
						testTakenBetweenTwoDates = getAllTestWithSpecificConcept(
								allTestWithResult, conceptId);
						numberOfLabTestByYear.put(months,
								testTakenBetweenTwoDates.size());

						continue;

					}
					if (months == endMonth) {

						Date startDat = df.parse("01/" + months + "/"
								+ startYear);
						Date endDat = endDate;
						List<Obs> allTestWithResult = getAllLabTestByCriteria(
								patientIdstr, locationIdstr, conceptId,
								startDat, endDat);

						testTakenBetweenTwoDates = getAllTestWithSpecificConcept(
								allTestWithResult, conceptId);
						numberOfLabTestByYear.put(months,
								testTakenBetweenTwoDates.size());

						continue;

					}

					Date startDat = df.parse("01/" + months + "/" + startYear);
					Date endDat = getLastDayOfMonth(startYear, months - 1);
					List<Obs> allTestWithResult = getAllLabTestByCriteria(
							patientIdstr, locationIdstr, conceptId, startDat,
							endDat);
					testTakenBetweenTwoDates = getAllTestWithSpecificConcept(
							allTestWithResult, conceptId);
					numberOfLabTestByYear.put(months, testTakenBetweenTwoDates
							.size());

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}

		return numberOfLabTestByYear;
	}

	public static Date getLastDayOfMonth(int year, int month) {

		// Setup a Calendar instance.
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		// Set the year as year
		cal.set(Calendar.YEAR, year);
		// Set the month as month(can be set as 1 or Calendar.FEBRUARY)
		cal.set(Calendar.MONTH, month);
		// Set the date as 1st - optional
		cal.set(Calendar.DATE, 1);
		System.out.println("Input date: " + cal.getTime());
		int lastDateOfMonth = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DATE, lastDateOfMonth);
		System.out.println("Output Date: " + cal.getTime());
		return cal.getTime();
	}

	public static List<Obs> getAllTestWithSpecificConcept(
			List<Obs> allTestsWithResult, int conceptId) {
		// create list from where all tests with a selected concept_id are
		// stored
		ArrayList<Obs> testTakenbetweenTwoDates = new ArrayList<Obs>();
		for (Obs test : allTestsWithResult) {
			if (test.getConcept().getConceptId() == conceptId) {
				testTakenbetweenTwoDates.add(test);

			}

		}

		return testTakenbetweenTwoDates;

	}

	public static List<Obs> getAllLabTestByCriteria(String patientIdStr,
			String locationIdstr, int conceptId, Date startDate, Date endDate) {

//		SimpleDateFormat df = OpenmrsUtil.getDateFormat();
		List<Obs> allTestWithResult = null;

		List<Obs> testTakenbetweenTwoDates = null;
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);

		if (patientIdStr != null && !patientIdStr.equals("")
				&& locationIdstr != null && !locationIdstr.equals("")
				&& startDate != null && endDate != null) {
			int locationId = Integer.parseInt(locationIdstr);
			int patientId = Integer.parseInt(patientIdStr);

			allTestWithResult = getAllTestWithResult(laboratoryService
					.getAllPatientLabtestByLocation(patientId, locationId,
							conceptId, startDate, endDate));

		} else if (locationIdstr != null && !locationIdstr.equals("")
				&& startDate != null && endDate != null) {
		

			int locationId = Integer.parseInt(locationIdstr);
			allTestWithResult = getAllTestWithResult(laboratoryService
					.getTestsBetweenTwoDateByLocation(locationId, startDate,
							endDate));
		

		}
		 else if (startDate != null && endDate != null
		 ) {
		 allTestWithResult =getAllTestWithResult(laboratoryService
		 .getPatientTestBeforeDate(startDate, endDate));
		
					
					 
				
		 }
		else if (patientIdStr != null && !patientIdStr.equals("")
				&& startDate != null && endDate != null) {
			int patientId = Integer.parseInt(patientIdStr);
			allTestWithResult = getAllTestWithResult(laboratoryService
					.getPatientLabtestBetweenTwoDate(patientId, conceptId,
							startDate, endDate));

		} else if (locationIdstr != null && !locationIdstr.equals("")
				&& patientIdStr != null && !patientIdStr.equals("")) {
			int locationId = Integer.parseInt(locationIdstr);
			int patientId = Integer.parseInt(patientIdStr);
			allTestWithResult = getAllTestWithResult(laboratoryService
					.getPatientLabtestByLocation(patientId, locationId,
							conceptId));

		} else if (locationIdstr != null && !locationIdstr.equals("")) {
			allTestWithResult = getAllTestWithResult(laboratoryService
					.getAllObsByLocation(Integer.parseInt(locationIdstr)));

		}

		else if (patientIdStr != null && !patientIdStr.equals("")) {
			int patientId = Integer.parseInt(patientIdStr);
			allTestWithResult = getAllTestWithResult(laboratoryService
					.getAllpatientObs(patientId));

		}
		return allTestWithResult;

	}



	/**
	 * Gets lab global properties
	 * 
	 * @return 
	 */
	public static Collection<Integer> getLabGlobalProperties() {
		List<Integer> labConceptIds = new ArrayList<Integer>();
		GlobalProperty gp = Context
				.getAdministrationService()
				.getGlobalPropertyObject("laboratorymodule.labtests.conceptIds");

		String[] conceptIds = gp.getPropertyValue().split(",");
		for (String s : conceptIds) {
			labConceptIds.add(Integer.valueOf(s));

		}

		return labConceptIds;
	}

	// display the name of all lab tets(Patient name,exams,date
	// d'observation,reslts)
	public static List<Object[]> getHistoryOfLabTests(List<Obs> labTests,HttpServletRequest request) {
		List<Object[]> listOflabtest = new ArrayList<Object[]>();
		Object labTest[] = null;
		int i=0;
		for (Obs labtst : labTests) {
			int personId = labtst.getPersonId();

			if (labtst.getConcept().getDatatype().isCoded()) {
				labTest = new Object[] {
						Context.getPersonService().getPerson(personId), labtst,
						labtst.getValueCoded().getName() };
				listOflabtest.add(labTest);
			}
			if (labtst.getConcept().getDatatype().isNumeric()) {
				labTest = new Object[] {
						Context.getPersonService().getPerson(personId), labtst,
						labtst.getValueNumeric() };
				listOflabtest.add(labTest);
			}
			if (labtst.getConcept().getDatatype().isText()) {
				labTest = new Object[] {
						Context.getPersonService().getPerson(personId), labtst,
						labtst.getValueText() };
				listOflabtest.add(labTest);
			}
			if (labtst.getConcept().getDatatype().isDate()) {
				labTest = new Object[] {
						Context.getPersonService().getPerson(personId), labtst,
						labtst.getValueDatetime() };
				listOflabtest.add(labTest);
			}

		}
		
		return listOflabtest;

	}
	public static EncounterType getEncounterTypeByService(){
		List<EncounterType> typeOfEncounter=Context.getEncounterService().getAllEncounterTypes(true);
		return null;
		
	}
	
	public static List<Concept> delimitedStringToConceptList(String delimitedString, String delimiter, Context context) {
		List<Concept> ret = null;
		
		if (delimitedString != null && context != null) {
			String[] tokens = delimitedString.split(delimiter);
			for (String token : tokens) {
				Integer conceptId = null;
				
				try {
					conceptId = new Integer(token);
				}
				catch (NumberFormatException nfe) {
					conceptId = null;
				}
				
				Concept c = null;
				
				if (conceptId != null) {
					c = Context.getConceptService().getConcept(conceptId);
				} else {
					c = Context.getConceptService().getConceptByName(token);
				}
				
				if (c != null) {
					if (ret == null)
						ret = new ArrayList<Concept>();
					ret.add(c);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Utility method to get a parsed date parameter
	 * 
	 * @param request the HTTP request object
	 * @param name the name of the date parameter
	 * @param def the default value if parameter doesn't exist or is invalid
	 * @return the date
	 */
	public  static Date getRightDate(HttpServletRequest request,String name) {
		
		String strDate = request.getParameter(name);
		if (strDate != null) {
			try {
				return Context.getDateFormat().parse(strDate);
			}
			catch (Exception ex) {
				//log.warn("Invalid date format: " + strDate);
			}
		}
		return null;
	}
}
