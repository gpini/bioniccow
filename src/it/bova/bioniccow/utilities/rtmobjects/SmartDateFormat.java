package it.bova.bioniccow.utilities.rtmobjects;

import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SmartDateFormat {

	boolean isPast(Date d){
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if(date.get(Calendar.ERA) < now.get(Calendar.ERA) ||
				date.get(Calendar.YEAR) < now.get(Calendar.YEAR) ||
				date.get(Calendar.MONTH) < now.get(Calendar.MONTH) ||
				date.get(Calendar.WEEK_OF_YEAR) < now.get(Calendar.WEEK_OF_YEAR) ||
				date.get(Calendar.DAY_OF_YEAR) < now.get(Calendar.DAY_OF_YEAR)) return true;
		else return false;
	}

	public static String format(String[] strings, Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern(strings, d, false));
		return sdf.format(d);
	}
	
	public static String formatInEnglish(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		return sdf.format(d);
	}
	
	public static String formatInEnglishWithHour(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy 'at' KK:mmaa", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getDefault());
		String date = sdf.format(d);
		return date;
	}
	
	public static String formatExtended(String[] strings, Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern(strings, d, true));
		return sdf.format(d);
	}


	private static String pattern(String[] strings, Date d, boolean weekDay) {
		if(strings.length < 21)
			return "EEE dd MMM yyyy";
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if(date.get(Calendar.ERA) == now.get(Calendar.ERA)) {
			if(date.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
				int daysBetween = date.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR);
				if(daysBetween < 7 && daysBetween > -7) {
					if(daysBetween == 0) return "'" + strings[12] + "'";
					else if(daysBetween == 1) return "'" + strings[13] + "'";
					else if(daysBetween == -1) return "'" + strings[11] + "'";
					else if(daysBetween > 1 && daysBetween < 7) {
						int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
						switch(dayOfWeek) {
						case Calendar.MONDAY :
							return "'" + strings[14] + "'";
						case Calendar.TUESDAY :
							return "'" + strings[15] + "'";
						case Calendar.WEDNESDAY :
							return "'" + strings[16] + "'";
						case Calendar.THURSDAY :
							return "'" + strings[17] + "'";
						case Calendar.FRIDAY :
							return "'" + strings[18] + "'";
						case Calendar.SATURDAY :
							return "'" + strings[19] + "'";
						case Calendar.SUNDAY :
							return "'" + strings[20] + "'";
						default : return "EEE dd MMM yyyy";
						}
					}
					else {
						int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
						switch(dayOfWeek) {
						case Calendar.MONDAY :
							return "'" + strings[4] + "'";
						case Calendar.TUESDAY :
							return "'" + strings[5] + "'";
						case Calendar.WEDNESDAY :
							return "'" + strings[6] + "'";
						case Calendar.THURSDAY :
							return "'" + strings[7] + "'";
						case Calendar.FRIDAY :
							return "'" + strings[8] + "'";
						case Calendar.SATURDAY :
							return "'" + strings[9] + "'";
						case Calendar.SUNDAY :
							return "'" + strings[10] + "'";
						default : return "EEE dd MMM yyyy";
						}
					}
				}
				else
					if(weekDay) return strings[3]; 
					else return strings[1]; 
			}
			else if(date.get(Calendar.YEAR) > now.get(Calendar.YEAR))
				if(weekDay) return strings[2];
				else return strings[0];
			else
				if(weekDay) return strings[2];
				else return strings[0];
		}
		else return "";
	}

}
