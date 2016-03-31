package org.openmrs.module.laboratorymodule.web.controller;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptName;

import org.openmrs.api.context.Context;

import org.openmrs.module.laboratorymodule.utils.LabUtils;

import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class PatientReportController extends ParameterizableViewController {
	protected final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings("deprecation")
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// Map the Laboratory type exams and its children
		Map<ConceptName, List<Object[]>> mappedLabExam = null;
		SimpleDateFormat df = OpenmrsUtil.getDateFormat();
		// List to store all exams with results

		Map<String, Object> model = new HashMap<String, Object>();
		// get parameters
		String patientIdStr = request.getParameter("patientId");
		String startD = request.getParameter("startDate");
		String endD = request.getParameter("endDate");

		log.info("starting DAte" + startD + "enddate" + endD);
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
		
	

		if (patientIdStr != null && !patientIdStr.equals("")) {
			int patientId = Integer.parseInt(patientIdStr);			

			if ((startDate != null) && (endDate!= null)) {
				// patientLabExam =laboratoryService
				// .getLabExamsByExamType(patientId);
				
				//mappedLabExam = MappedLabExamManagement.getMappedExamsByLabType(patientId,startDate,endDate);
				
				mappedLabExam = LabUtils.getPatientLabresults(patientId, startDate, endDate);
				request.getSession().setAttribute("patientMappedLabExam",mappedLabExam);

				model.put("mappedLabExams", mappedLabExam);
                 // System.out.println(">>>>>>Patient report"+mappedLabExam);
				model.put("patient", Context.getPatientService().getPatient(patientId));
				model.put("startDate", startD);
				model.put("endDate", endD);

			}

		}

		return new ModelAndView(getViewName(), model);

	}

}
