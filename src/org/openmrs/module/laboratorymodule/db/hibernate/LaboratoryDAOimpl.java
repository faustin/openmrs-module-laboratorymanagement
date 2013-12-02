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
package org.openmrs.module.laboratorymodule.db.hibernate;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;

import org.openmrs.Person;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.laboratorymodule.advice.HeaderFooterMgt;
import org.openmrs.module.laboratorymodule.db.LaboratoryDAO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 */
public class LaboratoryDAOimpl implements LaboratoryDAO {

	protected final Log log = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void saveOrderEncounter(Encounter encounter) {
		EncounterService encounterService = Context.getEncounterService();
		encounterService.saveEncounter(encounter);

	}

	public List<Encounter> getAllEncountersByProvider(int providerId) {
		// TODO Auto-generated method stub

		return null;
	}

	/**
	 * gives all observations by location from the database
	 * 
	 * @return list of all observation
	 */

	public List<Obs> getAllObsByLocation(int locationId) {
		List<Obs> patientObservations = new ArrayList<Obs>();

		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("SELECT  o.obs_id  FROM obs o where o.location_id ="
						+ locationId);

		List<Integer> testObsIds = query.list();
		for (Integer oneTestObsId : testObsIds) {
			patientObservations.add(Context.getObsService()
					.getObs(oneTestObsId));

		}

		return patientObservations;

	}

	public void getSampleCodeByPatient(int testObsId, int newobsId) {

		String sb = new String();
		sb = "update trac_sample_test  set  test_obs_id=" + newobsId
				+ " where test_obs_id=" + testObsId;
		Session session = sessionFactory.getCurrentSession();

		try {
			session.beginTransaction();
			@SuppressWarnings("unused")
			int query = session.createSQLQuery(sb).executeUpdate();
			session.getTransaction().commit();
		} catch (HibernateException e) {
			log.error(e);
		}

	}

	public List<EncounterType> getAllEncounterType() {
		List<EncounterType> encounterTypeList = new ArrayList<EncounterType>();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("SELECT encounter_type_id FROM encounter_type e");
		List<Integer> encounterTypeIds = query.list();
		for (Integer encounterTypeId : encounterTypeIds) {
			encounterTypeList.add(Context.getEncounterService()
					.getEncounterType(encounterTypeId));

		}

		return encounterTypeList;
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public List<Obs> getPatientTestBeforeDate(Date startDate, Date endDate) {
		// Date testEndDate=LaboratoryMgt.getDateParameter(endDate);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		List<Obs> patientObservations = new ArrayList<Obs>();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("SELECT  o.obs_id  FROM obs o where  o.obs_datetime  between  "

						+ "'"
						+ df.format(startDate)
						+ "'and "
						+ "'"
						+ df.format(endDate) + "'");

		List<Integer> testObsIds = query.list();

		for (Integer testObsId : testObsIds) {
			if (testObsId != null) {
				// System.out.println(">>>>>test obsId" + testObsId);
				patientObservations.add(Context.getObsService().getObs(
						testObsId));

			}

		}

		return patientObservations;
	}

	@SuppressWarnings("unchecked")
	public List<Obs> getTestOfPatientBetweenTwoDate(int patientId,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> patientObservations = new ArrayList<Obs>();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("SELECT obs_id FROM obs o where cast(o.obs_datetime as date) "
						+ "between "
						+ "'"
						+ df.format(startDate)
						+ "'"
						+ " and "
						+ "'"
						+ df.format(endDate)
						+ "'"
						+ " and person_id= " + patientId);

		/*
		 * 
		 * .createSQLQuery("SELECT obs_id FROM obs o  " +
		 * "where o.obs_datetime  between  " + "'" + df.format(startDate) +
		 * "'and " + "'" + df.format(endDate) + "' and o.person_id =" +
		 * patientId );
		 */System.out.println(">>>>>>>>>tests between two dates patient query"
				+ query.toString());

		List<Integer> testObsIds = query.list();

		for (Integer testObsId : testObsIds) {
			if (testObsId != null) {
				// System.out.println(">>>>>test obsId" + testObsId);
				patientObservations.add(Context.getObsService().getObs(
						testObsId));

			}

		}

		return patientObservations;
	}

	public List<Obs> getTestsBetweenTwoDateByLocation(int locationId,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> patientObservations = new ArrayList<Obs>();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("SELECT  distinct ob.obs_id FROM obs ob "
						+ "where  ob.location_id ="
						+ locationId
						+ " and  ob.obs_datetime  between  "
						+ "'"
						+ df.format(startDate)
						+ "'and "
						+ "'"
						+ df.format(endDate)
						+ "' and  ob.obs_id  in (select test_obs_id  from  trac_sample_test) ");

		List<Integer> testObsIds = query.list();

		for (Integer testObsId : testObsIds) {
			if (testObsId != null) {
				// System.out.println(">>>>>test obsId" + testObsId);
				patientObservations.add(Context.getObsService().getObs(
						testObsId));

			}

		}

		return patientObservations;
	}

	public List<Obs> getAllPatientLabtestByLocation(int patientId,
			int locationId, int conceptId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> patientObservations = new ArrayList<Obs>();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("select o.obs_id from obs o where "
						+ "  o.person_id=" + patientId + " and o.location_id="
						+ locationId + " and o.concept_id=" + conceptId + " "
						+ " and o.obs_datetime between '"
						+ df.format(startDate) + "' and '" + df.format(endDate)
						+ "'");
		log.info("ZZZZlab test by locato and patioend between two dates"
				+ query.toString());
		List<Integer> testObsIds = query.list();

		for (Integer testObsId : testObsIds) {
			if (testObsId != null) {
				// System.out.println(">>>>>test obsId" + testObsId);
				patientObservations.add(Context.getObsService().getObs(
						testObsId));

			}

		}

		return patientObservations;
	}

	public List<Obs> getPatientLabtestBetweenTwoDate(int patientId,
			int conceptId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> patientObservations = new ArrayList<Obs>();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("select o.obs_id from obs o where "
						+ "  o.person_id="
						+ patientId
						+ "  and o.concept_id="
						+ conceptId
						+ " "
						+ " and o.obs_datetime between '"
						+ df.format(startDate)
						+ "' and '"
						+ df.format(endDate)
						+ "' and o.obs_id in (select t.test_obs_id from trac_sample_test t) ");

		System.out
				.println("ZZZZlab test by locato and patioend between two dates"
						+ query.toString());
		List<Integer> testObsIds = query.list();
		for (Integer testObsId : testObsIds) {
			if (testObsId != null) {
				// System.out.println(">>>>>test obsId" + testObsId);
				patientObservations.add(Context.getObsService().getObs(
						testObsId));

			}

		}

		return patientObservations;
	}

	public List<Obs> getPatientLabtestByLocation(int patientId, int locationId,
			int conceptId) {
		// TODO Auto-generated method stub

		List<Obs> patientObservations = new ArrayList<Obs>();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session
				.createSQLQuery("select o.obs_id from obs o where "
						+ "  o.person_id=" + patientId + " and o.location_id="
						+ locationId + " and o.concept_id=" + conceptId + " ");
		log.info("ZZZZlab test by locato and patioend between two dates"
				+ query.toString());
		List<Integer> testObsIds = query.list();
		for (Integer testObsId : testObsIds) {
			if (testObsId != null) {
				// System.out.println(">>>>>test obsId" + testObsId);
				patientObservations.add(Context.getObsService().getObs(
						testObsId));

			}

		}

		return patientObservations;
	}

	public void exportToPDF(HttpServletRequest request,
			HttpServletResponse response, List<Object[]> listOflabtest,
			String filename, String title, int conceptId)
			throws DocumentException, IOException {
		// TODO Auto-generated method stub
		Concept cpt = Context.getConceptService().getConcept(conceptId);
		System.out.println(">>>>>>list to export" + listOflabtest);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		response.setContentType("application/pdf");
		Document document = new Document();
		try {

			response.setHeader("Content-Disposition",
					"attachment; filename=\"laboratory.pdf\"");
			PdfWriter writer = PdfWriter.getInstance(document, response
					.getOutputStream());
			document.open();
			writer.setBoxSize("art", PageSize.A4);

			float[] colsWidth = { 5f, 20f, 17f, 17f, 17f, 16f, 20f };
			PdfPTable table = new PdfPTable(colsWidth);
			// add a title
			PdfPCell cell = new PdfPCell(new Paragraph("CD4 lab tests"));
			cell.setColspan(2);

			document.add(new Paragraph(
					"                                Laboratory tests:"
							+ cpt.getName()));

			document.add(new Paragraph("  "));
			document.add(new Paragraph("  "));

			table.addCell("#");
			table.addCell("GIVEN NAME");
			table.addCell("FAMILY NAME");
			table.addCell("TEST NAME");
			table.addCell("TESTED ON");
			table.addCell("LOCATION");
			table.addCell("TEST RESULT");

			for (int i = 0; i < listOflabtest.size(); i++) {

				Object[] labtest = listOflabtest.get(i);
				Person p = (Person) labtest[0];
				Obs ob = (Obs) labtest[1];

				table.addCell(i + 1 + "");
				table.addCell(p.getGivenName() + "");
				table.addCell(p.getFamilyName() + "");
				table.addCell(ob.getConcept().getName() + "");
				table.addCell(df.format(ob.getObsDatetime()) + "");
				table.addCell(ob.getLocation() + "");
				if (cpt.getDatatype().isNumeric()) {
					table.addCell(ob.getValueNumeric() + "");
				}
				if (cpt.getDatatype().isCoded()) {
					table.addCell(ob.getValueCoded().getName() + "");

				}
				if (cpt.getDatatype().isText()) {
					table.addCell(ob.getValueText() + "");

				}
				if (cpt.getDatatype().isDate()) {
					table.addCell(df.format((ob.getValueDatetime())) + "");

				}

			}

			document.add(table);
			document.getJavaScript_onLoad();
			document.close();
			// document.add(new Paragraph("Hello world"));
			// document.close();
		} catch (DocumentException e) {
		}
	}

	public List<Obs> getAllLabTest(Integer labConceptId) {
		// TODO Auto-generated method stub

		// List to store all lab test by lab test type
		List<Obs> allLabtest = new ArrayList<Obs>();
		ObsService obsServ = Context.getObsService();
		Session session = getSessionFactory().getCurrentSession();

		SQLQuery query = session
				.createSQLQuery("SELECT o.obs_id FROM obs o where o.concept_id ="
						+ labConceptId
						+ " and o.obs_datetime between '2011-08-01'and '2011-11-11' ");

		// System.out.println("ZZZZlab test by locato and patioend between two dates"
		// + query.toString());
		List<Integer> labTestObsIds = query.list();
		for (Integer labTestObsId : labTestObsIds) {
			allLabtest.add(obsServ.getObs(labTestObsId));

		}

		return allLabtest;
	}

	/**
	 * get all Lab obs
	 * 
	 * @see org.openmrs.module.laboratorymodule.db.LaboratoryDAO#getLabExamsByExamType(int,
	 *      java.util.Collection, java.util.Date, java.util.Date)
	 */
	public List<Obs> getLabExamsByExamType(int patientId,
			Collection<Integer> cptIds, Date startDate, Date endDate) {
		// TODO Auto-generated method stu
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> labExamsByCategory = new ArrayList<Obs>();
		ObsService labObbService = Context.getObsService();
		SQLQuery query = null;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT o.obs_id FROM obs o where o.concept_id in ( ");

		int i = 1;
		for (Integer onecptId : cptIds) {

			if (i < cptIds.size()) {
				strbuf.append(" " + onecptId + ",");
			}
			if (i == cptIds.size()) {
				strbuf.append(" " + onecptId);
			}
			i = i + 1;

		}
		strbuf.append(" ) and o.voided=0 and   o.person_id=" + patientId);
		strbuf.append("  " + " and  cast(o.obs_datetime as date) between  '"
				+ df.format(startDate) + "' and '" + df.format(endDate) + "'");
		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());

		List<Integer> obsIdFromQuery = query.list();

		for (Integer integer : obsIdFromQuery) {
			labExamsByCategory.add(labObbService.getObs(integer));
		}

		return labExamsByCategory;

	}

	public List<Obs> getLabExamsByExamTypeBetweenTwoDates(Date startDate,
			Date endDate, Integer conceptId) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> labExamsByCategory = new ArrayList<Obs>();
		ObsService labObbService = Context.getObsService();
		SQLQuery query = null;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT o.obs_id FROM obs o where o.concept_id ="
				+ conceptId + "");
		strbuf.append("  " + " and o.obs_datetime between  '"
				+ df.format(startDate) + "' and '" + df.format(endDate) + "'");

		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());

		List<Integer> obsIdFromQuery = query.list();

		for (Integer integer : obsIdFromQuery) {
			labExamsByCategory.add(labObbService.getObs(integer));
		}

		return labExamsByCategory;
	}

	public void exportPatientReportToPDF(HttpServletRequest request,
			HttpServletResponse response,
			Map<ConceptName, List<Object[]>> mappedLabExam, String filename,
			String title, int patientId ) throws DocumentException, IOException {

		Document document = new Document();
		Patient patient = Context.getPatientService().getPatient(patientId);
		// List<PatientBill> patientBills =
		// (List<PatientBill>)request.getAttribute("reportedPatientBillsPrint");

		/*
		 * PatientBill pb = null;
		 * 
		 * pb = Context.getService(BillingService.class).getPatientBill(
		 * Integer.parseInt(request.getParameter("patientBills")));
		 */
		/*
		 * String filename = pb.getBeneficiary().getPatient().getPersonName()
		 * .toString().replace(" ", "_"); filename =
		 * pb.getBeneficiary().getPolicyIdNumber().replace(" ", "_") + "_" +
		 * filename + ".pdf";
		 */
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "report"); // file name

		PdfWriter writer = PdfWriter.getInstance(document, response
				.getOutputStream());
		writer.setBoxSize("art", new Rectangle(0, 0, 2382, 3369));
		writer.setBoxSize("art", PageSize.A4);

		HeaderFooterMgt event = new HeaderFooterMgt();
		writer.setPageEvent(event);

		document.open();
		document.setPageSize(PageSize.A4);
		// document.setPageSize(new Rectangle(0, 0, 2382, 3369));

		document.addAuthor(Context.getAuthenticatedUser().getPersonName()
				.toString());// the name of the author

		FontSelector fontTitle = new FontSelector();
		fontTitle.addFont(new Font(FontFamily.COURIER, 10.0f, Font.ITALIC));

		// Report title
		Chunk chk = new Chunk("Printed on : "
				+ (new SimpleDateFormat("dd-MMM-yyyy").format(new Date())));
		chk.setFont(new Font(FontFamily.COURIER, 10.0f, Font.BOLD));
		Paragraph todayDate = new Paragraph();
		todayDate.setAlignment(Element.ALIGN_RIGHT);
		todayDate.add(chk);
		document.add(todayDate);
		document.add(fontTitle.process("REPUBLIQUE DU RWANDA\n"));
		document.add(fontTitle.process("POLICE NATIONALE\n"));
		document.add(fontTitle.process("KACYIRU POLICE HOSPITAL\n"));
		document.add(fontTitle.process("B.P. 6183 KIGALI\n"));
		document.add(fontTitle.process("Tél : 584897\n"));
		document.add(fontTitle.process("E-mail : medical@police.gov.rw"));
		// End Report title

		document.add(new Paragraph("\n"));
		chk = new Chunk("Laboratory results");
		chk.setFont(new Font(FontFamily.COURIER, 10.0f, Font.BOLD));
		chk.setUnderline(0.2f, -2f);
		Paragraph pa = new Paragraph();
		pa.add(chk);
		pa.setAlignment(Element.ALIGN_CENTER);
		document.add(pa);
		document.add(new Paragraph("\n"));

		document.add(fontTitle.process("Family Name: "
				+ patient.getFamilyName() + "\n"));
		document.add(fontTitle.process("Given name: " + patient.getGivenName()
				+ "\n"));
		document.add(fontTitle.process("Age: " + patient.getAge() + "\n"));
		

		// title row
		FontSelector fontTitleSelector = new FontSelector();
		fontTitleSelector.addFont(new Font(FontFamily.COURIER, 9, Font.ITALIC));
		// Table of identification;
		PdfPTable table = null;
		table = new PdfPTable(2);
		table.setWidthPercentage(100f);

		// tableHeader.addCell(table);

		// document.add(tableHeader);

		document.add(new Paragraph("\n"));

		// Table of lab report items;
		float[] colsWidth = { 6f, 3f, 6f };
		table = new PdfPTable(colsWidth);
		table.setWidthPercentage(100f);
		BaseColor bckGroundTitle = new BaseColor(170, 170, 170);
		BaseColor bckGroundTitl = new BaseColor(Color.yellow);

		// table Header
		PdfPCell cell = new PdfPCell(fontTitleSelector.process("Exam"));

		cell.setBackgroundColor(bckGroundTitle);

		table.addCell(cell);

		cell = new PdfPCell(fontTitleSelector.process("Result"));
		cell.setBackgroundColor(bckGroundTitle);

		table.addCell(cell);

		cell = new PdfPCell(fontTitleSelector.process("Normal Range"));
		cell.setBackgroundColor(bckGroundTitle);
		table.addCell(cell);
		/*
		 * cell = new PdfPCell(fontTitleSelector.process("Date "));
		 * cell.setBackgroundColor(bckGroundTitle); table.addCell(cell);
		 */

		// normal row
		FontSelector fontselector = new FontSelector();
		fontselector.addFont(new Font(FontFamily.COURIER, 8, Font.NORMAL));

		// empty row
		FontSelector fontTotals = new FontSelector();
		fontTotals.addFont(new Font(FontFamily.COURIER, 9, Font.BOLD));

		// ===========================================================
		for (ConceptName cptName : mappedLabExam.keySet()) {

			cell = new PdfPCell(fontTitleSelector.process("" + cptName));
			cell.setBackgroundColor(bckGroundTitl);
			table.addCell(cell);
			cell = new PdfPCell(fontTitleSelector.process(""));
			table.addCell(cell);
			cell = new PdfPCell(fontTitleSelector.process(""));
			table.addCell(cell);

			List<Object[]> labExamHistory = mappedLabExam.get(cptName);
			for (Object[] labExam : labExamHistory) {
				// table Header
				// Object[] labe = listOflabtest.get(i);
				Obs ob = (Obs) labExam[0];
				cell = new PdfPCell(fontTitleSelector.process(""
						+ ob.getConcept().getName()));

				table.addCell(cell);
				if (ob.getConcept().getDatatype().isNumeric()) {
					cell = new PdfPCell(fontTitleSelector.process(""
							+ ob.getValueNumeric()));
					table.addCell(cell);

				}

				if (ob.getConcept().getDatatype().isCoded()) {
					cell = new PdfPCell(fontTitleSelector.process(""
							+ ob.getValueCoded().getName()));
					table.addCell(cell);

				}

				if (ob.getConcept().getDatatype().isText()) {
					cell = new PdfPCell(fontTitleSelector.process(""
							+ ob.getValueText()));
					table.addCell(cell);

				}

				cell = new PdfPCell(fontTitleSelector.process(""
						+ (labExam[1] != null ? labExam[1] : "-")));
				table.addCell(cell);

				fontselector.addFont(new Font(FontFamily.COURIER, 8,
						Font.NORMAL));

				// empty row
				// FontSelector fontTotals = new FontSelector();
				fontTotals.addFont(new Font(FontFamily.COURIER, 9, Font.BOLD));

			}

		}

		cell = new PdfPCell(fontTitleSelector
				.process("Names, Signature et Stamp of Lab Chief\n"
						//+ Context.getAuthenticatedUser().getPersonName()));
						+ Context.getUserService().getUser(140).getPersonName()));
		
		
		
		
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		// ================================================================
		table.addCell(cell);

		document.add(table);

		// Table of signatures;
		table = new PdfPTable(2);
		table.setWidthPercentage(100f);

		cell = new PdfPCell(fontTitleSelector.process(" "));

		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		cell = new PdfPCell(fontTitleSelector
				.process("Names, Signature and  Stamp of Provider\n"
						+ Context.getAuthenticatedUser().getPersonName()));
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		document.add(table);
		document.close();
		
		

		document.close();

	}

	@Override
	public List<Obs> getAllNegtiveLabExams(Date startDate, Date endDate,
			int conceptId) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> labExamsByCategory = new ArrayList<Obs>();
		ObsService labObbService = Context.getObsService();
		SQLQuery query = null;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT o.obs_id FROM obs o where o.concept_id ="
				+ conceptId + "");
		strbuf.append("  "
				+ " and o.value_coded in (664) and  o.obs_datetime between  '"
				+ df.format(startDate) + "' and '" + df.format(endDate) + "'");

		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());

		List<Integer> obsIdFromQuery = query.list();

		for (Integer integer : obsIdFromQuery) {
			labExamsByCategory.add(labObbService.getObs(integer));
		}

		return labExamsByCategory;
	}

	@Override
	public List<Obs> getAllPositiveLabExams(Date startDate, Date endDate,
			int conceptId) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Obs> labExamsByCategory = new ArrayList<Obs>();
		ObsService labObbService = Context.getObsService();
		SQLQuery query = null;
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT o.obs_id FROM obs o where o.concept_id ="
				+ conceptId + "");
		strbuf
				.append("  "
						+ " and o.value_coded not in(664) and o.obs_datetime between  '"
						+ df.format(startDate) + "' and '" + df.format(endDate)
						+ "'");

		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());

		List<Integer> obsIdFromQuery = query.list();

		for (Integer integer : obsIdFromQuery) {
			labExamsByCategory.add(labObbService.getObs(integer));
		}

		return labExamsByCategory;
	}

	@Override
	public List<Order> getLabOrders(int patientId, Collection<Integer> cptIds,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub

		OrderService orderServc = Context.getOrderService();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Order> labOrdeByCategory = new ArrayList<Order>();
		ObsService labObbService = Context.getObsService();
		SQLQuery query = null;
		StringBuffer strbuf = new StringBuffer();
		strbuf
				.append("SELECT o.order_id FROM orders o where o.concept_id in ( ");

		int i = 1;
		for (Integer onecptId : cptIds) {
			if (i < cptIds.size()) {
				strbuf.append(" " + onecptId + ",");
			}
			if (i == cptIds.size()) {
				strbuf.append(" " + onecptId);

			}
			i = i + 1;

		}
		strbuf.append(" ) and  o.patient_id=" + patientId);
		strbuf.append("  and  o.voided= 0 ");
		strbuf.append("  " + " and  cast(o.start_date as date) =  '"
				+ df.format(startDate) + "'");
		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());
		System.out.println("ZZZZlaborder query" + query.toString());
		List<Integer> oderIdsIdFromQuery = query.list();

		for (Integer orderId : oderIdsIdFromQuery) {
			labOrdeByCategory.add(orderServc.getOrder(orderId));
		}

		System.out.println(">>>>laborders size" + labOrdeByCategory.size());

		return labOrdeByCategory;
	}

	@Override
	public List<Order> getLabOrdersBetweentwoDate(int patientId,
			Date startDate, Date enddate) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		SQLQuery query = null;
		List<Order> ordersList = new ArrayList<Order>();
		OrderService orderServic = Context.getOrderService();
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT  o.order_id  FROM orders o where o.patient_Id ="
				+ patientId + "");
		strbuf.append("  " + " and  cast(o.start_date as date) between  '"
				+ df.format(startDate) + "' and '" + df.format(enddate) + "'");

		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());
		System.out.println(">>>>>order query" + query.toString());

		List<Integer> orderIdsFromQuery = query.list();
		for (Integer orderId : orderIdsFromQuery) {
			ordersList.add(orderServic.getOrder(orderId));

		}
		System.out.println("OrderListBetween two dates" + ordersList);

		return ordersList;

	}

	@Override
	public List<Order> getLabOrdersBetweentwoDate(Date startDate, Date enddate) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		SQLQuery query = null;
		List<Order> ordersList = new ArrayList<Order>();
		OrderService orderServic = Context.getOrderService();
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT  o.order_id  FROM orders o where " + "");
		strbuf.append("  " + "  cast(o.start_date as date) between  '"
				+ df.format(startDate) + "' and '" + df.format(enddate) + "'");

		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());
		System.out.println(">>>>>order query" + query.toString());

		List<Integer> orderIdsFromQuery = query.list();
		for (Integer orderId : orderIdsFromQuery) {
			ordersList.add(orderServic.getOrder(orderId));

		}
		System.out.println("OrderListBetween two dates" + ordersList);

		return ordersList;
	}

	@Override
	public List<Order> getLabOrdersByLabCode(String labCode) {
		// TODO Auto-generated method stub

		SQLQuery query = null;
		List<Order> ordersList = new ArrayList<Order>();
		OrderService orderServic = Context.getOrderService();
		StringBuffer strbuf = new StringBuffer();
		strbuf
				.append("SELECT  o.order_id  FROM orders o where  o.voided = 0 and  o.accession_number ='"
						+ labCode + "'");

		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());
		System.out.println(">>>>>order query" + query.toString());

		List<Integer> orderIdsFromQuery = query.list();
		for (Integer orderId : orderIdsFromQuery) {
			ordersList.add(Context.getOrderService().getOrder(orderId));

		}
		return ordersList;
	}

	@Override
	public List<Obs> getAllpatientObs(int patientId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFoundLabCode(String labCode) {
		// TODO Auto-generated method stub

		boolean isFound = false;
		SQLQuery query = null;

		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT DISTINCT accession_number FROM orders o");
		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());

		List<String> accessionNumbers = query.list();
		if (accessionNumbers.contains(labCode)) {
			isFound = true;
		}
		return isFound;
	}

	/**
	 * @see org.openmrs.module.laboratorymodule.db.LaboratoryDAO#getPatientLabordersBetweendates(int,
	 *      java.util.Date, java.util.Date)
	 */
	@Override
	public Collection<Order> getPatientLabordersBetweendates(int patientId,
			Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		SQLQuery query = null;
		Collection<Order> ordersList = new ArrayList<Order>();
		OrderService orderServic = Context.getOrderService();
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("SELECT  o.order_id  FROM orders o where o.patient_Id ="
				+ patientId + "");
		strbuf.append("  " + " and   cast(o.start_date as date) between  '"
				+ df.format(startDate) + "' and '" + df.format(endDate) + "'");

		query = sessionFactory.getCurrentSession().createSQLQuery(
				strbuf.toString());

		List<Integer> orderIdsFromQuery = query.list();
		for (Integer orderId : orderIdsFromQuery) {
			ordersList.add(orderServic.getOrder(orderId));

		}

		return ordersList;
	}

	/**
	 * @see org.openmrs.module.laboratorymodule.db.LaboratoryDAO#getObsByLabCode(java.lang.String)
	 */
	@Override
	public List<Obs> getObsByLabCode(String labCode) {
		Criteria query = sessionFactory.getCurrentSession().createCriteria(
				Obs.class).add(Restrictions.eq("accessionNumber", labCode))
				.add(Restrictions.eq("voided", false));
		return query.list();
	}

	/**
	 * @see org.openmrs.module.laboratorymodule.db.LaboratoryDAO#getObsByLabOrder(org.openmrs.Order,
	 *      org.openmrs.Concept)
	 */
	@Override
	public List<Obs> getObsByLabOrder(Order order, Concept cpt) {
		log
				.info(">>>>>>>>>>>log info> within the existingorderObs>>>has concept>>"
						+ order.getConcept().getConceptId());
		Criteria query = sessionFactory.getCurrentSession().createCriteria(
				Obs.class).add(Restrictions.eq("order", order)).add(
				Restrictions.eq("voided", false));

		if (cpt != null)
			query.add(Restrictions.eq("concept", cpt));

		return query.list();
	}

	@Override
	public Collection<Encounter> getPatientEncountersByDate(int patientId,
			Date startDate, EncounterType encounterType) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Encounter labEncounter = null;
		Collection<Encounter> encountersByDate = new ArrayList<Encounter>();
		String formattedDate = df.format(startDate);
		Criteria query = sessionFactory.getCurrentSession().createCriteria(
				Encounter.class).add(Restrictions.eq("patientId", patientId))
				.add(Restrictions.eq("encounterType", encounterType));
		Collection<Encounter> encounterslist = query.list();
	
		if (encounterslist.size() > 0) {
			for (Encounter encounter : encounterslist) {
				String encounterDateStr = df.format(encounter.getEncounterDatetime());
				if (encounterDateStr.equals(df.format(startDate))) {
					encountersByDate.add(encounter);
				}
			}

		}
	
		return encountersByDate;
	}
}
