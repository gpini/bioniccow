package it.bova.bioniccow.utilities.rtmobjects;

import java.util.Calendar;
import java.util.Date;

public class SmartDateComparator {

	public static boolean isOverdue(Date now, Date d){
		Calendar now2 = Calendar.getInstance();
		now2.setTime(now);
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if(!isToday(now, d) &&
				date.getTime().before(now))
			return true;
		else return false;
	}
	
	public static boolean isToday(Date now, Date d){
		Calendar now2 = Calendar.getInstance();
		now2.setTime(now);
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if(date.get(Calendar.ERA) == now2.get(Calendar.ERA) &&
				date.get(Calendar.YEAR) == now2.get(Calendar.YEAR) &&
				date.get(Calendar.MONTH) == now2.get(Calendar.MONTH) &&
				date.get(Calendar.WEEK_OF_YEAR) == now2.get(Calendar.WEEK_OF_YEAR) &&
				date.get(Calendar.DAY_OF_YEAR) == now2.get(Calendar.DAY_OF_YEAR))
			return true;
		else return false;
	}
	
	public static boolean isInNextDaysOfThisWeek(Date now, Date d){
		Calendar now2 = Calendar.getInstance();
		now2.setTime(now);
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if(date.get(Calendar.ERA) == now2.get(Calendar.ERA) &&
				date.get(Calendar.YEAR) == now2.get(Calendar.YEAR) &&
				date.get(Calendar.MONTH) == now2.get(Calendar.MONTH) &&
				date.get(Calendar.WEEK_OF_YEAR) == now2.get(Calendar.WEEK_OF_YEAR) &&
				date.get(Calendar.DAY_OF_YEAR) > now2.get(Calendar.DAY_OF_YEAR))
			return true;
		else return false;
	}
	
	public static boolean isLaterThanThisWeek(Date now, Date d){
		Calendar now2 = Calendar.getInstance();
		now2.setTime(now);
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if(!isToday(now, d) &&
				!isInNextDaysOfThisWeek(now, d) &&
				date.getTime().after(now))
			return true;
		else return false;
	}
	
}
