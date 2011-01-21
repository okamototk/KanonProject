/**
 * 
 */
package com.projity.pm.graphic.laf;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * @author avigil
 * 
 */
public class LafUtils {

	public static void addMouseOverHighlight(JComponent parent, final Color hoverColor){
		addMouseOverHighlight(parent,hoverColor,false);
	}
	
	public static void addMouseOverHighlight(JComponent parent, final Color hoverColor, boolean recursive) {
		
		for (Component child : parent.getComponents()) {
			final Color initialColor = child.getBackground();
			final boolean initialOpacity = child.isOpaque();
			if (recursive && child instanceof JComponent) {
				addMouseOverHighlight((JComponent) child,hoverColor,recursive);
			}
			child.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					JComponent comp = (JComponent) e.getSource();
					comp.setBackground(hoverColor);
					comp.setOpaque(true);
					comp.repaint();
				}

				public void mouseExited(MouseEvent e) {
					JComponent comp = (JComponent) e.getSource();
					comp.setOpaque(initialOpacity);
					comp.setBackground(initialColor);
					comp.repaint();
				}
			});
		}
	}

public static void addMouseOverBorder(JComponent parent, final Color borderColor, final int borderSize, boolean recursive) {
		
		for (Component child : parent.getComponents()) {
			final boolean initialOpacity = child.isOpaque();
			final Border initialBorder;
			final Border mouseOverBorder = BorderFactory.createMatteBorder(borderSize,borderSize,borderSize,borderSize, borderColor);
			final Border emptyBorder = new EmptyBorder(borderSize,borderSize,borderSize,borderSize);
			
			if (recursive && child instanceof JComponent) {
				initialBorder = ((JComponent)child).getBorder();
				addMouseOverBorder((JComponent) child,borderColor, borderSize, recursive);
			} else {
				initialBorder = null;
			}
			
			final Border paddedBorder = new CompoundBorder(initialBorder,emptyBorder);
			if(child instanceof JComponent) {
				((JComponent)child).setBorder(paddedBorder);
			}
			
			child.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					JComponent comp = (JComponent) e.getSource();
					comp.setBorder(mouseOverBorder);
					comp.setOpaque(true);
					comp.repaint();
				}

				public void mouseExited(MouseEvent e) {
					JComponent comp = (JComponent) e.getSource();
					comp.setOpaque(initialOpacity);
					comp.setBorder(paddedBorder);
					comp.repaint();
				}
			});
		}
	}
}
