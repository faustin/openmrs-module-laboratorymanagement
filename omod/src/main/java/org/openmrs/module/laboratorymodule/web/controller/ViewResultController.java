package org.openmrs.module.laboratorymodule.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ViewResultController extends ParameterizableViewController {
	protected final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings( { "unchecked", "deprecation" })
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Obs> testsWithResult = new ArrayList<Obs>();
		SimpleDateFormat df = OpenmrsUtil.getDateFormat();
		ObsService oService = Context.getObsService();
		LaboratoryService laboratoryService = Context
				.getService(LaboratoryService.class);
		// LaboratoryService labService =
		// Context.getService(LaboratoryService.class);
		Map parameterMap = request.getParameterMap();

		Map<String, Object> model = new HashMap<String, Object>();

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

		if (request.getParameter("patientId") != null
				&& !request.getParameter("patientId").equals("")) {

			int patientId = Integer.parseInt(request.getParameter("patientId"));
			log.info(">>>>>patientId" + patientId);

			if ((startDate == null) && (endDate == null)) {

				

				testsWithResult = LaboratoryMgt.getAllTestWithResult(oService
						.getObservationsByPerson(Context.getPersonService()
								.getPerson(patientId)));

			}
			if ((startDate != null) && (endDate != null)) {
				testsWithResult = LaboratoryMgt
						.getAllTestWithResult(laboratoryService
								.getTestOfPatientBetweenTwoDate(patientId,
										startDate, endDate));
				log.info(">>>>>patientId" + patientId);
				log.info("<<<<<>>>>>all tests with their results"
						+ testsWithResult);

			}

			model.put("thePatient", Context.getPatientService().getPatient(
					patientId));
			model.put("patientObs", testsWithResult);
			model.put("startdate", startDate);
			model.put("enddate", endDate);

		}
		// get all tests between two date by location

		else if (request.getParameter("locationId") != null
				&& !request.getParameter("locationId").equals("")) {
			int locationId = Integer.parseInt(request
					.getParameter("locationId"));
			Location location = Context.getLocationService().getLocation(
					locationId);

			if ((startDate != null) && (endDate != null)) {

				testsWithResult = LaboratoryMgt
						.getAllTestWithResult(laboratoryService
								.getTestsBetweenTwoDateByLocation(locationId,
										startDate, endDate));

				model.put("patientObs", testsWithResult);
				model.put("startdate", startDate);
				model.put("enddate", endDate);
				model.put("location", location);
			}
			if ((startDate == null) && (endDate == null)) {
				log.info("hellooo this is locationId" + locationId);

				testsWithResult = LaboratoryMgt
						.getAllTestWithResult(laboratoryService
								.getAllObsByLocation(locationId));
				model.put("patientObs", testsWithResult);

			}

		} else if (startD != null && !startD.equals("") && endD != null
				&& !endD.equals("")) {
			testsWithResult = LaboratoryMgt
					.getAllTestWithResult(laboratoryService
							.getPatientTestBeforeDate(startDate, endDate));
			model.put("patientObs", testsWithResult);
			model.put("startdate", startDate);
			model.put("enddate", endDate);
		}
		if (parameterMap.size() == 0) {
			model.put("msg", "At least one field must be selected");

		}

		return new ModelAndView(getViewName(), model);

	}
}
