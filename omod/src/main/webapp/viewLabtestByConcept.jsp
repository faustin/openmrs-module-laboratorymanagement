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

<script type="text/javascript" charset="utf-8">
	var $t = jQuery.noConflict();
	$t(document).ready( function() {
		$t('#example').dataTable( {
			"sPaginationType" :"full_numbers"
		});
	});
</script>
<br>
<br>
<div><c:if test="${fn:length(listOflabtest)>0}">
	<div>
	<div><b>${testTitle}</b>
	<div style="float: right;">
	<form action="exportToPDF.form" method="get">
	<input		type="hidden" name="testType" value="${conceptId}">
	<input		type="hidden" name="startDate" value="${startDate}">
	<input		type="hidden" name="endDate" value="${endDate}">
	 <input		type="submit" value="Export to pdf">
	 </form>
	</div>
	</div>
	<div id="dt_example">
	<div id="container">
	<table cellpadding="0" cellspacing="0" border="0" class="display"
		id="example">
		<thead>
			<tr>
				<th class="obsConceptName"><spring:message
					code="laboratorymodule.number" /></th>
				<th><spring:message code="laboratorymodule.givenName" /></th>
				<th><spring:message code="laboratorymodule.familyName" /></th>
				<th><spring:message code="laboratorymodule.testName" /></th>
				<th><spring:message code="laboratorymodule.testedOn" /></th>
				<th><spring:message code="laboratorymodule.location" /></th>
				<th><spring:message code="laboratorymodule.testResults" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="labtest" items="${listOflabtest}" varStatus="num">
				<tr>
					<c:choose>
						<c:when test="${labtest[1].valueNumeric != null}">
							<td>${num.count}</td>
							<td><c:out value="${labtest[0].givenName}" /></td>
							<td><c:out value="${labtest[0].familyName}" /></td>
							<td><c:out value="${labtest[1].concept.name}" /></td>
							<td><openmrs:formatDate date="${labtest[1].obsDatetime}" /></td>
							<td><c:out value="${labtest[1].location}" /></td>
							<td><c:out value="${labtest[2]}" /></td>
						</c:when>
						<c:when test="${labtest[1].valueCoded != null}">
							<td>${num.count}</td>
							<td><c:out value="${labtest[0].givenName}" /></td>
							<td><c:out value="${labtest[0].familyName}" /></td>
							<td><c:out value="${labtest[1].concept.name}" /></td>
							<td><openmrs:formatDate date="${labtest[1].obsDatetime}" /></td>
							<td><c:out value="${labtest[1].location}" /></td>
							<td><c:out value="${labtest[2].name}" /></td>
						</c:when>
						<c:when test="${labtest[1].valueText != null}">
							<td>${num.count}</td>
							<td><c:out value="${labtest[0].givenName}" /></td>
							<td><c:out value="${labtest[0].familyName}" /></td>
							<td><c:out value="${labtest[1].concept.name}" /></td>
							<td><openmrs:formatDate date="${labtest[1].obsDatetime}" /></td>
							<td><c:out value="${labtest[1].location}" /></td>
							<td><c:out value="${labtest[1].valueText}" /></td>
						</c:when>
						<c:when test="${labtest[1].valueDatetime != null}">
							<td>${num.count}</td>
							<td><c:out value="${labtest[0].givenName}" /></td>
							<td><c:out value="${labtest[0].familyName}" /></td>
							<td><c:out value="${labtest[1].concept.name}" /></td>
							<td><openmrs:formatDate date="${labtest[1].obsDatetime}" /></td>
							<td><c:out value="${labtest[1].location}" /></td>
							<td><openmrs:formatDate date="${labtest[1].valueDatetime}" /></td>
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






