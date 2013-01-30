<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/menuStyle.css" />
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/jquery.js" />
<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/chosen.jquery.min.js" />
<openmrs:htmlInclude file="/moduleResources/laboratorymodule/chosen.css" />
<openmrs:htmlInclude
	file="/moduleResources/laboratorymodule/jsControl.js" />
<div>
<form name="laboForm" id="labotryform" method="get">
<table>
	<tr>
		<td><spring:message code="laboratorymodule.labCode" /></td>
		<td><input type="text" name="labCode" value="${labCode}"></td>
	</tr>
	<tr>
		<td></td>
		<td><input type="submit"
			value="<spring:message code="laboratorymodule.Search"/>" /><input
			type="reset" value="<spring:message code="laboratorymodule.cancel"/>" /></td>
	</tr>
</table>
</form>
</div>
<br>
<br>

<c:if test="${fn:length(mapLabeTest)!=0}">
	<form action="addResult.form?patientId=${param.patientId}&locationId=${param.locationId}&save=true" method="post">
	
<input type="hidden" name="patient_id" value="${patientId}"/>

		<c:forEach var="mapLab" items="${mapLabeTest}"
		varStatus="num">
		
		<fieldset><legend><b>${mapLab.key.name}</b></legend> <c:forEach
			var="labOrder" items="${mapLab.value}" varStatus="num">
			<c:set var="order" value="${labOrder[0]}" />
			<c:set var="concept" value="${order.concept}" />
			<c:set var="units" value="${labOrder[1]}" />
			<c:set var="obsResult" value="${resultsMap[order]}" />
			<c:set var="fieldName"
				value="labTest-${order.concept.conceptId}-${order.orderId}" />

			<c:choose>
				<c:when test="${concept.numeric}">
					<!-- Lab test is numeric -->
					<table>
						<tr>
							<td colspan="3" style="width: 250px; font-weight: italic"><c:out
								value="${concept.name}" /></td>
							<td><input type="text" name="${fieldName}"
								value="${obsResult != null ? obsResult.valueNumeric : ''}" /> <c:out
								value="${units}" /></td>
						</tr>
					</table>
				</c:when>

				<c:when test="${concept.datatype.text}">
					<!-- Lab test is text -->
					<table>

						<tr>
							<c:if test="${concept.name.name eq 'WIDAL TEST'}">
								<td colspan="3" style="width: 250px; font-weight: italic"><c:out value="${concept.name}" /></td>
								<td><textarea name="${fieldName}" rows="1" cols="30">${obsResult != null ? obsResult.valueText : ''}</textarea></td>
							</c:if>
							<c:if test="${concept.name.name != 'WIDAL TEST'}">
									<td colspan="3" style="width: 250px; font-weight: italic"><c:out value="${concept.name}" /></td>
								<td><input type="text" name="${fieldName}"
									value="${obsResult != null ? obsResult.valueText : ''}" /></td>
							</c:if>

						</tr>
					</table>
				</c:when>

				<c:when test="${concept.datatype.coded}">
					<!-- Lab test is coded -->
					<table>
						<tr>
							<td colspan="3" style="width: 250px; font-weight: italic"><c:out
								value="${concept.name}" /></td>
							<td><c:choose>
								<c:when test="${multipleAnswerConcepts[concept]}">
									<c:forEach items="${concept.answers}" var="answer">
										<input type="checkbox" name="${fieldName}"
											checked="${obsResult.valueCoded != null ? 'checked' : ''}"
											value="${answer.answerConcept.conceptId}" />
										${answer.answerConcept.name}
										&nbsp;&nbsp;&nbsp;
									</c:forEach>
								</c:when>
								<c:otherwise>
									<select name="${fieldName}">
										<c:forEach items="${concept.answers}" var="answer">
											<option value="${answer.answerConcept.conceptId}"
												<c:if test="${obsResult != null && obsResult.valueCoded.conceptId == answer.answerConcept.conceptId}">
													selected="selected"
												</c:if>>${answer.answerConcept.name}
											</option>
									</c:forEach>
									</select>

									</c:otherwise>
							</c:choose></td>
						</tr>
					</table>
				</c:when>

				<c:when test="${concept.datatype.answerOnly}">
					<!-- Lab test is a construct:display the lab tests linked to the Order considered as a gp of Lab tests -->
					<c:forEach var="oneGroupedtest" items="${groupedTests}" 	varStatus="num">
					<c:set var="conceptName" value="${concept.name}" />
					<c:set var="concptNameFromMap" value="${oneGroupedtest.key.name}" />
					
					<c:if test="${conceptName eq concptNameFromMap}">
				
					
					<fieldset><legend>${concept.name}</legend> <c:forEach
						items="${oneGroupedtest.value}" var="groupedTest">
					
						<c:set var="childConcept" value="${groupedTest[0]}" />
						<c:set var="unit" value="${groupedTest[1]}" />
						<c:set var="childResult" value="${obsResult[childConcept]}" />

						<!-- Append parent construct concept to fieldname -->
						<c:set var="fieldName"
							value="labTest-${childConcept.conceptId}-${order.orderId}-${concept.conceptId}" />

						<c:choose>
							<c:when test="${childConcept.datatype.numeric}">
								<!-- Lab test is numeric -->
								<table>
									<tr>
										<td colspan="3" style="width: 250px; font-weight: italic"><c:out
											value="${childConcept.name}" /></td>
										<td><input type="text" name="${fieldName}"
											value="${childResult != null ? childResult.valueNumeric : ''}" /><c:out
								value="${unit}" /></td>
									</tr>
								</table>
							</c:when>
 
							<c:when test="${childConcept.datatype.text}">
								<!-- Lab test is text -->
								<table>
									<tr>
										<!-- if other results are found,write them in textarea -->
										<c:if
											test="${childConcept.name.name eq 'OTHER LAB TEST RESULT' || 'OTHER RESULT'}">

											<td colspan="3" style="width: 250px; font-weight: italic"><c:out
												value="${childConcept.name}" /></td>

											<td><textarea name="${fieldName}" rows="1" cols="30">${childResult != null ? childResult.valueText : ''}</textarea></td>

										</c:if>
										<c:if
											test="${childConcept.name.name != 'OTHER LAB TEST RESULT'}">
											<td colspan="3" style="width: 250px; font-weight: italic"><c:out
												value="${childConcept.name}" /></td>
											<td><input type="text" name="${fieldName}"
												value="${childResult != null ? childResult.valueText : ''}" /></td>
											
										</c:if>


									</tr>
								</table>
							</c:when>
							<c:when test="${childConcept.datatype.coded}">
								<!-- Lab test is coded -->
								<table>
									<tr>
										<td colspan="3" style="width: 250px; font-weight: italic"><c:out
											value="${childConcept.name}" /></td>
										<td><c:choose>
											<c:when test="${multipleAnswerConcepts[childConcept]}">
												<c:forEach items="${childConcept.answers}" var="answer">
													<input type="checkbox" name="${fieldName}"
														checked="${childResult.valueCoded != null ? 'checked' : ''}"
														value="${answer.answerConcept.conceptId}" />
														${answer.answerConcept.name}
														&nbsp;&nbsp;&nbsp;
													</c:forEach>
												</c:when>
												<c:otherwise>
													<select name="${fieldName}">
														<c:forEach items="${childConcept.answers}" var="answer">
															<option value="${answer.answerConcept.conceptId}"
																<c:if test="${childResult != null && childResult.valueCoded.conceptId == answer.answerConcept.conceptId}">
																	selected="selected"
																</c:if>>${answer.answerConcept.name}
															</option>
												</c:forEach>
												</select>
												</c:otherwise>
										</c:choose></td>
									</tr>
								</table>
							</c:when>
						</c:choose>

					</c:forEach></fieldset>
					</c:if>
					
					</c:forEach>
				</c:when>

			</c:choose>

		</c:forEach></fieldset>
	</c:forEach> <input type="submit" value="update" /> <input type="reset"
		value="cancel" /></form>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>

