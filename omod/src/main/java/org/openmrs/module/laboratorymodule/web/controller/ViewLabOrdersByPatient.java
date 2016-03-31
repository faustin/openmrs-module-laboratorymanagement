package org.openmrs.module.laboratorymodule.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;




import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ViewLabOrdersByPatient extends ParameterizableViewController {

	protected final Log log = LogFactory.getLog(getClass());

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyy");

		OrderService ordService=Context.getOrderService();
		 Collection<Order>incompleteLabOrders=new ArrayList<Order>();
		if (request.getParameter("encounterId") != null
				&& !request.getParameter("encounterId").equals("")) {
			String strDate=request.getParameter("startDate");
			if (request.getParameter("numericValue") != null) {
				saveLabExam(request);
				model.put("msg", "The result is successifully  saved");
				
			}
			if (request.getParameter("answerConceptId") != null) {
				saveLabExam(request);
				model.put("msg", "the result is successifully  saved");
				
			}
			
			log.info(">>>>>>>>>>>>>>>>><exam the startDate "+strDate);		
			
			int encounterId = Integer.parseInt(request.getParameter("encounterId"));
			Encounter labEncounter=Context.getEncounterService().getEncounter(encounterId);
			Date labEncounterDate=labEncounter.getEncounterDatetime();	
			
			String labEncounterDateStr=df.format(labEncounterDate);
			Collection<Order> labOrders = ordService
					.getOrdersByPatient(Context.getPatientService().getPatient(
							labEncounter.getPatientId()));
		
			for (Order order : labOrders){
				String orderStrDate=df.format(order.getStartDate());
				
				if (orderStrDate.equals(labEncounterDateStr)) {
					if (order.getAutoExpireDate()==null) {
						incompleteLabOrders.add(order);
						
					}
					
				}		
						
				
			}
			
			
			model.put("patient",labEncounter.getPatient());
			model.put("encounterId", encounterId);
			model.put("strDate", strDate);
			model.put("labOrders", incompleteLabOrders);			

		}

		
		return new ModelAndView(getViewName(), model);
	}
	public void saveLabExam(HttpServletRequest request) throws ParseException {
		
		OrderService ordService = Context.getOrderService();

		LocationService locService = Context.getLocationService();
		ConceptService cptService = Context.getConceptService();
		PatientService ptService = Context.getPatientService();

		String orderIdStr = request.getParameter("orderId");
		String labEncounterId = request.getParameter("encounterId");
		Patient ptent = Context.getEncounterService().getEncounter(
				Integer.parseInt(labEncounterId)).getPatient();
		int orderId = Integer.parseInt(orderIdStr);
		Order labOrder = ordService.getOrder(orderId);
		Obs labExamWithResult = null;
		

		if (request.getParameter("numericValue") != null) {
			labExamWithResult = new Obs();
			// update lab order
//			Patient patient = ptService.getPatient(Integer.valueOf(request
//					.getParameter("patientId")));
			Location location = locService.getLocation(Integer
					.valueOf(request.getParameter("locationId")));
			Concept concept = cptService.getConcept(Integer.valueOf(request
					.getParameter("conceptId")));
			String strDate = request.getParameter("obsDateTime");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			labOrder.setAutoExpireDate(new Date());
			labOrder.setDiscontinued(true);
			labOrder.setDiscontinuedDate(new Date());
			labOrder.setDiscontinuedBy(Context.getAuthenticatedUser());
			ordService.updateOrder(labOrder);

			Date observedOn = sdf.parse(strDate);
			// labExamWithResult.setOrder(labOrder);
			labExamWithResult.setEncounter(Context.getEncounterService()
					.getEncounter(Integer.parseInt(labEncounterId)));
			labExamWithResult.setConcept(concept);
			labExamWithResult.setObsDatetime(observedOn);
			labExamWithResult.setLocation(location);
			labExamWithResult.setPerson(ptent);
			// set the value
			labExamWithResult.setValueNumeric(Double.parseDouble(request
					.getParameter("numericValue")));

			Context.getObsService().saveObs(labExamWithResult, null);
		}
		if (request.getParameter("answerConceptId") != null) {
			if (request.getParameter("save") != null) {
//				Patient patient = ptService.getPatient(Integer.valueOf(request
//						.getParameter("patientId")));
				Location location = locService.getLocation(Integer
						.valueOf(request.getParameter("locationId")));
				Concept concept = cptService.getConcept(Integer.valueOf(request
						.getParameter("conceptId")));
				String strDate = request.getParameter("obsDateTime");
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

				labExamWithResult = new Obs();
				
				labOrder.setAutoExpireDate(new Date());
				labOrder.setDiscontinued(true);
				labOrder.setDiscontinuedDate(new Date());
				labOrder.setDiscontinuedBy(Context.getAuthenticatedUser());
				ordService.updateOrder(labOrder);
				
				
				

				
				Date observedOn = sdf.parse(strDate);
				labExamWithResult.setEncounter(Context.getEncounterService()
						.getEncounter(Integer.parseInt(labEncounterId)));
				labExamWithResult.setConcept(concept);
				labExamWithResult.setObsDatetime(observedOn);
				labExamWithResult.setLocation(location);
				labExamWithResult.setPerson(Context.getEncounterService().getEncounter(Integer.parseInt(labEncounterId)).getPatient());
				// set the value
				labExamWithResult.setValueCoded(cptService.getConcept(Integer
						.valueOf(request.getParameter("answerConceptId"))));
				
				Context.getObsService().saveObs(labExamWithResult, null);

			}
		}
		
		
	}

}
