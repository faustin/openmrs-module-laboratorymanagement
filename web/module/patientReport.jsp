<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<div>
<div id="searchform "
	style="background: #E2E4FF; border: 1px #808080 solid; padding: 0.5em; margin-bottom: 0em">
<form action=" " method="post">
<table>
	<tr>
		<td>Patient:</td>
		<td><openmrs:fieldGen type="org.openmrs.Patient"
			formFieldName="patientId" val="${patient}" /></td>
	</tr>
	<tr>
		<td><spring:message code="laboratorymodule.from" /></td>
		<td><openmrs_tag:dateField formFieldName="startDate"
			startValue="${startdate}" /></td>

		<td><spring:message code="laboratorymodule.to" /></td>
		<td><openmrs_tag:dateField formFieldName="endDate"
			startValue="${enddate}" /></td>
	</tr>
	<tr>
		<td><input type="submit" name="submitButton "
			value="<spring:message code="laboratorymodule.search"/>"></td>
	</tr>
</table>
</form>
</div>
<br>
<br>
<br>
<c:if test="${fn:length(mappedLabExams)>0}">
	<b>LABORATORY TEST RESULTS</b>
	<div style="float: right;">
	<form action="printLabResult.form" method="get"><input
		type="hidden" name="patientId" value="${patient.patientId}"> <input
		type="hidden" name="startDate" value="${startDate}"> <input
		type="hidden" name="endDate" value="${endDate}"> <input
		type="submit" value="Pdf Lab results"></form>
	</div>

	<br>

	<div id="Identification"
		style="border: 2px #000000 double; width: 100 %;">
	<div>
	<table>
		<tr>
			<td><spring:message code="laboratorymodule.givenName" /> :</td>
			<td>${patient.givenName}</td>
		</tr>
		<tr>
			<td>patient name:</td>
			<td>${patient.familyName}</td>
		</tr>
		<tr>
			<td>Gender:</td>
			<td><img
				src="${pageContext.request.contextPath}/images/${patient.gender == 'M' ? 'male' : 'female'}.gif" /></td>

		</tr>
		<tr>
			<td>Patient Id:</td>
			<td>${patient.patientId}</td>

		</tr>
	</table>
	</div>


	</div>
	<br>
	<br>
	<div id="labResults" style="border: 2px #000000 double; width: 100 %;">

	<table width="100%" border="0">
		<td><b>Test name</b></td>
		<td><b>Result</b></td>
		<td><b>Normal range</b></td>
		<td>Orderer</td>
		<c:forEach var="labExamMap" items="${mappedLabExams}"
			varStatus="status">
			<tr>
				<td><b>${labExamMap.key}</b></td>
				<td></td>
				<td></td>
				<td></td>

			</tr>
			<c:forEach var="labTest" items="${labExamMap.value}"
				varStatus="status">
				<tr>
					<c:choose>
						<c:when test="${labTest[0].valueCoded != null}">
							<td><i><c:out value="${labTest[0].concept.name}" /></i></td>
							<td><i><c:out value="${labTest[0].valueCoded.name}" /></i></td>
							<td>-</td>
							<td><i><c:out value="${labTest[0].order.orderer.familyName}" /></i></td>
						</c:when>
						<c:when test="${labTest[0].valueNumeric != null}">
							<td><i><c:out value="${labTest[0].concept.name}" /></i></td>
							<td><i><c:out value="${labTest[0].valueNumeric}" /></i></td>
							<td><i><c:out value="${labTest[1]}" /></i></td>

							<td><i><c:out value="${labTest[0].order.orderer.familyName}" /></i></td>
						</c:when>
						<c:when test="${labTest[0].valueText != null}">
							<td><i><c:out value="${labTest[0].concept.name}" /></i></td>
							<td><i><c:out value="${labTest[0].valueText}" /></i></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</c:when>
						<c:when test="${labTest[0].valueDatetime != null}">
							<td><i><c:out value="${labTest[0].concept.name}" /></i></td>
							<td><i><openmrs:formatDate date="${labTest[0].valueDatetime}" /></i></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</c:when>
					</c:choose>

				</tr>
			</c:forEach>
		</c:forEach>
	</table>
	</div>
</c:if></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>