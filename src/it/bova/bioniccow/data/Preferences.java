package it.bova.bioniccow.data;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences  {
	
	private SharedPreferences prefs;
	private Editor editor;
	
	public enum PrefParameter {
		INSTALLATION_DATE("install_date"),
		LAST_NAVIGATION_OPTION("last_nav_opt"),
		LAST_SYNCH("last_synch"),
		TOKEN("token"),
		FIRST_SYNC_DONE("synch_done"),
		FIRST_TOKEN_SET_DONE("token_set"),
		NAVIGATION_HINT_SHOWED("navigation"),
		FOLDER_HELP_SHOWN("help_folder"),
		TIMELINE("timeline"),
		HIDE_FOLDERS("hide_folders"),
		VOTE_REQUESTED("voted");
		private String name;
		private PrefParameter(String name) {this.name = name;}
		public String toString() {return this.name;}
	}
	
	public Preferences(Context context) {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context); 
		this.editor = prefs.edit();
	}
	
	/**
	 * Check the existance of a paramater
	 * @param param the parameter type
	 * @return true if the parameter is found, false otherwise
	 */
	public boolean contains(PrefParameter param) {
		return this.prefs.contains(param.toString());
	}
	
	/**
	 * Gets the desired string
	 * @param param the parameter type
	 * @return the desired String if found, "" if not found or if it is not a String object
	 */
	public String getString(PrefParameter param, String defValue) {
		try {
			return this.prefs.getString(param.toString(), defValue);
		} catch (ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Gets the desired boolean
	 * @param param the parameter type
	 * @return the desired boolean if found, false if not found or if it is not a boolean
	 */
	public boolean getBoolean(PrefParameter param, boolean defValue) {
		try {
			return this.prefs.getBoolean(param.toString(), defValue);
		} catch (ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Gets the desired integer
	 * @param param the parameter type
	 * @return the desired integer if found, 0 if not found or if it is not a integer
	 */
	public int getInteger(PrefParameter param, int defValue) {
		try {
			return this.prefs.getInt(param.toString(), defValue);
		} catch (ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Gets the desired long
	 * @param param the parameter type
	 * @return the desired long if found, 0L if not found or if it is not a long
	 */
	public long getLong(PrefParameter param, long defValue) {
		try {
			return this.prefs.getLong(param.toString(), defValue);
		} catch (ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Gets the desired float
	 * @param param the parameter type
	 * @return the desired float if found, 0.0F if not found or if it is not a float
	 */
	public float getFloat(PrefParameter param, float defValue) {
		try {
			return this.prefs.getFloat(param.toString(), defValue);
		} catch (ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Inserts a string with the specified parameter 
	 * @param param the parameter type
	 * @param string the string value to be inserted
	 * @return true if the parameter has been stored, false otherwise
	 */
	public boolean putString(PrefParameter param, String string) {
		return this.editor.putString(param.toString(), string).commit();
	}
	
	/**
	 * Inserts a boolean with the specified parameter 
	 * @param param the parameter type
	 * @param bool the bool value to be inserted
	 * @return true if the parameter has been stored, false otherwise
	 */
	public boolean putBoolean(PrefParameter param, boolean bool) {
		return this.editor.putBoolean(param.toString(), bool).commit();
	}
	
	/**
	 * Inserts an integer with the specified parameter 
	 * @param param the parameter type
	 * @param i the integer value to be inserted
	 * @return true if the parameter has been stored, false otherwise
	 */
	public boolean putInteger(PrefParameter param, int i) {
		return this.editor.putInt(param.toString(), i).commit();
	}
	
	/**
	 * Inserts a long with the specified parameter 
	 * @param param the parameter type
	 * @param l the long value to be inserted
	 * @return true if the parameter has been stored, false otherwise
	 */
	public boolean putLong(PrefParameter param, long l) {
		return this.editor.putLong(param.toString(), l).commit();
	}
	
	/**
	 * Inserts a float with the specified parameter 
	 * @param param the parameter type
	 * @param d the float value to be inserted
	 * @return true if the parameter has been stored, false otherwise
	 */
	public boolean putFloat(PrefParameter param, float f) {
		return this.editor.putFloat(param.toString(), f).commit();
	}
	
	

}
