package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Recurrence;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SmartAddFormat {
	public static String formatPriority(Priority priority) {
		if(priority == null || priority.getLevel() == 4)
			return "";
		else
			return " !" + priority.getLevel();
	}
	
	public static String formatDate(int day, int month, int year) {
		Calendar date = Calendar.getInstance();
		date.set(year, month - 1, day);
		//qualunque sia la lingua in inglese lo capisce
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
		if(day > 0 && month > 0 & year > 0) {
			return " ^" + sdf.format(date.getTime());
		}
		else return "";
	}
	
	public static String formatTime(int hour, int minute) {
		if(hour >= 0 || minute >= 0) {
			return " " + hour + ":" + minute;
		}
		else return "";
	}
	
	public static String formatRepeat(Recurrence recurrence) {
		if(recurrence != null) {
			//formatta in inglese in altri linguaggi pu� non funzionare
			return " *" + TaskFormat.formatRecurrenceInEnglish(recurrence);
		}
		else return "";
	}
	
	public static String formatRepeat(String recurrence) {
		if(recurrence != null && !recurrence.equals("")) {
			return " *" + recurrence;
		}
		else return "";
	}
	
	public static String formatEstimate(float minutes, float hours, float days) {
		if(minutes != 0 || hours != 0 || days != 0) {
			StringBuilder sb = new StringBuilder(" =");
			if(minutes != 0) sb.append(minutes + "min ");
			if(hours != 0) sb.append(hours + "hour ");
			if(days != 0) sb.append(days + "day ");
			return sb.toString();
		}
		else return "";
	}
	
	
	public static String formatTaskList(String tasklist) {
		if(tasklist == null || tasklist.equals("")) return "";
		else return " #" + tasklist;
	}
	
	public static String formatLocation(String location) {
		if(location == null || location.equals("")) return "";
		else return " @" + location;
	}
	
	public static String formatTags(String tags) {
		if(tags == null || tags.equals("")) return "";
		else {
			String[] tagArray = tags.split("\\s+");
			StringBuilder sb = new StringBuilder("");
			for(String tag : tagArray) {
				sb.append(" #" + tag);
			}
			return sb.toString();
		}
	}
	
	public static String formatUrl(String url) {
		if(url.length() > 8) {
			String prefix1 = url.substring(0, 8);
			String prefix2 = prefix1.substring(0, 7);
			if(prefix1.equals("https://") || prefix2.equals("http://"))
				return " " + url;
			else 
				return " http://" + url;
		}
		else if(url.equals(""))
			return "";
		else
			return " http://" + url;
	}
}
