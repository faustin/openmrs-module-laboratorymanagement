package org.openmrs.module.laboratorymodule.web.controller;

import java.util.ArrayList;
import java.util.Collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

import org.openmrs.module.laboratorymodule.service.LaboratoryService;
import org.openmrs.module.laboratorymodule.utils.GlobalPropertiesMgt;
import org.openmrs.module.laboratorymodule.utils.LabUtils;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class AddResultController extends ParameterizableViewController {

	protected final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings( { "unchecked", "deprecation" })
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		Map<Concept, List<Object[]>> mapLabtests = new HashMap<Concept, List<Object[]>>();
		Patient patient = null;
		String labCode = "";
		// Each order is mapped to obs
		Map<Order, Object> resultMap = new HashMap<Order, Object>();
		// the Map where the key is the concept/Lab test and the key is the List
		// of all lab tests linked to that concept
		Map<Concept, List<Object[]>> groupedMultipletest = new HashMap<Concept, List<Object[]>>();

		LaboratoryService laboratoryService = Context.getService(LaboratoryService.class);

		if (request.getParameter("labCode") != null
				&& !request.getParameter("labCode").equals("")) {
			labCode = request.getParameter("labCode");
			if (laboratoryService.isFoundLabCode(labCode)) {

				patient = LabUtils.getPatientByLabCode(labCode);
				Collection<Order> incompleteLabOrders = LabUtils.findOrdersByLabCode(labCode);				

				List<Object[]> incompleteLabExams = null;
				List<Object[]> orderHistryList = null;
				Object[] orderHistory = null;

				int intLabSetIds[] = {7836,7217, 7192, 7202, 7243, 7244, 7265,7222, 7193, 7918, 7835, 8046 };

				for (int labConceptId : intLabSetIds) {
					Concept labConcept = Context.getConceptService().getConcept(labConceptId);
					List<ConceptSet> cptSets = Context.getConceptService().getConceptSets(labConcept);

					List<Concept> cptList = new ArrayList<Concept>();
					for (ConceptSet conceptSet : cptSets) {
						Concept cpt = conceptSet.getConcept();
						cptList.add(cpt);
					}
					// run through all orders and group all Lab tests that are
					// from one group
					incompleteLabExams = new ArrayList<Object[]>();
					for (Order labOrder : incompleteLabOrders) {
						// Look up labObs(as result) for this order

						if (cptList.contains(labOrder.getConcept())) {
							List<Obs> obsList = laboratoryService.getObsByLabOrder(labOrder.getOrderId());

							// If order concept is a construct
							if (labOrder.getConcept().getDatatype().isAnswerOnly()) {
								// Declaration of Map<x,y> where x stands for
								// CptChildren and Y the Obs of that Cpt
								Map<Concept, Obs> orderResults = new HashMap<Concept, Obs>();
								for (Obs labObs : obsList) {
									orderResults.put(labObs.getConcept(),labObs);
								}
								resultMap.put(labOrder, orderResults);
								orderHistryList = LabUtils.getIncompleteLabOrderForOrderWithMultipleTests(labOrder);

								groupedMultipletest.put(labOrder.getConcept(),orderHistryList);
							} 
							else {
								// for each order we have an obs??
								for (Obs labObs : obsList) {
									resultMap.put(labOrder, labObs);
								
								}
							}
							orderHistory = LabUtils	.getIncompleteLabExam(labOrder);
							incompleteLabExams.add(orderHistory);
						}

					}
					if (incompleteLabExams.size() > 0) {
						mapLabtests.put(labConcept, incompleteLabExams);
					}
				}

				model.put("multipleAnswerConcepts", GlobalPropertiesMgt.getConceptHasMultipleAnswers());
				model.put("resultsMap", resultMap);
				// model.put("incompleteLabOrders", incompleteLabExams);
				model.put("labCode", labCode);
				model.put("mapLabeTest", mapLabtests);
				model.put("groupedTests", groupedMultipletest);			

				model.put("patient", patient);
				model.put("patientId", patient.getPatientId());

			} else {
				model.put("msg", "Lab code" + labCode + " is not found");

			}

		}

		/*
		 * if (request.getParameter("save") != null) { // Map of the request
		 * parameters, with parameter names as map keys // and parameter values
		 * as map values Map<String, String[]> parameterMap =
		 * request.getParameterMap();
		 * LabUtils.addLabresults(parameterMap,request); model.put("msg",
		 * "Laboratory results are now saved"); }
		 */

		if (request.getParameter("save") != null) {
			// Map of the request parameters, with parameter names as map keys
			// and parameter values as map values
			Map<String, String[]> parameterMap = request.getParameterMap();
			LabUtils.addLabresults(parameterMap, request);

			/**
			 * <<<<<<<<<< APPOINTMENT CONSULTATION WAITING CREATED HERE
			 * >>>>>>>>>>parameterMap
			 */

			if (request.getParameter("patient_id") != null
					&& !request.getParameter("patient_id").equals("")) {
				patient = Context.getPatientService().getPatient(
						Integer.parseInt(request.getParameter("patient_id")));
				LabUtils.createWaitingConsAppointment(patient, null);
			}

			/**
			 * <<<<<<<<<< APPOINTMENT STUFF ENDS HERE!!
			 * >>>>>>>>>>>>>>>>>>>>>>>>>>>
			 */

			model.put("msg", "Laboratory results are successfully saved !");
		}

		return new ModelAndView(getViewName(), model);

	}

}
