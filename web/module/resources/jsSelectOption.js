function createDeleteButton(baseName) {
	var deleteButton = $t(document.createElement("span")).attr("id",
			"delete_" + fieldGroupCount).attr("class", "bt").attr("style",
			"cursor: pointer;").text("X");
	deleteButton.click( function() {
		var idString = $t(this).attr("id");
		var selectorText = "#" + baseName + "tableid_"
				+ idString.substring(idString.indexOf("_") + 1);

		$t(selectorText).hide(200, function() {
			$t(selectorText).remove();
		});
	});
	return deleteButton;
}

function addOptionsToSelect(selectElement, oIDispArray, oIValArray) {
	selectElement.append($t(document.createElement("option")).attr("value", "")
			.text(""));
	for ( var j = 0; j < oIDispArray.length; j++) {
		selectElement.append($t(document.createElement("option")).attr("value",
				oIValArray[j]).text(oIDispArray[j]));
	}
}

function createNakedOptionSelect(nameValue, oIDispArray, oIValArray, classAttr) {
	var selectElement = $t(document.createElement("select")).attr("id",
			fieldGroupCount).attr("class", classAttr).attr("name", nameValue);
	addOptionsToSelect(selectElement, oIDispArray, oIValArray);
	var tableRow = $t(document.createElement("tr")).append(
			$t(document.createElement("td")).append(selectElement));

	return tableRow;
}

function createTest(baseName, idArray, valArray, classAttr, label, label1,label2) {

	// the containing table
	var table = $t(document.createElement("table")).attr("id",
			baseName + "tableid_" + ++fieldGroupCount).attr("width", "100%");

	// selector
	var optionSelectRow = createNakedOptionSelect("test_" + fieldGroupCount,
			idArray, valArray, classAttr);

	// delete button
	var deleteButton = createDeleteButton(baseName);

	var code = $t(document.createElement("input")).attr("name",
			"name_" + fieldGroupCount).attr("width", "10");
	// adding titles
	table.append($t(document.createElement("thead")).append(
			$t(document.createElement("tr")).attr("align", "center")
			.append(
					$t(document.createElement("th")).text(label))
					.append(
					$t(document.createElement("th")).text(label1))
					
					.append(
							$t(document.createElement("th")).text(label2))
							
			
			
			

	));

	// adding row fields
	table.append($t(document.createElement("tr")).attr("align", "center")
			.append(
					$t(document.createElement("td")).attr("width", "30%")
							.append(optionSelectRow)).append(
					$t(document.createElement("td")).attr("width", "20%")
							.append(code)).append(
					$t(document.createElement("td")).attr("width", "10%")
							.append(deleteButton)));

	// add the line separator between tables

	table
			.append($t(document.createElement("tr")).append(
					$t(document.createElement("td")).attr("colspan", "5")
							.append(
									$t(document.createElement("hr")).attr(
											"color", "#C9C9C9"))));

	// add the entire set of elements to the div
	table.hide();
	$t("#" + baseName).append(table);
	table.show(200);
}
function Validate() {
	Message = ""
	Message = Message + CheckPatientId()

	if (Message == "") {
		return true
	} else {
		alert(Message)
		return false
	}

}

function CheckPatientId() {
	UserName = document.f1.patientId.value

	if (patientId == "") {
		Message = "Something's wrong with your name" + "\n"
	} else {
		Message = ""
	}
	return Message

}
