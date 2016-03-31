package org.openmrs.module.laboratorymodule.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.Obs;

import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;

import org.openmrs.module.laboratorymodule.advice.LabTestConstants;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;

import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class ViewStatisticsController extends ParameterizableViewController {
	protected final Log log = LogFactory.getLog(getClass());

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map parameterMap = request.getParameterMap();
		String check[] = request.getParameterValues("id");

		// mapped test with number of tests ny year
		Map<Concept, Map<Integer, Integer>> testWithNumberOfTestByYear = new TreeMap<Concept, Map<Integer, Integer>>();
		String conceptIdStr = "";
		SimpleDateFormat df = OpenmrsUtil.getDateFormat();
		Map<String, Object> model1 = new HashMap<String, Object>();
		List<Obs> allTestWithResult = null;
		List<Obs> testTakenbetweenTwoDates = null;
		Map<Concept, Integer> labTestMap = new HashMap<Concept, Integer>();
		// get the convset
		Concept concept = Context.getConceptService().getConcept(
				LabTestConstants.LABORATORY_EXAMINATIONS_CONSTRUCT);
		// each object is an array to store test,result according to the
		// selected labtests
		List<Object[]> listOflabtest = new ArrayList<Object[]>();
		
		// get all the members of this convset
		Collection<ConceptSet> setMembers = concept.getConceptSets();
		Collection<Concept> concepts = LaboratoryMgt
				.getAllRequiredLabConcepts(setMembers);
		// get parameters
		String startD = request.getParameter("startDate");
		String endD = request.getParameter("endDate");
		String patientIdStr = request.getParameter("patientId");
		String locationIdStr = request.getParameter("locationId");
		String[] selected = request.getParameterValues("id");

		// Store parameters ussing session in array that are needed in chart
		// Object chartParameters[]=null;

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
		if (selected == null) {

			if (startDate != null && endDate != null
					|| (locationIdStr != null && locationIdStr.length() != 0)
					|| (patientIdStr != null && patientIdStr.length() != 0)) {

				conceptIdStr = request.getParameter("conceptId");
				int conceptId = Integer.parseInt(conceptIdStr);
				// store chart parameters into session.
				Object chartParameters[] = new Object[] { startDate, endDate,
						conceptIdStr, locationIdStr, patientIdStr };
				request.getSession().setAttribute("chartParameter",
						chartParameters);
				Concept cpt = Context.getConceptService().getConcept(conceptId);

				allTestWithResult = LaboratoryMgt.getAllLabTestByCriteria(
						patientIdStr, locationIdStr, conceptId, startDate,
						endDate);

				testTakenbetweenTwoDates = LaboratoryMgt
						.getAllTestWithSpecificConcept(allTestWithResult,conceptId);
				listOflabtest=LaboratoryMgt.getHistoryOfLabTests(testTakenbetweenTwoDates,request);
				request.getSession().setAttribute("labTakentestconcept", listOflabtest);
				labTestMap.put(cpt, listOflabtest.size());
				if (startDate != null && endDate != null) {
					Map<Integer, Integer> labTestByYear = LaboratoryMgt.getAlllabTestSizeByyear(patientIdStr,locationIdStr, conceptId, startDate,
									endDate);

					// This is a map where key is conceppt and value is map<K,V>
					// ie
					// k=Number of year and V=Number of test
					testWithNumberOfTestByYear.put(Context.getConceptService()
							.getConcept(conceptId), labTestByYear);
				

				}

				// // String[] selected = request.getParameterValues("id");
				//
				// if (selected != null && selected.length != 0) {
				//
				// }

			

				model1.put("testWithItsMappedByYear",
						testWithNumberOfTestByYear);

				model1.put("labTestMap", labTestMap);
				model1.put("startdate", startDate);
				model1.put("enddate", endDate);
				model1.put("locationIdstr", locationIdStr);
				model1.put("patientIdstr", patientIdStr);
				model1.put("conceptId", Integer.parseInt(conceptIdStr));

			}
		}
		if (selected != null && selected.length != 0) {
			
			LaboratoryService laboratoryService = Context.getService(LaboratoryService.class);		
			
			// int i = 5497;
			Collection<Integer> labconceptIds =LaboratoryMgt.getLabGlobalProperties();
			ConceptService cptService = Context.getConceptService();
			// Map the concepts with its size
			Map<Concept, Integer> mappedLabConcepts = new HashMap<Concept, Integer>();
			// For every labConceptId there is a mapped number of exams
			int i=0;
			for (Integer labConceptId : labconceptIds) {		
				
				mappedLabConcepts.put(cptService.getConcept(labConceptId),LaboratoryMgt. getHistoryOfLabTests(LaboratoryMgt.getAllTestWithResult(laboratoryService.getAllLabTest(labConceptId)), request).size());						
				request.getSession().setAttribute("labTest_"+i,LaboratoryMgt.getHistoryOfLabTests(LaboratoryMgt.getAllTestWithResult(laboratoryService.getAllLabTest(labConceptId)), request));	
				//System.out.println("the history of lab test>>>>>>>>>>"+LaboratoryMgt.getHistoryOfLabTests(LaboratoryMgt.getAllTestWithResult(laboratoryService.getAllLabTest(labConceptId)), request));			
				
				i++;
			
			}
			
			
			

			//Map<Concept, Integer> mappedLabConcepts = LaboratoryMgt	.getMappedLabConcepts(request);
			model1.put("mappedLabExams", mappedLabConcepts);
			log.info(">>>>>>>mapped lab" + mappedLabConcepts);
		} else

			model1.put("msg", "At least two fields must be selected");

		model1.put("labConcepts", concepts);

		return new ModelAndView(getViewName(), model1);

	}

}
