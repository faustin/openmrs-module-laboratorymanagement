package org.openmrs.module.laboratorymodule.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.advice.MappedLabExamManagement;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class AddLabCodeController extends ParameterizableViewController {
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		Map<String, Object> model = new HashMap<String, Object>();
		
		Date lastMidnight = LaboratoryMgt.getPreviousMidnight(null);
		Date zeroDay = LaboratoryMgt.addDaysToDate(lastMidnight, 0);

		Date startDate = LaboratoryMgt.getDateParameter(request, "startDate",
				zeroDay);
		Date endDate = LaboratoryMgt.getDateParameter(request, "endDate",
				lastMidnight);
		int  patientId=Integer.parseInt(request.getParameter("patientId"));
		String labCodeStr="";	
		
		if (request.getParameter("labCode")!=null) {
			
			//Create labEncounter
			
			Encounter labEncounter=new Encounter();
			labEncounter.setEncounterDatetime(new Date());
			labEncounter.setEncounterType(Context.getEncounterService().getEncounterType(14));
			labEncounter.setPatient(Context.getPatientService().getPatient(patientId));
			labEncounter.setLocation(Context.getLocationService().getLocation(1));
			labEncounter.setProvider(Context.getAuthenticatedUser());
			Context.getEncounterService().saveEncounter(labEncounter);
			
			
			labCodeStr=request.getParameter("labCode");			
	
			Map<Concept, Collection<Order>> mappedLabOrders = MappedLabExamManagement
			.getMappedLabOrder(patientId, startDate, endDate);
			for (Concept labConcept  : mappedLabOrders.keySet()) {
				Collection<Order>labOrders=mappedLabOrders.get(labConcept);
				// iterate each order and update it adding accession number as LabCode
				for (Order laborder : labOrders) {
					laborder.setAccessionNumber(labCodeStr);
					laborder.setAccessionNumber(labCodeStr);
					laborder.setPatient(laborder.getPatient());
					laborder.setStartDate(laborder.getStartDate());
					laborder.setEncounter(labEncounter);
					Context.getOrderService().saveOrder(laborder);			
					
					
				}
				
			}
			model.put("msg", "Lab coded  is  successiful added");		
			
		}
		

		model.put("startDate",startDate);
		model.put("endDate", endDate);
		model.put("patientId",patientId);
		model.put("appointmentId", request.getAttribute("appointmentId"));
		return new ModelAndView(getViewName(), model);
		
		
		
	}
}