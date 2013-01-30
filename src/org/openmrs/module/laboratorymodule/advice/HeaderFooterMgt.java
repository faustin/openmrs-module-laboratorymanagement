package org.openmrs.module.laboratorymodule.advice;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterMgt extends PdfPageEventHelper{
	public void onEndPage(PdfWriter writer, Document document) {
		Rectangle rect = writer.getBoxSize("art");

		Phrase header = new Phrase(String.format("- %d -", writer
				.getPageNumber()));
		header.setFont(new Font(FontFamily.COURIER, 4, Font.NORMAL));

		if (document.getPageNumber() > 1) {
			ColumnText.showTextAligned(writer.getDirectContent(),
					Element.ALIGN_CENTER, header, (rect.getLeft() + rect
							.getRight()) / 2, rect.getTop() + 40, 0);
		}

		Phrase footer = new Phrase(String.format("- %d -", writer
				.getPageNumber()));
		footer.setFont(new Font(FontFamily.COURIER, 4, Font.NORMAL));

		ColumnText.showTextAligned(writer.getDirectContent(),
				Element.ALIGN_CENTER, footer, (rect.getLeft() + rect
						.getRight()) / 2, rect.getBottom() - 40, 0);

	}

}
