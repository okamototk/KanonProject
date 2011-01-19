/*
The contents of this file are subject to the Common Public Attribution License 
Version 1.0 (the "License"); you may not use this file except in compliance with 
the License. You may obtain a copy of the License at 
http://www.projity.com/license . The License is based on the Mozilla Public 
License Version 1.1 but Sections 14 and 15 have been added to cover use of 
software over a computer network and provide for limited attribution for the 
Original Developer. In addition, Exhibit A has been modified to be consistent 
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis, 
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the 
specific language governing rights and limitations under the License. The 
Original Code is OpenProj. The Original Developer is the Initial Developer and 
is Projity, Inc. All portions of the code written by Projity are Copyright (c) 
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the 
Projity End-User License Agreeement (the Projity License), in which case the 
provisions of the Projity License are applicable instead of those above. If you 
wish to allow use of your version of this file only under the terms of the 
Projity License and not to allow others to use your version of this file under 
the CPAL, indicate your decision by deleting the provisions above and replace 
them with the notice and other provisions required by the Projity  License. If 
you do not delete the provisions above, a recipient may use your version of this 
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices 
in Exhibits A and B of the license at http://www.projity.com/license. You should 
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright © 2006, 2007 
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj, 
an open source solution from Projity. Attribution URL: http://www.projity.com 
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with 
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined 
in the CPAL as a work which combines Covered Code or portions thereof with code 
not governed by the terms of the CPAL. However, in addition to the other notice 
obligations, all copies of the Covered Code in Executable and Source Code form 
distributed must, as a form of attribution of the original author, include on 
each user interface screen the "OpenProj" logo visible to all users.  The 
OpenProj logo should be located horizontally aligned with the menu bar and left 
justified on the top left of the screen adjacent to the File menu.  The logo 
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it 
must direct them back to http://www.projity.com.  
*/
package com.projity.server.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringEscapeUtils;

import net.sf.mpxj.AccrueType;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.FieldContainer;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.Priority;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.mspdi.DatatypeConverter;

import com.projity.configuration.CircularDependencyException;
import com.projity.configuration.Configuration;
import com.projity.datatype.Duration;
import com.projity.datatype.Rate;
import com.projity.exchange.Context;
import com.projity.exchange.ImportedCalendarService;
import com.projity.field.CustomFields;
import com.projity.grouping.core.VoidNodeImpl;
import com.projity.options.CalendarOption;
import com.projity.pm.assignment.Assignment;
import com.projity.pm.calendar.CalendarService;
import com.projity.pm.calendar.WorkCalendar;
import com.projity.pm.calendar.WorkDay;
import com.projity.pm.calendar.WorkRange;
import com.projity.pm.calendar.WorkingCalendar;
import com.projity.pm.calendar.WorkingHours;
import com.projity.pm.resource.ResourceImpl;
import com.projity.pm.task.NormalTask;
import com.projity.pm.task.Project;
import com.projity.strings.Messages;
import com.projity.util.DateTime;
import com.projity.util.MathUtils;
/**
 *
 */
public class NewMPXConverter {
//
//	public static int nameFieldWidth = Configuration.getFieldFromId("Field.name").getTextWidth();
//	
//	public static void toProjityProject(ProjectHeader projectHeader,Project project, Context context) {
//		WorkCalendar cal = null; // = CalendarService.getInstance().findDocumentCalendar(projectHeader.getCalendarName(),project);
//		
//		String calName = projectHeader.getCalendarName();
//		if (cal == null) {
//			ProjectCalendar mpxCal = ImportedCalendarService.getInstance().findImportedMPXCalendar(projectHeader.getCalendarName());
//			cal = ImportedCalendarService.getInstance().findImportedCalendar(mpxCal);
//		}
//		try {
//			if (cal != null)
//				project.setBaseCalendar(cal);
//		} catch (CircularDependencyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		String name =Messages.getString("Text.Untitled"); //2007 name treatment here and below
//		if (context.isXml()) {
//			name = projectHeader.getProjectTitle(); // not always present
//			if (name == null || name.trim().length() == 0) { // if no title, then use file name
//				name = projectHeader.getName();
//				if (name != null && name.endsWith(".xml"))
//					name = name.substring(0,name.lastIndexOf(".xml"));
//			}
//		} else {
//			name = projectHeader.getName();
//		}
//		if (name == null || name.length() == 0)
//			name = Messages.getString("Text.Untitled");
//		project.setName(truncName(name));
//		
//		project.setManager(projectHeader.getManager());
//		project.setNotes(projectHeader.getComments());
//		final Project proj = project;
//// This code was moved after the end of import. Caused major bug otherwise		
////		if (projectHeader.getScheduleFrom() == ScheduleFrom.FINISH)
////			project.setForward(false);
//			
//		Date d = projectHeader.getStatusDate();
//		if (d != null)
//			project.setStatusDate(DateTime.gmt(d));
//		
//	}
//	
//	public static void toProjityOptions(ProjectHeader projectHeader, Context context) {
//		
//		CalendarOption calendarOption = CalendarOption.getInstance();
//		calendarOption.setHoursPerDay(projectHeader.getMinutesPerDay().doubleValue()/60.0D);
//		calendarOption.setHoursPerWeek(projectHeader.getMinutesPerWeek().doubleValue()/60.0D);
//		
//		calendarOption.setDaysPerMonth(projectHeader.getDaysPerMonth().doubleValue());
//		
//		Date d = projectHeader.getDefaultStartTime();
//		if (d != null) {
//			GregorianCalendar defaultStart = new GregorianCalendar();
//			defaultStart.setTime(d);
//			calendarOption.setDefaultStartTime(defaultStart);
//		}
//		Date e = projectHeader.getDefaultEndTime();
//		if (e != null) {
//			GregorianCalendar defaultEnd = new GregorianCalendar();
//			defaultEnd.setTime(e);
//			calendarOption.setDefaultEndTime(defaultEnd);
//		}
//		
//	}
//	public static void toMPXOptions(ProjectHeader projectHeader) {
//		
//		CalendarOption calendarOption = CalendarOption.getInstance();
////		projectHeader.setDefaultHoursInDay(new Float(calendarOption.getHoursPerDay()));
//		projectHeader.setMinutesPerDay(new Integer((int) (60 * calendarOption.getHoursPerDay())));
////		projectHeader.setDefaultHoursInWeek(new Float(calendarOption.getHoursPerWeek()));
//		projectHeader.setMinutesPerWeek(new Integer((int) (60 * calendarOption.getHoursPerWeek())));
//
//		projectHeader.setDaysPerMonth(new Integer((int) Math.round(calendarOption.getDaysPerMonth())));
//		
//		projectHeader.setDefaultStartTime(calendarOption.getDefaultStartTime().getTime());
//		projectHeader.setDefaultEndTime(calendarOption.getDefaultEndTime().getTime());
//	}
//	
//	public static void toMPXProject(Project project,ProjectHeader projectHeader) {
//		WorkCalendar baseCalendar=project.getBaseCalendar();
//		projectHeader.setCalendarName(baseCalendar.getName()); // use unique id for name - this is a hack
//		projectHeader.setName(project.getName());
//		projectHeader.setProjectTitle(project.getName()); //TODO separate title and name
//		projectHeader.setComments(project.getNotes());
//		projectHeader.setManager(project.getManager());
//		projectHeader.setComments(removeInvalidChars(project.getNotes()));
//		projectHeader.setStartDate(DateTime.fromGmt(new Date(project.getStartDate())));
//		projectHeader.setFinishDate(DateTime.fromGmt(new Date(project.getFinishDate())));
//		projectHeader.setDefaultStartTime(CalendarOption.getInstance().getDefaultStartTime().getTime());
//		projectHeader.setDefaultEndTime(CalendarOption.getInstance().getDefaultEndTime().getTime());
//
//	}
//
//	
//	public static void toProjityCalendar(ProjectCalendar mpx, WorkingCalendar workCalendar, Context context) {
//		if (mpx.getName() != null)
//			workCalendar.setName(mpx.getName());
//		workCalendar.setId(mpx.getUniqueID());
////			if (baseCalendarName.equals(ProjectCalendar.DEFAULT_BASE_CALENDAR_NAME)) {
//		ProjectCalendar baseCalendar = null;
//		WorkingCalendar standardCal = CalendarService.getInstance().getStandardInstance();
//		if (!mpx.isBaseCalendar()) {
//			baseCalendar = mpx.getBaseCalendar();
//			if (baseCalendar == null) {
////				System.out.println("imported calendar " + mpx.getName() + " base cal name " + mpx.getBaseCalendarName() + " not found");
//				baseCalendar = context.getDefaultMPXCalendar();
//				mpx.setBaseCalendar(baseCalendar);
//			}	
//			WorkCalendar base = ImportedCalendarService.getInstance().findImportedCalendar(baseCalendar);
//			try {
//				if (base == null) {
//					System.out.println("null base calendar");
//					base = standardCal;
//				}
//				workCalendar.setBaseCalendar(base);
//			} catch (CircularDependencyException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		WorkDay weekDay;
//		Day d;
//		for (int i = 1; i <= 7; i++) {// MPX days go from SUNDAY=1 to SATURDAY=7
//			d = Day.getInstance(i);
//			ProjectCalendarHours mpxDay = mpx.getCalendarHours(d);
//			if (mpxDay == null) { 
//				weekDay = null; // means use default for day
//				if (mpx.isBaseCalendar()) { // in base calendars, it means non working day
//					if (!mpx.isWorkingDay(d)) { // if base calendar is working, then this is an exceptoin
//						if (standardCal.getWeekDay(i-1).isWorking())
//							weekDay = WorkDay.getNonWorkingDay();
//					}
//				} else {
//					if (!mpx.isWorkingDay(d)) { // if base calendar is working, then this is an exceptoin
//						if (baseCalendar != null && baseCalendar.isWorkingDay(d))
//							weekDay = WorkDay.getNonWorkingDay();
//					}
//				}
//			} else {
//				weekDay = new WorkDay();
//				toProjityCalendarDay(mpxDay,weekDay);
//				if (mpx.isBaseCalendar()) {
//					if (standardCal.getWeekDay(i-1).hasSameWorkHours(weekDay))
//						weekDay = null;
//				}
//			}
//			workCalendar.setWeekDay(i-1,weekDay);
//		}
//		
//		List mpxExceptions = mpx.getCalendarExceptions();
//		Iterator iter = mpxExceptions.iterator();
//		while (iter.hasNext()) {
//			ProjectCalendarException exception = (ProjectCalendarException) iter.next();
//			long start = DateTime.gmt(exception.getFromDate());
//			long end = DateTime.gmt(exception.getToDate());
//			for (long time = start; time < end; time = DateTime.nextDay(time)) {
//				WorkDay day = new WorkDay(time,time); // projity does not do ranges - need an entry per date
//				toProjityExceptionDay(exception,day);
//				workCalendar.addOrReplaceException(day);
//			}
//		}
//		workCalendar.removeEmptyDays(); // fixes problem with days with no working hours
//	}
//	public static void toMpxCalendar(WorkingCalendar workCalendar,ProjectCalendar mpx) {
//		mpx.setName(workCalendar.getName());
////		mpx.setUniqueID((int) workCalendar.getId()); // TODO watch out for int overrun
//
//		WorkingCalendar wc = workCalendar;
//		if (workCalendar.isBaseCalendar())
//			wc = (WorkingCalendar) workCalendar.getBaseCalendar();
//		for (int i = 0; i < 7; i++) {// MPX days go from SUNDAY=1 to SATURDAY=7
//			WorkDay day= workCalendar.isBaseCalendar() ? workCalendar.getDerivedWeekDay(i) : workCalendar.getWeekDay(i);
//			ProjectCalendarHours mpxDay = null;
//			Day d = Day.getInstance(i+1);
//			if (day == null) {
//				mpx.setWorkingDay(d,ProjectCalendar.DEFAULT);
//			} else {
//				mpx.setWorkingDay(d,day.isWorking());
//				if (day.isWorking()) {
//					mpxDay = mpx.addCalendarHours(Day.getInstance(i+1));
//					toMpxCalendarDay(day,mpxDay);
//				}
//			}
//		}
//		
//		WorkDay[] workDays=workCalendar.getExceptionDays();
//		if (workDays!=null)
//			for (int i=0;i<workDays.length;i++){
//				if (workDays[i]==null||workDays[i].getStart()==0L||workDays[i].getStart()==Long.MAX_VALUE)
//					continue;
//				ProjectCalendarException exception=mpx.addCalendarException();
//				Date start = new Date(workDays[i].getStart());
//				exception.setFromDate(start);
//				GregorianCalendar cal = DateTime.calendarInstance();
//				// days go from 00:00 to 23:59
//				cal.setTime(start);
//				cal.set(Calendar.HOUR,23);
//				cal.set(Calendar.MINUTE,59);
//				exception.setToDate(DateTime.fromGmt(cal.getTime()));
//				toMpxExceptionDay(workDays[i],exception);
//				exception.setWorking(workDays[i].isWorking());
//			}
//		WorkCalendar baseCalendar=workCalendar.getBaseCalendar();
//		if (baseCalendar!=null){
//			mpx.setBaseCalendar(ImportedCalendarService.getInstance().findExportedCalendar(baseCalendar));
//		}
//		
//
//		//mpx.setUniqueID((int)workCalendar.getUniqueId());
//	}
//	
//	// Note that there is no common interface or base class between ProjectCalendarHours and ProjectCalendarException, so this code is repeated for each	
//	public static  void toProjityCalendarDay(ProjectCalendarHours mpxDay, WorkDay day) {
//		WorkingHours workingHours = new WorkingHours();
//		if (mpxDay == null) {
//			return;
//		}
//		DateRange mpxDateRange=mpxDay.getDateRange(0);
//		if (mpxDateRange!=null)
//			workingHours.setInterval(0,mpxDateRange.getStartDate(), mpxDateRange.getEndDate());
//		mpxDateRange=mpxDay.getDateRange(1);
//		if (mpxDateRange!=null) 
//			workingHours.setInterval(1,mpxDateRange.getStartDate(), mpxDateRange.getEndDate());
//		mpxDateRange=mpxDay.getDateRange(2);
//		if (mpxDateRange!=null)
//			workingHours.setInterval(2,mpxDateRange.getStartDate(), mpxDateRange.getEndDate());
//		day.setWorkingHours(workingHours);
//	}
//	public static  void toMpxCalendarDay(WorkDay day,ProjectCalendarHours mpxDay) {
//		if  (day==null)
//			return;
//		WorkingHours workingHours=day.getWorkingHours();
//		WorkRange range;
//		
//		for (int i = 0; i <3; i++) {
//			range=workingHours.getInterval(i);
//			if (range!=null)
//				mpxDay.addDateRange(new DateRange(DateTime.fromGmt(range.getNormalizedStartTime()),DateTime.fromGmt(range.getNormalizedEndTime())));
//		}		
//	}
//	
//	// Note that there is no common interface or base class between ProjectCalendarHours and ProjectCalendarException, so this code is repeated for each
//	public static  void toProjityExceptionDay(ProjectCalendarException mpxDay, WorkDay day) {
//		WorkingHours workingHours = new WorkingHours();
//		if (mpxDay == null)
//			return;
//		
//		workingHours.setInterval(0,mpxDay.getFromTime1(), mpxDay.getToTime1());
//		workingHours.setInterval(1,mpxDay.getFromTime2(), mpxDay.getToTime2());
//		workingHours.setInterval(2,mpxDay.getFromTime3(), mpxDay.getToTime3());
//		day.setWorkingHours(workingHours);
//	}
//	public static  void toMpxExceptionDay(WorkDay day,ProjectCalendarException mpxDay) {
//		if  (day==null)
//			return;
//		WorkingHours workingHours=day.getWorkingHours();
//		WorkRange range;
//		
//		range=workingHours.getInterval(0);
//		if (range!=null){
//			mpxDay.setFromTime1(DateTime.fromGmt(range.getNormalizedStartTime()));
//			mpxDay.setToTime1(DateTime.fromGmt(range.getNormalizedEndTime()));
//		}
//		
//		range=workingHours.getInterval(1);
//		if (range!=null){
//			mpxDay.setFromTime2(DateTime.fromGmt(range.getNormalizedStartTime()));
//			mpxDay.setToTime2(DateTime.fromGmt(range.getNormalizedEndTime()));
//		}
//		
//		range=workingHours.getInterval(2);
//		if (range!=null){
//			mpxDay.setFromTime3(DateTime.fromGmt(range.getNormalizedStartTime()));
//			mpxDay.setToTime3(DateTime.fromGmt(range.getNormalizedEndTime()));
//		}
//	}
//	/**
//	 * Convert an mpx resource into a projity resource
//	 * @param mpxResource
//	 * @param projityResource
//	 * @param context 
//	 */
//	public static void toProjityResource(Resource mpxResource,
//			ResourceImpl projityResource, Context context) {
//		projityResource.setName(truncName(mpxResource.getName()));
//		projityResource.setNotes(mpxResource.getNotes());
//		projityResource.setAccrueAt(mpxResource.getAccrueAt().getValue());
//		projityResource
//				.setCostPerUse(mpxResource.getCostPerUse().doubleValue());
//		projityResource.setStandardRate(toProjityRate(mpxResource
//				.getStandardRate()));
//		projityResource.setOvertimeRate(toProjityRate(mpxResource
//				.getOvertimeRate()));
//		
//
//		projityResource.setGroup(mpxResource.getGroup());
//		projityResource.setInitials(mpxResource.getInitials());
//		projityResource.setEmailAddress(mpxResource.getEmailAddress());
//		projityResource.setId(mpxResource.getID());
//		projityResource.setExternalId(mpxResource.getUniqueID());
//
//		ProjectCalendar cal = mpxResource.getResourceCalendar();
//		WorkingCalendar workCalendar = WorkingCalendar.getInstance();
//		workCalendar.setName(projityResource.getName());
//		if (cal != null)
//			toProjityCalendar(cal,workCalendar,context);
//		else
//			try {
//				workCalendar.setBaseCalendar(CalendarService.getInstance().getDefaultInstance());
//			} catch (CircularDependencyException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		projityResource.setWorkCalendar(workCalendar);
//	
//		toProjityCustomFields(projityResource.getCustomFields(),mpxResource, CustomFieldsMapper.getInstance().resourceMaps,context);
//		
//	}
//	
//	public static void toMPXResource(ResourceImpl projityResource,Resource mpxResource) {
//		mpxResource.setName(removeInvalidChars(projityResource.getName()));
//		mpxResource.setNotes(removeInvalidChars(projityResource.getNotes()));
//		mpxResource.setAccrueAt(AccrueType.getInstance(projityResource.getAccrueAt()));
//		mpxResource
//				.setCostPerUse(new Double(projityResource.getCostPerUse()));
//		mpxResource.setStandardRate(toMPXRate(projityResource
//				.getStandardRate()));
//		mpxResource.setOvertimeRate(toMPXRate(projityResource
//				.getOvertimeRate()));
//		//TODO set calendar
//		mpxResource.setGroup(projityResource.getGroup());
//		mpxResource.setEmailAddress(projityResource.getEmailAddress());
//		
//		mpxResource.setInitials(projityResource.getInitials());
//		mpxResource.setID((int)projityResource.getId());
//		long uid = projityResource.getExternalId(); // try using external id of one set
//		if (uid <= 0)
//			uid = projityResource.getId();
//		mpxResource.setUniqueID((int)uid); // note using id and not unique id
//
//		WorkingCalendar projityCalendar = (WorkingCalendar)projityResource.getWorkCalendar();
//		if (projityCalendar != null) { // there should be a calendar, except for the unassigned instance
//			ProjectCalendar mpxCalendar = null;
//			try {
//				mpxCalendar = mpxResource.addResourceCalendar();
//			} catch (MPXJException e) {
//				e.printStackTrace();
//				return;
//			}
//				toMpxCalendar(projityCalendar,mpxCalendar);
//		}
//		//TODO The follwing only work because the UID of the resource is the id and not the unique id. A big unique id value  overflows the UID element of the custom field.  It works
//		// here because the id is small
//		toMpxCustomFields(projityResource.getCustomFields(),mpxResource, CustomFieldsMapper.getInstance().resourceMaps);
//		
//		
//	}
//
//	public static void toProjityCustomFields(CustomFields projityFields,FieldContainer mpx, CustomFieldsMapper.Maps maps, Context context) {
//		for (int i = 0; i < maps.costMap.length; i++) {
//			Number c = (Number) mpx.getCurrentValue(maps.costMap[i]);
//			if (c != null)
//				projityFields.setCustomCost(i,c.doubleValue());
//		}
//		for (int i = 0; i < maps.dateMap.length; i++) {
//			Date d = (Date) mpx.getCurrentValue(maps.dateMap[i]);
//			if (d != null)
//				projityFields.setCustomDate(i,d.getTime());
//		}
//		for (int i = 0; i < maps.durationMap.length; i++) {
//			net.sf.mpxj.Duration d = (net.sf.mpxj.Duration) mpx.getCurrentValue(maps.durationMap[i]);
//			if (d != null)
//				projityFields.setCustomDuration(i,toProjityDuration(d,context));
//		}
//		for (int i = 0; i < maps.finishMap.length; i++) {
//			Date d = (Date) mpx.getCurrentValue(maps.finishMap[i]);
//			if (d != null)
//				projityFields.setCustomFinish(i,d.getTime());
//		}
//		for (int i = 0; i < maps.flagMap.length; i++) {
//			Boolean b = (Boolean) mpx.getCurrentValue(maps.flagMap[i]);
//			if (b != null)
//				projityFields.setCustomFlag(i,b.booleanValue());
//		}
//		for (int i = 0; i < maps.numberMap.length; i++) {
//			Number n = (Number) mpx.getCurrentValue(maps.numberMap[i]);
//			if (n != null)
//				projityFields.setCustomNumber(i,n.doubleValue());
//		}
//		for (int i = 0; i < maps.startMap.length; i++) {
//			Date d = (Date) mpx.getCurrentValue(maps.startMap[i]);
//			if (d != null)
//				projityFields.setCustomStart(i,d.getTime());
//		}
//		for (int i = 0; i < maps.textMap.length; i++) {
//			String s = (String) mpx.getCurrentValue(maps.textMap[i]);
//			if (s != null)
//				projityFields.setCustomText(i,s);
//		}
//	}
//	
//	public static void toMpxCustomFields(CustomFields projityFields,FieldContainer mpx, CustomFieldsMapper.Maps maps) {
//		for (int i = 0; i < maps.costMap.length; i++) {
//			double cost = projityFields.getCustomCost(i);
//			if (cost != 0.0D)
//				mpx.set(maps.costMap[i],new Double(cost));
//		}
//		for (int i = 0; i < maps.dateMap.length; i++) {
//			long d = projityFields.getCustomDate(i);
//			if (d != 0)
//				mpx.set(maps.dateMap[i],new Date(d));
//		}
//		for (int i = 0; i < maps.durationMap.length; i++) {
//			long d = projityFields.getCustomDuration(i);
//			if (Duration.millis(d) != 0)
//				mpx.set(maps.durationMap[i],toMPXDuration(d));
//		}
//		for (int i = 0; i < maps.finishMap.length; i++) {
//			long d = projityFields.getCustomFinish(i);
//			if (d != 0)
//				mpx.set(maps.finishMap[i],new Date(d));
//		}
//		for (int i = 0; i < maps.flagMap.length; i++) {
//			boolean b = projityFields.getCustomFlag(i);
//			if (b == true)
//				mpx.set(maps.flagMap[i],Boolean.TRUE);
//		}
//		for (int i = 0; i < maps.numberMap.length; i++) {
//			double n = projityFields.getCustomNumber(i);
//			if (n != 0.0D)
//				mpx.set(maps.numberMap[i],new Double(n));
//		}
//		for (int i = 0; i < maps.startMap.length; i++) {
//			long d = projityFields.getCustomStart(i);
//			if (d != 0)
//				mpx.set(maps.startMap[i],new Date(d));
//		}
//		for (int i = 0; i < maps.textMap.length; i++) {
//			String s = projityFields.getCustomText(i);
//			if (s != null) {
//				mpx.set(maps.textMap[i],MPXConverter.removeInvalidChars(s));
//			}
//		}
//	}
//
//	public static void toMPXAssignment(Assignment assignment, ResourceAssignment mpxAssignment) {
////		long work = assignment.isDefault() ? 0 : assignment.getWork(null); // microsoft considers no work on default assignments
//		long work = assignment.getWork(null); // microsoft considers no work on default assignments
//    	mpxAssignment.setWork(MPXConverter.toMPXDuration(work));
//    	mpxAssignment.setUnits(MathUtils.roundToDecentPrecision(assignment.getUnits()*100.0D));
//    	mpxAssignment.setRemainingWork(MPXConverter.toMPXDuration(assignment.getRemainingWork())); //2007
//    	long delay = Duration.millis(assignment.getDelay());
//    	if (delay != 0) {
//    		// mpxj uses default options when dealing with assignment delay
//    		CalendarOption oldOptions = CalendarOption.getInstance();
//    		CalendarOption.setInstance(CalendarOption.getDefaultInstance());
//
//        	mpxAssignment.setDelay(MPXConverter.toMPXDuration(assignment.getDelay()));
//            CalendarOption.setInstance(oldOptions);
//    	}
//
//    	long levelingDelay = Duration.millis(assignment.getLevelingDelay());
//    	if (levelingDelay != 0) {
//    		// mpxj uses default options when dealing with assignment delay
//    		CalendarOption oldOptions = CalendarOption.getInstance();
//    		CalendarOption.setInstance(CalendarOption.getDefaultInstance());
//
//        	mpxAssignment.setDelay(MPXConverter.toMPXDuration(assignment.getLevelingDelay()));
//            CalendarOption.setInstance(oldOptions);
//    	}
//    	
//    	
//    	mpxAssignment.setWorkContour(WorkContour.getInstance(assignment.getWorkContourType()));
//
//    	
//	}
//
//	/**
//	 * Convert an mpx task into a projity task
//	 * @param mpxTask
//	 * @param projityTask
//	 */	
//	public static  void toProjityTask(Task mpxTask, NormalTask projityTask, Context context) {
//		//TODO what about resources that are unassigned, do they have a fictive task associated?
//		
//		projityTask.setName(truncName(mpxTask.getName()));
//		if (mpxTask.getWBS() != null)
//			projityTask.setWbs(mpxTask.getWBS());
//		//projityTask.setUniqueId(mpxTask.getUniqueIDValue());
//		
//		projityTask.setNotes(mpxTask.getNotes());
//		projityTask.getCurrentSchedule().setStart(DateTime.gmt(mpxTask.getStart())); // start needs to be correct for assignment import
//		projityTask.getCurrentSchedule().setFinish(DateTime.gmt(mpxTask.getFinish())); // finish needs to be correct for assignment import
//		projityTask.setId(mpxTask.getID());
//		projityTask.setCreated(toNormalDate(mpxTask.getCreateDate()));
//		projityTask.setDuration(toProjityDuration(mpxTask.getDuration(),context)); // set duration without controls
//		projityTask.setEstimated(mpxTask.getEstimated());
//		if (mpxTask.getDeadline() != null)
//			projityTask.setDeadline(DateTime.gmt(mpxTask.getDeadline()));
//
//		Priority priority = mpxTask.getPriority();
//		if (priority != null)
//			projityTask.setPriority(mpxTask.getPriority().getValue());
//		Number fc = mpxTask.getFixedCost();
//		if (fc != null)	
//			projityTask.setFixedCost(fc.doubleValue());
//		Date constraintDate = DateTime.gmtDate(mpxTask.getConstraintDate());
//		ConstraintType ct = mpxTask.getConstraintType();
//		if (ct != null)
//			projityTask.setScheduleConstraint(ct.getValue(),constraintDate == null ? 0 : constraintDate.getTime());
//
//		ProjectCalendar mpxCalendar = mpxTask.getCalendar();
//		if (mpxCalendar != null) {
//			WorkCalendar cal = ImportedCalendarService.getInstance().findImportedCalendar(mpxCalendar);
//			if (cal == null)
//				System.out.println("Error finding imported calendar " + mpxCalendar.getName());
//			else
//				projityTask.setWorkCalendar(cal);
//		}
//		
////		System.out.println("reading %" + mpxTask.getPercentageComplete().doubleValue());
////		projityTask.setPercentComplete(mpxTask.getPercentageComplete().doubleValue());
//		
//		//use stop and not percent complete because of rounding issues - this is a little odd, but true. setting stop in assignment can set % complete
//		if (mpxTask.getStop() != null)
//			projityTask.setStop(DateTime.gmt(mpxTask.getStop()));
//		
//		
//		projityTask.setLevelingDelay(toProjityDuration(mpxTask.getLevelingDelay(),context));
////		Date bs = toNormalDate(mpxTask.getBaselineStart());
//		projityTask.setEffortDriven(mpxTask.getEffortDriven());
//		if (mpxTask.getType() != null)
//			projityTask.setSchedulingType(mpxTask.getType().getValue());
//		
//		Number fixed = mpxTask.getFixedCost();
//		if (fixed != null)
//			projityTask.setFixedCost(fixed.doubleValue());
//		projityTask.setFixedCostAccrual(mpxTask.getFixedCostAccrual().getValue());
//		
//		toProjityCustomFields(projityTask.getCustomFields(),mpxTask, CustomFieldsMapper.getInstance().taskMaps,context);
//	}
//	
//	public static String removeInvalidChars(String in) { // had case of user with newlines in task names
//		if (in == null)
//			return null;
//		StringBuffer inBuf = new StringBuffer(in);
//		for (int i = 0; i <inBuf.length(); i++) {
//			char c = inBuf.charAt(i);
//			if (c == '\r' || c == '\n' || c == '\t') // using escape chars of the form &#x0000; is not good - they show up in MSP literally. MSP doesn't seem to support newlines anyway
//				inBuf.setCharAt(i,' ');
//		}
//		return inBuf.toString();
//		
//	}
//	public static  void toMPXTask(NormalTask projityTask, Task mpxTask) {
//		mpxTask.setName(removeInvalidChars(projityTask.getName()));
//		if (projityTask.getWbs() != null)
//			mpxTask.setWBS(removeInvalidChars(projityTask.getWbs()));
//		mpxTask.setNotes(removeInvalidChars(projityTask.getNotes()));
//		mpxTask.setID((int)projityTask.getId());
//		mpxTask.setUniqueID((int)projityTask.getId()); // note using id for unique id
//		mpxTask.setCreateDate(projityTask.getCreated());
//		mpxTask.setDuration(toMPXDuration(projityTask.getDuration())); // set duration without controls
//		mpxTask.setStart(DateTime.fromGmt(new Date(projityTask.getStart())));
//		mpxTask.setFinish(DateTime.fromGmt(new Date(projityTask.getEnd())));
//		mpxTask.setCritical(new Boolean(projityTask.isCritical()));
//		mpxTask.setEstimated(projityTask.isEstimated());
//		mpxTask.setEffortDriven(projityTask.isEffortDriven());
//		mpxTask.setType(TaskType.getInstance(projityTask.getSchedulingType()));
//		mpxTask.setConstraintType(ConstraintType.getInstance(projityTask.getConstraintType()));
//		mpxTask.setConstraintDate(DateTime.fromGmt(new Date(projityTask.getConstraintDate())));
//		mpxTask.setPriority(Priority.getInstance(projityTask.getPriority()));
//		mpxTask.setFixedCost(projityTask.getFixedCost());
//		mpxTask.setFixedCostAccrual(AccrueType.getInstance(projityTask.getFixedCostAccrual()));
////		mpxTask.setPercentageComplete(projityTask.getPercentComplete()/100.0D);
//		mpxTask.setLevelingDelay(toMPXDuration(projityTask.getLevelingDelay()));
//		if (projityTask.getDeadline() != 0)
//			mpxTask.setDeadline(DateTime.fromGmt(new Date(projityTask.getDeadline())));
//
//		//2007
//		mpxTask.setTotalSlack(toMPXDuration(projityTask.getTotalSlack()));
//		mpxTask.setRemainingDuration(toMPXDuration(projityTask.getRemainingDuration()));
//		if (projityTask.getStop() != 0)
//			mpxTask.setStop(DateTime.fromGmt(new Date(projityTask.getStop())));
////		if (projityTask.getResume() != 0)
////			mpxTask.setResume(DateTime.fromGmt(new Date(projityTask.getResume())));
//			
//		WorkCalendar cal = projityTask.getWorkCalendar();
//
//		if (cal != null)
//			mpxTask.setCalendar(ImportedCalendarService.getInstance().findExportedCalendar(cal));
//
////	Not needed - it will be set when hierarchy is done		mpxTask.setOutlineLevel(new Integer(projityTask.getOutlineLevel()));
//
//		toMpxCustomFields(projityTask.getCustomFields(),mpxTask, CustomFieldsMapper.getInstance().taskMaps);
//	}
//
//	public static void toMPXVoid(VoidNodeImpl projityVoid, Task mpxTask) {
//		mpxTask.setID((int)projityVoid.getId());
//		mpxTask.setUniqueID((int)projityVoid.getId());
//		mpxTask.setNull(true);
//		// below is for mpxj 2007. These values need to be set
//		mpxTask.setCritical(false); 
//		mpxTask.setTotalSlack(toMPXDuration(0));
//
//	}
//	
//	
//	
//	/*
//	 * Helper function to convert an mpx date into a projity long
//	 */
//	public static long toProjityDate(Date mpxDate) {
//		if (mpxDate == null)
//			return 0;
//		return mpxDate.getTime();
//	}
//	
//	
//	/*
//	 * Because MpxDate is too big to serialize
//	 */
//	public static Date toNormalDate(Date mpxDate) {
//		if (mpxDate==null) 
//			return null;
//		return new Date(DateTime.gmt(mpxDate));
//	}
//
//	
//	
//	/**
//	 * Helper function to convert an mpx value into a projity value
//	 * @param value
//	 * @return
//	 */
//	public static Rate toProjityRate(net.sf.mpxj.Rate rate) {
//		double value = rate.getAmount() / Duration.timeUnitFactor(rate.getUnits().getValue());
//		return new Rate(value,rate.getUnits().getValue());
//	}
//	public static net.sf.mpxj.Rate toMPXRate(Rate rate) {
//		double value = rate.getValue() * Duration.timeUnitFactor(rate.getTimeUnit());
//		return new net.sf.mpxj.Rate(value,TimeUnit.getInstance(rate.getTimeUnit()));
//	}
//	
//	/**
//	 * Helper function to convert an mpx duration into a projity duration
//	 * @param duration
//	 * @return
//	 */
//	public static long toProjityDuration(net.sf.mpxj.Duration duration, Context context) {
//		long result = 0;
//		if (duration == null)
//			return 0;
////		MPXDuration d = duration.convertUnits(TimeUnit.HOURS);
////		System.out.println("to projty dura " + d.getDuration() + " unit " + d.getUnits().getValue());
////		return Duration.getInstance(d.getDuration()/3.0D,d.getUnits().getValue());
//		// mpxj uses default options when importing link leads and lags
//		
//		if (context.isXml()) {
//			CalendarOption oldOptions = CalendarOption.getInstance();
//			CalendarOption.setInstance(CalendarOption.getDefaultInstance());
//	
//			result =  Duration.getInstance(duration.getDuration(),duration.getUnits().getValue());
//			CalendarOption.setInstance(oldOptions);
//		} else {
//			result =  Duration.getInstance(duration.getDuration(),duration.getUnits().getValue());
//		}
//		return result;
//	}
//	public static net.sf.mpxj.Duration toMPXDuration(long duration) {
//		return net.sf.mpxj.Duration.getInstance(Duration.getValue(duration),TimeUnit.getInstance(Duration.getType(duration)));
//		//TODO put the correct formula
//	}
//	public static final String dateToXMLString(long time) {
//	    Calendar date = DatatypeConverter.printDate(new Date(time));
//	    System.out.println("commented out dateToXMLString");
//	    String result = ""; //
//	//    String result =com.sun.msv.datatype.xsd.DateTimeType.theInstance.serializeJavaObject(date, null);
//		return result;
//	}
//	private static String truncName(String name) {
//		if (name == null)
//			return null;
//		if (name.length() > nameFieldWidth)
//			name = name.substring(0,nameFieldWidth);
//		return name;
//	}
}
