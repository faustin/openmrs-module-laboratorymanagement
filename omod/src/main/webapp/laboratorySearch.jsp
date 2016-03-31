<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/style.css" />
<b class="boxHeader">Search by</b>
<div class="box">
<table>
<tr>
		<td>
		<form name="searchbyPatient" action="laboratorySearch.form" method="post">
		</td>
		<td>Patient:</td>
		<td><openmrs:fieldGen type="org.openmrs.Patient"
			formFieldName="patientId" val="${patient}" /></td>
		<td><input type="submit" name="submitButton "
			value=" Search by person"></td>
		</form>
	</tr>

   

	<tr>
		<td>
		<form name="searchbydate" action="laboratorySearch.form" method="post">
		</td>
		<td>Date:</td>
		<td><openmrs_tag:dateField formFieldName="laboratoryDate"
			startValue="${obsDate}" /></td>
		<td><input type="submit" name="submitButton "
			value=" Search by date"></td>
		</form>
	</tr>
	
	
	<tr>
		<td>
		<form name="searchbyprovider" action="laboratorySearch.form"
			method="post">
		</td>
		<td>Provider:</td>
		<td><select name="providerId">
			<c:forEach var="user" items="${users}">
				<option value="${user.userId}">${user.lastName}</option>
			</c:forEach>
		</select></td>
		<td><input type="submit" name="submitButton "
			value=" Search by provider"></td>

		</form>
		</td>
	</tr>
	<tr>

		<form name="searchbylocation" action="laboratorySearch.form"
			method="post">
		<td>Location:</td>

		<td><openmrs:fieldGen type="org.openmrs.Location"
			formFieldName="locationId" val="${location}" /></td>
		<td><input type="submit" name="submitButton "
			value="Search by location"></td>

		</form>

	</tr>
</table>
</div>
<br />
<b class="boxHeader">All encounters</b>
<div class="box">
<table cellspacing="0" cellpadding="0" class="patientEncounters">
	<tr>

		<th>Patient Name</th>
		<th class="encounterDatetimeHeader">Encounter Date</th>
		<th class="encounterProviderHeader">Provider</th>
		<th class="encounterLocationHeader">Location</th>
		<th class="encounterEntererHeader">Enterer</th>

	</tr>
	<c:forEach var="encounter" items="${encounters}" varStatus="status">
		<tr>
			<td><c:out value="${encounter.patient.familyName}" /></td>
			<td><c:out value="${encounter.encounterDatetime}" /></td>
			<td><c:out value="${encounter.provider}" /></td>
			<td><c:out value="${encounter.location}" /></td>
			<td><c:out value="${encounter.provider}" /></td>

		</tr>
	</c:forEach>
</table>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>