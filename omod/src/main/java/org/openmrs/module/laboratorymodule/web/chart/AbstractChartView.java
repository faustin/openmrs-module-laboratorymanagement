/**
 * Auto generated file comment
 */
package org.openmrs.module.laboratorymodule.web.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.springframework.web.servlet.view.AbstractView;

/**
 *
 */
public abstract class AbstractChartView extends AbstractView {

	protected static final Log log = LogFactory.getLog(AbstractChartView.class);

	private static Font font = new Font("Verdana", Font.PLAIN, 10);

	private static Color bkColor = new Color(240, 240, 250);
	
	Object chartParameters[]=null;
	

	/**
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		 chartParameters= (Object[]) request.getSession()
		.getAttribute("chartParameter");
		
	
		Date startDate=(Date) chartParameters[0];
		Date endDate=(Date) chartParameters[1];
		String conceptIdStr=(String) chartParameters[2];
		int conceptId=Integer.parseInt(conceptIdStr);
		String locationIdstr=(String) chartParameters[3];
		String patientIdStr=(String) chartParameters[4];
		
		                                              
		
		
		response.setContentType("image/png");

		// Disable caching
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");

		int width = (request.getParameter("width") != null && request
				.getParameter("width").compareTo("") != 0) ? (Integer
				.valueOf(request.getParameter("width"))) : 450;
		int height = (request.getParameter("height") != null && request
				.getParameter("height").compareTo("") != 0) ? (Integer
				.valueOf(request.getParameter("height"))) : 350;		
		
				
				JFreeChart chart =createChart(model, request, patientIdStr, locationIdstr, conceptId, startDate, endDate);
				chart.setBackgroundPaint(Color.WHITE);
				chart.getPlot().setOutlineStroke(new BasicStroke(0));
				chart.getPlot().setOutlinePaint(getBackgroundColor());
				chart.getPlot().setBackgroundPaint(getBackgroundColor());
				
				chart.getPlot().setNoDataMessage("No Data Available");
				
				ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, width, height);		

	}
	
	/**
	 * Generates a JFreeChart
	 * 
	 * @param model the model
	 * @param request the servlet request
	 * @return the chart object
	 */
	protected abstract JFreeChart createChart(Map<String, Object> model, HttpServletRequest request,String patientIdstr,String locationIdstr,int conceptId,Date startDate,Date endDate);
	
	/**
	 * Gets the font for chart rendering
	 */
	protected Font getFont() {
		return font;
	}
	
	/**
	 * Gets the plot background color
	 */
	protected Color getBackgroundColor() {
		return bkColor;
	}

}
