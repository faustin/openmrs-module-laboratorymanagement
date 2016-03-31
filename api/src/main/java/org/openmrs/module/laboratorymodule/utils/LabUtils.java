package org.openmrs.module.laboratorymodule.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;

import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.EveryOrder;
import org.openmrs.module.laboratorymodule.LabOrder;
import org.openmrs.module.laboratorymodule.LabOrderParent;

import org.openmrs.module.laboratorymodule.OrderObs;
import org.openmrs.module.laboratorymodule.PatientLabOrder;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;
import org.openmrs.module.mohappointment.model.MoHAppointment;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.openmrs.util.OpenmrsConstants;

public class LabUtils {
	protected static final Log log = LogFactory.getLog(LabUtils.class);

	public static Obs createObsGr(Order labOrder, Concept cpt) {
		Obs obs = new Obs();
		obs.setPerson(labOrder.getPatient());
		obs.setObsDatetime(new Date());
		obs.setPerson(labOrder.getPatient());
		obs.setLocation(labOrder.getEncounter().getLocation());
		obs.setDateCreated(new Date());
		obs.setCreator(Context.getAuthenticatedUser());
		obs.setConcept(cpt);

		return obs;
	}

	/**
	 * Convenience method to create an obs
	 * 
	 * @param existingObs
	 *            the existing obs (may be null)
	 * @param labOrder
	 *            the lab order
	 * @param cpt
	 *            the obs concept
	 * @param obsValue
	 *            the value of the obs
	 * @return the new obs
	 */
	public static Obs createObs(Obs existingObs, Order labOrder, Concept cpt, String obsValue, String comment) {
		Encounter labEncounter = labOrder.getEncounter();
		log.info("This is the comment" + comment);
		// if the obs is null ,then create a new obs
		if (existingObs == null) {

			existingObs = new Obs();
			existingObs.setEncounter(labEncounter);
			existingObs.setObsDatetime(new Date());
			existingObs.setOrder(labOrder);
			existingObs.setLocation(labOrder.getEncounter().getLocation());
			existingObs.setAccessionNumber(labOrder.getAccessionNumber());
			existingObs.setPerson(labOrder.getPatient());
			existingObs.setConcept(cpt);

		}

		if (obsValue != null && !obsValue.equals("")) {
			log.info(">>>>>>>>>values from form" + obsValue + "test name"
					+ cpt.getName().getName());

			if (cpt.getDatatype().isText()) {
				existingObs.setValueText(obsValue);
			} else if (cpt.getDatatype().isCoded()) {
				existingObs.setValueCoded(Context.getConceptService()
						.getConcept(Integer.parseInt(obsValue)));
			} else if (cpt.getDatatype().isNumeric()) {

				existingObs.setValueNumeric(Double.parseDouble(obsValue));
			}
		}
		existingObs.setComment(comment);
		return existingObs;
	}

	public static List<Concept> getConceptsByOrder(Order order) {

		List<Concept> members = null;

		if (order.getConcept().getConceptSets() != null) {
			members = new ArrayList<Concept>();
			for (ConceptSet set : order.getConcept().getConceptSets())
				members.add(set.getConcept());
		}
		return members;
	}

	/**
	 * Adds Lab exams creating the obs for each Lab tests linking to the values
	 * 
	 * @param Map
	 *            <String, String[]> : Map of the request parameters, with
	 *            parameter names as map keys and parameter values as map values
	 */
	public static void addLabresults(Map<String, String[]> parameterMap,
			HttpServletRequest request) {
		String comment = "";
		for (String parameterName : parameterMap.keySet()) {
			String resultComments = new String("comment");
			if (!parameterName.startsWith("labTest-")) {
				continue;
			}
			
			String[] parameterValues = parameterMap.get(parameterName);
			
			String[] splittedParameterName = parameterName.split("-");

			String conceptIdStr = splittedParameterName[1];
			String orderIdStr = splittedParameterName[2];
			//String singleValue = parameterValues[0];
			
			for (String singleValue : parameterValues) {			
			
		
			// if the value from select box is -2,go to the next test than
			// continuing
			if (singleValue.equals("-2"))
				continue;
			resultComments = resultComments + "-" + conceptIdStr + "-"
					+ orderIdStr;

			if (request.getParameter(resultComments) != null) {
				comment = request.getParameter(resultComments);				

			}

			Integer conceptId = Integer.parseInt(conceptIdStr);
			int orderId = Integer.parseInt(orderIdStr);

			// int parentConceptId=splittedParameterName.length == 4 ?
			// Integer.parseInt(splittedParameterName[3]) : 0;
			Order labOrder = Context.getOrderService().getOrder(orderId);
			Concept memberConcept = Context.getConceptService().getConcept(conceptId);

			// Can this order's concept take multiple answers?
			Map<Concept, Boolean> multipleAnswerConcepts = GlobalPropertiesMgt.getConceptHasMultipleAnswers();
			boolean isMultipleAnswer = multipleAnswerConcepts.containsKey(memberConcept);

			if (isMultipleAnswer) {
				saveMultipleResultsOnOnelabtest(labOrder, parameterValues,	memberConcept, comment);
			}
			else {
				log.info("is this single answer (" + singleValue+ ") obs for concept " + memberConcept);
				saveSingleResult(labOrder, singleValue, memberConcept, comment);
			}
		  }
		}
	}

	/**
	 * Saves a lab test as multiple obs, i.e. multiple answers for the same
	 * question concept
	 * 
	 * @param labOrder
	 * @param parameterValues
	 * @param memberConcept
	 */
	public static void saveMultipleResultsOnOnelabtest(Order labOrder,
			String[] parameterValues, Concept memberConcept, String comment) {
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		// Before creating the new Obs,delete the existing obs
		laboratoryService.deleteExistingOrderObs(labOrder, memberConcept);
		log.info(">>>>>>>labb concept" + memberConcept
				+ "+is a cpt to be saved");

		// After saving ,create the new obs and save it.For exemple if stool
		// exam is member concept of stool examination
		// save the selected ones (as answers) after deleting the existing
		// obs.These are applicable to the similar cpt
		for (String paramValue : parameterValues) {
			log.info(">>>>>>>>>>lab tests>>>>>>>>>" + memberConcept
					+ "has value" + paramValue);
			Obs labObs = createObs(null, labOrder, memberConcept, paramValue,
					comment);
			Context.getObsService().saveObs(labObs, "Save lab obs");
		}
	}

	/**
	 * Saves a lab test result as a single obs
	 * 
	 * @param labOrder
	 *            the lab order
	 * @param singleValue
	 *            the test result value
	 * @param memberConcept
	 *            the lab test concept
	 */
	public static void saveSingleResult(Order labOrder, String singleValue,
			Concept memberConcept, String comment) {
		Obs labObservation = null;
	
		//List<Obs> obss = laboratoryService.getExistingOrderObs(labOrder,	memberConcept);		

		//Obs existingObs = obss.size() == 0 ? null : obss.get(0);
		
		Obs existingObs = LaboratoryMgt.getTheExistingObsByLabOrder(labOrder, memberConcept);
		log.info(">>>>>>>>is this >>>>existing obs> "+existingObs);

		log.info(">>>>>>>>>>" + singleValue + "+is for lab tests "
				+ memberConcept.getName().getName());

		if (singleValue != null && !singleValue.equals("")) {
			labObservation = LabUtils.createObs(existingObs, labOrder,	memberConcept, singleValue, comment);
			Context.getObsService().saveObs(labObservation,	"Updated lab test result");
		}

	}

	/**
	 * Gets the Lab Exams to be selected on the Patient Dashboard a param
	 * pantientId
	 * 
	 * @return Map<ConceptName, Collection<Concept>>
	 */
	public static Map<ConceptName, Collection<Concept>> getLabExamsToOrder(
			int patientId) {
		ConceptService cptService = Context.getConceptService();
		Map<ConceptName, Collection<Concept>> mappedLabOrder = new HashMap<ConceptName, Collection<Concept>>();

		// Initilializes an integer array of length 2 where the first element is
		// 7005, second element is 7006 and so on.
		int intLabSetIds[] = { 7836, 7217, 7192, 7243, 7244, 7265, 7222, 7193,	8046, 7991, 7193 };
		@SuppressWarnings("unused")
		Object testStatus[] = null;
		for (int labSetid : intLabSetIds) {
			Concept cpt = cptService.getConcept(labSetid);
			Collection<ConceptSet> setMembers = cpt.getConceptSets();
			Collection<Concept> cptsLst = new ArrayList<Concept>();
			for (ConceptSet setMember : setMembers) {
				cptsLst.add(setMember.getConcept());
			}
			mappedLabOrder.put(cpt.getName(), cptsLst);

		}
		return mappedLabOrder;

	}

	public static Obs updateLabObs() {
		return null;

	}

	/**
	 * Saves the selected Lab Orders on the Dashboard by the provider/clinician
	 * 
	 * @param labConceptIds
	 *            selected from the forms
	 * @param labOrder
	 * @param patient
	 *            to whom is ordered Lab order
	 */
	public static void saveSelectedLabOrders(
			Map<String, String[]> parameterMap, Patient patient) {
		String labOrderTypeIdStr = GlobalPropertiesMgt.getLabOrderTypeId();
		int labOrderTypeId = Integer.parseInt(labOrderTypeIdStr);

		for (String parameterName : parameterMap.keySet()) {

			if (!parameterName.startsWith("lab-")) {
				continue;
			}
			String[] parameterValues = parameterMap.get(parameterName);
			String[] splittedParameterName = parameterName.split("-");

			String gpCptIdStr = splittedParameterName[1];
			String pcptIdstr = splittedParameterName[2];
			String chldCptIdStr = splittedParameterName.length > 3 ? splittedParameterName[3]
					: "";
			String SingleLabConceptIdstr = parameterValues[0];
			String accessionNumber = "access-" + gpCptIdStr + "-" + pcptIdstr+ "-" + chldCptIdStr;

			Order labOrder = new Order();
			
			
			labOrder.setOrderer(Context.getProviderService().getProvider(Context.getAuthenticatedUser().getId()));
			labOrder.setPatient(patient);
			labOrder.setConcept(Context.getConceptService().getConcept(
					Integer.parseInt(SingleLabConceptIdstr)));
			labOrder.setDateActivated(new Date());
			// labOrder.setAccessionNumber(accessionNumber);
			labOrder.setOrderType(Context.getOrderService().getOrderType(labOrderTypeId));
			Context.getOrderService().saveOrder(labOrder,null);

		}

	}

	/**
	 * Finds Lab orders by patient to whom Lab orders are ordered*
	 * 
	 * @param patient
	 * @param startDate
	 * @param endDate
	 * @param location
	 * @return Map<Concept, Collection<Order>>
	 */
	public static Map<Concept, Collection<Order>> findPatientLabOrders(
			int patientId, Date startDate, Date endDate, Location location) {
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		Collection<Order> labOrders = laboratoryService
				.getLabOrdersBetweentwoDate(patientId, startDate, endDate);

		Map<Concept, Collection<Order>> mappedLabOrders = new HashMap<Concept, Collection<Order>>();
		ConceptService cptService = Context.getConceptService();

		int intLabSetIds[] = { 8004, 7836, 7217, 7192, 7243, 7244, 7265, 7222, 7193, 7918, 7991,7835, 8046 };

		for (int labSetid : intLabSetIds) {
			Concept cpt = cptService.getConcept(labSetid);
			Collection<ConceptSet> setMembers = cpt.getConceptSets();
			Collection<Integer> cptsLst = new ArrayList<Integer>();
			for (ConceptSet setMember : setMembers) {
				cptsLst.add(setMember.getConcept().getConceptId());
			}

			List<Order> labOrderslist = laboratoryService.getLabOrders(
					patientId, cptsLst, startDate, endDate);

			if (labOrderslist.size() > 0) {
				mappedLabOrders.put(cpt, labOrderslist);

			}
		}
		log.info(">>>>>>>>>lab mapped size is" + mappedLabOrders.size());
		return mappedLabOrders;

	}

	/**
	 * gets Lab Encounter of patient occured in Laboratory
	 * 
	 * @param patientId
	 *            to whom this Lab Encounter belongs
	 * @return Encounter
	 */
	@SuppressWarnings("deprecation")
	public static Encounter getLabEncounter(int patientId, Date startDate) {
		LocationService locService = Context.getLocationService();
		String locationStr = Context.getAuthenticatedUser().getUserProperties()
				.get(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION);
		Location dftLoc = locService.getDefaultLocation();
		dftLoc = locService.getLocation(Integer.valueOf(locationStr));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		EncounterType encounterType = Context.getEncounterService()
				.getEncounterType(14);
		// Get patient encounters by date
		Encounter labEncounter = null;
		Collection<Encounter> encountersList = laboratoryService
				.getPatientEncountersByDate(patientId, startDate, encounterType);

		if (encountersList.size() == 0) {
			labEncounter = labEncounter = new Encounter();
			labEncounter.setEncounterDatetime(new Date());
			labEncounter.setEncounterType(encounterType);
			labEncounter.setPatient(Context.getPatientService().getPatient(
					patientId));
			log.info(">>>>>>>>>>>>>>>>get default location"
					+ Context.getLocationService().getDefaultLocation());

			labEncounter.setLocation(dftLoc);
			labEncounter.setProvider(Context.getAuthenticatedUser());
		}
		if (encountersList.size() > 0) {
			for (Encounter encounter : encountersList) {
				String encounterDateStr = df.format(encounter
						.getEncounterDatetime());
				if (encounterDateStr.equals(df.format(startDate))) {
					labEncounter = encounter;
				}
			}
		}
		return labEncounter;
	}

	/**
	 * Adds Lab code to Patient Lab orders
	 * 
	 * @param patientId
	 * @param labOrder
	 * @param labCode
	 * @param startDate
	 * @param endDate
	 */
	public static void addLabCodeToOrders(int patientId, String labCode,
			Date startDate, Date endDate) {
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		Collection<Order> labOrders = laboratoryService
				.getLabOrdersBetweentwoDate(patientId, startDate, endDate);
		// Create the lab Encounter
		Encounter labEncounter = getLabEncounter(patientId, startDate);
		Context.getEncounterService().saveEncounter(labEncounter);
		for (Order laborder : labOrders) {
			laborder.setAccessionNumber(labCode);
			laborder.setPatient(laborder.getPatient());
			laborder.setDateActivated(laborder.getEffectiveStartDate());
			laborder.setEncounter(labEncounter);
			Context.getOrderService().saveOrder(laborder,null);
			log.info(">>>>>>>Rulindo >lab order start date>>>"
					+ laborder.getEffectiveStartDate() + " and lab code" + labCode);
			// get conceptSet
			Collection<ConceptSet> childCptSet = laborder.getConcept()
					.getConceptSets();

			if (childCptSet != null) {

			}

		}

	}

	/**
	 * Creates labOrder
	 * 
	 * @param labOrder
	 * @return Order
	 */
	public static Order createlabOrder(Order labOrder) {
		return null;
	}

	/**
	 * Finds Orders by Lab code
	 * 
	 * @param labCode
	 * @return Collection<Order>
	 */
	public static Collection<Order> findOrdersByLabCode(String labCode) {
		Collection<Order> incompleteLabOrders = new ArrayList<Order>();
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		Collection<Order> labOrders = laboratoryService
				.getLabOrdersByLabCode(labCode);

		for (org.openmrs.Order order : labOrders) {
			if (order.getAccessionNumber() != null) {
				incompleteLabOrders.add(order);

			}
		}
		return incompleteLabOrders;
	}

	/**
	 * Gets the patient that corresponds to the given lab code.
	 * 
	 * @param labCode
	 * @return patient the patient matching the lab code
	 */
	public static Patient getPatientByLabCode(String labCode) {

		Integer patientId = null;
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		Collection<Order> labOrders = laboratoryService
				.getLabOrdersByLabCode(labCode);

		for (org.openmrs.Order order : labOrders) {
			if (order.getAccessionNumber() != null) {
				patientId = order.getPatient().getPatientId();
				break;
			}
		}

		return Context.getPatientService().getPatient(patientId);
	}

	/**
	 * Gets incomplete lab exam(Lab exam with no results)
	 * 
	 * @param labOrder
	 * @return Object[]
	 */
	public static Object[] getIncompleteLabExam(Order labOrder) {

		Object[] orderHistory = null;
		ConceptService cptService = Context.getConceptService();
		if (labOrder.getConcept().isNumeric()) {
			Concept c = labOrder.getConcept();
			orderHistory = new Object[] { labOrder,
					cptService.getConceptNumeric(c.getConceptId()).getUnits() };

		}

		if (labOrder.getConcept().getDatatype().isAnswerOnly()) {

			orderHistory = new Object[] { labOrder };

		}
		if (labOrder.getConcept().getDatatype().isText()) {
			orderHistory = new Object[] { labOrder };
		}
		if (labOrder.getConcept().getDatatype().isCoded()) {
			orderHistory = new Object[] { labOrder };

		}
		return orderHistory;

	}

	public static Map<Order, Obs> getObsByLabCode(String labCode) {

		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		Map<Order, Obs> resultMap = new HashMap<Order, Obs>();
		Collection<Order> incompleteLabOrders = LabUtils
				.findOrdersByLabCode(labCode);

		for (Order labOrder : incompleteLabOrders) {
			// Look up labObs(as result) for this order
			List<Obs> obsList = laboratoryService.getObsByLabOrder(labOrder
					.getOrderId());
			for (Obs labObs : obsList) {
				if (labObs != null)
					resultMap.put(labOrder, labObs);
			}

		}

		return resultMap;
	}

	/**
	 * Adds Results to Lab exams
	 * 
	 * @param labOrders
	 */
	public static void addResultsToLabExams(List<Order> labOrders) {

	}

	/**
	 * gets the results report but of patient
	 * 
	 * @param patient
	 * @param startdate
	 * @param endDate
	 * @return
	 */
	public static List<Order> getResultsReport(Patient patient, Date startdate,
			Date endDate) {
		return null;

	}

	public static Collection<Integer> getListOfChildConceptIds(Concept cpt) {

		Collection<ConceptSet> setMembers = cpt.getConceptSets();
		// Collection<Integer> cptsLst =getListOfChildConceptIds(labSetid);
		Collection<Integer> cptsLst = new ArrayList<Integer>();
		for (ConceptSet setMember : setMembers) {
			Concept childConcept = setMember.getConcept();

			if (childConcept.isSet()) {
				Collection<ConceptSet> setMemebrChildren = childConcept
						.getConceptSets();
				for (ConceptSet mbrCpt : setMemebrChildren) {
					cptsLst.add(mbrCpt.getConcept().getConceptId());
				}
			}

			cptsLst.add(setMember.getConcept().getConceptId());
			// the set member is also a set of Lab tests go through each memeber

		}

		return cptsLst;

	}

	/**
	 * Gets the Lab orders that can be ordered
	 * 
	 * @param conceptCategories
	 * @return
	 */
	public static List<LabOrderParent> getsLabOrdersByCategories(
			List<Concept> conceptCategories) {
		List<LabOrderParent> lopList = new ArrayList<LabOrderParent>();

		for (Concept concept : conceptCategories) {
			LabOrderParent labOrderParent = new LabOrderParent();
			LabOrder labOrder = null;

			labOrderParent.setGrandFatherConcept(concept);
			// if grand father is set,run through members as parent concepts
			if (concept.isSet()) {
				// get children cpts
				Collection<ConceptSet> concSet = concept.getConceptSets();
				List<LabOrder> lo = new ArrayList<LabOrder>();
				for (ConceptSet cs : concSet) {
					labOrder = new LabOrder();
					labOrder.setParentConcept(cs.getConcept());
					// if parent is set run through children
					if (cs.getConcept().isSet()) {
						// get gd children
						List<ConceptSet> parentConcept = Context
								.getConceptService().getConceptSets(
										cs.getConcept());
						List<Concept> childrenConcept = new ArrayList<Concept>();
						for (ConceptSet pc : parentConcept) {
							childrenConcept.add(pc.getConcept());
						}
						labOrder.setChildrenConcept(childrenConcept);
					}

					lo.add(labOrder);
				}
				labOrderParent.setLabTests(lo);
			}
			lopList.add(labOrderParent);

		}
		return lopList;
	}

	/**
	 * Gets all categorized ordered Lab tests by patient
	 * 
	 * @param conceptCategories
	 * @return
	 */
	public static List<PatientLabOrder> getsOrderedLabTestByCategory(
			List<Concept> conceptCategories, int patientId, Date startDate,
			Date endDate) {
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		List<PatientLabOrder> lopList = new ArrayList<PatientLabOrder>();
		PatientLabOrder patientLabOrder = new PatientLabOrder();
		for (Concept everyConcept : conceptCategories) {
			patientLabOrder.setGrandFatherConcept(everyConcept);

			if (everyConcept.isSet()) {

				Collection<ConceptSet> concSet = everyConcept.getConceptSets();
				Collection<Integer> cptsLst = new ArrayList<Integer>();

				List<EveryOrder> everyOrdersList = new ArrayList<EveryOrder>();

				for (ConceptSet cs : concSet) {
					EveryOrder everyOrder = new EveryOrder();

					everyOrder.setParentConcept(cs.getConcept());
					// if parent is set run through children

					if (cs.getConcept().isSet()) {
						// get gd children
						List<ConceptSet> parentConcepts = Context
								.getConceptService().getConceptSets(
										cs.getConcept());
						List<Concept> childrenConcept = new ArrayList<Concept>();

						for (ConceptSet pc : parentConcepts) {
							childrenConcept.add(pc.getConcept());
							cptsLst.add(pc.getConcept().getConceptId());
						}
						List<Order> ordersList = laboratoryService
								.getLabOrders(patientId, cptsLst, startDate,
										endDate);
						everyOrder.setLabOrders(ordersList);
						everyOrdersList.add(everyOrder);
					}
				}
				patientLabOrder.setLabOrders(everyOrdersList);

			}
			lopList.add(patientLabOrder);

		}

		return lopList;

	}

	/**
	 * Gets mapped Lab orders to dates
	 * 
	 * @param orders
	 * @param patient
	 *            to whom belongs the Lab orders
	 * @return Map<Date, List<OrderObs>>
	 */
	public static Map<Date, List<OrderObs>> getMappedOrderToObs(
			List<Order> orders, Patient patient) {
		Object testStatus[] = null;
		Object obsResult[] = null;
		List<Object[]> obsList = null;
		List<Date> dates = new ArrayList<Date>();
		int labOrderTypeId = Integer.parseInt(GlobalPropertiesMgt
				.getLabOrderTypeId());
		List<OrderObs> orderObsList = null;
		List<Obs> observations = Context.getObsService()
				.getObservationsByPerson(patient);
		Map<Date, List<OrderObs>> orderObsMap = new HashMap<Date, List<OrderObs>>();

		for (Order order : orders) {
			if (order.getOrderType().getOrderTypeId() == labOrderTypeId) {
				dates.add(order.getEffectiveStartDate());
			}
		}
		Set<Date> dateSet = new HashSet<Date>(dates);
		if (dateSet.size() > 0) {
			for (Date date : dateSet) {
				orderObsList = new ArrayList<OrderObs>();

				for (Order order : orders) {
					if (order.getOrderType().getOrderTypeId() == labOrderTypeId
							&& order.getEffectiveStartDate().equals(date)) {
						obsList = new ArrayList<Object[]>();
						OrderObs orderObs = new OrderObs();
						for (Obs obs : observations) {
							if (obs.getOrder() != null) {
								if (order.getOrderId() == obs.getOrder()
										.getOrderId()) {
									ConceptNumeric cptNumeric = Context
											.getConceptService()
											.getConceptNumeric(
													obs.getConcept()
															.getConceptId());
									obsResult = new Object[] { obs,
											getNormalRanges(cptNumeric) };
									obsList.add(obsResult);
								}
							}

							ConceptNumeric cptNumeric = Context
									.getConceptService().getConceptNumeric(
											order.getConcept().getConceptId());
							testStatus = new Object[] { getNormalRanges(cptNumeric) };
						}

						orderObs.setObss(obsList);
						orderObs.setOrder(order);
						orderObs.setOrderStatus(testStatus);
						orderObsList.add(orderObs);
					}
				}
				List<OrderObs> orderObsListSorted = sortList(orderObsList);
				orderObsMap.put(date, orderObsListSorted);
			}
		}

		return orderObsMap;

	}

	public static void setConsultationAppointmentAsAttended(MoHAppointment app) {
		AppointmentUtil.saveAttendedAppointment(app);
	}

	/**
	 * Creates waiting appointment in Laboratory service
	 * 
	 * @param patient
	 */
	public static void createWaitingLabAppointment(Patient patient,
			Encounter encounter) {
		MoHAppointment waitingAppointment = new MoHAppointment();
		Services service = AppointmentUtil.getServiceByConcept(Context
				.getConceptService().getConcept(new Integer(6710)));

		// Setting appointment attributes
		waitingAppointment.setAppointmentDate(new Date());
		waitingAppointment.setAttended(false);
		waitingAppointment.setVoided(false);
		waitingAppointment.setCreatedDate(new Date());
		waitingAppointment.setCreator(Context.getAuthenticatedUser());
		waitingAppointment.setProvider(Context.getAuthenticatedUser()
				.getPerson());
		waitingAppointment
				.setNote("This is a waiting patient to the Laboratory");

		waitingAppointment.setProvider(Context.getAuthenticatedUser()
				.getPerson());
		log.info("________PROVIDER________"
				+ Context.getAuthenticatedUser().getPerson().getFamilyName());

		waitingAppointment.setPatient(patient);
		waitingAppointment.setLocation(Context.getLocationService()
				.getDefaultLocation());

		log.info("<<<<<<<<<____Service____" + service.toString()
				+ "__________>>>>>>>");

		waitingAppointment.setService(service);

		if (encounter != null)
			waitingAppointment.setEncounter(encounter);

		AppointmentUtil.saveWaitingAppointment(waitingAppointment);

	}

	/**
	 * Creates waiting appointment in Consultation service
	 * 
	 * @param patient
	 *            the patient
	 */
	public static void createWaitingConsAppointment(Patient patient,
			Encounter encounter) {

		MoHAppointment waitingAppointment = new MoHAppointment();
		Services service = AppointmentUtil.getServiceByConcept(Context
				.getConceptService().getConcept(new Integer(8053)));

		// Setting appointment attributes
		waitingAppointment.setAppointmentDate(new Date());
		waitingAppointment.setAttended(false);
		waitingAppointment.setVoided(false);
		waitingAppointment.setCreatedDate(new Date());
		waitingAppointment.setCreator(Context.getAuthenticatedUser());
		waitingAppointment.setProvider(Context.getAuthenticatedUser()
				.getPerson());
		waitingAppointment
				.setNote("This is a waiting patient to the Consultation");
		log.info("________PROVIDER________"
				+ Context.getAuthenticatedUser().getPerson().getFamilyName());

		waitingAppointment.setPatient(patient);
		waitingAppointment.setLocation(Context.getLocationService()
				.getDefaultLocation());

		log.info("<<<<<<<<<____Service____" + service.toString()
				+ "__________>>>>>>>");

		waitingAppointment.setService(service);

		if (encounter != null)
			waitingAppointment.setEncounter(encounter);

		AppointmentUtil.saveWaitingAppointment(waitingAppointment);

	}

	/**
	 * Maps each Lab order to obs
	 * 
	 * @param labOrder
	 * @param obsList
	 * @return Map<Order,Object>
	 */
	public static Map<Order, Object> mapLabOrderToObs(Order labOrder,
			List<Obs> obsList) {
		Map<Order, Object> resultMap = new HashMap<Order, Object>();

		if (labOrder.getConcept().getDatatype().isAnswerOnly()) {
			// Declaration of Map<x,y> where x stands for CptChildren and Y the
			// Obs of that Cpt
			Map<Concept, Obs> orderResults = new HashMap<Concept, Obs>();
			for (Obs labObs : obsList) {
				orderResults.put(labObs.getConcept(), labObs);
			}
			resultMap.put(labOrder, orderResults);
		} else {
			// for each order we have an obs??
			for (Obs labObs : obsList) {
				resultMap.put(labOrder, labObs);
			}
		}

		return resultMap;
	}

	/**
	 * Gets a List of all Lab orders with multiple tests(Exple:Stool examination
	 * with its children)
	 * 
	 * @param labOrder
	 *            (linked to the Lab test whose children are tests)
	 * @return List<Object[]>
	 */
	public static List<Object[]> getIncompleteLabOrderForOrderWithMultipleTests(Order labOrder) {
		
		Collection<ConceptSet> cptSets = labOrder.getConcept().getConceptSets();
		//Object[] orderHistory = null;
		List<Object[]> orderHistoryList = new ArrayList<Object[]>();
		for (ConceptSet conceptSet : cptSets) {			
			Concept cpt = conceptSet.getConcept();
			if(cpt.isSet()){				
				
				for (ConceptSet cptSt : cpt.getConceptSets()) {					
					Concept concept = cptSt.getConcept();					
					Object[] orderHistory =getIncompleteLabOrder(concept);
					//log.info(">>>>>>>>children of concept"+concept.getName()+":"+orderHistory[0]);
					orderHistoryList.add(orderHistory);
				}
			}			
			else{
				Object[] orderHistory =getIncompleteLabOrder(cpt);
				orderHistoryList.add(orderHistory);  
			
			}
			

		}

		return orderHistoryList;

	}
public static Object[] getIncompleteLabOrder(Concept cpt){
	Object[] orderHistory = null;
	if(cpt.getDatatype().isNumeric()){
	ConceptNumeric	cptNumeric =Context.getConceptService().getConceptNumeric(cpt.getConceptId());
	
		if(cptNumeric.getUnits()!=null){
			orderHistory = new Object[] { cpt,cptNumeric.getUnits() };	
		}
		
		
	}
	if (cpt.getDatatype().isText()) {
		orderHistory = new Object[] { cpt };	
	}
	if (cpt.getDatatype().isCoded()) {
		orderHistory = new Object[] { cpt };
	}
		
	if (cpt.isSet()) {
		orderHistory = new Object[] { cpt };
		
	}
	
	return orderHistory;
	
}

	private static Comparator<OrderObs> LAB_RESULT_COMPARATOR = new Comparator<OrderObs>() {
		// This is where the sorting happens.
		public int compare(OrderObs ord1, OrderObs ord2) {
			int compareInt = 0;
			compareInt = ord1.getOrder().getEffectiveStartDate().compareTo(
					ord2.getOrder().getEffectiveStartDate());

			return compareInt;
		}
	};

	public static List<OrderObs> sortList(List<OrderObs> labResults) {
		List<OrderObs> sortedLabResult = new ArrayList<OrderObs>();

		for (OrderObs ord : labResults)
			sortedLabResult.add(ord);

		// Sorting Lab results with Concepts name
		Collections.sort(sortedLabResult, LAB_RESULT_COMPARATOR);

		return sortedLabResult;
	}

	public static void cancelLabOrder(String labOrderIdStr) {
		Integer orderId = Integer.parseInt(labOrderIdStr);
		Order labOrder = Context.getOrderService().getOrder(orderId);
		labOrder.setVoided(true);
		labOrder.setVoidedBy(Context.getAuthenticatedUser());
		labOrder.setDateVoided(new Date());
		Context.getOrderService().saveOrder(labOrder,null);

	}

	public static String getNormalRanges(ConceptNumeric cptNumeric) {
		String normalRange = "";
		if (cptNumeric != null && cptNumeric.getLowNormal() == null
				&& cptNumeric.getHiNormal() != null) {
			normalRange = normalRange + "<" + cptNumeric.getHiNormal() + "   "
					+ cptNumeric.getUnits();

		}
		if (cptNumeric != null && cptNumeric.getHiNormal() == null
				&& cptNumeric.getLowNormal() != null) {
			normalRange = normalRange + ">" + cptNumeric.getLowNormal() + "   "
					+ cptNumeric.getUnits();

		}
		if (cptNumeric != null && cptNumeric.getHiNormal() != null
				&& cptNumeric.getLowNormal() != null) {
			normalRange = normalRange + cptNumeric.getLowNormal() + " - "
					+ cptNumeric.getHiNormal() + "  " + cptNumeric.getUnits();

		}
		if (cptNumeric != null && cptNumeric.getHiNormal() == null
				&& cptNumeric.getLowNormal() == null) {
			normalRange = normalRange + cptNumeric.getUnits();

		}

		return normalRange;
	}

	/**
	 * Gets a list of a concept set by lab concept
	 * 
	 * @param labConcept
	 * @return List<Integer>
	 */
	public static List<Concept> getListOfConceptSetByConcept(Concept labConcept) {
		List<ConceptSet> cptSets = Context.getConceptService().getConceptSets(
				labConcept);

		List<Concept> cptList = new ArrayList<Concept>();
		for (ConceptSet conceptSet : cptSets) {
			Concept cpt = conceptSet.getConcept();
			cptList.add(cpt);

		}

		return cptList;

	}

	/**
	 * Gets mapped Lab orders to order results
	 * 
	 * @param labOrder
	 * @param obsList
	 * @return Map<Order, Object>
	 */
	public static Map<Order, Object> getMappedLabOrderToOrderResult(
			Order labOrder, List<Obs> obsList) {
		Map<Order, Object> resultMap = new HashMap<Order, Object>();
		// Declaration of Map<x,y> where x stands for CptChildren and Y the Obs
		// of that Cpt
		Map<Concept, Obs> orderResults = new HashMap<Concept, Obs>();
		for (Obs labObs : obsList) {
			orderResults.put(labObs.getConcept(), labObs);
		}
		resultMap.put(labOrder, orderResults);
		return resultMap;
	}

	public static Map<Order, Object> getMappedOrderToObs(Order labOrder,
			List<Obs> obsList) {
		Map<Order, Object> resultMap = new HashMap<Order, Object>();
		for (Obs labObs : obsList) {
			resultMap.put(labOrder, labObs);
		}
		return resultMap;
	}

	public static Map<ConceptName, List<Object[]>> getPatientLabresults(
			int patientId, Date startDate, Date endDate) {

		ConceptService cptService = Context.getConceptService();
		Map<ConceptName, List<Object[]>> mappedLabExam = new HashMap<ConceptName, List<Object[]>>();
		Map<Integer, List<Obs>> mappedLabtest = new HashMap<Integer, List<Obs>>();
		// Collection of Lab tests concept linked to lab order
		Collection<Integer> labTestConceptIds = new ArrayList<Integer>();
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		Collection<Order> labOrderList = laboratoryService
				.getPatientLabordersBetweendates(patientId, startDate, endDate);
		// run through Lab orders and get Lab concept
		for (Order order : labOrderList) {
			labTestConceptIds.add(order.getConcept().getConceptId());
		}
		// Lab concept to run through
		int labConceptIds[] = {8004,7836, 7265, 7243, 7244, 7835, 7192, 7222,	7217, 7835, 7193, 7918, 8046, 7991, 7202 };

		for (int labConceptId : labConceptIds) {
			Concept groupConcept = cptService.getConcept(labConceptId);
			List<Object[]> labExamHistory = new ArrayList<Object[]>();

			List<Object[]> labExamHistory1 = new ArrayList<Object[]>();

			ArrayList<Integer> cptList = new ArrayList<Integer>();
			Collection<Integer> childrenConceptIds = getConceptIdChilren(labConceptId);

			// run through Lab tests conceptIds list and check whether it is
			// contained in children Concepts
			for (Integer conceptId : labTestConceptIds) {

				Concept cpt = cptService.getConcept(conceptId);

				if (childrenConceptIds.contains(conceptId)) {

					if (cpt.isSet()) {

						Collection<Integer> cptsCollection = getConceptIdChilren(conceptId);

						log.info(">>>>>>>>>>this lab concept_ids collection "
								+ cptsCollection);

						//List<Obs> obsWithValues = laboratoryService.getLabExamsByExamType(patientId,cptsCollection, startDate, endDate);
						List<Obs> obsWithValues = getObsByLabOrder(patientId, cpt, cptsCollection, startDate, endDate);
					
						labExamHistory1 = getParametersOfLabConcept(obsWithValues);
						mappedLabExam.put(cpt.getName(), labExamHistory1);

					} else {
						cptList.add(conceptId);
						List<Obs> obsWithValues = laboratoryService
								.getLabExamsByExamType(patientId, cptList,
										startDate, endDate);
						labExamHistory = getParametersOfLabConcept(obsWithValues);

					}

				}
			}
			if (labExamHistory.size() > 0) {
				// map the group concept name to Lab exam history
				mappedLabExam.put(groupConcept.getName(), labExamHistory);
			}

		}
		return mappedLabExam;

	}

	/**
	 * Gets a collection of children concepts when the Lab concept is set
	 * 
	 * @param conceptId
	 * @return Collection<Integer>
	 */
	public static Collection<Integer> getConceptIdChilren(int conceptId) {

		List<Integer> childrenConceptIds = new ArrayList<Integer>();
		ConceptService cptService = Context.getConceptService();
		Concept labConcept = cptService.getConcept(conceptId);
		Collection<ConceptSet> cptsSet = labConcept.getConceptSets();
		for (ConceptSet conceptSet : cptsSet) {
			int cptId = conceptSet.getConcept().getConceptId();
			childrenConceptIds.add(cptId);
		}

		return childrenConceptIds;

	}

	public static List<Object[]> getParametersOfLabConcept(
			List<Obs> obsWithValues) {
		Object testStatus[] = null;
		ConceptService cptService = Context.getConceptService();
		List<Object[]> labExamHistory = new ArrayList<Object[]>();
		for (Obs oneLabObs : obsWithValues) {
			ConceptNumeric cptNumeric = cptService.getConceptNumeric(oneLabObs
					.getConcept().getConceptId());
			// at index 0,put the obs as one Lab obs and then at index 1 the
			// normal range
			if (oneLabObs != null && oneLabObs.getOrder() != null) {
				testStatus = new Object[] { oneLabObs,
						getNormalRanges(cptNumeric) };

				labExamHistory.add(testStatus);
			}
		}
		return labExamHistory;
	}

	/**
	 * Gets all Obs with values/result but linked to the ordered Lab orders
	 * 
	 * @param patientId
	 * @param startDate
	 * @param endDate
	 * @return List<Obs>
	 */
	public static List<Obs> getObsByLabOrder(int patientId, Concept cpt,
			Collection<Integer> cptIds, Date startDate, Date endDate) {
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		List<Obs> obsWithValues = laboratoryService.getLabExamsByExamType(
				patientId, cptIds, startDate, endDate);

		List<Obs> obsToLabOrder = new ArrayList<Obs>();
		for (Obs obs : obsWithValues) {
			if (obs.getOrder().getConcept().getConceptId() == cpt
					.getConceptId()) {
				obsToLabOrder.add(obs);
			}

		}
		return obsToLabOrder;

	}
	
public  static Object[] getPatientIdentificationFromLab(int patientId,Date startDate, Date endDate){
	
	ConceptService cptService = Context.getConceptService();
	LaboratoryService laboratoryService = Context.getService(LaboratoryService.class);
	Collection<Integer> labTestConceptIds = new ArrayList<Integer>();
	Object patientIdentifElement [] = null;
    Collection<Order> labOrderList =  laboratoryService.getPatientLabordersBetweendates(patientId, startDate, endDate);

	int labConceptIds[] = {8004,7836, 7265, 7243, 7244, 7835, 7192, 7222,	7217, 7835, 7193, 7918, 8046, 7991, 7202 };
	for (Order order : labOrderList) {
		labTestConceptIds.add(order.getConcept().getConceptId());
	}
	for (int labConceptId : labConceptIds) {	

		ArrayList<Integer> cptList = new ArrayList<Integer>();
		Collection<Integer> childrenConceptIds = getConceptIdChilren(labConceptId);

		// run through Lab tests conceptIds list and check whether it is
		// contained in children Concepts
		for (Integer conceptId : labTestConceptIds) {

			Concept cpt = cptService.getConcept(conceptId);

			if (childrenConceptIds.contains(conceptId)) {

				if (cpt.isSet()) {

					Collection<Integer> cptsCollection = getConceptIdChilren(conceptId);
					

					//List<Obs> obsWithValues = laboratoryService.getLabExamsByExamType(patientId,cptsCollection, startDate, endDate);
					List<Obs> obsWithValues = getObsByLabOrder(patientId, cpt, cptsCollection, startDate, endDate);
					Obs labObs =obsWithValues.get(0);
					patientIdentifElement = new Object[] {labObs.getAccessionNumber(),labObs.getOrder() };
				
					

				}
				else {
					
					cptList.add(conceptId);
					List<Obs> obsWithValues = laboratoryService.getLabExamsByExamType(patientId, cptList,	startDate, endDate);
					try {						
						Obs labObs = obsWithValues.get(0);
						patientIdentifElement  = new Object[] { labObs.getAccessionNumber(),labObs.getOrder() };
					} catch (Exception e) {
						// TODO: handle exception
					}
				
					
				}
				

			}
		}
		

	}
		
		return patientIdentifElement ;
		
	}

}
