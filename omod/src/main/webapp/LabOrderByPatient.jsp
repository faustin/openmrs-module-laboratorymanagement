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

<div>
<br />
<c:if test="${fn:length(labOrders)>0}">
	<div align="center"><b >Laboratory orders ordered on  xx/xx/yyyy</b></div>

	<div id="Identification"
		style="border: 2px #000000 double; width: 100 %;">
		<table style="">
			<tr>
				<td><spring:message code="laboratorymodule.tracNetId" />:</td>
				<td>${patient.patientIdentifier}</td>
			</tr>
			<tr>
				<td><spring:message code="laboratorymodule.givenName" /> :</td>
				<td>${patient.givenName}</td>
			</tr>
			<tr>
				<td><spring:message code="laboratorymodule.familyName" />:</td>
				<td>${patient.familyName}</td>
			</tr>
			<tr>
				<td><spring:message code="laboratorymodule.gender" />:</td>
				<td><img
					src="${pageContext.request.contextPath}/images/${patient.gender == 'M' ? 'male' : 'female'}.gif" /></td>
			</tr>
		</table>
		
		</div>

	<div>	 
	<div id="dt_example">
	<div id="container">
	
	<table cellpadding="0" cellspacing="0" border="0" class="display"
		id="example">
		<thead>
			<tr id="obsListingHeaderRow">
				<th class="obsConceptName"><spring:message
					code="laboratorymodule.number" /></th>

				<th><spring:message code="laboratorymodule.orderId" /></th>
				<th><spring:message code="laboratorymodule.testName" /></th>
				<th><spring:message code="laboratorymodule.OrderedOn" /></th>
				<th><spring:message code="laboratorymodule.status" /></th>
				<th><spring:message code="laboratorymodule.edit" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="labOrder" items="${labOrders}" varStatus="num">
				<tr>
					<td>${num.count}</td>

					<td><c:out value="${labOrder.orderId}" /></td>
					<td><c:out value="${labOrder.concept.name}" /></td>
					<td><openmrs:formatDate date="${labOrder.startDate}" /></td>

					<td><a
						href="${pageContext.request.contextPath}/module/laboratorymodule/addResultToExam.form?orderId=${labOrder.orderId}&encounterId=${encounterId}">${labOrder.autoExpireDate
					== null ? 'Incomplete' : 'Complete'}</a></td>
					<td><a
						href="${pageContext.request.contextPath}/admin/observations/obs.form?obsId=${Observation.obsId}">
					<img src="${pageContext.request.contextPath}/images/edit.gif"
						title="Edit" border="0"></a></td>

				</tr>
			</c:forEach>
		</tbody>


	</table>
	</div>
	</div>
	</div>
</c:if> <c:if test="${fn:length(labOrders)==0}">
There are no waiting exams for the patient:<b>${patient.familyName} </b> 

</c:if></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>