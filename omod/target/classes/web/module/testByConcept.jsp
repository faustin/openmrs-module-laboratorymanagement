<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<table cellpadding="0" cellspacing="0" border="0" class="display"
		id="example">
		
		<tr><td><spring:message code="laboratorymodule.givenName" /></td></tr>
		
<c:forEach var="testByConcept" items="${testTakenByConcept}"		varStatus="num">
<tr>


</tr>
				
	</c:forEach>		
		
</table>


	
				




<%@ include file="/WEB-INF/template/footer.jsp"%>