package org.openmrs.module.laboratorymodule.extension.html;

import org.openmrs.module.web.extension.PatientDashboardTabExt;

public class LabOrderDashboardTab extends PatientDashboardTabExt {

	@Override
	public String getPortletUrl() {
		// TODO Auto-generated method stub
		return "labOrderPortlet";
	}

	@Override
	public String getRequiredPrivilege() {
		// TODO Auto-generated method stub
		return "Patient Dashboard - View Laboratory Order";
	}

	@Override
	public String getTabId() {
		// TODO Auto-generated method stub
		return "LaboratoryTabId";
	}

	@Override
	public String getTabName() {
		// TODO Auto-generated method stub
		return "Laboratory Order";
	}

}
