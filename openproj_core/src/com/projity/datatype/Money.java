/*
The contents of this file are subject to the Common Public Attribution License 
Version 1.0 (the "License"); you may not use this file except in compliance with 
the License. You may obtain a copy of the License at 
http://www.projity.com/license . The License is based on the Mozilla Public 
License Version 1.1 but Sections 14 and 15 have been added to cover use of 
software over a computer network and provide for limited attribution for the 
Original Developer. In addition, Exhibit A has been modified to be consistent 
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis, 
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the 
specific language governing rights and limitations under the License. The 
Original Code is OpenProj. The Original Developer is the Initial Developer and 
is Projity, Inc. All portions of the code written by Projity are Copyright (c) 
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the 
Projity End-User License Agreeement (the Projity License), in which case the 
provisions of the Projity License are applicable instead of those above. If you 
wish to allow use of your version of this file only under the terms of the 
Projity License and not to allow others to use your version of this file under 
the CPAL, indicate your decision by deleting the provisions above and replace 
them with the notice and other provisions required by the Projity  License. If 
you do not delete the provisions above, a recipient may use your version of this 
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices 
in Exhibits A and B of the license at http://www.projity.com/license. You should 
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright © 2006, 2007 
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj, 
an open source solution from Projity. Attribution URL: http://www.projity.com 
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with 
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined 
in the CPAL as a work which combines Covered Code or portions thereof with code 
not governed by the terms of the CPAL. However, in addition to the other notice 
obligations, all copies of the Covered Code in Executable and Source Code form 
distributed must, as a form of attribution of the original author, include on 
each user interface screen the "OpenProj" logo visible to all users.  The 
OpenProj logo should be located horizontally aligned with the menu bar and left 
justified on the top left of the screen adjacent to the File menu.  The logo 
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it 
must direct them back to http://www.projity.com.  
*/
package com.projity.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import com.projity.strings.Messages;

/**
 * 
 */
public class Money extends BigDecimal {
	private static final long serialVersionUID = -8182666966278921881L;
	private static NumberFormat moneyFormat = null;
	private static DecimalFormat moneyCompactFormat = null;
	private static String CurrencyCode;
	private static Currency CURRENCY;
	private static Locale Locale;
	
	static{
		moneyFormat = NumberFormat.getCurrencyInstance();
		CURRENCY = moneyFormat.getCurrency();
		CurrencyCode = CURRENCY.getCurrencyCode();
	}
	
	public static void setCurrency(String currencyCode) {
		CurrencyCode = currencyCode;
		
		CURRENCY = Currency.getInstance(currencyCode);
		getMoneyFormatInstance().setCurrency(CURRENCY);
		
		getMoneyCompactFormatInstance().setCurrency(CURRENCY);
	}
	public static NumberFormat getMoneyFormatInstance() {
		if (moneyFormat == null) {
			if(null == Locale){
				moneyFormat = NumberFormat.getCurrencyInstance();
			} else {
				moneyFormat = NumberFormat.getCurrencyInstance(Locale);
			}
			moneyFormat.setGroupingUsed(true);	
			CURRENCY = moneyFormat.getCurrency();
		}
		
		return moneyFormat;
	}
	public static NumberFormat getMoneyCompactFormatInstance() {
		if (moneyCompactFormat == null) {
			moneyCompactFormat = ( null == Locale ) ? (DecimalFormat)NumberFormat.getCurrencyInstance() : (DecimalFormat)NumberFormat.getCurrencyInstance(Locale);
			moneyCompactFormat.setGroupingUsed(false);
			moneyCompactFormat.setMaximumFractionDigits(0);
		}
		return moneyCompactFormat;
	}
	
	public static NumberFormat getFormat(boolean compact) {
		return compact ? getMoneyCompactFormatInstance() : getMoneyFormatInstance() ;
	}
	
	public static Money getInstance(double arg0) {
		return new Money(arg0);
	}
	
	public static Money getInstance(String arg0) {
		return new Money(arg0);
	}
	public static Money getInstance(Number arg0) {
		if(null == arg0){
			return getInstance(0.0);
		} else {
			return getInstance(arg0.doubleValue());
		}
	}
	
	public static void setLocale(Locale l) {
		Locale = l;
	}

	/**
	 * @param arg0
	 */
	private Money(double arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public Money(String arg0) throws NumberFormatException{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public Money(BigInteger arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public Money(BigInteger arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public double getPrimitiveTypeValue()
	{
		return doubleValue();
	}
	
	public static String formatCurrency(double value,boolean compact){
		String formatted;
		if (compact){
			if (value<100) formatted =  normalCurrencyFormat(value,Math.floor(value)==value);
			else if (value<10000){ 
				value=Math.floor(value);
				formatted =  normalCurrencyFormat(value,true);
			}else if (value<100000){
				value=value/1000;
				formatted =  normalCurrencyFormat(value,Math.floor(value)==value)+Messages.getString("Text.thousandsAbbreviation"); //$NON-NLS-1$
			}else if (value<1000000){
				value=value/1000;
				formatted =  normalCurrencyFormat(value,true)+Messages.getString("Text.thousandsAbbreviation"); //$NON-NLS-1$
			}else if (value<100000000){
				value=value/1000000;
				formatted =  normalCurrencyFormat(value,Math.floor(value)==value)+Messages.getString("Text.millionsAbbreviation"); //$NON-NLS-1$
			}else{
				value=value/1000000;
				formatted =  normalCurrencyFormat(value,true)+Messages.getString("Text.millionsAbbreviation"); //$NON-NLS-1$
			}
		}else formatted =  normalCurrencyFormat(value, false);
		
		formatted=replaceCurrencyCode(formatted);

		return formatted;
	}
	
	public static String parseCurrency(String value){
		
		value = replaceCurrencySymbol(value);
		Number val = 0.0;
		try {
			val = moneyFormat.parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				val = moneyFormat.getNumberInstance().parse(value);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		val = val.doubleValue();
		
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(0);
		String retVal = df.format(val);
		System.out.println("returning : "+retVal);
		return retVal;
	}
	
	public static Number parseCurrencyToDouble(String value){
		
		Number val = 0.0;
		
		try {
			val = moneyFormat.parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//value does not have currency symbol or code
			e.printStackTrace();
			try {
				value = stripCurrencySymbol(value);
				val = moneyFormat.getNumberInstance().parse(value);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		val = val.doubleValue();
		
		System.out.println("returning : "+val);
		return val;
	}
	
	public static String stripCurrencySymbol(String val){
		String newCurrencySymbol = CurrencySymbolConversion.getCurrencySymbol(CurrencyCode);

		String newVal = val.replace(newCurrencySymbol, "");
		return newVal;
	}
	
	public static String replaceCurrencyCode(String val){
		String newCurrencySymbol = CurrencySymbolConversion.getCurrencySymbol(CurrencyCode);

		String newVal = val.replace(CurrencyCode, newCurrencySymbol);
		return newVal;
	}
	
	public static String replaceCurrencySymbol(String val){
		String newCurrencySymbol = CurrencySymbolConversion.getCurrencySymbol(CurrencyCode);
		String newVal = val.replace(newCurrencySymbol, CurrencyCode);
		return newVal;
	}
	
	public static String normalCurrencyFormat(double value,boolean compact){
		String val =  compact?Money.getMoneyCompactFormatInstance().format(value):Money.getMoneyFormatInstance().format(value);
		val = replaceCurrencyCode(val);
		//System.out.println("returning: "+val+" from Money.normalCurrencyFormat()");
		return val;
	}

	public String getFormattedValue() {

		return Money.normalCurrencyFormat(doubleValue(), false);
	}
	
	
	}
	
