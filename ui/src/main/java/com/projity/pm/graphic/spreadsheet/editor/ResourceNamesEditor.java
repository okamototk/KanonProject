/**
 * 
 */
package com.projity.pm.graphic.spreadsheet.editor;

import java.awt.Component;

import javax.swing.JTable;

import com.projity.dialog.util.ResourceNamesField;
import com.projity.field.Field;
import com.projity.pm.graphic.spreadsheet.SpreadSheetModel;
import com.projity.pm.task.NormalTask;

/**
 * @author avigil
 * 
 */
public class ResourceNamesEditor extends SimpleEditor {

	private ResourceNamesField resourceNamesField;

	/**
	 * 
	 */
	public ResourceNamesEditor() {
		// TODO Auto-generated constructor stub
		setClickCountToStart(0);
	}

	/**
	 * @param clazz
	 */
	public ResourceNamesEditor(Class clazz) {
		super(clazz);
		setClickCountToStart(1);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean arg2, int row, int column) {
		Field field = ((SpreadSheetModel) table.getModel())
				.getFieldInColumn(column + 1);
		NormalTask task = null;
		if (((SpreadSheetModel) table.getModel()).getObjectInRow(row) instanceof NormalTask) {
			task = (NormalTask) ((SpreadSheetModel) table.getModel())
					.getObjectInRow(row);
			((SpreadSheetModel) table.getModel()).getObjectInRow(row);
		}

		String resourceNames;

		if (value instanceof String) {
			resourceNames = (String) value;
		} else {
			resourceNames = "";
		}
		this.resourceNamesField = new ResourceNamesField(resourceNames, field, task);

		return this.resourceNamesField;
	}
}
