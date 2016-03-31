package org.openmrs.module.laboratorymodule.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;

public class GlobalPropertiesMgt {

	public static Collection<Concept> getLabGlobalPropert() {
		List<Concept> labConcepts = new ArrayList<Concept>();
		GlobalProperty gp = Context
				.getAdministrationService()
				.getGlobalPropertyObject("laboratorymodule.labtests.conceptIds");
		String[] conceptIds = gp.getPropertyValue().split(",");
		for (String s : conceptIds) {
			labConcepts.add(Context.getConceptService().getConcept(
					Integer.valueOf(s)));

		}
		return labConcepts;
	}
	
	/**
	 * gets the concepts that has multiple answers
	 * @return Map<Concept,Bolean>
	 */
	public static Map<Concept, Boolean> getConceptHasMultipleAnswers() { Map<Concept, Boolean> labConcepts = new HashMap<Concept, Boolean>();
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject("laboratorymodule.multipleAnswerConceptIds");
		String[] conceptIds = gp.getPropertyValue().split(",");
		for (String s : conceptIds) {
			labConcepts.put(Context.getConceptService().getConcept(Integer.valueOf(s)), true);
		}
		return labConcepts;
	}

	public  static String getLabOrderEncounterTypeFromGlobalProperties() {
		return Context.getAdministrationService().getGlobalProperty(
				"laboratorymodule.encounterType.labOrderEncounterTypeId");

	}
	public static  String getLabOrderTypeId() {
		return Context.getAdministrationService().getGlobalProperty(
				"laboratorymodule.orderType.labOrderTypeId");

	}
	/**
	 * Gets the a list of categorized grouup of lab exams
	 * 
	 * @return List<Concept>
	 */
	public static  List<Concept> getLabExamCategories() {
		List<Concept>conceptCategories=new ArrayList<Concept>();
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject("laboratorymodule.labExamCategory");
				String[] conceptIds = gp.getPropertyValue().split(",");
		for (String conceptIdstr: conceptIds) {
			Concept cpt=Context.getConceptService().getConcept(Integer.valueOf(conceptIdstr)) ;
		conceptCategories.add(cpt);	
			
		}
		return conceptCategories;
	}

}
