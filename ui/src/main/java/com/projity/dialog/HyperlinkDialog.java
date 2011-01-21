/**
 * 
 */
package com.projity.dialog;

import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.projity.datatype.Hyperlink;

/**
 * @author avigil
 *
 */
public class HyperlinkDialog extends AbstractDialog {
	
	private static final long serialVersionUID = 1L;

  //  private boolean displayLabel = true; //display the label
    private String label;
    private String address;
    
	public static class Form {
	//	private boolean displayLabel = true;
		private String label;
		private String address;
		
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
//		public boolean isDisplayLabel() {
//			return displayLabel;
//		}
//		public void setDisplayLabel(boolean displayLabel) {
//			this.displayLabel = displayLabel;
//		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}	
		
	}
	private Form form;
	
//	private JRadioButton displayLabelButton;
//	private JRadioButton displayAddressButton;
//	private ButtonGroup radioGroup;
	
	private JTextField labelField;
	private JTextField addressField;
	
//	public final void setDisplayLabelSelected(boolean displayLabelSelected) {
//		this.displayLabel = displayLabelSelected;
//    	displayLabelButton.setEnabled(displayLabelSelected);
//    	displayAddressButton.setEnabled(!displayLabelSelected);
//    	displayLabel = displayLabelSelected;	
//	}
	
	protected boolean bind(boolean get) {
		if (form == null)
			return false;
		if (get) {
//			displayLabelButton.setSelected(displayLabel);
//			displayAddressButton.setSelected(!displayLabel);
			labelField.setText(label);
			addressField.setText(address);
		} else {
//			displayLabel = displayLabelButton.getSelectedObjects()!=null;
//			form.setDisplayLabel(displayLabel);
			address = addressField.getText();
			form.setAddress(address);
			label = labelField.getText();
			form.setLabel(label);
		}
		return true;
	}
	
	public HyperlinkDialog(Frame owner, Hyperlink link) {
		super(owner, "Edit url", true); //$NON-NLS-1$
		this.label = link.getLabel();
		this.address = link.getAddress();
//		this.displayLabel = true;
		
		//this.setDisplayLabelSelected(displayLabel);
		
		form = new Form();
		
	}
	
	/**
	 * Creates, intializes and configures the UI components. Real applications
	 * may further bind the components to underlying models.
	 */
	protected void initControls() {
		
//		displayLabelButton = new JRadioButton("Display Name"); 
//		displayAddressButton = new JRadioButton("Display Link"); 
//		
//		radioGroup = new ButtonGroup();
//		radioGroup.add(displayLabelButton);
//		radioGroup.add(displayAddressButton);

		labelField = new JTextField(label);
		addressField = new JTextField(address);

		bind(true);
	}

	/* (non-Javadoc)
	 * @see com.projity.dialog.AbstractDialog#createContentPanel()
	 */
	@Override
	public JComponent createContentPanel() {
		// Separating the component initialization and configuration
		// from the layout code makes both parts easier to read.
		initControls();
		FormLayout layout = new FormLayout("default, 3dlu, fill:150dlu:grow", // cols 
				"p, 6dlu, p, 6dlu, p, 6dlu, p, 6dlu, p"); // rows 

		// Create a builder that assists in adding components to the container.
		// Wrap the panel with a standardized border.
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
//		builder.append("Options");
//		builder.nextLine(1);
//		builder.append(displayLabelButton);
//		builder.nextLine(1);
//		builder.append(displayAddressButton);
//		builder.nextLine(2);
		
//		builder.append("Properties");
//		builder.nextLine(2);
		builder.append("Display Name");
		builder.append(labelField);
		builder.nextLine(2);
		builder.append("URL");
		builder.append(addressField);
		
		builder.nextLine(1);
		
		return builder.getPanel();
	}
	
	/**
	 * @return Returns the form.
	 */
	public Form getForm() {
		return form;
	}
	
	public Object getBean() {
		return form;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
}
