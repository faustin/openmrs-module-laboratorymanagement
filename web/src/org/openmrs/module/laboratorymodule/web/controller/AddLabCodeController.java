package org.openmrs.module.laboratorymodule.web.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.util.Log;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.advice.MappedLabExamManagement;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class AddLabCodeController extends ParameterizableViewController {
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();		
	
		int  patientId=Integer.parseInt(request.getParameter("patientId"));
        String startDate = request.getParameter("startDate");
        String labCode =request.getParameter("labcode");
       
        
		
        //model.put("endDate", endDate);
	    model.put("startDate",startDate);
		model.put("patientId",patientId);
		model.put("labCode",labCode);
		
		
		
		model.put("appointmentId", request.getAttribute("appointmentId"));
		return new ModelAndView(getViewName(), model);
		
		
		
	}
}