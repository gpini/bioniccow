package it.bova.bioniccow.data;

import it.bova.rtmapi.Settings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

public class AccountSettings extends DataObservable<Settings> {
	
	public AccountSettings(Context context) {
		super(context);
	}

	private static Settings settings;
	private static List<DataObserver<Settings>> observers = 
			new ArrayList<DataObserver<Settings>>();
	private static final String SETTINGS_FILENAME = "settings.dat";
	
	
	@Override
	Settings getData() {
		return settings;
	}

	@Override
	void setData(Settings set) {
		if(set != null) settings = set;
	}

	@Override
	Settings emptyData() {
		String timezone = Calendar.getInstance().getTimeZone().getID(); 
		String language = Locale.getDefault().getLanguage();
		return new Settings(timezone, language, 1, 0, "");
	}

	@Override List<DataObserver<Settings>> getObservers() {
		return observers;
	}
	
	@Override String getFileName() {
		return SETTINGS_FILENAME;
	}
	
	public Locale getLocale(Settings settings) {
		String language = settings.getLanguage();
		if(language.equals("")) return Locale.getDefault();
		else return new Locale(language);
	}
	
	public TimeZone getTimeZone() {
		String timezone = settings.getTimezone();
		if(timezone.equals("")) return Calendar.getInstance().getTimeZone();
		return TimeZone.getTimeZone(timezone);
	}
}
