package com.projity.field;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.projity.datatype.Duration;
import com.projity.datatype.DurationFormat;
import com.projity.datatype.Money;
import com.projity.datatype.Rate;
import com.projity.datatype.RateFormat;
import com.projity.datatype.Work;
import com.projity.options.CalendarOption;
import com.projity.pm.calendar.WorkCalendar;
import com.projity.util.DateTime;

public class FieldDataConverter {
	private DateFormat dateFormat = DateTime.getYyyyMmDd();
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	public void setDurationMultiplier(double durationMultiplier) {
		this.durationMultiplier = durationMultiplier;
	}
	public void setRateMultiplier(double rateMultiplier) {
		this.rateMultiplier = rateMultiplier;
	}
//	private double durationMultiplier = CalendarOption.getInstance().getHoursPerDay() * WorkCalendar.MILLIS_IN_HOUR;
//	private double rateMultiplier = 1.0 / WorkCalendar.MILLIS_IN_HOUR;
	private double durationMultiplier = 1.0;//CalendarOption.getInstance().getHoursPerDay() * WorkCalendar.MILLIS_IN_HOUR;
	private double rateMultiplier = 1.0;
	private RateFormat moneyRate = new RateFormat(null,true,false,true);

	public static FieldDataConverter createDayInstance() {
		FieldDataConverter con = new FieldDataConverter();
		con.durationMultiplier = CalendarOption.getInstance().getHoursPerDay() * WorkCalendar.MILLIS_IN_HOUR;
		con.rateMultiplier = 1.0 / WorkCalendar.MILLIS_IN_HOUR;
		return con;
		
	}
	
	public long convertToDateLong(Object obj) {
		if (obj == null)
			return 0;
		if (obj instanceof Long)
			return ((Long) obj).longValue();
		else if (obj instanceof Date)
			return ((Date)obj).getTime();
		else if (obj instanceof String) {
			try {
				return dateFormat.parse((String) obj).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		} else if (obj instanceof Calendar)
			return ((Calendar)obj).getTimeInMillis();
		return 0;
		
	}
	public Object convert(Field field, Object obj) {
		try {
			if (field.isDate()) {
				return convertToDateLong(obj);
			} else if (field.isRate()) {
				if (obj instanceof Rate)
					return obj;
				if (obj instanceof Number) {
					return ((Number)obj).doubleValue() * rateMultiplier; 
				} else if (obj instanceof String) {
					return moneyRate.parseObject((String)obj);
				}
				return null;
			} else if (field.isMoney()) {
				if (obj instanceof Number)
					return Money.getInstance(((Number)obj).doubleValue());
				else if (obj instanceof Money)
					return obj;
				else if (obj instanceof String) {
					return Money.getInstance(Double.valueOf((String)obj));
				}
				return null;
			} else if (field.isWork()) {
				if (obj instanceof Work)
					return obj;
				else if (obj instanceof Number) {
					return Work.getInstanceFromDouble(((Number)obj).doubleValue() * durationMultiplier);
				} else if (obj instanceof String) {
					Duration d =  (Duration) DurationFormat.getInstance().parseObject((String) obj);
					return new Work(Duration.millis(d.getEncodedMillis()));
				} else if (obj instanceof Duration) {
					Duration d = (Duration)obj;
					return new Work(d.getEncodedMillis());
				}
				return null;
			} else if (field.isDuration()) {
				if (obj instanceof Duration)
					return obj;
				else if (obj instanceof Number) {
					return Duration.getInstanceFromDouble(((Number)obj).doubleValue() * durationMultiplier);
				} else if (obj instanceof String) {
					return DurationFormat.getInstance().parseObject((String) obj);
				} else if (obj instanceof Work) {
					Work d = (Work)obj;
					return new Duration(d.getEncodedMillis());
				}
				return null;
			} else if (field.isNumber()) {
				if (obj instanceof Number) {
					return obj; 
				} else if (obj instanceof String) {
					return Double.valueOf((String) obj);
				} else if (obj instanceof Duration || obj instanceof Work) {
					Duration.millis(((Duration)obj).getEncodedMillis());
				} else if (obj instanceof Rate) {
					return ((Rate)obj).getValue() / rateMultiplier;
				}
				return null;
			}
		} catch (ParseException e) {
			return null;
		}
		return obj;
	}
}
