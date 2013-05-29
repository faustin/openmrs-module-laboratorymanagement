package org.openmrs.module.laboratorymodule.utils;

import java.text.ParseException;
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
import org.hibernate.hql.ast.util.NodeTraverser.VisitationStrategy;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;

import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.EveryOrder;
import org.openmrs.module.laboratorymodule.LabOrder;
import org.openmrs.module.laboratorymodule.LabOrderParent;
import org.openmrs.module.laboratorymodule.OrderObs;
import org.openmrs.module.laboratorymodule.PatientLabOrder;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.AppointmentState;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.springframework.web.HttpRequestHandler;

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
	public static Obs createObs(Obs existingObs, Order labOrder, Concept cpt,
			String obsValue, String comment) {
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
			String singleValue = parameterValues[0];
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
			Concept memberConcept = Context.getConceptService().getConcept(
					conceptId);

			// Can this order's concept take multiple answers?
			Map<Concept, Boolean> multipleAnswerConcepts = GlobalPropertiesMgt
					.getConceptHasMultipleAnswers();
			boolean isMultipleAnswer = multipleAnswerConcepts
					.containsKey(memberConcept);

			if (isMultipleAnswer) {
				saveMultipleResultsOnOnelabtest(labOrder, parameterValues,
						memberConcept, comment);
			} else {
				log.info("Saving single answer (" + singleValue
						+ ") obs for concept " + memberConcept);
				saveSingleResult(labOrder, singleValue, memberConcept, comment);
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

		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		List<Obs> obss = laboratoryService.getExistingOrderObs(labOrder,
				memberConcept);

		log.info(">>>>>>>>List obs >>>>>cpt>>>>>>" + obss);
		// since each obs is linked to one ,then get it at index 0

		Obs existingObs = obss.size() == 0 ? null : obss.get(0);

		log.info(">>>>>>>>>>" + singleValue + "+is for lab tests "
				+ memberConcept.getName().getName());

		if (singleValue != null && !singleValue.equals("")) {
			labObservation = LabUtils.createObs(existingObs, labOrder,
					memberConcept, singleValue, comment);
			Context.getObsService().saveObs(labObservation,
					"Updated lab test result");
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
		int intLabSetIds[] = { 7836, 7217, 7192, 7243, 7244, 7265, 7222, 7193,
				8046, 7991, 7193 };
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
			String accessionNumber = "access-" + gpCptIdStr + "-" + pcptIdstr
					+ "-" + chldCptIdStr;

			Order labOrder = new Order();
			labOrder.setOrderer(Context.getAuthenticatedUser());
			labOrder.setPatient(patient);
			labOrder.setConcept(Context.getConceptService().getConcept(
					Integer.parseInt(SingleLabConceptIdstr)));
			labOrder.setStartDate(new Date());
			// labOrder.setAccessionNumber(accessionNumber);
			labOrder.setOrderType(Context.getOrderService().getOrderType(
					labOrderTypeId));
			Context.getOrderService().saveOrder(labOrder);

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
		log.info(">>>>lab orders>>>lab orders" + labOrders);
		Map<Concept, Collection<Order>> mappedLabOrders = new HashMap<Concept, Collection<Order>>();
		ConceptService cptService = Context.getConceptService();

		int intLabSetIds[] = { 7836, 7217, 7192, 7243, 7244, 7265, 7222, 7193,
				7918, 7835, 8046 };

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
	public static Encounter getLabEncounter(int patientId) {
		Encounter labEncounter = new Encounter();
		labEncounter.setEncounterDatetime(new Date());
		labEncounter.setEncounterType(Context.getEncounterService()
				.getEncounterType(14));
		labEncounter.setPatient(Context.getPatientService().getPatient(
				patientId));
		labEncounter.setLocation(Context.getLocationService().getLocation(1));
		labEncounter.setProvider(Context.getAuthenticatedUser());

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
		Encounter labEncounter = getLabEncounter(patientId);
		Context.getEncounterService().saveEncounter(labEncounter);
		for (Order laborder : labOrders) {
			laborder.setAccessionNumber(labCode);
			laborder.setPatient(laborder.getPatient());
			laborder.setStartDate(laborder.getStartDate());
			laborder.setEncounter(labEncounter);
			Context.getOrderService().saveOrder(laborder);
			log.info(">>>>>>>Rulindo >lab order start date>>>"
					+ laborder.getStartDate() + " and lab code" + labCode);
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
				dates.add(order.getStartDate());
			}
		}
		Set<Date> dateSet = new HashSet<Date>(dates);
		if (dateSet.size() > 0) {
			for (Date date : dateSet) {
				orderObsList = new ArrayList<OrderObs>();

				for (Order order : orders) {
					if (order.getOrderType().getOrderTypeId() == labOrderTypeId
							&& order.getStartDate().equals(date)) {
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

	public static void setConsultationAppointmentAsAttended(Appointment app) {
		AppointmentUtil.saveAttendedAppointment(app);
	}

	/**
	 * Creates waiting appointment in Laboratory service
	 * 
	 * @param patient
	 * @throws ParseException
	 */
	public static void createWaitingLabAppointment(Patient patient,
			Encounter encounter) throws ParseException {
		Appointment waitingAppointment = new Appointment();
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
		if (!AppointmentUtil.isPatientAlreadyWaitingThere(patient,
				new AppointmentState(4, "WAITING"), service, new Date()))
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

		Appointment waitingAppointment = new Appointment();
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
	public static List<Object[]> getIncompleteLabOrderForOrderWithMultipleTests(
			Order labOrder) {
		ConceptService cptService = Context.getConceptService();
		Collection<ConceptSet> cptSets = labOrder.getConcept().getConceptSets();
		Object[] orderHistory = null;
		List<Object[]> orderHistoryList = new ArrayList<Object[]>();
		for (ConceptSet conceptSet : cptSets) {
			Concept cpt = conceptSet.getConcept();

			if (cpt.getDatatype().isNumeric()) {
				orderHistory = new Object[] {
						cpt,
						cptService.getConceptNumeric(cpt.getConceptId())
								.getUnits() };
				orderHistoryList.add(orderHistory);
			}
			if (cpt.getDatatype().isText()) {
				orderHistory = new Object[] { cpt };
				orderHistoryList.add(orderHistory);
			}
			if (cpt.getDatatype().isCoded()) {
				orderHistory = new Object[] { cpt };
				orderHistoryList.add(orderHistory);
			}
			if (cpt.isSet()) {
				orderHistory = new Object[] { cpt };
				orderHistoryList.add(orderHistory);
			}

		}

		return orderHistoryList;

	}

	private static Comparator<OrderObs> LAB_RESULT_COMPARATOR = new Comparator<OrderObs>() {
		// This is where the sorting happens.
		public int compare(OrderObs ord1, OrderObs ord2) {
			int compareInt = 0;
			compareInt = ord1.getOrder().getStartDate()
					.compareTo(ord2.getOrder().getStartDate());

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
		Context.getOrderService().saveOrder(labOrder);

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

}
