<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/jquery.js" />
<script language="javascript" type="text/javascript">
	var $k = jQuery.noConflict();
</script>
<div
	style="background: #E2E4FF; border: 1px #808080 solid; padding: 0.5em; margin-bottom: 0em">
<form id="frm" action="testStatistics.form" method="post">
<table>
	<tr>
		<td>
		<div style="float: right;">
		<table>
			<tr>
				<td>Patient:</td>
				<td><openmrs:fieldGen type="org.openmrs.Patient"
					formFieldName="patientId" val="${patient}" /></td>
			</tr>
			<tr>
				<td>Location</td>
				<td><openmrs_tag:locationField formFieldName="locationId"
					initialValue="${param.locationId}" /></td>
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
				<td><input type="submit"
					value="<spring:message code="laboratorymodule.update"/>" /></td>
			</tr>
		</table>
		</div>
		</td>
		<td>
		<div style="float: left;">
		<table>
			<tr>
				<td>
				<div id="examStstcs">Display all statistics:<input
					type="checkbox" name="id" values="yes"></div>
				</td>
			</tr>
			<tr>
				<td><spring:message code="laboratorymodule.test" /></td>
				<td><select name="conceptId">
					<c:forEach items="${labConcepts}" var="labConcept">

		<option value="${labConcept.conceptId}"	<c:if test="${labConcept.conceptId==conceptId}">selected='selected'</c:if>>${labConcept.name}</option>

					</c:forEach>
				</select></td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
</table>
</form>
</div>
<br />
<br />
<br />
<div>
<div><c:if test="${fn:length(labTestMap)>0}">
	<b class="boxHeader">Number of Lab tests</b>
	<div>
	<div>
	<table width="100%">

		<c:forEach var="mappedLab" items="${labTestMap}" varStatus="status">
			<tr>
				<td width="70%">${mappedLab.key.name}</td>
				<td><a
					href="${pageContext.request.contextPath}/module/laboratorymodule/testByConcept.form?testType=${mappedLab.key.conceptId}&patientId=${patientIdstr}&startDate=${startdate}&endDate=${enddate}&locationId=${locationIdstr}">${mappedLab.value}</a></td>
			</tr>
		</c:forEach>
	</table>
	</div>
	</div>
	<c:if test="${fn:length(testWithItsMappedByYear)>0}">
		<div class="chartHolder">
		<center></center>
		</div>
		<img src='chart.htm?chart=xyChartTest&width=1000&height=400'
			&startDate=${startdate } width='1000' height='400' />
	</c:if>
</c:if> <c:if test="${fn:length(mappedLabExams)>0}">
	<table width="70%">
		<tr>
			<td><b>Exams</b></td>
			<td><b>Number of exams</b></td>
		</tr>
		<c:forEach var="labExams" items="${mappedLabExams}" varStatus="status">
			<tr>
				<td width="70%">${labExams.key.name}</td>
				<td><a
					href="${pageContext.request.contextPath}/module/laboratorymodule/testByConcept.form?lineNumber=${status.count}">${labExams.value}</a></td>
			</tr>
		</c:forEach>
	</table>
</c:if></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>






