package it.bova.bioniccow.utilities;

import java.util.Calendar;

import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

public class SimpleDatePickerDialog extends DatePickerDialog {
	
	//private OnDateSetListener callBack;
	private String[] dateFormatStrings;
	
    public SimpleDatePickerDialog(Context context, String[] dateFormatStrings,
    		OnDateSetListener callBack, int myYear, int monthOfYear, int dayOfMonth) {
        super(context, callBack, myYear, monthOfYear, dayOfMonth);
        //this.callBack = callBack;
        this.dateFormatStrings = dateFormatStrings;
        updateTitle(myYear, monthOfYear, dayOfMonth);
    }
    
    public void onDateChanged(DatePicker view, int year,
            int month, int day) {
        updateTitle(year, month, day);
    }
    
    private void updateTitle(int year, int month, int day) {
    	Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        this.setTitle(SmartDateFormat.formatExtended(dateFormatStrings, cal.getTime()));
    }   

}
	    
