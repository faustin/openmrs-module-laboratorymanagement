 

<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/menuStyle.css" />
<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/jsSelectOption.js" />
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/jquery.js" />
<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/laboratoryFormValidator.js" />
<script>
var $t = jQuery.noConflict();

var fieldGroupCount = 0;
	var idArray = new Array();
	var valArray = new Array();
	<c:forEach items="${labConcepts}" var="labConcept">
		idArray.push("<c:out value="${labConcept.name}"/>");
		valArray.push("<c:out value="${labConcept.conceptId}"/>");
	</c:forEach>
		$t(document).ready(function() {
			$t("#addTest").click(
					function() {
						createTest("testList", idArray, valArray, "dynamicTest", "Test name","Lab code","Delete");
					});	
			
		});
		
</script>
<br />
<b class="boxHeader" style="width: 100%"><spring:message
	code="laboratorymodule.laboratoryForm" /></b>
<div
	style="background: #E2E4FF; border: 1px #808080 solid; padding: 0.5em; margin-bottom: 0em">
<form name="laboForm" id="labotryform" method="get"
	onsubmit="return checkform(this)">
<table>
	<tr>
		<td><spring:message code="laboratorymodule.patientName" /></td>
		<td><openmrs:fieldGen type="org.openmrs.Patient"
			formFieldName="patientId" val="${patient}" /></td>
	</tr>

	<tr>
		<td>Provider:</td>
		<td><openmrs:fieldGen type="org.openmrs.User"
			formFieldName="userId" val="${user }" /></td>
	</tr>

	<tr>
		<td><spring:message code="laboratorymodule.site" /></td>
		<td><openmrs:fieldGen type="org.openmrs.Location"
			formFieldName="locationId" val="${location}" /></td>
	</tr>
	<tr>
		<td></td>
		<td>
		<div id="testList"></div>
		</td>

	</tr>
	<tr>
		<td></td>
		<td><span id="addTest" class="bouton"><img src="${pageContext.request.contextPath}/images/add.gif" style="cursor: pointer;" /></span></td>

	</tr>
	<tr>
		<td><spring:message code="laboratorymodule.acquiredDate" /></td>
		<td><openmrs_tag:dateField formFieldName="acquiredDate"
			startValue="" /></td>

	</tr>
	<tr>
		<td></td>
		<td><input type="submit"
			value="<spring:message code="laboratorymodule.save"/>" /><input
			type="reset" value="<spring:message code="laboratorymodule.cancel"/>" /></td>
	</tr>
</table>
</form>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>

