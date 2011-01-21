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
package com.projity.dialog;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DateField;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.projity.options.CalendarOption;
import com.projity.pm.resource.Resource;
import com.projity.pm.task.Project;
import com.projity.strings.Messages;
import com.projity.util.DateTime;

/**
 *
 */
public class ResourceSubstitutionDialog extends AbstractDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	Project project;
	public static class Form {
		Boolean ignoreInProgress;
		Boolean entireProject;

		long rescheduleDate = CalendarOption.getInstance().makeValidStart(DateTime.gmt(new Date()), true);
		
		Resource fromResource;
		public Resource getFromResource() {
			return fromResource;
		}
		public void setFromResource(Resource fromResource) {
			this.fromResource = fromResource;
		}
		public Resource getToResource() {
			return toResource;
		}
		public void setToResource(Resource toResource) {
			this.toResource = toResource;
		}
		Resource toResource;
	    public Boolean getEntireProject() {
	        return entireProject;
	    }
	    public void setEntireProject(Boolean entireProject) {
	        this.entireProject = entireProject;
	    }
	    public Boolean isIgnoreInProgress() {
	        return ignoreInProgress;
	    }
	    public void setIgnoreInProgress(Boolean ignoreInProgress) {
	        this.ignoreInProgress = ignoreInProgress;
	    }
	    public String toString() {
	    	return ToStringBuilder.reflectionToString(this);
	    }
		public long getRescheduleDate() {
			return rescheduleDate;
		}
		public void setRescheduleDate(long rescheduleDate) {
			this.rescheduleDate = rescheduleDate;
		}
    }	
    
    private Form form;
    boolean hasTasksSelected;
    JRadioButton entireProject;
    JRadioButton selectedTask;
    ButtonGroup projectOrTask;
    JComboBox fromResource;
    JComboBox toResource;
    
	DateField rescheduleDateChooser;
	JCheckBox ignoreInProgress;
    
	public static ResourceSubstitutionDialog getInstance(Frame owner, Form form, boolean hasTasksSelected, Project project) {
		return new ResourceSubstitutionDialog(owner, form,hasTasksSelected,project);
	}

	private ResourceSubstitutionDialog(Frame owner, Form form, boolean hasTasksSelected,Project project) {
		super(owner, Messages.getString("ResourceSubstitutionDialogBox.title"), true); //$NON-NLS-1$
		this.hasTasksSelected = hasTasksSelected;
		this.project = project;
		if (form != null)
			this.form = form;
		else
			this.form = new Form();
	}
	
	protected void initControls() {
	    entireProject = new JRadioButton(Messages.getString("UpdateProjectDialogBox.EntireProject")); //$NON-NLS-1$
	    entireProject.setSelected(true);
	    selectedTask = new JRadioButton(Messages.getString("UpdateProjectDialogBox.SelectedTasks")); //$NON-NLS-1$
	    if (!hasTasksSelected)
	    {
	    	selectedTask.setEnabled(false);
	    }
	    projectOrTask = new ButtonGroup();
	    projectOrTask.add(entireProject);
	    projectOrTask.add(selectedTask);
	    
		rescheduleDateChooser = CalendarFactory.createDateField();
		
		ignoreInProgress= new JCheckBox("Ignore in-progress assignments"); //$NON-NLS-1$
		ignoreInProgress.setSelected(false);
		List resources = (List) project.getResourcePool().getResourceList().clone();
		resources.add(0,null); // allow blank
		fromResource = new JComboBox(resources.toArray());
		toResource = new JComboBox(resources.toArray());
		ok.setEnabled(false);
		toResource.addActionListener(this);
		fromResource.addActionListener(this);
	};
	
	protected boolean bind(boolean get) {
		if (form == null)
			return false;
		if (get) {
		    entireProject.setSelected((form.getEntireProject()).booleanValue());
		    ignoreInProgress.setSelected(form.isIgnoreInProgress().booleanValue());		
			rescheduleDateChooser.setValue(new Date(form.getRescheduleDate()));
			fromResource.setSelectedItem(form.getFromResource());
			toResource.setSelectedItem(form.getToResource());
		} else {
			Boolean b1=new Boolean(entireProject.isSelected());
			form.setEntireProject(b1);
			Boolean b3=new Boolean(ignoreInProgress.isSelected());
			form.setIgnoreInProgress(b3);
			
			long d2 = DateTime.gmt((Date) rescheduleDateChooser.getValue());
			d2 = CalendarOption.getInstance().makeValidStart(d2, true);
			form.setRescheduleDate(d2);
			form.setFromResource((Resource) fromResource.getSelectedItem());
			form.setToResource((Resource) toResource.getSelectedItem());
			
		}
		return true;
	}
	
	public JComponent createContentPanel() {
	
		initControls();
		
		FormLayout layout = new FormLayout(
		        "p,3dlu,p,3dlu,p", //$NON-NLS-1$
	    		  "p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p"); //$NON-NLS-1$

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		
		builder.append("Original resource:");
		builder.nextColumn(2);
		builder.append(fromResource);
		builder.nextLine(2);
		
		builder.append("Replace with:");
		builder.nextColumn(2);
		builder.append(toResource);
		builder.nextLine(2);
		
		builder.append("Starting from:");
		builder.nextColumn(2);
		builder.append(rescheduleDateChooser);
		builder.nextLine(2);
		
		builder.append(Messages.getString("UpdateProjectDialogBox.For")); //$NON-NLS-1$
		//builder.nextLine(2);
		
		builder.nextColumn(2);
		builder.append(entireProject);
		
		builder.nextLine(2);
		builder.append("");
		builder.nextColumn(2);
		builder.append(selectedTask);	
		builder.nextLine(2);
		
		builder.append(ignoreInProgress);
		builder.nextLine(2);
		
		return builder.getPanel();
	}
	
	
	public Form getForm() {
		return form;
	}
	public Object getBean(){
		return form;
	}

	public void actionPerformed(ActionEvent e) {
		boolean canOk = toResource.getSelectedItem() != null; // from can be null
		canOk = canOk && (toResource.getSelectedItem() != fromResource.getSelectedItem());
		ok.setEnabled(canOk);
	}

	@Override
	public void onOk() {
		// TODO Auto-generated method stub
		super.onOk();
		
		
	}
	
	
	
	
}
