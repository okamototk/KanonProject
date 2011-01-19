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

Attribution Information: Attribution Copyright Notice: Copyright � 2006, 2007
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
package com.projity.exchange;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;

import com.projity.contrib.util.Log;
import com.projity.contrib.util.LogFactory;

import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ScheduleFrom;
import net.sf.mpxj.Task;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.planner.PlannerReader;
import net.sf.mpxj.reader.AbstractProjectReader;

import com.projity.association.InvalidAssociationException;
import com.projity.configuration.CircularDependencyException;
import com.projity.configuration.Settings;
import com.projity.exchange.ResourceMappingForm.MergeField;
import com.projity.functor.StringList;
import com.projity.grouping.core.Node;
import com.projity.grouping.core.NodeFactory;
import com.projity.grouping.core.model.NodeModel;
import com.projity.job.Job;
import com.projity.job.JobCanceledException;
import com.projity.job.JobRunnable;
import com.projity.options.CalendarOption;
import com.projity.pm.assignment.Assignment;
import com.projity.pm.assignment.AssignmentService;
import com.projity.pm.assignment.contour.ContourTypes;
import com.projity.pm.calendar.CalendarService;
import com.projity.pm.calendar.WorkingCalendar;
import com.projity.pm.dependency.Dependency;
import com.projity.pm.dependency.DependencyService;
import com.projity.pm.resource.EnterpriseResource;
import com.projity.pm.resource.ResourceImpl;
import com.projity.pm.resource.ResourcePool;
import com.projity.pm.task.NormalTask;
import com.projity.server.access.ErrorLogger;
import com.projity.server.data.EnterpriseResourceData;
import com.projity.server.data.MPXConverter;
import com.projity.server.data.MSPDISerializer;
import com.projity.server.data.Serializer;
import com.projity.server.data.mspdi.ModifiedMSPDIReader;
import com.projity.session.Session;
import com.projity.session.SessionFactory;
import com.projity.strings.Messages;
import com.projity.util.Alert;
import com.projity.util.DateTime;
import com.projity.util.Environment;
/**
 * This class is based on the project mpxj http://www.tapsterrock.com/mpxj/
 * The enumerated types in projity currently correspond exactly to the types in mpx, so there is no need to convert them.
 * However, if the projity enumerations change, it will be necessary to map them to mpx types.
 *
 */
public class NewMicrosoftImporter {// extends ServerFileImporter{
//	static Log log = LogFactory.getLog(MicrosoftImporter.class);
//	ProjectFile mpx = null;
//	AbstractProjectReader reader;
//	List allTasks = null;
//	ArrayList subprojects;
//	private Date earliestStart = DateTime.getMaxDate();
//	private HashMap taskMap = new HashMap(); // keeps track of mapping mpx tasks to projity tasks
//	private HashMap resourceMap = new HashMap(); // keeps track of mappy mpx resources to projity resources
//	List allResources=null;
//	public static final boolean ADD_SUMMARY_TASK = false; // whether to automatically add an extra project summary task or not when importing mpp
//	Task dummyFirstTask = null;
//	private static final String ERROR_XML = Messages.getString("MicrosoftImporter.ErrorImportingXML"); //$NON-NLS-1$
//	private static final String ABORT = "Job aborted"; //$NON-NLS-1$
//	private String errorDescription = null;
//	private Exception lastException = null;
//
//	private Context context = new Context();
//	public NewMicrosoftImporter() {
//	}
//
////	public void run() {
////		try {
////			throw new NullPointerException("Broken by refactorisation?");
////			//importFile();
////		} catch (Exception e) {
////			if (e.getMessage() == CircularDependencyException.RUNTIME_EXCEPTION_TEXT)
////				return;
////			if (errorDescription != ABORT)
////  			   ServerLogger.log("Import Exception: " + errorDescription,lastException == null ? e : lastException);
////			e.printStackTrace();
////		} finally {
////			Environment.setImporting(false);
////		}
////	}
//
//	private static String fileSuffixes[] = {"",".xml",".mpp",".mpx",".planner"};
//
//	/**
//	 * This method imports an entire mpx, mpp or xml file
//	 *
//	 * @param filename
//	 *            name of the inputfile
//	 * @throws Exception
//	 *             on file read error
//	 */
//    public Job getImportFileJob(){
//    	subprojects = new ArrayList();
//    	errorDescription = null;
//    	lastException = null;
//    	Session session=SessionFactory.getInstance().getSession(resourceMapping==null);
//		Job job=new Job(session.getJobQueue(),"importFile",Messages.getString("MicrosoftImporter.Importing"),true); //$NON-NLS-1$ //$NON-NLS-2$
//    	job.addRunnable(new JobRunnable(Messages.getString("MicrosoftImporter.PrepareResources"),1.0f){ //$NON-NLS-1$
//
//			public Object run() throws Exception{
//				Environment.setImporting(true); // will avoid certain popups
// 				log.info("doing file import");		 //$NON-NLS-1$
//				InputStream stream = null;
//				setProgress(0.05f);
//				int dot = fileName.lastIndexOf("."); //$NON-NLS-1$
//				if (fileName.startsWith("http")) { //$NON-NLS-1$
//					fileName = URLDecoder.decode(fileName,"UTF-8"); //$NON-NLS-1$
//					if (fileName.startsWith("http://")) { //$NON-NLS-1$
//						stream = new URL(fileName).openStream();
//					}
//			//		URL url = new URL(fileName);
//			//		stream = url.openStream();
//			//		HttpURLConnection sourceConnection = (HttpURLConnection) url.openConnection();
//			//		sourceConnection.setFollowRedirects(true);
//			//		sourceConnection.connect();
//			//		stream = sourceConnection.getInputStream();
//				} else {
//					String originalName = fileName;
//					for (int i = 0; i < fileSuffixes.length; i++) {
//						fileName = originalName + fileSuffixes[i];
//						try {
//							stream = new FileInputStream(fileName);
//							break;
//						} catch (java.io.FileNotFoundException e) {
//							
//						}
//					}
//				}
//				if (stream == null) {
//					Alert.warn(Messages.getString("Warn.fileNotFound"));
//					throw(new JobCanceledException(ABORT));
//				}
//					
//				String extension;
//				if (dot == -1) // assume xml if nothing set
//					extension = "xml"; //$NON-NLS-1$
//				else
//					extension = fileName.substring(dot+1).toLowerCase(); // get part after .
//
//				if (extension.equals("mpp")) { //$NON-NLS-1$
//			    	reader = new MPPReader();
//					try {
//						mpx = reader.read(stream);
//					} catch (Exception ex) {
//						lastException = ex;
//						ErrorLogger.log("Exception importing " + extension + " file",ex); //$NON-NLS-1$ //$NON-NLS-2$
//						mpx = null;
//					}
//				} else if (extension.equals("mpx")) { //$NON-NLS-1$
//					reader = new MPXReader();
//					try {
//						mpx = reader.read(stream);
//					} catch (Exception ex) {
//						lastException = ex;
//						ErrorLogger.log("Exception importing " + extension + " file",ex); //$NON-NLS-1$ //$NON-NLS-2$
//						mpx = null;
//					}
//
//				}else if (extension.equals("xml")) { //xml //$NON-NLS-1$
//					try {
//						context.setXml(true);
////hk						reader = new ModifiedMSPDIReader();
//						mpx = reader.read(stream);
//					} catch (Exception ex) {
//						lastException = ex;
//						System.out.println("Can't read xml: " + ex.getMessage()); //$NON-NLS-1$
//						ex.printStackTrace();
//						mpx = null;
//						errorDescription = ERROR_XML;
//					}
//				} else if (extension.equals("planner")) {  //$NON-NLS-1$
//					reader = new PlannerReader();
//					try {
//						mpx = reader.read(stream);
//					} catch (Exception ex) {
//						lastException = ex;
//						ErrorLogger.log("Exception importing " + extension + " file",ex); //$NON-NLS-1$ //$NON-NLS-2$
//						mpx = null;
//					}
//
//				}
//				if (stream != null) // close the stream
//					stream.close();
//
//					//JAXB is not on classpath right now
//			//		if (mpx == null) {
//			//			try {
//			//				mpx = new MSPDIFile(fileName);
//			//			} catch (Exception ex) {
//			//				mpx = null;
//			//			}
//			//		}
//				if (mpx == null) {
//					String errorText = (errorDescription == null) ? Messages.getString("Message.ImportError") : errorDescription; //$NON-NLS-1$
//
//					job.error(errorText,false);
//					job.cancel();
//
//					Environment.setImporting(false); // will avoid certain popups
//					throw lastException == null ? new Exception("Failed to import file") : lastException; //$NON-NLS-1$
//				}
//				setProgress(0.2f);
//			log.info("prepare resources"); //$NON-NLS-1$
//				allResources= mpx.getAllResources();
//				prepareResources(mpx.getAllResources(),new Predicate(){
//					public boolean evaluate(Object arg) {
//						Resource resource=(Resource)arg;
//						if  (resource != null) {
//							Integer id = resource.getID();
//							if (id != null && id.longValue() != 0L)
//								return true;
//						}
//						return false;
//					}
//				},true);
//				//setProgress(0.3f);
//				setProgress(1f);
//				return null;
//			}
//    	});
//    	job.addSwingRunnable(new JobRunnable("Import resources",1.0f){ //$NON-NLS-1$
//			public Object run() throws Exception{
//				ResourceMappingForm form=getResourceMapping();
//				if (form!=null&&form.isLocal()) //if form==null we are in a case were have no server access. popup not needed
//					if (!job.okCancel(Messages.getString("Message.ServerUnreacheableReadOnlyProject"),true)){ //$NON-NLS-1$
//						setProgress(1.0f);
//						errorDescription = ABORT;
//						Environment.setImporting(false); // will avoid certain popups
//						throw new Exception(ABORT);
//					}
//
//			log.info("import resources");		 //$NON-NLS-1$
//				if(!importResources()){
//					setProgress(1.0f);
//					errorDescription = ABORT;
//					Environment.setImporting(false); // will avoid certain popups
//					throw new Exception(ABORT);
//				}
//				//setProgress(0.4f);
//				setProgress(1f);
//				return null;
//	    	}
//    	});
//    	job.addRunnable(new JobRunnable("Finish import",1.0f){ //$NON-NLS-1$
//			public Object run() throws Exception{
//
//			log.info("import options"); //$NON-NLS-1$
//				importOptions();
//				setProgress(0.45f);
//			log.info("import calendars"); //$NON-NLS-1$
//					importCalendars();
//					setProgress(0.5f);
//			log.info("import tasks");		 //$NON-NLS-1$
//					importTasks();
//					setProgress(0.6f);
//			log.info("import project fields");		 //$NON-NLS-1$
//					importProjectFields();
//					setProgress(0.7f);
//			log.info("import dependencies");		 //$NON-NLS-1$
//					importDependencies();
//					setProgress(0.8f);
//			log.info("import hierarchy");		 //$NON-NLS-1$
//					importHierarchy();
//					setProgress(0.85f);
//			log.info("import assignments"); //$NON-NLS-1$
//					importAssignments();
//					setProgress(0.9f);
//			log.info("about to initialize");		 //$NON-NLS-1$
//				if (project.getName() == null)
//					project.setName("error - name not set on import"); //$NON-NLS-1$
//
////				CalendarService.getInstance().renameImportedBaseCalendars(project.getName());
//				try {
//					project.initialize(false,false); // will run critical path
//				} catch (RuntimeException e) {
//					if (e.getMessage()==CircularDependencyException.RUNTIME_EXCEPTION_TEXT) {
//						Environment.setImporting(false); // will avoid certain popups
//						Alert.error(e.getMessage());
//						mpx = null;
//						project = null;
//						throw new Exception(e.getMessage());
//					}
//				}
//				//project.setGroupDirty(!Environment.getStandAlone());
//				if (!Environment.getStandAlone()) project.setAllDirty();
//
//				project.setBoundsAfterReadProject();
//				if (mpx.getProjectHeader().getScheduleFrom() == ScheduleFrom.FINISH)
//					project.setForward(false);
//				Environment.setImporting(false); // will avoid certain popups
//				setProgress(1.0f);
//				mpx=null;// remove reference
//				return project;
//
//    		}
//    	});
////    	job.addSwingRunnable(new JobRunnable("Local: addProject"){
////    		public Object run() throws Exception{
////    			Project project=(Project)getPreviousResult();
////    			if (project!=null) projectFactory.addProject(project,true);
////    			return project;
////    		}
////    	});
////
////		session.schedule(job);
//    	return job;
//    }
//
//
//	private void importCalendars() {
//		List calendars = mpx.getBaseCalendars();
//		Iterator iter = calendars.iterator();
//		ImportedCalendarService service = ImportedCalendarService.getInstance();
//		String importedDuplicateText = " " + Settings.LEFT_BRACKET + mpx.getProjectHeader().getProjectTitle() + Settings.RIGHT_BRACKET; //$NON-NLS-1$
//		while (iter.hasNext()) {
//			ProjectCalendar cal = (ProjectCalendar) iter.next();
//			if (ProjectCalendar.DEFAULT_BASE_CALENDAR_NAME.equals(cal.getName())) // bug fix - name can be null
//				context.setDefaultMPXCalendar(cal);
//			WorkingCalendar workCalendar = WorkingCalendar.getStandardBasedInstance();
//			MPXConverter.toProjityCalendar(cal,workCalendar,context);
//			if (CalendarService.findBaseCalendar(workCalendar.getName()) != null) { // if calendar with that name exists already, change the name of this one
//
//				//TODO eventually avoid duplicating if calendars are truly identical
//				workCalendar.setName(workCalendar.getName() + importedDuplicateText);
//			}
//			service.addImportedCalendar(workCalendar,cal);
//		}
//	}
//
//
//	/**
//	 * This method imports all resources defined in the file into the projity model
//	 *
//	 * @param file
//	 *            MPX file
//	 */
//	private void importLocalResources(){
//		ResourceImpl projityResource;
//		ResourcePool resourcePool = project.getResourcePool();
//		project.setLocal(true);
//		resourcePool.setLocal(true);
//		resourcePool.setMaster(false);
//        resourcePool.updateOutlineTypes();
//		Resource resource;
//		Iterator iter = allResources.iterator();
//		while (iter.hasNext()) {
//			resource = (Resource) iter.next();
//			if (resource.getNull() || resource.getID() == null) // skip empty lines.  they are created later by examining ids
//				continue;
//			if (resource.getID() == 0) {// if is special unassigned resource, map to unassigned resource singleton
//				resourceMap.put(resource, ResourceImpl.getUnassignedInstance());
//			} else {
//				projityResource = resourcePool.newResourceInstance();
//				resourceMap.put(resource, projityResource);
//				MPXConverter.toProjityResource(resource, projityResource, context);
//				projityResource.getGlobalResource().setLocal(true);
//				// imported resources without a calendar should use project calendar as base
//				if (projityResource.getBaseCalendar() == null)
//					try {
//						projityResource.setBaseCalendar(project.getBaseCalendar());
//					} catch (CircularDependencyException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				// Add to resource hierarchy.  MSProject does not actually have a hierarchy
//				Node resourceNode = NodeFactory.getInstance().createNode(projityResource); // get a node for this resource
//				resourcePool.addToDefaultOutline(null,resourceNode);
//			}
//		}
//		insertResourceVoids();
//	}
//
//
//	protected boolean importResources() throws Exception{
//		return importResources(resourceMap,new Closure() {
//			public void execute(Object arg0) {
//				importLocalResources();
//			}
//		});
//	}
//
//	protected boolean importResources(HashMap resourceMap,Closure importLocalResources) throws Exception{
//		ResourceMappingForm form=getResourceMapping();
//
//
//
//		if (form==null||form.isLocal()){
//			importLocalResources.execute(null);
//		}else{
//			if (!form.execute()) return false;
//			if (form.isLocal()){
//				importLocalResources.execute(null);
//				/*ResourcePool resourcePool = project.getResourcePool();
//				project.setTemporaryLocal(true);
//				resourcePool.setLocal(true);
//				resourcePool.setMaster(false);
//				resourcePool.updateOutlineTypes();*/
//				return true;
//			}
//
//			com.projity.pm.resource.Resource projityResource=null;
//			int projityResourceCount=0;
//			ResourcePool resourcePool = project.getResourcePool();
//			project.setTemporaryLocal(true);
//			Object srcResource;
//			EnterpriseResourceData data;
//			Map enterpriseResourceDataMap=new HashMap();
//			for (Iterator i=form.getResources().iterator();i.hasNext();){
//				data=(EnterpriseResourceData)i.next();
//				if (data.isLocal()) {
//					projityResource=ResourceImpl.getUnassignedInstance();
//				} else {
////					try {
//						projityResource=Serializer.deserializeResourceAndAddToPool(data,resourcePool,null);
//
//						//Handles only flat outlines
//						Node node=NodeFactory.getInstance().createNode(projityResource);
//						resourcePool.addToDefaultOutline(null,node,projityResourceCount++,false);
//		                ((ResourceImpl)projityResource).getGlobalResource().setResourcePool(resourcePool);
////					} catch (Exception e) {}
//				}
//				enterpriseResourceDataMap.put(data,projityResource);
//
//			}
//			Iterator ir = form.getImportedResources().iterator();
//			Iterator sr = form.getSelectedResources().iterator();
//			while (ir.hasNext()) {
//				srcResource = ir.next();
//				data=(EnterpriseResourceData)sr.next();
//				projityResource=(com.projity.pm.resource.Resource)enterpriseResourceDataMap.get(data);
//				resourceMap.put(srcResource,projityResource );
//			}
//
//			resourcePool.setMaster(false);
//			resourcePool.updateOutlineTypes();
//
//			project.setAccessControlPolicy(form.getAccessControlType());
//			project.resetRoles(form.getAccessControlType()==0);
//
////			Iterator ir = form.getImportedResources().iterator();
////			Iterator sr = form.getSelectedResources().iterator();
////			while (ir.hasNext()) {
////				resource = (Resource) ir.next();
////				data=(EnterpriseResourceData)sr.next();
////				if (enterpriseResourceDataMap.containsKey(data))
////					projityResource=(com.projity.pm.resource.Resource)enterpriseResourceDataMap.get(data);
////				else{
////					if (data.isLocal()) {
////						projityResource=ResourceImpl.getUnassignedInstance();
////					} else {
//////						try {
////							projityResource=Serializer.deserializeResourceAndAddToPool(data,resourcePool);
////
////							//Handles only flat outlines
////							Node node=NodeFactory.getInstance().createNode(projityResource);
////							resourcePool.addToDefaultOutline(null,node,projityResourceCount++,false);
////			                ((ResourceImpl)projityResource).getGlobalResource().setResourcePool(resourcePool);
//////						} catch (Exception e) {}
////					}
////					enterpriseResourceDataMap.put(data,projityResource);
////				}
////				resourceMap.put(resource,projityResource );
////			}
//		}
//		return true;
//	}
//
//
//	private void importOptions() {
//		ProjectHeader projectHeader = mpx.getProjectHeader();
//		MPXConverter.toProjityOptions(projectHeader,context);
//
//	}
//
//	private void importProjectFields() {
//		ProjectHeader projectHeader = mpx.getProjectHeader();
//		MPXConverter.toProjityProject(projectHeader,project,context);
//		//project.setWorkCalendar((WorkCalendar) Dictionary.get(WorkCalendar.CALENDAR_CATEGORY,projectHeader.getCalendarName()));
//		Date projectStart = null; // this value is wrong sometimes! MPXConverter.toNormalDate(projectHeader.getStartDate());
//		// if a start found in project header, use it, otherwise use earliest starting task for start
//		if (projectStart != null) {
//			project.setStart(DateTime.gmt(projectStart));
//		} else {
//			project.setStart(earliestStart.getTime()); // earliest start already made gmt
//		}
//		//project.setManager(projectHeader.getManager());
//		//project.setNotes(projectHeader.getComments());
//	}
//	/**
//	 * This method imports all tasks defined in the file into the projity model
//	 *
//	 */
//	private void importTasks() {
//		allTasks = mpx.getAllTasks();
//		Iterator iter = allTasks.iterator();
//		Task task;
//		while (iter.hasNext()) {
//			task = (Task) iter.next();
//			if (task.getNull()) {
//				System.out.println("skipping null task" + task.getName() + " " + task.getID()); //$NON-NLS-1$ //$NON-NLS-2$
//				continue;
//			}
//			if (task.getSubProject() != null)
//				subprojects.add(task.getName());
//			if (task.getOutlineNumber() != null && task.getOutlineLevel() == 0) { // there is a dummy first task with outline level 0 that is used to hold project info
//				if (dummyFirstTask != null)
//					log.warn("Encountered more than one dummy first tasks"); //$NON-NLS-1$
//				dummyFirstTask = task;
//				project.setName(task.getName()); // project name is first task's name
//				if (!ADD_SUMMARY_TASK) { // mpp files have an initial task that should be ignored
//					taskMap.put(task, null);
//					continue;
//				}
//
//			}
//			NormalTask projityTask = project.newNormalTaskInstance(false);
//			projityTask.setOwningProject(project);
//			projityTask.setProjectId(project.getUniqueId());
//			taskMap.put(task, projityTask);
////			if (task.getCalendarName() != null)
////				System.out.println("task calendar for :" + task.getName() + " is " + task.getCalendarName());
//
////	System.out.println("Task " + task.getID() + " level " + task.getOutlineLevelValue() + " number " + task.getOutlineNumber() +  " wbs " + task.getWBS());
//			MPXConverter.toProjityTask(task, projityTask,context);
//			Date start = DateTime.gmtDate(task.getStart());
//			earliestStart = (earliestStart==null)?start:DateTime.min(earliestStart,start);
//
////	System.out.println("imported task #" + count++ + " name " + projityTask );
//
//		}
//		if (!subprojects.isEmpty())
//			Alert.warn(Messages.getString("MicrosoftImporter.ImportWithSubprojects") + StringList.list(subprojects)); //$NON-NLS-1$
//	}
//
//
//
//
//	/**
//	 * Import dependencies. Must be done after importing tasks
//	 *
//	 * @throws Exception
//	 */
//	public void importDependencies() throws Exception {
//		Iterator taskIter = allTasks.iterator();
//
//		// mpxj uses default options when importing link leads and lags, even when mpp format
//		CalendarOption oldOptions = CalendarOption.getInstance();
//		CalendarOption.setInstance(CalendarOption.getDefaultInstance());
//
//
//		while (taskIter.hasNext()) { // go thru all tasks
//			Task task = (Task) taskIter.next();
//			if (task == dummyFirstTask)
//				continue;
//			if (task == null)
//				System.out.println("null task"); //$NON-NLS-1$
//			List rels = task.getPredecessors();
//			if (rels != null) {
//				Iterator relIter = rels.iterator();
//				while (relIter.hasNext()) { // go thru all predecessors
//					Relation relation = (Relation) relIter.next();
//					addDependency(task, relation);
//				}
//			}
//		}
//		CalendarOption.setInstance(oldOptions);
//	}
//
//	/**
//	 * Add a new dependeny into the projity model based on mpx task and predecessor
//	 * @param mpxTask
//	 * @param mpxRelation
//	 * @return
//	 */
//	private Dependency addDependency(Task mpxTask, Relation mpxRelation) {
//		com.projity.pm.task.Task predecessor = (com.projity.pm.task.Task) taskMap.get(mpx
//				.getTaskByUniqueID(mpxRelation.getTaskUniqueID()));
//		com.projity.pm.task.Task successor = (com.projity.pm.task.Task) taskMap.get(mpxTask);
//
//		if (predecessor == null || successor == null) {
//			System.out.println("invalid dependency -pred task not found - maybe duplicate task UIDs" //$NON-NLS-1$
//					+ " pred UID=" + mpxRelation.getTaskUniqueID()); //$NON-NLS-1$
//			return null;
//		}
//		Dependency dependency;
//		try {
//			dependency = DependencyService.getInstance().newDependency(
//					predecessor, successor, mpxRelation.getType().getValue(),
//					MPXConverter.toProjityDuration(mpxRelation.getDuration(),context), null);
//		} catch (InvalidAssociationException e) {
//			log.error("Error adding dependency:" + e.getMessage()); //$NON-NLS-1$
//			dependency = null;
//		}
//		return dependency;
//	}
//
//
//	/**
//	 * Import the hierarchy information into the projity model
//	 * reflecting the parent-child relationships between the tasks.
//	 *
//	 */
//	private void importHierarchy() {
//		List childTasks = mpx.getChildTasks();
//		Iterator iter = childTasks.iterator();
//		Task task=null;
//		while (iter.hasNext() == true) {
//			task = (Task) iter.next();
//			importHierarchy(task,null);
//		}
//		insertTaskVoids();
////		((AbstractMutableNodeHierarchy)project.getTaskOutline().getHierarchy()).dump();
//	}
//
//
//	/**
//	 * Helper method called recursively to import the hierarchy of child tasks.
//	 *
//	 * @param task
//	 *            task whose children are to be imported
//	 * @param parent
//	 *            the parent of the task
//	 */
//	private void importHierarchy(Task task, Node parentNode) {
//		Node taskNode = null;
//		com.projity.pm.task.Task projityTask = (com.projity.pm.task.Task)taskMap.get(task);
//		if (projityTask != null) {
//			taskNode = NodeFactory.getInstance().createNode(projityTask); // get a node for this task
//			project.addToDefaultOutline(parentNode,taskNode);
//		}
//		List tasks = task.getChildTasks();
//		Iterator iter = tasks.iterator();
//		Task child;
//		while (iter.hasNext() == true) {
//			child = (Task) iter.next();
//			importHierarchy(child,taskNode);
//		}
//	}
//
///**
// * Insert blank lines into the task list.  this is accomplished by looking for gaps in the ids.  Note that this assumes
// * that the tasks are numbered sequentially!  No sorting by id is done.
// *
// */
//	private void insertTaskVoids() {
//		long previousId = 0;
//		Iterator taskIter = allTasks.iterator();
//		NodeModel nodeModel = project.getTaskOutline();
//		while (taskIter.hasNext()) { // go thru all tasks
//			Task task = (Task) taskIter.next();
//			if (task.getNull()) // when importing mspdi format, nulls appear.  However to simplify, I do same treatment as .mpp files, using the id.
//				continue;
//
//			int id = task.getID();
//			int blankLines = (int) (id - previousId -1) ; // how many void nodes to insert
//			previousId = id;
//			if (blankLines == 0)
//				continue;
//			Object obj= taskMap.get(task);
//			Node nextNode = nodeModel.search(obj);
//			if (nextNode == null)
//				continue;
//			for (int i = 0; i < blankLines; i++) { // insert void siblings before
//				Node voidNode = NodeFactory.getInstance().createVoidNode();
//				nodeModel.addBefore(nextNode,voidNode,NodeModel.SILENT);
//			}
//		}
//	}
///** Same as insertTaskVoids.  Because MPXJ does not provide a base interface, I have to copy/paste this code (it could be done with callbacks, but it's not that complicated)
// *
// */
//	private void insertResourceVoids() {
//		long previousId = 0;
//		Iterator iter = mpx.getAllResources().iterator();
//		NodeModel nodeModel = project.getResourcePool().getResourceOutline();
//		while (iter.hasNext()) { // go thru all tasks
//			Resource resource = (Resource) iter.next();
//			if (resource.getNull() || resource.getID() == null) // when importing mspdi format, nulls appear.  However to simplify, I do same treatment as .mpp files, using the id.
//				continue;
//
//			int id = resource.getID();
//			int blankLines = (int) (id - previousId -1) ; // how many void nodes to insert
//			previousId = id;
//			if (blankLines == 0)
//				continue;
//			Object obj = resourceMap.get(resource);
//			Node nextNode = nodeModel.search(obj);
//			for (int i = 0; i < blankLines; i++) { // insert void siblings before
//				Node voidNode = NodeFactory.getInstance().createVoidNode();
//				nodeModel.addBefore(nextNode,voidNode,NodeModel.SILENT);
//			}
//		}
//	}
//
//	/**
//	 * Import mpx assignments into projity model
//	 *
//	 */
//	private void importAssignments() {
//		List allAssignments = mpx.getAllResourceAssignments();
//		Iterator iter = allAssignments.iterator();
//		ResourceAssignment assignment;
//		while (iter.hasNext() == true) {
//			assignment = (ResourceAssignment) iter.next();
//			addAssignment(assignment);
//		}
//	}
//
//	/**
//	 * Add a new projity assignment given an mpx assignment
//	 * @param mpxAssignment
//	 * @return new assignment
//	 */
//	private Assignment addAssignment(ResourceAssignment mpxAssignment) {
//		NormalTask task = (NormalTask) taskMap.get(mpxAssignment.getTask());
//		// for some reason, mpxj returns a units value that is multiplied by 100
//		if (task == null) {
//			System.out.println("null task in assignment - dummy is " +(mpxAssignment.getTask() == this.dummyFirstTask)); //$NON-NLS-1$
//			return null;
//		}
//		com.projity.pm.resource.Resource resource;
//		if (mpxAssignment.getResourceUniqueID().intValue() == EnterpriseResource.UNASSIGNED_ID) // if unassigned, use unassigned id.  Relevant for msdi imports
//			resource = ResourceImpl.getUnassignedInstance();
//		else
//			resource = (com.projity.pm.resource.Resource) resourceMap.get(mpxAssignment.getResource());
//		if (resource == null) {
//			System.out.println("null resource in assignment - ignored. resource id was " +mpxAssignment.getResourceUniqueID()); //$NON-NLS-1$
//			return null;
//		}
//
//		Assignment assignment = AssignmentService.getInstance().newAssignment(
//				task
//				,resource
//				,mpxAssignment.getUnits().doubleValue() / 100.0
//				,0 //The delay will be calculated by the setStart below.  This code is not needed: MPXConverter.toProjityDuration(mpxAssignment.getDelay(),context) //TODO maybe use default calendar options as in dependency import
//				,null);
//		assignment.setStart(DateTime.gmt(mpxAssignment.getStart())); // will take care of delay
//
//		if (mpxAssignment.getFinish() != null)
//			assignment.setEnd(DateTime.gmt(mpxAssignment.getFinish())); // in case assignment doesn't span full task, must truncate it
////		else
////			System.out.println("mpxAssignment.getFinish() = " + mpxAssignment.getFinish());
//
////TODO leveling delay - not supported by mpxj
//
//		WorkContour contour = mpxAssignment.getWorkContour();
//		if (contour == null)
//			assignment.setWorkContourType(ContourTypes.FLAT);
//		else if (contour.getValue() == WorkContour.CONTOURED.getValue())
//			assignment.makeContourPersonal();
//		else
//			assignment.setWorkContourType(contour.getValue());
//
//		List timephasedList = getTimephasedList(mpxAssignment);
//		if (timephasedList != null) {
//			ModifiedMSPDIReader.readAssignmentBaselinesAndTimephased(assignment,timephasedList);
//		}
//		return assignment;
//	}
//
//	private List getTimephasedList(ResourceAssignment mpxAssignment) {
//		if (!context.isXml()) // mpp format does not have timephased data
//			return null;
//		return null;
////		return ((ModifiedMSPDIReader)reader).getTimephasedList(mpxAssignment);
//	}
//
//
//
//	/**
//	 * Currently not implemented
//	 */
//	public Job getExportFileJob(){
//    	Session session=SessionFactory.getInstance().getLocalSession();
//		Job job=new Job(session.getJobQueue(),"exportFile","Exporting...",true); //$NON-NLS-1$ //$NON-NLS-2$
//    	job.addRunnable(new JobRunnable("Local: export",1.0f){ //$NON-NLS-1$
//    		public Object run() throws Exception{
//     			MSPDISerializer serializer = new MSPDISerializer();
//    			serializer.setJob(this);
//    			serializer.saveProject(project,fileName);
//    			return null;
//    		}
//    	});
//		//session.schedule(job);
//    	return job;
//
//	}
}