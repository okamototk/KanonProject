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
package com.projity.pm.assignment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.swing.undo.UndoableEditSupport;

import com.projity.command.Command;
import com.projity.datatype.TimeUnit;
import com.projity.grouping.core.Node;
import com.projity.pm.resource.Resource;
import com.projity.pm.resource.ResourceImpl;
import com.projity.pm.resource.ResourcePool;
import com.projity.pm.scheduling.SchedulingType;
import com.projity.pm.task.NormalTask;
import com.projity.pm.task.Project;
import com.projity.pm.task.ProjectFactory;
import com.projity.session.SaveOptions;
import com.projity.session.SessionFactory;
import com.projity.strings.Messages;
import com.projity.undo.NodeUndoInfo;
import com.projity.undo.ScheduleBackupEdit;
import com.projity.util.Alert;
import com.projity.util.Environment;

/**
 * Manages the creation and deleting of assignments as well as events
 */
public class AssignmentService {

	private static AssignmentService instance = null;
	private boolean substituting = false;

	public static AssignmentService getInstance() {
		if (instance == null)
			instance = new AssignmentService();
		return instance;
	}


	public void newAssignments(Collection tasks, Collection resources, double units, long delay, Object eventSource,boolean undo) {
		if (tasks.size()==0||resources.size()==0) return;
		int transactionId = 0;
		Project transactionProject = null;
		for (Iterator i=tasks.iterator();i.hasNext();){
			NormalTask task = (NormalTask)i.next();
//			if (!task.isAssignable())
//				continue;
			if (transactionId == 0) {
				transactionProject = task.getProject();
				transactionProject.beginUndoUpdate();
				transactionId = transactionProject.fireMultipleTransaction(0,true);

				//backup before any assignment operation
				transactionProject.getUndoController().getEditSupport().postEdit(new ScheduleBackupEdit(tasks,this));
			}
			// if task currently has no assignments, then we should not change duration if adding several at once
			boolean taskHasNoAssignments = !task.hasRealAssignments() || !task.hasLaborAssignment();
			int oldSchedulingType = task.getSchedulingType();
			boolean oldEffortDriven = task.isEffortDriven();
			if (taskHasNoAssignments) {// if adding for first time
				task.setSchedulingType(SchedulingType.FIXED_DURATION);
				task.setEffortDriven(false);
			}

			Iterator r = resources.iterator();
			while (r.hasNext()) {
				Resource resource = (Resource) r.next();
				if (null == task.findAssignment(resource)) {
//					double units = 1.0D;
//TODO Bug 330: this is slow and uses tons of memory when assigning many at once. optimizing by doing just one update
//The result is that AssignmentNodeModel.objectChanged(ObjectEvent objectEvent) is called for each assignment
//This needs to be batched as its current memory usage is unacceptable and it takes very long
//Perhaps one solution would be to replace hierarchy search() with a hash table for mapping impls to nodes

//TODO It throws an event for assignment. A service for updating all the assignments at once should be added.
					Assignment assignment = newAssignment(task,resource,units,0,eventSource,true);
					if (!resource.isLabor()) // for assigning non temporal resources, use the value of 1
						assignment.setRateUnit(TimeUnit.NON_TEMPORAL);
				}
			}
			if (taskHasNoAssignments) {// if adding for first time, put back effort driven value
				task.setSchedulingType(oldSchedulingType);
				task.setEffortDriven(oldEffortDriven);
			}
		}
		if (transactionId != 0) {
			transactionProject.fireMultipleTransaction(transactionId,false);
			transactionProject.endUndoUpdate();
		}

	}


/**
 * When importing, we don't update or recalculate duration
 * @param task
 * @param resource
 * @param units
 * @param delay
 * @param eventSource
 * @return
 */
	public Assignment newAssignment(NormalTask task, Resource resource, double units, long delay, Object eventSource,boolean undo) {
		Assignment assignment = Assignment.getInstance(task, resource, units, delay);
		if (!connect(assignment,eventSource,undo))
			return null;
		return assignment;
	}
	public Assignment newAssignment(NormalTask task, Resource resource, double units, long delay, Object eventSource) {
		return newAssignment(task,resource,units,delay,eventSource,true);
	}

	public boolean connect(Assignment assignment, Object eventSource) {
		return connect(assignment, eventSource,true);
	}
	public boolean connect(Assignment assignment, Object eventSource,boolean undo) {
		if (!connect(assignment,eventSource,new NodeUndoInfo(undo)))
			return false;
//		UndoableEditSupport undoableEditSupport=getUndoableEditSupport(assignment);
//		if (undoableEditSupport!=null&&undo){
//			undoableEditSupport.postEdit(new AssignmentCreationEdit(assignment,eventSource));
//		}
		return true;
	}
	public boolean connect(Node node, Object eventSource, boolean undo) {
		return connect((Assignment)node.getImpl(),eventSource,new NodeUndoInfo(node,undo));
	}
	public boolean connect(Assignment assignment, Object eventSource, NodeUndoInfo undo) {
		if (!assignment.getTask().isAssignable() && !Environment.isImporting())
			return false;
		((NormalTask)assignment.getTask()).addAssignment(assignment);
		assignment.getResource().addAssignment(assignment);
		if (eventSource != null){
			assignment.getDocument().getObjectEventManager().fireCreateEvent(eventSource,assignment,undo);
			((ResourcePool)assignment.getResource().getDocument()).getObjectEventManager().fireCreateEvent(eventSource,assignment,undo);
		}
		return true;
	}
	public void remove(Node node, Object eventSource,boolean undo) {
		remove((Assignment)node.getImpl(),true,eventSource,new NodeUndoInfo(node,undo));
	}
//	public void remove(Assignment assignment, Object eventSource) {
//		remove(assignment, eventSource,true);
//	}
	public void remove(Assignment assignment, Object eventSource, boolean undo) {
		remove(assignment,true,eventSource,new NodeUndoInfo(undo));
	}
	public void checkProtectedAndremove(Assignment assignment, Object eventSource, boolean undo) {
		if (assignment.isProtected()){
			assignment.showProtectionWarning();
			return;
		}
		remove(assignment,true,eventSource,new NodeUndoInfo(undo));
	}

	public void remove(Assignment assignment, boolean cleanTaskLink, Object eventSource, boolean undo) {
		remove(assignment,cleanTaskLink,eventSource,new NodeUndoInfo(undo));
//		remove(assignment,(undo)?UNDO:eventSource);
//		UndoableEditSupport undoableEditSupport=getUndoableEditSupport(assignment);
//		if (undoableEditSupport!=null&&undo){
//			undoableEditSupport.postEdit(new AssignmentDeletionEdit(assignment,eventSource));
//		}
	}
	public void remove(Collection assignments, Object eventSource,boolean undo) {
		UndoableEditSupport undoableEditSupport=null;

		try {
			for (Iterator i=assignments.iterator();i.hasNext();){
				Assignment assignment=(Assignment)i.next();
//				if (undoableEditSupport==null&&undo){
//					undoableEditSupport=getUndoableEditSupport(assignment);
//					if (undoableEditSupport!=null){
//						undoableEditSupport.beginUpdate();
//					}
//				}
				remove(assignment,true,eventSource,undo);
			}
		} finally{
//			if (undoableEditSupport!=null&&undo){
//				undoableEditSupport.endUpdate();
//			}
		}
	}

	public void remove(Assignment assignment, boolean cleanTaskLink, Object eventSource, NodeUndoInfo undo) {
			NormalTask task=(NormalTask)assignment.getTask();
			Resource resource=assignment.getResource();

			if (task.findAssignment(resource) == null)
				return; // avoids endless loop 9/1/06 hk


			if (cleanTaskLink) task.removeAssignment(assignment);
			resource.removeAssignment(assignment);

//		//remove assignment snapshots too 18/7/2006 lc
//		//if (resource!=ResourceImpl.getUnassignedInstance())
//        for (int s=0;s<Settings.numBaselines();s++){
//            TaskSnapshot snapshot=(TaskSnapshot)task.getSnapshot(new Integer(s));
//            if (snapshot==null) continue;
//            AssociationList snapshotAssignments=snapshot.getHasAssignments().getAssignments();
//            if (snapshotAssignments.size()>0){
//                for (Iterator j=snapshotAssignments.iterator();j.hasNext();){
//                    Assignment snapshotAssignment=(Assignment)j.next();
//                    if (snapshotAssignment.getTask()==assignment.getTask()&&snapshotAssignment.getResource()==assignment.getResource())
//                    	j.remove();
//                }
//            }
//            //if (snapshotAssignments.size()==0&&s!=Snapshottable.CURRENT.intValue()) task.setSnapshot(new Integer(s), null);
//        }


//			if (eventSource == null){ //case when default assignment is removed
//				if ((undo==null||(undo!=null&&undo.isUndo()))){
//					UndoableEditSupport undoableEditSupport=getUndoableEditSupport(assignment);
//					if (undoableEditSupport!=null){
//						undoableEditSupport.postEdit(new AssignmentDeletionEdit(assignment));
//					}
//				}
//
//			}else {
			if (eventSource != null){
				if (cleanTaskLink) assignment.getDocument().getObjectEventManager().fireDeleteEvent(eventSource,assignment,undo);
				if (assignment.getResource().getDocument() != null) // it's null if local project
					((ResourcePool)assignment.getResource().getDocument()).getObjectEventManager().fireDeleteEvent(eventSource,assignment);
			}
	}

	public void remove(Collection assignmentList, Object eventSource) {
		Assignment assignment;
		Iterator i = assignmentList.iterator();
		while (i.hasNext()) {
			assignment = (Assignment)i.next();
			remove(assignment,true,eventSource,null);
		}
	}

	//fix
	public void remove(Collection assignmentList, Collection toRemove) {
		Assignment assignment;
		Iterator i = assignmentList.iterator();
		while (i.hasNext())
			toRemove.add(i.next());
	}


	//undo
	public UndoableEditSupport getUndoableEditSupport(Assignment assignment) {
		if (assignment.getTask()==null) return null;
		else return assignment.getTask().getProject().getUndoController().getEditSupport();
	}

	public boolean replaceResourceOnTaskWithTransaction(NormalTask task, Resource oldResource, Resource newResource, boolean skipInProgress, long startingFrom) {
		int transactionId = task.getProject().fireMultipleTransaction(0,true);
		task.getProject().beginUndoUpdate();
		boolean result = replaceResourceOnTask(task,oldResource,newResource,skipInProgress,startingFrom);
		task.getProject().endUndoUpdate();
		task.getProject().fireMultipleTransaction(transactionId,false);
		return result;
	}
	public boolean replaceResourceOnTask(NormalTask task, Resource oldResource, Resource newResource, boolean skipInProgress, long startingFrom) {
		if (newResource == null) {
			System.out.println("No new resource found. Aborting replace!");
			return false;
		}
		if (oldResource == newResource)
			return false;
		if (oldResource == null)
			oldResource = ResourceImpl.getUnassignedInstance();
		Assignment oldAssignment = task.findAssignment(oldResource);
		if (oldAssignment == null)
			return false;
		if (oldAssignment.isComplete())
			return false; // if complete, then don't replace

		Assignment existingAssignment = task.findAssignment(newResource); // see if already assigned, so need to increase units
		long oldActualWork = oldAssignment.getActualWork(null);
		if (skipInProgress && oldActualWork > 0)
			return false;

		if (oldAssignment.getEnd() <= startingFrom) // if this assigment is before the date
			return false;

		double oldUnits = oldAssignment.getRemainingUnits();
		boolean effortDriven = task.isEffortDriven();
		task.setEffortDriven(false);
		int schedulingType = task.getSchedulingType();

		long splitDate = Math.max(oldAssignment.getResume(), startingFrom); // date on which to break the assignment in two
		long oldAssignmentKeep = oldAssignment.work(0,splitDate); // work to preserve on original
		if (oldResource == ResourceImpl.getUnassignedInstance() && task.getProject().isActualsProtected()) {// special case of unassigned with percent complete - need project's version of actuals protected
			oldAssignmentKeep = 0; // percent complete must be removed since can't add it to a resource if protected
			task.setPercentComplete(0);
			oldAssignment.setDirty(true);
		}
		long amountToTransfer = oldAssignment.work() - oldAssignmentKeep;


//		System.out.println("old Units " + oldUnits + " old resume " + new java.util.Date(oldAssignment.getResume()) + " split " + new java.util.Date(splitDate) +  "keep on old " + DurationFormat.format(oldAssignmentKeep) + " transfer" + DurationFormat.format(amountToTransfer));

		if (existingAssignment != null) { // increase work if already assigned
			existingAssignment.setWork(existingAssignment.work() + amountToTransfer,null);
			existingAssignment.setDirty(true);
		} else { // replace the resource
			task.setSchedulingType(SchedulingType.FIXED_WORK);
			Assignment newAssignment = newAssignment(task, newResource,oldUnits, 0,this,true);
			// JGao - 6/4/2009 Need to acount the Units are not 100 %
			amountToTransfer /= oldUnits;
			newAssignment.getDetail().setDuration(amountToTransfer);
			newAssignment.setStart(splitDate);
			System.out.println("Made new assignment " + newAssignment + " " + amountToTransfer);
//			System.out.println("ass delay after set start "  + DurationFormat.format(newAssignment.getDelay()));
		}

		if (oldAssignmentKeep == 0) {// get rid of old assignment if not started
			System.out.println("Removed old assignment " + oldAssignment);
			remove(oldAssignment, this,true);
		} else {// get rid of remaining work
			System.out.println("Keeping old assignment " + oldAssignment + " " + oldAssignmentKeep);
			oldAssignment.setDuration( (long)(oldAssignmentKeep / oldUnits) ); //
			oldAssignment.setDirty(true);
		}

		task.setSchedulingType(schedulingType);
		task.setEffortDriven(effortDriven);
		task.setDirty(true);
		return true;
	}

	public boolean replaceResource(long projectId, long oldResourceId, long newResourceId, Date startingFrom, final int taskIds[]) {
		final Project project = ProjectFactory.getInstance().findFromId(projectId);
		if (!SessionFactory.getInstance().getSession(false).getLock(project, true)) { // note calling with true
			Alert.error(Messages.getString("cannotLockDuringResourceSubstitution"));
			return false;
		}


		if (taskIds != null)
			Arrays.sort(taskIds); // for binary search later
		final long startingTime =(startingFrom == null) ? 0 : startingFrom.getTime();
		final Resource oldResource = project.getResourcePool().findById(oldResourceId);
		final Resource newResource = project.getResourcePool().findById(newResourceId);

		System.out.println("old resource " + oldResource + " id " + oldResourceId);
		System.out.println("new resource " + newResource + " id " + newResourceId);
		Command command = new Command(Messages.getString("ResourceSubstitutionDialogBox.title"), project) {
			public void execute(Object arg0) {
				NormalTask task = (NormalTask)arg0;
				if (taskIds != null && Arrays.binarySearch(taskIds, (int)task.getUniqueId()) < 0) // skip if not in list
					return;

				AssignmentService.getInstance().replaceResourceOnTask(task, oldResource, newResource, false,startingTime);
			}};
		int transactionId = project.fireMultipleTransaction(0,true);
		project.forTasks(command);
		project.fireMultipleTransaction(transactionId,false);
		// publish
		SaveOptions opt=new SaveOptions();
		opt.setPublish(true);
		opt.setSync(true);
		opt.setReadActualsFirst(false);
		ProjectFactory.getInstance().saveProject(project, opt);

//		SessionFactory.getInstance().getSession(false).publish(project, false);
//		SessionFactory.getInstance().getSession(false).refreshMetadata(project);

		Alert.warn(Messages.getString("Message.afterSubstitution"));
		boolean completelyRemoved = oldResource.getAssignments().isEmpty();
		if (completelyRemoved) {
			SessionFactory.getInstance().getSession(false).removeAllocation(project, oldResource);
			project.getResourcePool().remove(oldResource);
		}
		return completelyRemoved;
	}


	public boolean isSubstituting() {
		return substituting;
	}


	public void setSubstituting(boolean substituting) {
		this.substituting = substituting;
	}

}


