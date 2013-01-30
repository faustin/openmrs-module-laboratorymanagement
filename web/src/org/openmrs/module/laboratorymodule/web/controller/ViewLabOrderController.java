package org.openmrs.module.laboratorymodule.web.controller;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;
import org.openmrs.module.laboratorymodule.utils.LabUtils;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ViewLabOrderController extends ParameterizableViewController {
	protected final Log log = LogFactory.getLog(getClass());

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		Date lastMidnight = LaboratoryMgt.getPreviousMidnight(null);
		Date zeroDay = LaboratoryMgt.addDaysToDate(lastMidnight, 0);
		
		String patientIdStr = request.getParameter("patientId");
		Date startDate = LaboratoryMgt.getDateParameter(request, "startDate",zeroDay);
		Date endDate = LaboratoryMgt.getDateParameter(request, "endDate",zeroDay);
		// String locationIdStr=request.getParameter("locationId");
		LaboratoryService laboratoryService = Context.getService(LaboratoryService.class);
		Map<Concept, Collection<Order>> mappedLabOrders = null;

		Map<String, Object> model = new HashMap<String, Object>();
		if (patientIdStr != null && patientIdStr.length() > 0) {
			int patientId = Integer.parseInt(request.getParameter("patientId"));
			if (request.getParameter("save") != null) {
				String labCodeStr = request.getParameter("labCode");
				//if the labCode  does not exist ,add lab code to Lab  oders  
				if (!laboratoryService.isFoundLabCode(labCodeStr)) {
					String labCode = request.getParameter("labCode");
					LabUtils.addLabCodeToOrders(patientId, labCode, startDate,endDate);
					model.put("msg", "Lab code is successfully added");
					
					/**
					 *  ___________ >> Lab Appointment ends here:
					 *  Found using: Patient, Lab Service, Appointment Date ,and AppointmentState
					 */
					Services clinicalService = AppointmentUtil.getServiceByConcept(Context
							.getConceptService().getConcept(6710));
					List<Appointment> appointments = AppointmentUtil.getAppointmentsByPatientAndDate(
							Context.getPatientService().getPatient(patientId), clinicalService, new Date());
					
					for(Appointment app: appointments){
						if(app.getAppointmentState().getDescription().equalsIgnoreCase("waiting"))
							AppointmentUtil.saveAttendedAppointment(app);
					}
					/**
					 * _______________________END of APPOINTMENT Stuff_______________________
					 */
					
				} else
					model.put("msg", "The Lab code " + labCodeStr+ "  already exists");
				model.put("labCode", labCodeStr);
			}
			
			mappedLabOrders = LabUtils.findPatientLabOrders(patientId,	startDate, endDate, null);		
			model.put("patient", Context.getPatientService().getPatient(
					patientId));
		}

		model.put("mappedLabOrders", mappedLabOrders);
		model.put("startDate", startDate);
		model.put("enddate", endDate);
		return new ModelAndView(getViewName(), model);
	}
}
