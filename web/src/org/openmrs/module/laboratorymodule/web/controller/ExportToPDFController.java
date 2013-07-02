package org.openmrs.module.laboratorymodule.web.controller;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.MappedLabExamManagement;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;
import org.openmrs.module.laboratorymodule.utils.LabUtils;
import org.openmrs.util.OpenmrsUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public class ExportToPDFController extends AbstractController {
	protected final Log log = LogFactory.getLog(getClass());

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SimpleDateFormat df = OpenmrsUtil.getDateFormat();

		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		String conceptIdStr = request.getParameter("testType");
		String patientIdstr=request.getParameter("patientId");
		
		
		String startD = request.getParameter("startDate");
		String endD = request.getParameter("endDate");
		
		
		
		// Date declaration
		Date startDate = null;
		Date endDate = null;
		/*
		 * Data validation
		 */

		if (startD != null && startD.length() != 0) {
			startDate = df.parse(startD);
		}
		if (endD != null && endD.length() != 0) {
			endDate = df.parse(endD);
		}
		

		if (patientIdstr!=null){
			int patientId=Integer.parseInt(patientIdstr);
			
			//Map<ConceptName, List<Object[]>> mappedLabExam= MappedLabExamManagement	.getMappedExamsByLabType(patientId,startDate,endDate);
			Map<ConceptName, List<Object[]>>	mappedLabExam = LabUtils.getPatientLabresults(patientId, startDate, endDate);
			//Map<ConceptName, List<Object[]>> mappedLabExam  = (Map<ConceptName, List<Object[]>>) request.getSession().getAttribute("patientMappedLabExam");
			laboratoryService.exportPatientReportToPDF(request, response, mappedLabExam, "patientReport.pdf", "test",patientId);
					
			
		}
		
		return null;
	}
}
