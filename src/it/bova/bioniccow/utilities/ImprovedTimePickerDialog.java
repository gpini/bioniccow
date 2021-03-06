package it.bova.bioniccow.utilities;

import it.bova.bioniccow.R;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;

public class ImprovedTimePickerDialog extends TimePickerDialog {
	
	private OnTimeSetListener callBack;
	
    public ImprovedTimePickerDialog(Context context, OnTimeSetListener callBack,
            int hour, int minute) {
        super(context, callBack, hour, minute, DateFormat.is24HourFormat(context));
        this.callBack = callBack;
        this.setButton(DialogInterface.BUTTON_NEUTRAL,
            	context.getResources().getText(R.string.never),
            	new DialogInterface.OnClickListener() {
            		public void onClick(DialogInterface dialog, int which) {
            			if (which == DialogInterface.BUTTON_NEUTRAL) {	
            				ImprovedTimePickerDialog.this.dismiss();
            				ImprovedTimePickerDialog.this.callBack.onTimeSet(null, -1, -1);
            			}
            		}
            });  
    }


}
	    
