/**
 * 
 */
package com.projity.pm.graphic.spreadsheet.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.text.DateFormat;
import java.text.Format;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.event.DocumentEvent;

import com.projity.datatype.Hyperlink;
import com.projity.dialog.BaselineDialog;
import com.projity.dialog.HyperlinkDialog;
import com.projity.dialog.util.HyperlinkField;
import com.projity.field.Field;
import com.projity.menu.HyperLinkToolTip;
import com.projity.options.CalendarOption;
import com.projity.options.EditOption;
import com.projity.pm.graphic.ChangeAwareTextField;
import com.projity.pm.graphic.frames.GraphicManager;
import com.projity.pm.graphic.spreadsheet.SpreadSheetModel;
import com.projity.pm.graphic.spreadsheet.editor.DateEditor.ExtDateField;
import com.projity.pm.task.NormalTask;
import com.projity.util.DateTime;

/**
 * @author avigil
 *
 */
public class HyperlinkEditor extends SimpleEditor {

	private HyperlinkField hyperlinkField;
	
	/**
	 * 
	 */
	public HyperlinkEditor() {
		// TODO Auto-generated constructor stub
		
	}

	/**
	 * @param clazz
	 */
	public HyperlinkEditor(Class clazz) {
		super(clazz);
		setClickCountToStart(1);
	}

	@Override
	public Object getCellEditorValue() {
		String val = this.hyperlinkField.getLink().getAddress()+"@|@|@"+this.hyperlinkField.getLink().getLabel()+"@|@|@1";
		return val;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean arg2, int row, int column) {
		Field field = ((SpreadSheetModel)table.getModel()).getFieldInColumn(column+1);
		NormalTask task=null;
		if (((SpreadSheetModel)table.getModel()).getObjectInRow(row) instanceof NormalTask) {
			task = (NormalTask) ((SpreadSheetModel)table.getModel()).getObjectInRow(row);
			((SpreadSheetModel)table.getModel()).getObjectInRow(row);
		} 

		Hyperlink link;
		
		if (value instanceof Hyperlink) {
			link = (Hyperlink) value;
		} else if(value instanceof String){
			String[] values = ((String)value).split("\\@\\|\\@\\|\\@");
			String address = values[0];
			if(values.length == 1){
				return super.getTableCellEditorComponent(table, value, arg2, row, column);
			}
			String label = values[1];
			//String display = values[2];
			String displayText;
			if(label==null || label.length()==0){
				displayText=address;
			} else {
				displayText=label;
			}
			link = new Hyperlink(displayText,address);
		} else {
    		link = new Hyperlink("","");
    	}
		this.hyperlinkField = new HyperlinkField(link,field,task);
		
        return this.hyperlinkField;
	}

	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		if(this.hyperlinkField.getLink()!=null){
			fireEditingStopped();
			return super.stopCellEditing();
		}
		return super.stopCellEditing();
	}
	
	
	
}
