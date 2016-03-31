<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/moduleResources/quarterlyreporting/style.css" />
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/jquery.js" />
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/jquery.PrintArea.js" />
	
<script type="text/javascript" language="JavaScript">
	$(document).ready(function() {
		$("div#print_button").click(function() {
			$("div.printPatientExam").printArea();
		});	
	}
	);
</script>
<div>
<div
	style="background: #E2E4FF; border: 1px #808080 solid; padding: 0.5em; margin-bottom: 0em">
<form action="" method="post">
<table>
	<tr>
		<td><spring:message code="laboratorymodule.from" /></td>
		<td><openmrs_tag:dateField formFieldName="startDate"
			startValue="${startdate}" /></td>

		<td><spring:message code="laboratorymodule.to" /></td>
		<td><openmrs_tag:dateField formFieldName="endDate"
			startValue="${enddate}" /></td>
	</tr>
	<tr>
		<td>Location</td>
		<td><openmrs_tag:locationField formFieldName="locationId"
			initialValue="${param.locationId}" /></td>
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

<div id="print_button"  style="cursor: http:pointer" ><input type="button" value="Print lab report "></div>
<div class="printPatientExam"  id="labResults" style="border: 2px #000000 double; width: 100 %;">

<table width="100%
" border="2" cellpadding="4">
	<tr>
		<td><b>Exam</b></td>
		<td><b>Positive </b></td>
		<td><b>Negative</b></td>
		<td><b>Total</b></td>
	</tr>
	<c:forEach var="labExamMap" items="${mappedLabExams}"
		varStatus="status">

		<tr>
			<td style=""><b>${labExamMap.key}</b></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<c:forEach var="labTest" items="${labExamMap.value}"
				varStatus="status">
				<tr>
					<td><c:out value="${labTest[0].name}" /></td>
					<td><a
						href="${pageContext.request.contextPath}/module/laboratorymodule/displayLabExams.form?poslabConceptId=${labTest[4]}&startDate=${startDate}&endDate=${endDate}"><c:out
						value="${labTest[1]}" /></a></td>
					<td><a
						href="${pageContext.request.contextPath}/module/laboratorymodule/displayLabExams.form?neglabConceptId=${labTest[4]}&startDate=${startDate}&endDate=${endDate}"><c:out
						value="${labTest[2]}" /></a></td>
					<td><a
						href="${pageContext.request.contextPath}/module/laboratorymodule/displayLabExams.form?totlabConceptId=${labTest[4]}&startDate=${startDate}&endDate=${endDate}"><c:out
						value="${labTest[3]}" /></a></td>

				</tr>

			</c:forEach>
		</tr>


		<!--  
	<c:forEach var="labTest" items="${labExamMap.value}"
		varStatus="status">
	<tr>
		<td><c:out value="${labTest[0]}" /></td>
		<td>20</td>
		<td>30</td>
		<td><c:out value="${labTest[3]}" /></td>
	</tr>	
</c:forEach>-->
	</c:forEach>
</table>
</div>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>