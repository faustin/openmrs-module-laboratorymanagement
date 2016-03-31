package org.openmrs.module.laboratorymodule.web.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ViewAlltestByConceptController extends
		ParameterizableViewController {
	protected final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings("unchecked")
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		
		Map<String, Object> model = new HashMap<String, Object>();
		String conceptIdstr = request.getParameter("testType");
		String locationIdstr = request.getParameter("locationId");
		String startDt = request.getParameter("startDate");
		String endDt = request.getParameter("endDate");
		String labTest = new String("Laboratory test: ");
		if (request.getParameter("lineNumber")!=null) {
			List<Object[]>listedLabTest=(List<Object[]>)request.getSession().getAttribute("labTest_"+(Integer.parseInt(request.getParameter("lineNumber"))-1));
			model.put("listOflabtest", listedLabTest);
		}
		if (request.getParameter("lineNumber")==null) {
			List<Object[]>listedLabTest =  (List<Object[]>) request.getSession().getAttribute("labTakentestconcept");
			Concept cpt = Context.getConceptService().getConcept(
				Integer.parseInt(conceptIdstr));	

			//model.put("testTitle",labTest+cpt.getDisplayString() );
			model.put("conceptId", conceptIdstr);
			model.put("locationId", locationIdstr);
			model.put("locationId", conceptIdstr);
			model.put("startDate", startDt);
			model.put("endDate", endDt);
			model.put("listOflabtest", listedLabTest);
			
		}
			
				
		
		return new ModelAndView(getViewName(), model);

	}
	
}