package it.bova.bioniccow.utilities;

import java.util.Calendar;

import it.bova.bioniccow.R;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.Toast;

public class ImprovedDatePickerDialog extends DatePickerDialog {
	
	private OnDateSetListener callBack;
	private String[] dateFormatStrings;
	
    public ImprovedDatePickerDialog(Context context, String[] dateFormatStrings,
    		OnDateSetListener callBack, int myYear, int monthOfYear, int dayOfMonth) {
        super(context, callBack, myYear, monthOfYear, dayOfMonth);
        this.callBack = callBack;
        this.dateFormatStrings = dateFormatStrings;
        this.setButton(DialogInterface.BUTTON_NEUTRAL,
            	context.getResources().getText(R.string.never),
            	new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int which) {
            			if (which == DialogInterface.BUTTON_NEUTRAL) {	
            				ImprovedDatePickerDialog.this.dismiss();
            				ImprovedDatePickerDialog.this.callBack.onDateSet(null, -1, -1, -1);
            			}
            		}
            });  
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
	    
