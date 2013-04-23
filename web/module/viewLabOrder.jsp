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
<div
	style="background: #E2E4FF; border: 1px #808080 solid; padding: 0.5em; margin-bottom: 0em">
<form action="viewLabOrder.form" method="post">
<table>
	<tr>
		<td>Patient:</td>
		<td><openmrs:fieldGen type="org.openmrs.Patient"
			formFieldName="patientId" val="${patient}" /></td>
	</tr>
	<tr>
		<td><spring:message code="laboratorymodule.from" /></td>
		<td><openmrs_tag:dateField formFieldName="startDate"
			startValue="${startDate}" /></td>
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
<br />
<c:if test="${fn:length(mappedLabOrders)>0}">
	<div align="center"><b>Laboratory orders ordered on <openmrs:formatDate
		date="${startdate}" /></b>
	<div align="right">

	<div align="right"><a
		href="${pageContext.request.contextPath}/module/laboratorymodule/labCode.form?patientId=${patient.patientId}&startDate=${startDat}">Add
	LabCode</a></div>
	</div>

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
	<table cellpadding="0" cellspacing="0" border="0" class="display">
		<thead>
			<tr id="obsListingHeaderRow">
				<th><spring:message code="laboratorymodule.testName" /></th>
				<th><spring:message code="laboratorymodule.OrderedOn" /></th>
				<th>Oderer</th>
				<th><spring:message code="laboratorymodule.labCode" /></th>
				<th><spring:message code="laboratorymodule.editOrder" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="labOrderMap" items="${mappedLabOrders}"
				varStatus="num">
				<tr>

					<td><b>${labOrderMap.key.name}</b></td>
					<td></td>
					<td></td>
				</tr>
				<c:forEach var="labOrder" items="${labOrderMap.value}"
					varStatus="status">
					<tr>
						<td><c:out value="${labOrder.concept.name}" /></td>
						<td><openmrs:formatDate date="${labOrder.startDate}" /></td>
						<td><c:out value="${labOrder.orderer.familyName}" /></td>						
						<td><c:out value="${labOrder.accessionNumber}" /></td>
						<td valign="top"><a href="editLabOrder.form?orderId=${labOrder.orderId}&&startDate=${startDat}&&labcode=${labOrder.accessionNumber}&&patientId=${labOrder.patient.patientId}"><spring:message code="laboratorymodule.edit" />
						</a>
					</td>
												
						
					</tr>
				</c:forEach>
			</c:forEach>
		</tbody>
	</table>
	</div>
	</div>
	</div>
</c:if> <c:if test="${fn:length(theLabOrders)>0}">

<table>
		<tr>
			<td>Number</td>
			<td>PatientId</td>
			<td>Ordered Tests</td>
			<td>Ordered On</td>
			<td>Orderer</td>
		</tr>
		<c:forEach var="labOrder" items="${theLabOrders}" varStatus="num">
			<tr>
				<td>${num.count}</td>
				<td><c:out value="${labOrder.patient.patientId}" /></td>
				<td><c:out value="${labOrder.concept.name}" /></td>
				<td><openmrs:formatDate date="${labOrder.startDate}" /></td>
				<td><c:out value="${labOrder.orderer.familyName}" /></td>

			</tr>
		</c:forEach>
	</table>
</c:if></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>