package org.openmrs.module.laboratorymodule.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class EditLabOrderController extends ParameterizableViewController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();		
	
        String startDate = request.getParameter("startDate");
        String labCode =request.getParameter("labcode");
        String orderId =request.getParameter("orderId");
        String patientId =request.getParameter("patientId");
        
       
        
		
        //model.put("endDate", endDate);
	    model.put("startDate",startDate);	
		model.put("labCode",labCode);
		model.put("orderId",orderId);
		model.put("patientId",patientId);
		
		
		
		model.put("appointmentId", request.getAttribute("appointmentId"));
		return new ModelAndView(getViewName(), model);
		
		
		
	}
	

}
