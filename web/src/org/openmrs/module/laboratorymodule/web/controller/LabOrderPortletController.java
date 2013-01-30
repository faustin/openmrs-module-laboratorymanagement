package org.openmrs.module.laboratorymodule.web.controller;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.LabOrderParent;
import org.openmrs.module.laboratorymodule.OrderObs;
import org.openmrs.module.laboratorymodule.advice.LabTestConstants;
import org.openmrs.module.laboratorymodule.utils.GlobalPropertiesMgt;
import org.openmrs.module.laboratorymodule.utils.LabUtils;
import org.openmrs.module.mohappointment.model.Appointment;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.openmrs.web.controller.PortletController;

public class LabOrderPortletController extends PortletController {

	@SuppressWarnings("unchecked")
	protected void populateModel(HttpServletRequest request,
			Map<String, Object> model) {
		//categories of lab tests on lab request form
		int hematologyId =LabTestConstants.hematologyId;
		int parasitologyId =LabTestConstants.PARASITOLOGYID;
		int hemostasisId = LabTestConstants.hemostasisId;
		int bacteriologyId = LabTestConstants.bacteriologyId;
		int urinaryChemistryId= LabTestConstants.urineChemistryId ;
		int immunoSerologyId = LabTestConstants.immunoSerologyId ;
		int bloodChemistryId = LabTestConstants.bloodChemistryId ;
		int toxicologyId = LabTestConstants.toxicologyId ;
		

		List<Concept> conceptCategories = GlobalPropertiesMgt.getLabExamCategories();
		List<LabOrderParent> lopList = LabUtils.getsLabOrdersByCategories(conceptCategories);

		// get all selected Lab tests and save them as Order
		Map<String, String[]> parameterMap = request.getParameterMap();

		String patientIdstr = request.getParameter("patientId");
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdstr));

		/**
		 * <<<<<<< Appointment Consultation waiting list management >>>>>>>
		 */
		Appointment appointment = null;
		// This is from the Provider's Appointment dashboard
		if (request.getParameter("appointmentId") != null) {
			appointment = AppointmentUtil.getWaitingAppointmentById(Integer.parseInt(request
							.getParameter("appointmentId")));
			LabUtils.setConsultationAppointmentAsAttended(appointment);
		}
		
		if(request.getParameter("orderId")!=null){			
			
			LabUtils.cancelLabOrder(request.getParameter("orderId"));
		}
		
		/**
		 * <<<<<<<<< APPOINTMENTS STUFF ENDS HERE >>>>>>>>
		 */

		if(request.getParameter("saveLabOrders")!=null){
			// Saving selected lab orders:	
			
			LabUtils.saveSelectedLabOrders(parameterMap, patient);
			model.put("msg", "The Lab order is successfully created");
					
				
			// Creating a waiting appointment in Laboratory when lab order is made:	
			LabUtils.createWaitingLabAppointment(patient, null);
		}

		
		// request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR,
		// "Orders created");

		Location dftLoc = Context.getLocationService().getDefaultLocation();

		User user = Context.getAuthenticatedUser();
		String providerName = user.getFamilyName() + " " + user.getGivenName();
		String patientName = patient.getFamilyName() + " "
				+ patient.getMiddleName() + " " + patient.getGivenName();

		// String orderTypeIdStr =
		// Context.getAdministrationService().getGlobalProperty("orderType.labOrderTypeId");
		Map<ConceptName, Collection<Concept>> mappedLabOrder = new HashMap<ConceptName, Collection<Concept>>();
		mappedLabOrder = LabUtils.getLabExamsToOrder(Integer
				.parseInt(patientIdstr));
		OrderService orderService = Context.getOrderService();
		// get observations by Person

		List<Order> orders = orderService.getOrdersByPatient(patient);
		Map<Date, List<OrderObs>> orderObsMap = LabUtils.getMappedOrderToObs(orders, patient);
		//Map<String, Object> orderObsMapOrdered = new TreeMap<String, Object>((Comparator<? super String>) orderObsMap);
		
		//int spermConceptId = LabTestConstants.SPERMCONCEPTID;

		model.put("dftLoc", dftLoc);
		// model.put("locations", locations);
		model.put("patientId", patientIdstr);
		model.put("mappedLabOrder", mappedLabOrder);
		model.put("obsMap", orderObsMap);
		model.put("providerName", providerName);
		model.put("patienName", patientName);
       // model.put("spermogram", Context.getConceptService().getConcept(spermConceptId).getName().getName());
		model.put("labOrderparList", lopList);
		model.put("hematology",Context.getConceptService().getConcept(hematologyId).getName().getName() );
		model.put("parasitology",Context.getConceptService().getConcept(parasitologyId).getName());
		model.put("hemostasis",Context.getConceptService().getConcept(hemostasisId).getName());
		model.put("bacteriology",Context.getConceptService().getConcept(bacteriologyId).getName());
		model.put("urinaryChemistry", Context.getConceptService().getConcept(urinaryChemistryId).getName());
		model.put("immunoSerology", Context.getConceptService().getConcept(immunoSerologyId).getName());
		model.put("bloodChemistry", Context.getConceptService().getConcept(bloodChemistryId).getName());
		model.put("toxicology", Context.getConceptService().getConcept(toxicologyId).getName());	
		
		
		super.populateModel(request, model);

	}

}
