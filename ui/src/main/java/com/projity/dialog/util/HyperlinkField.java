/**
 * 
 */
package com.projity.dialog.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.projity.datatype.Hyperlink;
import com.projity.dialog.HyperlinkDialog;
import com.projity.field.Field;
import com.projity.pm.graphic.IconManager;
import com.projity.pm.graphic.frames.GraphicManager;
import com.projity.pm.task.NormalTask;

/**
 * @author avigil
 *
 */
public class HyperlinkField extends JPanel {
	private JLabel display;
	private Hyperlink hyperlink;
	
	public HyperlinkField(Hyperlink link){
		display = createLinkLabel(link);
		setLayout(new BorderLayout());
		add(display, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	public HyperlinkField(Hyperlink link, Field f, NormalTask task) {
		display = createLinkLabel(link);
		setLayout(new BorderLayout());
		add(display, BorderLayout.CENTER);
		add(createLookupButton(link,f, task), BorderLayout.EAST);
	}
	
	public JLabel createLinkLabel(final Hyperlink link) {
		JLabel label = new JLabel(link.toString());
		label.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(link.getAddress()!=null && link.getAddress().length()>0){
					link.invoke();
				}
			}
			
		});

		return label;
	}
	
	public JButton createLookupButton(final Hyperlink link, final Field f, final NormalTask task) {
  		JButton edit= new JButton();
  		
  		edit.setToolTipText("Edit url"); 
  		ImageIcon icon = IconManager.getIcon("spreadsheet.edit.icon");
  		
		edit.setIcon(icon);
		edit.setPreferredSize(new Dimension(20,20));
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HyperlinkDialog dialog = new HyperlinkDialog(GraphicManager.getFrameInstance(),link);
				
				if(dialog.doModal()){
					//a change was saved, so we need to update the field
					hyperlink = new Hyperlink(dialog.getLabel(), dialog.getAddress());
					String displayText;
					if(dialog.getLabel()==null || dialog.getLabel().length()==0){
						displayText=dialog.getAddress();
					} else {
						displayText=dialog.getLabel();
					}
					display.setText("<html><a href=\""+hyperlink.getAddress()+"\">"+ displayText + "</a></html>");
				}
			}});
  		return edit;
  	}

	public Hyperlink getLink() {
		return hyperlink;
	}
	
}
