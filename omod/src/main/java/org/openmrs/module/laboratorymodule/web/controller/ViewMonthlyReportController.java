package org.openmrs.module.laboratorymodule.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.ConceptName;
import org.openmrs.module.laboratorymodule.advice.MappedLabExamManagement;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ViewMonthlyReportController extends ParameterizableViewController {
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<ConceptName, List<Object[]>> mappedLabExam = null;
		SimpleDateFormat df = OpenmrsUtil.getDateFormat();
		Map<String, Object> model = new HashMap<String, Object>();

		String startDat = request.getParameter("startDate");
		String endDat = request.getParameter("endDate");
		// Date declaration
		Date startDate = null;
		Date endDate = null;

		/*
		 * Data validation
		 */

		if (startDat != null && startDat.length() != 0) {
			startDate = df.parse(startDat);
		}
		if (endDat != null && endDat.length() != 0) {
			endDate = df.parse(endDat);
		}
		if (startDate != null && endDate != null) {
			mappedLabExam = MappedLabExamManagement
					.getMappedExamsByLabTypeBetweenTwoDates(request, startDate,
							endDate);

			model.put("mappedLabExams", mappedLabExam);
			model.put("startDate", startDat);
			model.put("endDate", endDat);

		}

		return new ModelAndView(getViewName(), model);

	}
}
