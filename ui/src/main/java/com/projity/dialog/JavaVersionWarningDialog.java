/**
 * 
 */
package com.projity.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.projity.datatype.Hyperlink;
import com.projity.strings.Messages;
import com.projity.util.Alert;
import com.projity.util.BrowserControl;

/**
 * @author avigil
 *
 */

public final class JavaVersionWarningDialog extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	
	JCheckBox dismissWarningCheckbox;

	private boolean init = false;
	private static boolean dismissedWarning = Preferences.userNodeForPackage(JavaVersionWarningDialog.class).getBoolean("dismissedWarning",false); //$NON-NLS-1$
	private static boolean resetData;
	public static boolean showDialog(Frame owner, boolean force) {
		resetData=!force;
		if (!dismissedWarning || force) {
			JavaVersionWarningDialog dlg = new JavaVersionWarningDialog(owner);
			if (!dismissedWarning)
				dlg.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // force user to click a button
			dlg.doModal();
			return true;
		} else {
			System.out.println("JavaVersionWarningDialog was already dismissed");
		}

		return false;
	}

	private JavaVersionWarningDialog(Frame owner) {
		super(owner, Messages.getContextString("Text.ApplicationTitle") + " Java Version Warning", true); //$NON-NLS-1$ //$NON-NLS-2$
	}

	// Building *************************************************************

	/**
	 * Builds the panel. Initializes and configures components first, then
	 * creates a FormLayout, configures the layout, creates a builder, sets a
	 * border, and finally adds the components.
	 *
	 * @return the built panel
	 */

	public JComponent createContentPanel() {
		FormLayout layout = new FormLayout("300px", // cols //$NON-NLS-1$
				"70px"); // rows //$NON-NLS-1$

		// Create a builder that assists in adding components to the container.
		// Wrap the panel with a standardized border.
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		JTextArea textArea = new JTextArea(Messages.getString("Message.unsupportedJavaVersion"),3,50);
		
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setOpaque(false);
		builder.append(textArea);
	
		Hyperlink link = new Hyperlink(Messages.getString("Text.javaUpgradeUrl"),Messages.getString("Text.javaUpgradeUrl"));
		JLabel linkLabel = link.createLinkLabel();
		
		builder.append(linkLabel);
		
		dismissWarningCheckbox= new JCheckBox("Don't show me this again."); //$NON-NLS-1$
		dismissWarningCheckbox.setSelected(false);
		
		builder.append(dismissWarningCheckbox);

		JComponent result =  builder.getPanel();
		return result;
	}

	@Override
	public ButtonPanel createButtonPanel() {
		ButtonPanel bp = super.createButtonPanel();

		return bp;
	}

	@Override
	protected boolean hasCloseButton() {
		return false;
	}

	@Override
	public void onOk() {
		if (!dismissedWarning && dismissWarningCheckbox.isSelected()){
			dismissedWarning = true;
			Preferences.userNodeForPackage(JavaVersionWarningDialog.class).putBoolean("dismissedWarning",dismissedWarning); //$NON-NLS-1$
		}
		super.onOk();
	}

}

