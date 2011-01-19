package com.projity.datatype;

import java.awt.Font;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.UIManager;

/**
 * @author avigil
 * 
 */
public class CurrencySymbolConversion {

	private static final Map<String, String> CURRENCY_SYMBOLS = new HashMap<String, String>();
	private static Font systemFont = (Font)UIManager.get("Label.font");

	static {
		System.out.println("systemFont: "+systemFont.getFontName());
		if((systemFont.canDisplayUpTo("\u00a5") < 0)){
			CURRENCY_SYMBOLS.put("JPY", "\u00a5");
		}
		if((systemFont.canDisplayUpTo("\u00a5") < 0)){
			CURRENCY_SYMBOLS.put("CNY", "\u00a5");
		}
		if((systemFont.canDisplayUpTo("\u20a9") < 0)){
			CURRENCY_SYMBOLS.put("KRW", "\u20a9");
		}
		if((systemFont.canDisplayUpTo("\u20b1") < 0)){
			CURRENCY_SYMBOLS.put("CUP", "\u20b1");
			System.out.println("could display cup string. value is: "+CURRENCY_SYMBOLS.get("CUP"));
		} 
		
	/*	
		 //TODO:  This still doesn't display.  We can screw with this later 
		 else if(systemFont.canDisplayUpTo(new String("\u20b1".getBytes())) <0){
			CURRENCY_SYMBOLS.put("CUP", new String("\u20b1".getBytes()));
			System.out.println("could display cup string after recreated.  value is: "+CURRENCY_SYMBOLS.get("CUP"));
		}
		
		else{
			try {
				if(systemFont.canDisplayUpTo(new String("\u20b1".getBytes("UTF8"), "UTF8")) <0){
					CURRENCY_SYMBOLS.put("CUP", new String("\u20b1".getBytes(), "UTF8"));
					System.out.println("could display cup string after recreated using UTF8 encoding.  value is: "+CURRENCY_SYMBOLS.get("CUP"));
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		
		if((systemFont.canDisplayUpTo("\u20a8") < 0)){
			CURRENCY_SYMBOLS.put("LKR", "\u20a8");
		}
		//test print these out
		for(String key:CURRENCY_SYMBOLS.keySet()){
			
			System.out.println(key+": "+CURRENCY_SYMBOLS.get(key));
		}
	}

	/**
	 * The standard Currency.getSymbol() is not always the currency symbol, for
	 * example en_US returns EUR for Euro's.
	 * 
	 * @return Returns the currency's symbol or the currency code if could not
	 *         find one.
	 */
	public static String getCurrencySymbol(String currencyCode) {
		if (currencyCode == null) {
			return "";
		}

		currencyCode = currencyCode.toUpperCase();

		// cache values as this code gets called a lot from table cell renderers
		String symbol = CURRENCY_SYMBOLS.get(currencyCode);
		if (symbol != null) {
			return symbol;
		}

		Currency currency = Currency.getInstance(currencyCode);

		// check the original locale first, mostly likely to match
		Locale locale = Locale.getDefault();
		symbol = currency.getSymbol(locale);
		if (!symbol.equals(currencyCode) && (systemFont.canDisplayUpTo(symbol) < 0) ) {
			CURRENCY_SYMBOLS.put(currencyCode, symbol);
			return symbol;
		}

		Locale[] allLocales = Locale.getAvailableLocales();
		for (int i = 0; i < allLocales.length; i++) {
			symbol = currency.getSymbol(allLocales[i]);
			if (!symbol.equals(currencyCode) && (systemFont.canDisplayUpTo(symbol) < 0) ) {
				CURRENCY_SYMBOLS.put(currencyCode, symbol);
				return symbol;
			}
		}

		CURRENCY_SYMBOLS.put(currencyCode, currencyCode);
		return currencyCode;
	}

}
