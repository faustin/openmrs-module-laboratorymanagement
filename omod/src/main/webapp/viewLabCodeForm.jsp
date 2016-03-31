<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>
<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/menuStyle.css" />
<!-- If orderId is null ,display a lab code form with default value -->
<div
	style="background: #E2E4FF; border: 1px #808080 solid; padding: 0.5em; margin-bottom: 0em">

<form id="labTest" 	action="viewLabOrder.form?save=true"  method="post">
<table>
	<tr>
		<td>Lab code</td>
		<td><input type="text" name="labCode" value="${labCode}" /></td>
	</tr>
	<tr>
		
		<td><input type="hidden" name="patientId" value="${patientId}" /></td>
	</tr>
		<tr>		
		<td><input type="hidden" name="startDate" value="${startDate}" /></td>
	</tr>		
	<tr>
		<td><input type="submit" value="Add Lab Code" /><input
			type="reset" value="cancel" /></td>
	</tr>
</table>
</form>
</div>




<%@ include file="/WEB-INF/template/footer.jsp"%>