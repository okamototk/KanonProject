/**
 *
 */
package com.projity.pm.graphic.spreadsheet.renderer;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.projity.field.FieldConverter;
import com.projity.pm.graphic.spreadsheet.common.CommonSpreadSheetModel;
import com.projity.util.Environment;

/**
 * @author avigil
 *
 */
public class NumberRenderer extends SimpleRenderer {

		private NumberFormat integerFormatter = NumberFormat.getIntegerInstance();
		private DecimalFormat decimalFormatter = new DecimalFormat();

		public NumberRenderer() {
			super();

			integerFormatter.setGroupingUsed(true);
			decimalFormatter.setGroupingUsed(true);
		}
		public NumberRenderer(boolean compact) {
			super(compact);

			integerFormatter.setGroupingUsed(true);
			decimalFormatter.setGroupingUsed(true);
		}

		/**
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {

//			 TODO Auto-generated method stub
			JLabel component;
			if(table==null){
				if(Environment.isNoPodServer()){
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				} else {
					setValue(null);
					component=this;
				}
			}
			else{
				component=(JLabel)super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
				CommonSpreadSheetModel model=(CommonSpreadSheetModel)table.getModel();
			    FontManager.setComponentFont(model.getCellProperties(model.getNode(row)),component);
			}


			if (value!=null){
				if(value instanceof Double) {
					component.setText(decimalFormatter.format((Double)value));
				} else if (value instanceof Integer) {
					component.setText(integerFormatter.format((Integer)value));
				}
			}

			component.setHorizontalAlignment(SwingConstants.RIGHT);
			return component;
		}
}
