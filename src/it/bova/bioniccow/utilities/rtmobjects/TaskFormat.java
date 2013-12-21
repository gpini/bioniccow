package it.bova.bioniccow.utilities.rtmobjects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.bova.rtmapi.Frequency;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.Recurrence.RecurrenceOption;
import it.bova.rtmapi.Task;
import it.bova.rtmapi.TaskList;

public class TaskFormat {
	private String blue; //da getResources().getColor(int colorId);
	private String orange; //da getResources().getColor(int colorId);
	private String[] dateFormatStrings;
	
	public TaskFormat(String[] dateFormatStrings, String blue, String orange) {
		this.blue = blue;
		this.orange = orange;
		this.dateFormatStrings = dateFormatStrings;
	}
	
	public TaskFormat(String[] dateFormatStrings) {
		this.blue = "#0000FF";
		this.orange = "##FF9900";
		this.dateFormatStrings = dateFormatStrings;
	}
		
	public String formatLabels(Task task, Map<String,TaskList> listMap, Map<String,Location> locMap, boolean useHtmlFormat) {
		StringBuilder sb = new StringBuilder("");
		String list = this.formatList(task, listMap);
		String loc = formatLocation(task, locMap, useHtmlFormat);
		String tags = formatTags(task, useHtmlFormat);
		if(!list.equals("")) sb.append(list);
		if(!loc.equals("")) sb.append(" " + loc);
		if(!tags.equals("")) sb.append(" " + tags);
		return sb.toString();
	}
	
	public String formatDate(Task task) {
		StringBuilder sb = new StringBuilder("");
		Date due = task.getDue();
		if(due != null) {
			String date = SmartDateFormat.format(dateFormatStrings, due);
			if(task.getHasDueTime()){
				DateFormat df2 = DateFormat.getTimeInstance(DateFormat.SHORT);
				String time = df2.format(due);
				Formatter formatter = new Formatter();
				String dateAndTime = formatter.format(dateFormatStrings[21], date, time).toString();
				formatter.close();
				sb.append(dateAndTime);
			}
			else 
				sb.append(date);
		}
		return sb.toString();
	}
		
	public String formatList(Task task, Map<String,TaskList> listMap) {
		StringBuilder sb = new StringBuilder("");
		TaskList list = listMap.get(task.getListId());
		String listName = "";
		if(list == null) listName = "----";
		else listName = list.getName();
		sb.append(listName);
		return sb.toString();
	}
		
	public String formatTags(Task task, boolean useHtmlFormat) {
		StringBuilder sb = new StringBuilder("");
		String[] tags = task.getTags();
		if(tags != null & tags.length != 0) {
			if(useHtmlFormat)
				sb.append("<font color=\"" + blue + "\">" + this.formatTags(tags,"","") + "</font>");
			else
				sb.append(this.formatTags(tags,"",""));
		}
		return sb.toString();
	}
	
	public String formatLocation(Task task, Map<String,Location> locMap, boolean useHtmlFormat) {	
		StringBuilder sb = new StringBuilder("");
		if(task.getLocationId() != null & !task.getLocationId().equals("")) {
			Location loc = locMap.get(task.getLocationId());
			String locName = "";
			if(loc == null) locName = "----";
			else locName = loc.getName();
			if(useHtmlFormat)
				sb.append("<font color=\"" + orange + "\"><i> " + locName + "</i></font>");
			else
				sb.append(" " + locName);
		}
		return sb.toString();
	}

	private String formatTags(String[] tags, String prefix, String suffix) {
		StringBuilder sb = new StringBuilder("");
		if(tags != null) {
			for(String tag : tags) {
				sb.append(" " + prefix + tag + suffix);
			}
		}
		return sb.toString();
	}
	
	public static String formatRepeatOptionInEnglish(Recurrence rec) {
		if(rec.hasOption())
			return formatRepeatOptionInEnglish(rec.getOption(), rec.getOptionValue()); 
		else return "";
	}
	
	public static String formatRepeatOptionInEnglish(RecurrenceOption option, String value) {
		String[] strings = new String[] {"once", "for %s times", "until %s", "on %s", "%s",
			"%1s %2s", "and", "unknown day", 
			"unknown by-day option", "unknown month day", "unknown date", "at %s"};
		String[] ordinals1 = new String[] {
			"on the 1st", "on the 2nd", "on the 3rd", "on the 4th", "on the 5th",
			"on the 6th", "on the 7th", "on the 8th", "on the 9th", "on the 10th",
			"on the 11th", "on the 12th", "on the 13th", "on the 14th", "on the 15th",
			"on the 16th", "on the 17th", "on the 18th", "on the 19th", "on the 20th",
			"on the 21st", "on the 22nd", "on the 23rd", "on the 24th", "on the 25th",
			"on the 26th", "on the 27th", "on the 28th", "on the 29th", "on the 30th",
			"on the 31st"};
		String[] ordinals2 = new String[] {
			"on the 1st", "on the 2nd", "on the 3rd", "on the 4th", "on the 5th",
			"on the last", "on the 2nd last", "on the 3rd last", "on the 4th last", "on the 5th last"};
		String[] weekdaysStrings =  new String[] {"Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		String[] dateFormatStrings =  new String[] {"past", "yesterday",
				"today", "tomorrow", "next"};
		return formatRepeatOption(true, dateFormatStrings, strings, ordinals1, ordinals2, weekdaysStrings, option, value); 
	}
	
	public String formatRepeatOption(String[] strings, String[] ordinals1, String[] ordinals2, String[] weekdaysStrings, RecurrenceOption option, String value) {
		return formatRepeatOption(false, this.dateFormatStrings, strings, ordinals1, ordinals2, weekdaysStrings, option, value);
	}	
	
	private static String formatRepeatOption(boolean forceEnglish, String[] dateFormatStrings, String[] strings, String[] ordinals1, String[] ordinals2, String[] weekdaysStrings, RecurrenceOption option, String value) {
		StringBuilder sb = new StringBuilder("");
		Formatter f = new Formatter();
			switch(option) {
			case COUNT :
				if(value.equals("1"))
					sb.append(strings[0]); //once
				else sb.append(f.format(strings[1], value)); //for %s times
				break;
			case BYDAY : 
				String firstChar = value.substring(0,1);
				Pattern p = Pattern.compile("[\\d\\-]");
				Matcher m = p.matcher(firstChar);
				boolean isFirstDigit = m.matches();
				if(isFirstDigit) {//by day
					try{
						int dayKind = Integer.parseInt(value.substring(0,value.length()-2));
						String weekdayString = value.substring(value.length()-2);
						sb.append(f.format(strings[5], formatOrdinalByDay(ordinals2, dayKind), formatWeekDay(weekdaysStrings, weekdayString)));
					} catch(NumberFormatException e) {
						sb.append(strings[7]);
					}
				}
				else {
					StringBuilder sb2 = new StringBuilder("");
					String[] weekdays = value.split(",");
					if(weekdays.length == 0)
						weekdays = new String[] {value};
					for(int i = 0; i < weekdays.length; i++) {
						sb2.append(formatWeekDay(weekdaysStrings, weekdays[i]));
						if(i == weekdays.length - 1)
							sb2.append("");
						else if(i == weekdays.length - 2)
							sb2.append(" " + strings[6] + " ");
						else 
							sb2.append( ", ");
					}
					sb.append(f.format(strings[3], sb2.toString()));
				}
				break;
			case BYMONTHDAY :
				try{
					int day = Integer.parseInt(value);
					sb.append(f.format(strings[4], formatOrdinalByMonthDay(ordinals1, day)));
				} catch(NumberFormatException e) {
					sb.append(f.format(strings[4], strings[9]));
				}
				break;
			case UNTIL :
				try {
					SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd'T'HHmmss" );
					if(forceEnglish) {
						sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
						Date toDate = sdf.parse(value);
						sb.append(f.format(strings[2], SmartDateFormat.formatInEnglishWithHour(toDate)));
					}
					else {
						sdf.setTimeZone(TimeZone.getDefault());
						Date toDate = sdf.parse(value);
						sb.append(f.format(strings[2], SmartDateFormat.format(dateFormatStrings, toDate)));
						Calendar cal = Calendar.getInstance();
						cal.setTime(toDate);
						if(cal.get(Calendar.MINUTE) != 0 || (cal.get(Calendar.HOUR_OF_DAY) != 0 && cal.get(Calendar.HOUR_OF_DAY) != 24)) {
							DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
							Formatter f2 = new Formatter();
							sb.append(" " + f2.format(strings[11], df.format(cal.getTime())));
							f2.close();
						}
					}
				} catch (ParseException e) {
					sb.append(f.format(strings[2], strings[10]));
				}
				break;
			}
		f.close();
		return sb.toString();
	}
	
	public String formatRepeatOption(String[] strings, String[] ordinals1, String[] ordinals2, String[] weekdaysStrings, Recurrence rec) {
		if(rec != null && rec.hasOption()) {
			RecurrenceOption option = rec.getOption();
			String value = rec.getOptionValue();
			return formatRepeatOption(false, this.dateFormatStrings, strings, ordinals1, ordinals2, weekdaysStrings, option, value);
		}
		else return "";
	}

	
	public static String formatRecurrenceInEnglish(Recurrence rec) {
		StringBuilder sb = new StringBuilder("");
		if(rec != null) {
			sb.append(formatRepeatFrequencyInEnglish(rec));
			if(rec.hasOption())
				sb.append(" " + formatRepeatOptionInEnglish(rec));	
		}
		return sb.toString();
	}

	public static String formatRepeatFrequencyInEnglish(Recurrence rec) {
		if(rec != null) {
			boolean every = rec.isEvery();
			Frequency freq = rec.getFrequency();
			int interval = rec.getInterval();
			return formatRepeatFrequencyInEnglish(freq, interval, every);
		}
		else return "";
	}

	public static String formatRepeatFrequencyInEnglish(Frequency freq, int interval, boolean every) {
		StringBuilder sb = new StringBuilder("");

		if(every) sb.append("every ");
		else sb.append("after ");
		switch(freq) {
		case DAILY :
			if(interval == 1)
				if(every) sb.append(" day");
				else sb.append("1 day");
			else sb.append(interval + " days");
			break;
		case WEEKLY :
			if(interval == 1)
				if(every) sb.append(" week");
				else sb.append("1 week");
			else sb.append(interval + " weeks");
			break;
		case MONTHLY :
			if(interval == 1) 
				if(every) sb.append(" month");
				else sb.append("1 month");
			else sb.append(interval + " months");
			break;
		case YEARLY :
			if(interval == 1)
				if(every) sb.append(" year");
				else sb.append("1 year");
			else sb.append(interval + " years");
			break;
		default :
			sb.append(interval + "???");
		}
		return sb.toString();
	}
	
	private static String formatWeekDay(String[] weekdays, String weekDay) {
		if(weekDay.equals("MO")) return weekdays[0];
		else if(weekDay.equals("TU")) return weekdays[1];
		else if(weekDay.equals("WE")) return weekdays[2];
		else if(weekDay.equals("TH")) return weekdays[3];
		else if(weekDay.equals("FR")) return weekdays[4];
		else if(weekDay.equals("SA")) return weekdays[5];
		else if(weekDay.equals("SU")) return weekdays[6];
		else return "???";
	}
	
	private static String formatOrdinalByDay(String[] ordinals, int value) {
		int tenRemainder = value % 10;
		switch (tenRemainder) {
			case 1:
				return ordinals[0];
			case 2:
				return ordinals[1];
			case 3:
				return ordinals[2];
			case 4:
				return ordinals[3];
			case 5:
				return ordinals[4];
			case -1:
				return ordinals[5];
			case -2:
				return ordinals[6];
			case -3:
				return ordinals[7];
			case -4:
				return ordinals[8];
			case -5:
				return ordinals[9];
			default :
				return "";
		}
	}
	
	private static String formatOrdinalByMonthDay(String[] ordinals, int value) {
		switch(value) {
			case 1:
				return ordinals[0];
			case 2:
				return ordinals[1];
			case 3:
				return ordinals[2];
			case 4:
				return ordinals[3];
			case 5:
				return ordinals[4];
			case 6:
				return ordinals[5];
			case 7:
				return ordinals[6];
			case 8:
				return ordinals[7];
			case 9:
				return ordinals[8];
			case 10:
				return ordinals[9];
			case 11:
				return ordinals[10];
			case 12:
				return ordinals[11];
			case 13:
				return ordinals[12];
			case 14:
				return ordinals[13];
			case 15:
				return ordinals[14];
			case 16:
				return ordinals[15];
			case 17:
				return ordinals[16];
			case 18:
				return ordinals[17];
			case 19:
				return ordinals[18];
			case 20:
				return ordinals[19];
			case 21:
				return ordinals[20];
			case 22:
				return ordinals[21];
			case 23:
				return ordinals[22];
			case 24:
				return ordinals[23];
			case 25:
				return ordinals[24];
			case 26:
				return ordinals[25];
			case 27:
				return ordinals[26];
			case 28:
				return ordinals[27];
			case 29:
				return ordinals[28];
			case 30:
				return ordinals[29];
			case 31:
				return ordinals[30];
			default :
				return "";
		}
	}
	
	
}
