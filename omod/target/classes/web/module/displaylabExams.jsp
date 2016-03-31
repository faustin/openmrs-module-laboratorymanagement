<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude file="/moduleResources/laboratorymodule/jquery.js" />
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/style.css" />
<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/jquery.dataTables.js" />

<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/demo_page.css" />

<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/demo_table.css" />

<b><spring:message code="laboratorymodule.searchBy" /></b>
<script language="javascript" type="text/javascript">
	var $k = jQuery.noConflict();
</script>

<script type="text/javascript" charset="utf-8">
	var $t = jQuery.noConflict();
	$t(document).ready( function() {
		$t('#example').dataTable( {
			"sPaginationType" :"full_numbers"
		});
	});
</script>

<div><br />
<c:if test="${fn:length(positiveLabExams)>0}">
	<div align="center"><b>Exams tested from ${startDate} to
	${endDate} </b></div>
	<div>
	<div id="dt_example">
	<div id="container">
	<table cellpadding="0" cellspacing="0" border="0" class="display"
		id="example">
		<thead>
			<tr id="obsListingHeaderRow">
				<th class="obsConceptName"><spring:message
					code="laboratorymodule.number" /></th>
				<th><spring:message code="laboratorymodule.patientName" /></th>
				<th><spring:message code="laboratorymodule.testName" /></th>
				<th><spring:message code="laboratorymodule.observedOn" /></th>
				<th><spring:message code="laboratorymodule.Results" /></th>


			</tr>
		</thead>
		<tbody>
			<c:forEach var="positiveLabExam" items="${positiveLabExams}"
				varStatus="num">

				<tr>
					<td>${num.count}</td>

					<td><c:out value="${positiveLabExam.person.familyName}" /></td>
					<td><c:out value="${positiveLabExam.concept.name}" /></td>
					<td><openmrs:formatDate date="${positiveLabExam.obsDatetime}" /></td>
					<td><c:out value="${positiveLabExam.valueCoded.name}" /></td>

				</tr>

			</c:forEach>
		</tbody>


	</table>
	</div>
	</div>
	</div>
</c:if> <c:if test="${fn:length(negativeLabExams)>0}">
	<div align="center"><b>Exams tested from ${startDate} to
	${endDate} </b></div>
	<div>
	<div id="dt_example">
	<div id="container">
	<table cellpadding="0" cellspacing="0" border="0" class="display"
		id="example">
		<thead>
			<tr id="obsListingHeaderRow">
				<th class="obsConceptName"><spring:message
					code="laboratorymodule.number" /></th>
				<th><spring:message code="laboratorymodule.patientName" /></th>
				<th><spring:message code="laboratorymodule.testName" /></th>
				<th><spring:message code="laboratorymodule.observedOn" /></th>
				<th><spring:message code="laboratorymodule.Results" /></th>


			</tr>
		</thead>
		<tbody>
			<c:forEach var="negativeLabExam" items="${negativeLabExams}"
				varStatus="num">

				<tr>
					<td>${num.count}</td>

					<td><c:out value="${negativeLabExam.person.familyName}" /></td>
					<td><c:out value="${negativeLabExam.concept.name}" /></td>
					<td><openmrs:formatDate date="${negativeLabExam.obsDatetime}" /></td>
					<td><c:out value="${negativeLabExam.valueCoded.name}" /></td>

				</tr>

			</c:forEach>
		</tbody>


	</table>
	</div>
	</div>
	</div>
</c:if> <c:if test="${fn:length(labExamsByName)>0}">
	<div><b>Exams tested from ${startDate} to ${endDate} </b></div>

	<div>
	<div id="dt_example">
	<div id="container">
	<table cellpadding="0" cellspacing="0" border="0" class="display"
		id="example">
		<thead>
			<tr id="obsListingHeaderRow">
				<th class="obsConceptName"><spring:message
					code="laboratorymodule.number" /></th>
				<th><spring:message code="laboratorymodule.patientName" /></th>
				<th><spring:message code="laboratorymodule.testName" /></th>
				<th><spring:message code="laboratorymodule.observedOn" /></th>
				<th><spring:message code="laboratorymodule.Results" /></th>


			</tr>
		</thead>
		<tbody>
			<c:forEach var="labExamByName" items="${labExamsByName}"
				varStatus="num">

				<tr>
					<c:choose>
						<c:when test="${labExamByName.valueCoded != null}">
							<td>${num.count}</td>

							<td><c:out value="${labExamByName.person.familyName}" /></td>
							<td><c:out value="${labExamByName.concept.name}" /></td>
							<td><openmrs:formatDate date="${labExamByName.obsDatetime}" /></td>
							<td><c:out value="${labExamByName.valueCoded.name}" /></td>
						</c:when>

						<c:when test="${labExamByName.valueNumeric != null}">
							<td>${num.count}</td>

							<td><c:out value="${labExamByName.person.familyName}" /></td>
							<td><c:out value="${labExamByName.concept.name}" /></td>
							<td><openmrs:formatDate date="${labExamByName.obsDatetime}" /></td>
							<td><c:out value="${labExamByName.valueNumeric}" /></td>



						</c:when>
					</c:choose>
				</tr>


			</c:forEach>
		</tbody>


	</table>
	</div>
	</div>
	</div>
</c:if></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>