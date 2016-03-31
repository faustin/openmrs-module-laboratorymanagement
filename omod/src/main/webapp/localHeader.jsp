<h2><spring:message code="laboratorymodule.title" /></h2>

<ul id="menu">
	<!-- 
	<li
		class="first<c:if test='<%= request.getRequestURI().contains("laboratorymoduleForm") %>'>active</c:if>">
	<a
		href="${pageContext.request.contextPath}/module/laboratorymodule/laboratorymoduleLink.form"><spring:message
		code="laboratorymodule.addOrder" /></a></li> -->


	<li
		class="first<c:if test='<%= request.getRequestURI().contains("viewLabOrder") %>'>active</c:if>">
	<a
		href="${pageContext.request.contextPath}/module/laboratorymodule/viewLabOrder.form"><spring:message
		code="laboratorymodule.viewLabOrder" /></a></li>

	<li
		class="<c:if test='<%= request.getRequestURI().contains("addResult") %>'>active</c:if>"><a
		href="${pageContext.request.contextPath}/module/laboratorymodule/addResult.form"><spring:message
		code="laboratorymodule.addResult" /></a></li>
	<li
		class="<c:if test='<%= request.getRequestURI().contains("testStatistics") %>'>active</c:if>"><a
		href="${pageContext.request.contextPath}/module/laboratorymodule/testStatistics.form"><spring:message
		code="laboratorymodule.viewStatistics" /></a></li>
	<li
		class="<c:if test='<%= request.getRequestURI().contains("patientReport") %>'>active</c:if>"><a
		href="${pageContext.request.contextPath}/module/laboratorymodule/patientReport.form"><spring:message
		code="laboratorymodule.patientReport" /></a></li>
	<li
		class="<c:if test='<%= request.getRequestURI().contains("monthlyReport") %>'>active</c:if>"><a
		href="${pageContext.request.contextPath}/module/laboratorymodule/monthlyReport.form"><spring:message
		code="laboratorymodule.ViewMonthlyReport" /></a></li>
	<openmrs:hasPrivilege privilege="View Lab configuration">
		<li
			class="<c:if test='<%= request.getRequestURI().contains("labConfiguration") %>'>active</c:if>"><a
			href="${pageContext.request.contextPath}/module/laboratorymodule/labConfiguration.form"><spring:message
			code="laboratorymodule.labConfiguration" /></a></li>
	</openmrs:hasPrivilege>

</ul>