package org.openmrs.module.laboratorymodule.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class DisplayLabExamsController extends ParameterizableViewController {

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		SimpleDateFormat df = OpenmrsUtil.getDateFormat();
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);

		String posLabConceptIdStr = request.getParameter("poslabConceptId");
		String neglabConceptIdStr = request.getParameter("neglabConceptId");
		String totlabConceptIdStr = request.getParameter("totlabConceptId");

		String startDateStr = request.getParameter("startDate");
		String endDateStr = request.getParameter("endDate");
		Date startDate = df.parse(startDateStr);
		Date endDate = df.parse(endDateStr);
		if (posLabConceptIdStr != null) {
			int labConceptId = Integer.parseInt(posLabConceptIdStr);

			List<Obs> positiveLabExams = laboratoryService
					.getAllPositiveLabExams(startDate, endDate, labConceptId);
			model.put("positiveLabExams", positiveLabExams);

		}
		if (neglabConceptIdStr != null) {
			int labConceptId = Integer.parseInt(neglabConceptIdStr);

			List<Obs> negativeLabExams = laboratoryService
					.getAllNegtiveLabExams(startDate, endDate, labConceptId);
			model.put("negativeLabExams", negativeLabExams);

		}
		if (totlabConceptIdStr != null) {
			int totlabConceptId = Integer.parseInt(totlabConceptIdStr);
			List<Obs> allLabExamsByName = LaboratoryMgt
					.getAllTestWithResult(laboratoryService
							.getLabExamsByExamTypeBetweenTwoDates(startDate,
									endDate, totlabConceptId));
			model.put("labExamsByName", allLabExamsByName);

		}
         model.put("startDate", startDateStr);
         model.put("endDate", endDateStr);
         
		return new ModelAndView(getViewName(), model);
	}
}
