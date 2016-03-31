<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/jquery1.js" />
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/demo_page1.css" />
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/demo_table1.css" />
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/jquery.dataTables1.js" />
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/jquery.simplemodal.js" />
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/basic.js" />
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/basic.css" />
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/scripts/jquery.PrintArea1.js" />

<style type="text/css">
@media print {
	.non-printable {
		display: none;
	}
}
</style>
<script>
	var $f = jQuery.noConflict();
	$f(document)
			.ready(
					function() {
						$f("#labOrderForm").hide();
						$f("#toggleForm").click( function() {
							$f("#labOrderForm").toggle();
						});

						oTable = $f('#example_table')
								.dataTable(
										{
											"fnDrawCallback" : function(
													oSettings) {
												if (oSettings.aiDisplay.length == 0) {
													$f('table#example_table')
															.css( {
																'width' :'100%'
															});
													return;
												}

												var nTrs = $f('tbody tr',
														oSettings.nTable);
												$f('table#example_table').css( {
													'width' :'100%'
												});
												var iColspan = nTrs[0]
														.getElementsByTagName('td').length;
												var sLastGroup = "";
												for ( var i = 0; i < nTrs.length; i++) {
													var iDisplayIndex = oSettings._iDisplayStart
															+ i;
													var sGroup = oSettings.aoData[oSettings.aiDisplay[iDisplayIndex]]._aData[0];
													if (sGroup != sLastGroup) {
														var nGroup = document
																.createElement('tr');
														var nCell = document
																.createElement('td');
														nCell.colSpan = iColspan;
														nCell.className = "group";
														nCell.innerHTML = sGroup;
														nGroup
																.appendChild(nCell);
														nTrs[i].parentNode
																.insertBefore(
																		nGroup,
																		nTrs[i]);
														sLastGroup = sGroup;
													}
												}
											},
											"aoColumnDefs" : [ {
												"bVisible" :false,
												"aTargets" : [ 0 ]
											} ],
											"aaSortingFixed" : [ [ 0, 'asc' ] ],
											"aaSorting" : [ [ 1, 'asc' ] ],
											"sDom" :'lfr<"giveHeight"t>ip'
										});

						$f('#print_lab_ordonance')
								.click(
										function(e) {
											var row = null;
											var s = '';
											var columns = $f(
													'#example_table thead th')
													.map( function() {
														return $f(this).text();
													});
											var tableObject = $f(
													'#example_table tbody tr')
													.map(
															function(i) {
																row = {};
																$f(this)
																		.find(
																				'td')
																		.each(
																				function(
																						i) {
																					var rowName = columns[i];
																					row[rowName] = $f(
																							this)
																							.text();
																					s += $f(
																							this)
																							.text() + ',';
																				});
																s += ';';
															});
											var res = s.split(';');
											var tmpArr = null;
											var leftStr = '';
											var rightStr = '';
											var count = 1;
											var date = '';
											var frequency = '';
											var patientName = $f(
													'#patientHeaderPatientName')
													.text();
											for ( var k = 0; k < res.length; k++) {
												tmpArr = res[k].split(',');
												for ( var j = 0; j < tmpArr.length; j++) {
													if (j == 1) {
														var num = parseInt(tmpArr[0]);
														if (count % 2 != 0) {
															if (tmpArr[1]) {
																leftStr += count
																		+ '. '
																		+ tmpArr[1]
																		+ '<br />';
																date = tmpArr[3];
																count++;
															}
														} else {
															if (tmpArr[1]) {
																rightStr += count
																		+ '. '
																		+ tmpArr[1]
																		+ '<br />';
																date = tmpArr[3];
																count++;
															}
														}
													}
												}
											}
											$f('#firstTd').html(leftStr);
											$f('#secondTd').html(rightStr);
											$f('#dateId').html(date);
										});

						$f("#print_btn").click( function() {
							$f(".printable").printArea();
						});

						$f('.parent')
								.click(
										function() {
											var parentId = this.id;
											var parentConceptId = parentId
													.split('_')[1];
											if ($f('#' + parentId)
													.is(':checked')) {
												$f('.child_' + parentConceptId)
														.attr('checked',
																'checked');
											} else {
												$f('.child_' + parentConceptId)
														.removeAttr('checked');
											}
										});
					});
</script>

<b><Oderable Exams></b>
<div align="center"><b>${model.msg}</b></div>
<fieldset><legend id="toggleForm" style="cursor: pointer;">Lab
Request Form</legend>
<div id="labOrderForm">
<form action="" method="post">
<div>
<table>

	<tr>
		<td><input type="hidden" name="patientId"
			value="${model.patientId}"></td>
	</tr>

	<tr>
		<!-- first column -->
		<td valign="top" width="20%"><c:forEach var="labOrderParent"
			items="${model.labOrderparList}">
			<c:set var="gdParentConcept"
				value="${labOrderParent.grandFatherConcept}" />
			<c:set var="parentConcepts" value="${labOrderParent.labTests}" />
			<c:set var="hematology" value="${model.hematology}" />
			<c:set var="parasitology" value="${model.parasitology}" />
			<c:set var="hemostasis" value="${model.hemostasis}" />
			<c:set var="bacteriology" value="${model.bacteriology}" />
			<c:set var="urinaryChemistry" value="${model.urinaryChemistry}" />
			<c:set var="immunoSerology" value="${model.immunoSerology}" />
			<c:set var="bloodChemistry" value="${model.bloodChemistry}" />
			<c:set var="toxicology" value="${model.toxicology}" />
			<c:set var="spermogram" value="${model.spermogram}" />



			<c:if test="${gdParentConcept.name.name eq  hematology }">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">
						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />
						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />
						<tr>
							<td><input name="${fieldNameP}"
								value="${parentConcept.conceptId}" type="checkbox"
								id="parent_${labOrder.parentConcept.conceptId}" class="parent"><c:out
								value="${labOrder.parentConcept.name}" /></td>
						</tr>
						<c:forEach var="childConcept" items="${childrenConcepts}"
							varStatus="status">
							<c:set var="fieldNameC"
								value="${fieldNameP}-${childConcept.conceptId}" />
							<tr>
								<td style="text-indent: 20px"><input name="${fieldNameC}"
									value="${childConcept.conceptId}"
									class="child_${labOrder.parentConcept.conceptId}"
									id="child_${childConcept.conceptId}" type="checkbox"> <c:out
									value="${childConcept.name}" /></td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
			</c:if>


			<c:if test="${gdParentConcept.name.name eq hemostasis}">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">
						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />
						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />
						<tr>
							<td><input name="${fieldNameP}"
								value="${parentConcept.conceptId}" type="checkbox"
								id="parent_${labOrder.parentConcept.conceptId}" class="parent"><c:out
								value="${labOrder.parentConcept.name}" /></td>

						</tr>

					</c:forEach>
				</table>
			</c:if>




			<c:if test="${gdParentConcept.name.name eq parasitology }">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">
						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />
						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />
						<tr>
							<td><input name="${fieldNameP}"
								value="${parentConcept.conceptId}" type="checkbox"
								id="parent_${labOrder.parentConcept.conceptId}" class="parent"><c:out
								value="${labOrder.parentConcept.name}" /></td>

						</tr>
						<!-- 
						<c:forEach var="childConcept" items="${childrenConcepts}" varStatus="status">
							<c:set var="fieldNameC" value="${fieldNameP}-${childConcept.conceptId}" />	
							<tr>
								<td style="text-indent: 20px"><input name="${fieldNameC}" value="${childConcept.conceptId}" class="child_${labOrder.parentConcept.conceptId}" id="child_${childConcept.conceptId}" type="checkbox">
									<c:out value="${childConcept.name}" /></td>
							</tr>
						</c:forEach> -->
					</c:forEach>
				</table>
			</c:if>
		</c:forEach></td>
		<!-- /First Column -->

		<!-- Second Column -->
		<td valign="top" width="20%"><c:forEach var="labOrderParent"
			items="${model.labOrderparList}">
			<c:set var="gdParentConcept"
				value="${labOrderParent.grandFatherConcept}" />
			<c:set var="parentConcepts" value="${labOrderParent.labTests}" />

			<!-- first column -->
			<c:if test="${gdParentConcept.name.name eq  urinaryChemistry}">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">

						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />
						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />

						<tr>
							<td><input name="${fieldNameP}"
								value="${labOrder.parentConcept.conceptId}" type="checkbox"
								id="parent_${labOrder.parentConcept.conceptId}" class="parent"><c:out
								value="${labOrder.parentConcept.name}" /></td>
						</tr>
						<c:forEach var="childConcept" items="${childrenConcepts}"
							varStatus="status">
							<c:set var="fieldNameC"
								value="${fieldNameP}-${childConcept.conceptId}" />
							<tr>
								<td style="text-indent: 20px"><input name="${fieldNameC}"
									value="${childConcept.conceptId}"
									class="child_${labOrder.parentConcept.conceptId}"
									id="child_${childConcept.conceptId}" type="checkbox"> <c:out
									value="${childConcept.name}" /></td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
			</c:if>
			<c:if test="${gdParentConcept.name.name eq bacteriology }">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">
						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />

						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />

						<c:if test="${parentConcept.name.name eq spermogram}">
							<tr>
								<td><input name="${fieldNameP}"
									value="${labOrder.parentConcept.conceptId}" type="checkbox"
									id="parent_${labOrder.parentConcept.conceptId}" class="parent"><c:out
									value="${labOrder.parentConcept.name}" /></td>
							</tr>

						</c:if>
						<c:if test="${parentConcept.name.name != spermogram}">
							<tr>
								<td><input name="${parentConcept.conceptId}"
									value="${labOrder.parentConcept.conceptId}" type="checkbox"
									id="parent_${labOrder.parentConcept.conceptId}" class="parent"><c:out
									value="${labOrder.parentConcept.name}" /></td>
							</tr>
							<c:forEach var="childConcept" items="${childrenConcepts}"
								varStatus="status">
								<c:set var="fieldNameC"
									value="${fieldNameP}-${childConcept.conceptId}" />
								<tr>
									<td style="text-indent: 20px"><input name="${fieldNameC}"
										value="${childConcept.conceptId}"
										class="child_${labOrder.parentConcept.conceptId}"
										id="child_${childConcept.conceptId}" type="checkbox">
									<c:out value="${childConcept.name}" /></td>
								</tr>
							</c:forEach>
						</c:if>
					</c:forEach>

				</table>
				
				
				
			
			</c:if>

		</c:forEach></td>
		<!-- Second Column -->

		<!-- Third Column -->
		<td valign="top" width="20%"><c:forEach var="labOrderParent"
			items="${model.labOrderparList}">
			<c:set var="gdParentConcept"
				value="${labOrderParent.grandFatherConcept}" />
			<c:set var="parentConcepts" value="${labOrderParent.labTests}" />


			<!-- first column -->
			<c:if test="${gdParentConcept.name.name eq immunoSerology}">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">
						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />

						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />
						<tr>
							<td><input name="${fieldNameP}"
								value="${parentConcept.conceptId}" type="checkbox"
								id="parent_${labOrder.parentConcept.conceptId}" class="parent">
							<c:out value="${labOrder.parentConcept.name}" /></td>
						</tr>
						<c:forEach var="childConcept" items="${childrenConcepts}"
							varStatus="status">
							<c:set var="fieldNameC"
								value="${fieldNameP}-${childConcept.conceptId}" />
							<tr>
								<td style="text-indent: 20px"><input name="${fieldNameC}"
									value="${childConcept.conceptId}"
									class="child_${labOrder.parentConcept.conceptId}"
									id="child_${childConcept.name}" type="checkbox"> <c:out
									value="${childConcept.name}" /></td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
			</c:if>



		</c:forEach></td>

		<!-- third Column -->

		<!-- fourth column -->

		<td valign="top" width="20%"><c:forEach var="labOrderParent"
			items="${model.labOrderparList}">
			<c:set var="gdParentConcept"
				value="${labOrderParent.grandFatherConcept}" />
			<c:set var="parentConcepts" value="${labOrderParent.labTests}" />

			<c:if test="${gdParentConcept.name.name eq bloodChemistry}">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">
						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />

						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />
						<tr>
							<td><input name="${fieldNameP}"
								value="${parentConcept.conceptId}" type="checkbox"
								id="${labOrder.parentConcept.name}_${labOrder.parentConcept.conceptId}"
								class="parent"><c:out
								value="${labOrder.parentConcept.name}" /></td>
						</tr>
						<c:forEach var="childConcept" items="${childrenConcepts}"
							varStatus="status">
							<c:set var="fieldNameC"
								value="${fieldNameP}-${childConcept.conceptId}" />
							<tr>
								<td style="text-indent: 20px"><input name="${fieldNameC}"
									value="${childConcept.conceptId}"
									class="parent_${labOrder.parentConcept.conceptId}"
									id="${childConcept.name}_${status.count}" type="checkbox">
								<c:out value="${childConcept.name}" /></td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
			</c:if>
		</c:forEach></td>

		<!-- fifth column -->

		<td valign="top" width="20%"><c:forEach var="labOrderParent"
			items="${model.labOrderparList}">
			<c:set var="gdParentConcept"
				value="${labOrderParent.grandFatherConcept}" />
			<c:set var="parentConcepts" value="${labOrderParent.labTests}" />

			<c:if test="${gdParentConcept.name.name eq toxicology }">
				<table>
					<tr>
						<td><b>${gdParentConcept.name.name}</b></td>
					</tr>
					<c:forEach var="labOrder" items="${parentConcepts}"
						varStatus="status">
						<c:set var="childrenConcepts" value="${labOrder.childrenConcept}" />

						<c:set var="parentConcept" value="${labOrder.parentConcept}" />
						<c:set var="fieldNameP"
							value="lab-${gdParentConcept}-${labOrder.parentConcept.conceptId}" />
						<tr>
							<td><input name="${fieldNameP}"
								value="${parentConcept.conceptId}" type="checkbox"
								id="${labOrder.parentConcept.name}_${labOrder.parentConcept.conceptId}"
								class="parent"><c:out
								value="${labOrder.parentConcept.name}" /></td>
						</tr>
						<c:forEach var="childConcept" items="${childrenConcepts}"
							varStatus="status">
							<c:set var="fieldNameC"
								value="${fieldNameP}-${childConcept.conceptId}" />
							<tr>
								<td style="text-indent: 20px"><input name="${fieldNameC}"
									value="${childConcept.conceptId}"
									class="parent_${labOrder.parentConcept.conceptId}"
									id="${childConcept.name}_${status.count}" type="checkbox">
								<c:out value="${childConcept.name}" /></td>
							</tr>
						</c:forEach>
					</c:forEach>
				</table>
			</c:if>


		</c:forEach></td>

	</tr>

</table>
<input type="submit" name="saveLabOrders" value="Save Lab orders"></div>

</form>
</div>
</fieldset>



<div id="dt_example">
<div id="container">

<div style="float: right"><img id="print_lab_ordonance"
	src="moduleResources/@MODULE_ID@/print_preview.gif"
	style="cursor: pointer;" title="Print Preview" /></div>
<table cellpadding="0" cellspacing="0" border="0" class="display"
	id="example_table" style="width: 100%">
	<thead>
		<tr>
			<th>Rendering engine</th>
			<th>&nbsp;</th>
			<th><spring:message code="Test" /></th>
			<th><spring:message code="Result" /></th>
			<th><spring:message code="Normal range" /></th>
			<th><spring:message code="comments" /></th>
			<th><spring:message code="Order date" /></th>
			
			<th><spring:message code="cancel" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${model.obsMap}" var="key" varStatus="num">
			<c:set var="LabTestcount" value="1" scope="page" />
			<c:forEach items="${key.value}" var="orderObs">
				<c:if test="${empty orderObs.obss}">
					<tr>
						<td><openmrs:formatDate date="${orderObs.order.startDate}"
							type="textbox" /></td>
						<td>&nbsp;</td>
						<td>${orderObs.order.concept.name.name}</td>
						<td>???</td>
						<td>${orderObs.orderStatus[0]}</td>
						<td>No comment</td>
						<td><openmrs:formatDate date="${orderObs.order.startDate}"
							type="textbox" /></td>
						
						<td><a
							href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${model.patientId}&orderId=${orderObs.order.orderId}">Cancel</a></td>
					</tr>
					<c:set var="LabTestcount" value="${LabTestcount + 1}" scope="page" />
				</c:if>
				<c:if test="${not empty orderObs.obss}">
					<c:forEach items="${orderObs.obss}" var="obs">
						<tr>
							<td><openmrs:formatDate date="${orderObs.order.startDate}"
								type="textbox" /></td>
							<td>&nbsp;</td>
							<td>${obs[0].concept.name.name}</td>
							<c:choose>
								<c:when test="${obs[0].valueCoded != null}">
									<td>${obs[0].valueCoded.name}</td>
									<td>-</td>
									<td>${obs[0].comment}</td>

								</c:when>
								<c:when test="${obs[0].valueNumeric != null}">
									<td>${obs[0].valueNumeric}</td>
									<td>${obs[1]}</td>
									<td>${obs[0].comment}</td>

								</c:when>
								<c:when test="${obs[0].valueText != null}">
									<td>${obs[0].valueText}</td>
                                     <td>-</td>
                                     <td>${obs[0].comment}</td>
								</c:when>
								<c:otherwise>
									<td>???</td>
									<td>-</td>
                                    <td>${obs[0].comment}</td>
								</c:otherwise>
							</c:choose>
							
							<td><openmrs:formatDate date="${orderObs.order.startDate}"
								type="textbox" /></td>
								
							<td><a
								href="${pageContext.request.contextPath}/patientDashboard.form?patientId=${model.patientId}&orderId=${orderObs.order.orderId}">Cancel</a></td>
						</tr>
						<c:set var="LabTestcount" value="${LabTestcount + 1}" scope="page" />
					</c:forEach>
				</c:if>
			</c:forEach>
		</c:forEach>
	</tbody>
</table>
</div>
</div>
-+

<div id="laborder-modal-content" style="display: none"><img
	id="print_btn"
	src="${pageContext.request.contextPath}/images/printer.gif"
	style="cursor: pointer;" title="Print" />
<div class="printable">
<div id="ordonnanceModal" style="font-size: 10px;">
<center><u>
<h3>MEDICAL PRESCRIPTION</h3>
</u>
<table width="100%" height="151" border="1" cellpadding="0"
	cellspacing="0">
	<tr align="left" valign="top">
		<td width="40%" height="149">
		<p>Kacyiru Police Hospital (KPH)</p>
		<p>MEDICAL LABORATORY DEPARTMENT</p>
		<p>PO. BOX: 6183 KIGALI- RWANDA</p>
		</td>
		<td width="20%" align="center" valign="top">
		<p><img src="moduleResources/@MODULE_ID@/images/kph_logo.gif"
			width="111" height="109" alt="kph" /></p>
		<p>POLICE NATIONALE</p>
		</td>
		<td width="40%">
		<p>DEPARTMENT DE LABORATOIRE MEDICAL</p>
		<p>BP: 6183 KIGALI - RWANDA</p>
		</td>
	</tr>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr>
		<td height="40%" align="left" valign="top">
		<p><u>Information du Medecin</u></p>
		Noms: ${model.providerName}<br />
		Tel:<br />
		Service<br />
		Date de demande:<span id="dateId"></span></td>
		<td height="40%" align="left" valign="top">
		<p><u>Information du patient</u></p>
		Noms: ${model.patient.familyName} ${model.patient.middleName}
		${model.patient.givenName}<br />
		Date de Naissance: ${model.patient.birthdate}<br />
		Sexe: ${model.patient.gender}<br />
		No de Labo:</td>
		<td height="20%" align="left" valign="top">
		<p><u>Status</u></p>
		Diagnostic</td>
	</tr>
	<tr>
		<td colspan="3" align="left" valign="top">
		<p><strong>Renseignments clinique: <br />
		<br /></p>
		</strong></td>
	</tr>
</table>
<p>&nbsp;</p>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2">
		<center>Lab Test</center>
		</td>
	</tr>
	<tr>
		<td width="50%" id="firstTd"></td>
		<td width="50%" align="left" id="secondTd"></td>
	</tr>
</table>
</center>
</div>
</div>
</div>
