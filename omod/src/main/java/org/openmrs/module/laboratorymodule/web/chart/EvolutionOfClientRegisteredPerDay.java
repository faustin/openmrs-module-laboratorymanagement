package org.openmrs.module.laboratorymodule.web.chart;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.LaboratoryMgt;
import org.openmrs.module.laboratorymodule.service.LaboratoryService;

/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

public class EvolutionOfClientRegisteredPerDay extends AbstractChartView {

	/**
	 * @see org.openmrs.module.vcttrac.web.view.chart.AbstractChartView#createChart(java.util.Map,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected JFreeChart createChart(Map<String, Object> model,
			HttpServletRequest request, String patientIdstr,
			String locationIdstr, int conceptId, Date startDate, Date endDate) {

		String categoryAxisLabel = " Year";
		String valueAxisLabel = "Number of Lab tests";

		String title = "EVolution of labotory test:"
				+ Context.getConceptService().getConcept(conceptId)
						.getDisplayString();

		JFreeChart chart = ChartFactory.createLineChart(title,
				categoryAxisLabel, valueAxisLabel, createDataset(patientIdstr,
						locationIdstr, conceptId, startDate, endDate), // data
				PlotOrientation.VERTICAL, true, false, false);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);

		// customise the range axis...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setUpperMargin(0.15);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// customise the renderer...
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setBaseShapesFilled(true);
		renderer
				.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setItemLabelsVisible(true);

		return chart;
	}

	/**
	 * Auto generated method comment
	 * 
	 * @return
	 */
	private CategoryDataset createDataset(String patientIdstr,
			String locationIdstr, int conceptId, Date startDate, Date endDate) {

		// row keys...
		Calendar ca1 = Calendar.getInstance();
		String series1 = Context.getConceptService().getConcept(conceptId)
				.getDisplayString();
		// Date format declaration
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<Obs> testTakenBetweenTwoDates = null;
		LaboratoryService labService = Context
				.getService(LaboratoryService.class);

		int startMonths = startDate.getMonth() + 1;
		int endMonth = endDate.getMonth() + 1;

		int startYear = startDate.getYear() + 1900;
		int endYear = endDate.getYear() + 1900;

		// create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (startYear == endYear) {

			for (int months = startMonths; months <= endMonth; months = months + 1) {

				try {
					if (months == startMonths) {
						// populate the map
						Date startDat = startDate;
						ca1.set(startYear, months - 1, 1);
						Date d = new java.util.Date(ca1.getTimeInMillis());
						Date endDat = LaboratoryMgt.getLastDayOfMonth(
								startYear, months - 1);
						List<Obs> allTestWithResult = LaboratoryMgt
								.getAllLabTestByCriteria(patientIdstr,
										locationIdstr, conceptId, startDat,
										endDat);
						testTakenBetweenTwoDates = LaboratoryMgt
								.getAllTestWithSpecificConcept(
										allTestWithResult, conceptId);

						int value = testTakenBetweenTwoDates.size();
						dataset.addValue(value, series1, ""
								+ new SimpleDateFormat("MMMM").format(d));
						continue;

					}
					if (months == endMonth) {
						ca1.set(startYear, months - 1, 1);
						Date d = new java.util.Date(ca1.getTimeInMillis());
						Date startDat = df.parse("01/" + months + "/"
								+ startYear);
						Date endDat = endDate;
						List<Obs> allTestWithResult = LaboratoryMgt
								.getAllLabTestByCriteria(patientIdstr,
										locationIdstr, conceptId, startDat,
										endDat);

						testTakenBetweenTwoDates = LaboratoryMgt
								.getAllTestWithSpecificConcept(
										allTestWithResult, conceptId);
						int value = testTakenBetweenTwoDates.size();
						dataset.addValue(value, series1, ""
								+ new SimpleDateFormat("MMMM").format(d));
						continue;
					}
					Date startDat = df.parse("01/" + months + "/" + startYear);
					ca1.set(startYear, months - 1, 1);
					Date d = new java.util.Date(ca1.getTimeInMillis());
					Date endDat = LaboratoryMgt.getLastDayOfMonth(startYear,
							months - 1);
					List<Obs> allTestWithResult = LaboratoryMgt
							.getAllLabTestByCriteria(patientIdstr,
									locationIdstr, conceptId, startDat, endDat);
					testTakenBetweenTwoDates = LaboratoryMgt
							.getAllTestWithSpecificConcept(allTestWithResult,
									conceptId);
					int value = testTakenBetweenTwoDates.size();
					dataset.addValue(value, series1, ""
							+ new SimpleDateFormat("MMMM").format(d));

				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}
		if (startYear != endYear) {
			for (int year = startYear; year <= endYear; year = year + 1) {
				try {

					if (year == startYear) {

						Date startDat = startDate;
						Date endDat = df.parse("31/12/" + year);
						

						List<Obs> allTestWithResult = LaboratoryMgt
						.getAllLabTestByCriteria(patientIdstr,
								locationIdstr, conceptId, startDat,
								endDat);

				testTakenBetweenTwoDates = LaboratoryMgt
						.getAllTestWithSpecificConcept(
								allTestWithResult, conceptId);
				int value = testTakenBetweenTwoDates.size();
				dataset.addValue(value, series1, ""+year);
					
						continue;

					}
					if (year == endYear) {
						Date startDat = df.parse("01/01/" + year);
						Date endDat = endDate;

						List<Obs> allTestWithResult = LaboratoryMgt
						.getAllLabTestByCriteria(patientIdstr,
								locationIdstr, conceptId, startDat,
								endDat);

				testTakenBetweenTwoDates = LaboratoryMgt
						.getAllTestWithSpecificConcept(
								allTestWithResult, conceptId);
				int value = testTakenBetweenTwoDates.size();
				dataset.addValue(value, series1, ""+year);

						continue;

					}
					Date startDat = df.parse("01/01/" + year);
					Date endDat = df.parse("31/12/" + year);

					List<Obs> allTestWithResult = LaboratoryMgt
					.getAllLabTestByCriteria(patientIdstr,
							locationIdstr, conceptId, startDat,
							endDat);

			testTakenBetweenTwoDates = LaboratoryMgt
					.getAllTestWithSpecificConcept(
							allTestWithResult, conceptId);
			int value = testTakenBetweenTwoDates.size();
			dataset.addValue(value, series1, ""+year);
					continue;

				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}
		return dataset;

	}

}