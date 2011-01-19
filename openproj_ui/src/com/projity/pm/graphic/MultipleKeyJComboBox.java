/**
 * 
 */
package com.projity.pm.graphic;

import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author avigil
 * 
 */
public class MultipleKeyJComboBox extends JComboBox {

	public MultipleKeyJComboBox() {
		super();
		setKeySelectionManager(new MyKeySelectionManager());
	}

	public MultipleKeyJComboBox(ComboBoxModel aModel) {
		super(aModel);
		setKeySelectionManager(new MyKeySelectionManager());
	}

	public MultipleKeyJComboBox(Object[] items) {
		super(items);
		setKeySelectionManager(new MyKeySelectionManager());
	}

	public MultipleKeyJComboBox(Vector<?> items) {
		super(items);
		setKeySelectionManager(new MyKeySelectionManager());
	}

	@Override
	public void processKeyEvent(KeyEvent e) {
		if (!selectWithKeyChar(e.getKeyChar())) {
			setKeySelectionManager(new MyKeySelectionManager());
		}

		e.consume();
	}

	public void resetKey() {
		setKeySelectionManager(new MyKeySelectionManager());
	}

	class MyKeySelectionManager implements JComboBox.KeySelectionManager {
		private long lastKeyTime = 0;

		private String pattern = "";

		public int selectionForKey(char aKey, ComboBoxModel model) {
			int index = 0;

			// Get the current time
			long delta = System.currentTimeMillis() - lastKeyTime;
			// If last key was typed less than 3 s ago, append to current
			// pattern
			if (delta < 3000) {
				pattern += ("" + aKey).toLowerCase();
			} else {
				pattern = ("" + aKey).toLowerCase();
			}

			// Search
			for (int i = 0; i < model.getSize(); i++) {
				String s = model.getElementAt(i).toString().toLowerCase();
				if (s.startsWith(pattern)) {
					index = i;
					break;
				}
			}

			if (index == 0) {
				pattern = "";
			}
			// Save current time
			lastKeyTime = System.currentTimeMillis();
			return index;
		}
	}
}
