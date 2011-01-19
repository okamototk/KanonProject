/**
 *
 */
package com.projity.pm.graphic.spreadsheet.renderer;

import com.projity.datatype.Money;

/**
 * @author avigil
 *
 */
public class MoneyRenderer extends SimpleRenderer {

	@Override
	protected void setValue(Object value) {
		// TODO Auto-generated method stub
		String strValue = "";
		if (value instanceof String) {
			strValue = (String) value;

		} else if (value instanceof Money) {
			Money moneyValue = (Money) value;
			strValue = ((Money)value).getFormattedValue();
		}
		//System.out.println("Value is: "+strValue);
		super.setValue(strValue);
	}



}
