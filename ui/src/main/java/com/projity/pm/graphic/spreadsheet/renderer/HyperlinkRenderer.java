/**
 * 
 */
package com.projity.pm.graphic.spreadsheet.renderer;

import java.awt.Component;

import javax.swing.JTable;

import com.projity.datatype.Hyperlink;
import com.projity.dialog.util.HyperlinkField;
import com.projity.field.Field;
import com.projity.pm.graphic.model.cache.GraphicNode;
import com.projity.pm.graphic.spreadsheet.SpreadSheetModel;
import com.projity.pm.graphic.spreadsheet.SpreadSheetParams;

/**
 * @author avigil
 *
 */
public class HyperlinkRenderer extends SimpleRenderer {

	/**
	 * 
	 */
	public HyperlinkRenderer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param compact
	 */
	public HyperlinkRenderer(boolean compact) {
		super(compact);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Component getComponent(Object value, GraphicNode node, Field field, SpreadSheetParams params) {
		// TODO Auto-generated method stub
		return super.getComponent("defaultHyperlinkValue", node, field, params);	
	}
	
	

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Field field = ((SpreadSheetModel)table.getModel()).getFieldInColumn(column+1);

		Hyperlink link;
		
		if (value instanceof Hyperlink) {
			link = (Hyperlink) value;
			
		} else if(value instanceof String){
			String[] values = ((String)value).split("\\@\\|\\@\\|\\@");
			
			if(values.length == 1){
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
			String address = values[0];
			
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
        return new HyperlinkField(link);
      
	}

	@Override
	protected void setValue(Object value) {
		if(value!=null){
			String[] values = ((String)value).split("\\@\\|\\@\\|\\@");
			String address = values[0];
			if(values.length == 1){
				super.setValue(address);
			} else {
				String label = values[1];
				String display = values[2];
				Hyperlink link = new Hyperlink(label,address);
				
				String displayText;
				if(label==null || label.length()==0){
					displayText=address;
				} else {
					displayText=label;
				}
				super.setValue(displayText);
			}
		} else {
			super.setValue(value);
		}
	}
	
	

}
