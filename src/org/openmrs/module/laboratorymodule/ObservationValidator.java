package org.openmrs.module.laboratorymodule;



import org.openmrs.Obs;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ObservationValidator implements Validator{

	public boolean supports(Class c) {
		// TODO Auto-generated method stub
		return Obs.class.isAssignableFrom(c);
	}

	public void validate(Object obj, Errors error) {
		// TODO Auto-generated method stub
		Obs patientObs = (Obs) obj;
		if (patientObs.getPerson()==null) {
			error.rejectValue("patient", "laboratorymodule.selectPatient");
			
		}
		
	}

}
