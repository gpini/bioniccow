package it.bova.bioniccow;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.actionbarsherlock.app.SherlockActivity;
import it.bova.bioniccow.asyncoperations.DefaultMessageReceiver;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBFolderGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBLocationsGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTagGetter;
import it.bova.bioniccow.asyncoperations.rtmobjects.DBTaskListsGetter;
import it.bova.bioniccow.data.Folder;
import it.bova.bioniccow.utilities.ImprovedSpinnerAdapter;
import it.bova.bioniccow.utilities.NetAvailabilityTask;
import it.bova.bioniccow.utilities.ImprovedDatePickerDialog;
import it.bova.bioniccow.utilities.ImprovedTimePickerDialog;
import it.bova.bioniccow.utilities.Label;
import it.bova.bioniccow.utilities.LabelAdapter;
import it.bova.bioniccow.utilities.SimpleDatePickerDialog;
import it.bova.bioniccow.utilities.SpaceTokenizer;
import it.bova.bioniccow.utilities.rtmobjects.CheckableTask;
import it.bova.bioniccow.utilities.rtmobjects.LocationComparator;
import it.bova.bioniccow.utilities.rtmobjects.ParcelableTask;
import it.bova.bioniccow.utilities.rtmobjects.SmartDateFormat;
import it.bova.bioniccow.utilities.rtmobjects.TaskFormat;
import it.bova.bioniccow.utilities.rtmobjects.TaskListComparator;
import it.bova.rtmapi.Location;
import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.Recurrence.RecurrenceOption;
import it.bova.rtmapi.TaskList;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TaskDetailActivity extends EditActivity {

	protected Spinner prioritySpinner;
	protected ImageView priorityDrawable;
	protected EditText nameInput;
	protected Button dateButton;
	protected Button timeButton;
	protected EditText estimateDayInput;
	protected EditText estimateHourInput;
	protected EditText estimateMinuteInput;
	protected Spinner repeatSpinner1;
	protected Spinner repeatSpinner2;
	protected Spinner repeatSpinner3;
	protected Button repeatOptionInput;
	protected EditText participantsEditText;
	protected EditText urlInput;

	protected View recurrenceDialogView;
	protected Spinner optionSpinner;
	protected View countLayout;
	protected Spinner countSpinner;
	protected View untilLayout;
	protected Button untilDateButton;
	protected Button untilTimeButton;
	protected View weeklyLayout;
	protected View monthly1Layout;
	protected View monthly2Layout;
	protected Spinner monthly1spinner1;
	protected Spinner monthly1spinner2;
	protected Spinner monthly2spinner;
	protected CheckBox checkbox1;
	protected CheckBox checkbox2;
	protected CheckBox checkbox3;
	protected CheckBox checkbox4;
	protected CheckBox checkbox5;
	protected CheckBox checkbox6;
	protected CheckBox checkbox7;

	protected MultiAutoCompleteTextView tagInput;
	protected List<Label> tagLabels;
	protected List<Label> folderLabels;
	protected LabelAdapter labelAdapter;

	protected TaskListAdapter tasklistAdapter;
	protected Spinner listSpinner;

	protected LocationAdapter locationAdapter;
	protected Spinner locationSpinner;

	//resources
	protected String[] dateFormatStrings;
	protected String[] repeatStrings1;
	protected String[] repeatStrings2;

	protected Integer hour = null;
	protected Integer minute = null;
	protected Integer day = null;
	protected Integer month = null; //this will be 0-based
	protected Integer year = null;
	protected String selectedListId = null;
	protected String selectedLocationId = null;

	protected static final int DATE_DIALOG = 0;
	protected DatePickerDialog.OnDateSetListener dateSetListener;
	protected static final int TIME_DIALOG = 1;
	protected TimePickerDialog.OnTimeSetListener timeSetListener;
	protected static final int DATE_DIALOG2 = 2;
	protected static final int TIME_DIALOG2 = 3;
	protected static final int EDIT_RECURRENCE = 4;

	protected CheckableTask taskToBeEdited = null;
	protected Integer hour2 = null;
	protected Integer minute2 = null;
	protected Integer day2 = null;
	protected Integer month2 = null; //this will be 0-based
	protected Integer year2 = null;
	protected RecurrenceOption newRecurrenceOption = null;
	protected String newRecurrenceString = "";
	protected TimePickerDialog.OnTimeSetListener timeSetListener2;
	
	protected NetAvailabilityTask nat;
	protected View connectionWarning;
	protected TextView connectionWarningText;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_detail);
		
		this.connectionWarning = this.findViewById(R.id.connectionWarning);
		this.connectionWarningText = (TextView) this.findViewById(R.id.connectionWarningText);

		//resources
		this.dateFormatStrings = this.getResources().getStringArray(R.array.smart_date_format_labels);
		this.repeatStrings1 = this.getResources().getStringArray(R.array.repeat_labels1);
		this.repeatStrings2 = this.getResources().getStringArray(R.array.repeat_labels2);

		this.loadForms();

	}


	public void onResume() {
		super.onResume();
		
		this.reloadTagLabels();
		this.reloadFolderLabels();

		this.updateDateButton();
		this.updateTimeButton();
		
		this.nat = new NetAvailabilityTask(this, 2000) { //check internet every 2 seconds
			@Override public void handleMessage(Message msg) {
				if(msg.what == 1) //connected 
					TaskDetailActivity.this.connectionWarning.setVisibility(View.GONE);
				if(msg.what == -1) { //not connected
					if(TaskDetailActivity.this.taskToBeEdited == null)
						TaskDetailActivity.this.connectionWarningText.setText(R.string.connectionWarningAdd);
					else
						TaskDetailActivity.this.connectionWarningText.setText(R.string.connectionWarningEdit);
					TaskDetailActivity.this.connectionWarning.setVisibility(View.VISIBLE);
				}
			}
		};
		new Thread(this.nat).start();


	}

	public void onPause() {
		super.onPause();	
		
		this.nat.cancel();

	}

	@Override public void onSaveInstanceState(Bundle savedInstanceState) {
		if(this.hour != null)
			savedInstanceState.putInt("hour", this.hour);
		if(this.minute != null)
			savedInstanceState.putInt("minute", this.minute);
		if(this.day != null)
			savedInstanceState.putInt("day", this.day);
		if(this.month != null)
			savedInstanceState.putInt("month", this.month);
		if(this.year != null)
			savedInstanceState.putInt("year", this.year);
		if(this.hour2 != null)
			savedInstanceState.putInt("hour2", this.hour2);
		if(this.minute2 != null)
			savedInstanceState.putInt("minute2", this.minute2);
		if(this.day2 != null)
			savedInstanceState.putInt("day2", this.day2);
		if(this.month2 != null)
			savedInstanceState.putInt("month2", this.month2);
		if(this.year2 != null)
			savedInstanceState.putInt("year2", this.year2);
		if(newRecurrenceOption != null)
			savedInstanceState.putSerializable("option", newRecurrenceOption);
		if(newRecurrenceString != null)
			savedInstanceState.putSerializable("string", newRecurrenceString);
		if(this.selectedListId != null)
			savedInstanceState.putString("listId", this.selectedListId);
		if(this.selectedLocationId != null)
			savedInstanceState.putString("locId", this.selectedLocationId);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		this.hour = savedInstanceState.getInt("hour",-1);
		if(this.hour == -1) this.hour = null;
		this.minute = savedInstanceState.getInt("minute",-1);
		if(this.minute == -1) this.minute = null;
		this.day = savedInstanceState.getInt("day", -1);
		if(this.day == -1) this.day = null;
		this.month = savedInstanceState.getInt("month", -1);
		if(this.month == -1) this.month = null;
		this.year = savedInstanceState.getInt("year", -1);
		if(this.year == -1) this.year = null;
		this.hour2 = savedInstanceState.getInt("hour2",-1);
		if(this.hour2 == -1) this.hour2 = null;
		this.minute2 = savedInstanceState.getInt("minute2",-1);
		if(this.minute2 == -1) this.minute = null;
		this.day2 = savedInstanceState.getInt("day2", -1);
		if(this.day2 == -1) this.day2 = null;
		this.month2 = savedInstanceState.getInt("month2", -1);
		if(this.month2 == -1) this.month2 = null;
		this.year2 = savedInstanceState.getInt("year2", -1);
		if(this.year2 == -1) this.year2 = null;
		newRecurrenceOption = (RecurrenceOption) savedInstanceState.getSerializable("option");
		newRecurrenceString = savedInstanceState.getString("string");
		this.selectedListId = savedInstanceState.getString("listId");
		this.selectedLocationId = savedInstanceState.getString("locId");
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//??
	}

	@Override public void onBackPressed() {	
		this.finish();
	}

	@Override protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG:
			if(year == null || month == null || day == null) {
				Calendar cal = Calendar.getInstance();
				return new ImprovedDatePickerDialog(this, dateFormatStrings, dateSetListener,
						cal.get(Calendar.YEAR), 
						cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH));
			}
			else	
				return new ImprovedDatePickerDialog(this, dateFormatStrings, dateSetListener,
						year, month, day);
		case TIME_DIALOG:
			if(hour == null || minute == null) 
				return new ImprovedTimePickerDialog(this, timeSetListener, 0, 0);
			else
				return new ImprovedTimePickerDialog(this, timeSetListener, hour, minute);
		case DATE_DIALOG2 :
			Calendar cal = Calendar.getInstance();
			if(year2 != null && month2 != null && day2 != null)
				cal.set(year2, month2, day2);
			SimpleDatePickerDialog sdpd = new SimpleDatePickerDialog(this,
					dateFormatStrings,
					new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int month, int day) {
					Calendar cal = Calendar.getInstance();
					cal.set(year, month, day, 0, 0, 0);
					TaskDetailActivity.this.year2 = year;
					TaskDetailActivity.this.month2 = month;
					TaskDetailActivity.this.day2 = day;
					TaskDetailActivity.this.untilDateButton.setText(SmartDateFormat.format(dateFormatStrings, cal.getTime()));
				}
			},
			cal.get(Calendar.YEAR), 
			cal.get(Calendar.MONTH),
			cal.get(Calendar.DAY_OF_MONTH));
			return sdpd;
		case TIME_DIALOG2:
			if(hour2 == null || minute2 == null) 
				return new ImprovedTimePickerDialog(this, timeSetListener2, 0, 0);
			else
				return new ImprovedTimePickerDialog(this, timeSetListener2, hour2, minute2);
		case EDIT_RECURRENCE:
			String SAVE = this.getResources().getString(R.string.save);
			String CANCEL = this.getResources().getString(R.string.cancel);
			optionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override public void onItemSelected(AdapterView<?> parent, View v,
						int pos, long id) {
					TaskDetailActivity.this.updateOptionSpinnerLayout(pos);
				}
				@Override public void onNothingSelected(AdapterView<?> v) {
					TaskDetailActivity.this.updateOptionSpinnerLayout(0);
				}
			});
			Integer[] array = new Integer[100];
			for(int i = 0; i < 100; i++) array[i] = i;
			ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, array);
			countSpinner.setAdapter(adapter);
			untilDateButton.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					TaskDetailActivity.this.showDialog(DATE_DIALOG2);
				}
			});
			this.untilTimeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					showDialog(TIME_DIALOG2);
				}
			});
			this.timeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
				public void onTimeSet(TimePicker view, int hour, int minute) {
					if(hour == -1 || minute == -1) {
						TaskDetailActivity.this.hour2 = null;
						TaskDetailActivity.this.minute2 = null;
					}
					else {
						TaskDetailActivity.this.hour2 = hour;
						TaskDetailActivity.this.minute2 = minute;
					}
					TaskDetailActivity.this.updateTimeButton2();
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle(R.string.recurrenceDialog)
			.setCancelable(true)
			.setView(recurrenceDialogView)
			.setPositiveButton(SAVE, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					switch(optionSpinner.getSelectedItemPosition()) {
					case 0 : //nothing
						newRecurrenceOption = null;
						newRecurrenceString = "";
						break;
					case 1 : //count
						if(countSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
							newRecurrenceOption = RecurrenceOption.COUNT;
							newRecurrenceString = countSpinner.getSelectedItem().toString();
						}
						break;
					case 2 : //until
						if(year2 != null && month2 != null && day2 != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
							//sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
							Calendar cal = Calendar.getInstance();
							cal.set(Calendar.MILLISECOND, 0);
							if(hour2 != null && minute2 != null)
								cal.set(year2, month2, day2, hour2, minute2, 0);
							else
								cal.set(year2, month2, day2, 0, 0, 0);
							newRecurrenceOption = RecurrenceOption.UNTIL;
							newRecurrenceString = sdf.format(cal.getTime());
						}
						else {
							newRecurrenceOption = null;
							newRecurrenceString = null;
						}
						break;
					case 3 : //weekday
						if(TaskDetailActivity.this.repeatSpinner3.getSelectedItemPosition() != 0 &&
						TaskDetailActivity.this.repeatSpinner3.getSelectedItemPosition() != 2)
							Toast.makeText(TaskDetailActivity.this, R.string.weeklyWrong, Toast.LENGTH_LONG).show();
						int days = 0;
						StringBuilder sb0 = new StringBuilder("");
						if(checkbox1.isChecked()) {
							sb0.append("MO");
							days++;
						}
						if(checkbox2.isChecked()) {
							if(days > 0) sb0.append(",");
							sb0.append("TU");
							days++;
						}
						if(checkbox3.isChecked()) {
							if(days > 0) sb0.append(",");
							sb0.append("WE");
							days++;
						}
						if(checkbox4.isChecked()) {
							if(days > 0) sb0.append(",");
							sb0.append("TH");
							days++;
						}
						if(checkbox5.isChecked()) {
							if(days > 0) sb0.append(",");
							sb0.append("FR");
							days++;
						}
						if(checkbox6.isChecked()) {
							if(days > 0) sb0.append(",");
							sb0.append("SA");
							days++;
						}
						if(checkbox7.isChecked()) {
							if(days > 0) sb0.append(",");
							sb0.append("SU");
							days++;
						}
						if(sb0.toString().equals("")){ //no day selected
							newRecurrenceOption = null;
							newRecurrenceString = "";
						}
						else {
							newRecurrenceOption = RecurrenceOption.BYDAY;
							newRecurrenceString = sb0.toString();
						}
						break;
					case 4 : //month1
						if(TaskDetailActivity.this.repeatSpinner3.getSelectedItemPosition() != 0 &&
						TaskDetailActivity.this.repeatSpinner3.getSelectedItemPosition() != 3)
							Toast.makeText(TaskDetailActivity.this, R.string.monthlyWrong, Toast.LENGTH_LONG).show();
						if(monthly1spinner1.getSelectedItemPosition() != Spinner.INVALID_POSITION &&
								monthly1spinner2.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
							StringBuilder sb1 = new StringBuilder("");
							int pos1 = monthly1spinner1.getSelectedItemPosition();
							if(pos1 >= 0 && pos1 < 5) sb1.append("" + (pos1 + 1));
							else if(pos1 >= 5 && pos1 < 10) sb1.append("" + (4 - pos1));
							int pos2 = monthly1spinner2.getSelectedItemPosition();
							switch(pos2) {
							case 0 : sb1.append("MO"); break;
							case 1 : sb1.append("TU"); break;
							case 2 : sb1.append("WE"); break;
							case 3 : sb1.append("TH"); break;
							case 4 : sb1.append("FR"); break;
							case 5 : sb1.append("SA"); break;
							case 6 : sb1.append("SU"); break;
							}
							newRecurrenceOption = RecurrenceOption.BYDAY;
							newRecurrenceString = sb1.toString();
						}
						break;
					case 5 : //month2
						if(TaskDetailActivity.this.repeatSpinner3.getSelectedItemPosition() != 0 &&
						TaskDetailActivity.this.repeatSpinner3.getSelectedItemPosition() != 3)
							Toast.makeText(TaskDetailActivity.this, R.string.monthlyWrong, Toast.LENGTH_LONG).show();
						if(monthly2spinner.getSelectedItemPosition() != Spinner.INVALID_POSITION) {
							newRecurrenceOption = RecurrenceOption.BYMONTHDAY;
							newRecurrenceString = "" + (monthly2spinner.getSelectedItemPosition() + 1);	
						}
						break;
					default : //nothing
						newRecurrenceOption = null;
						newRecurrenceString = "";
						break;
					}
					if(newRecurrenceOption == null) {
						repeatOptionInput.setText("-");
					}
					else {
						TaskFormat tf = new TaskFormat(dateFormatStrings);
						String[] strings = getResources().getStringArray(R.array.repeat_option_strings);
						String[] weekdaysString = TaskDetailActivity.this.getResources().getStringArray(R.array.weekdays);
						String[] ordinals1 = TaskDetailActivity.this.getResources().getStringArray(R.array.ordinals1);
						String[] ordinals2 = TaskDetailActivity.this.getResources().getStringArray(R.array.ordinals2);
						String option = tf.formatRepeatOption(strings, 
								ordinals1, ordinals2, weekdaysString, newRecurrenceOption, newRecurrenceString);
						repeatOptionInput.setText(option);
					}
				}
			})
			.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			return builder.create();
		}
		return null;
	}

	//	@Override protected void onPrepareDialog(int id, Dialog dialog) {
	//		
	//	}

	public void onRepeatOptionClicked(View v) {
		this.showDialog(EDIT_RECURRENCE);
	}

	private void updateDateButton() {
		if(day != null && month != null && year != null) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month , day);
			this.dateButton.setText(SmartDateFormat.format(this.dateFormatStrings, cal.getTime()));
		}
		else
			this.dateButton.setText("-");
	}

	private void updateTimeButton() {
		if(hour != null && minute != null) {
			Calendar cal = Calendar.getInstance();
			cal.set(0, 0, 0, hour, minute);
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
			this.timeButton.setText(df.format(cal.getTime()));
		}
		else
			this.timeButton.setText("-");
	}

	private void updateTimeButton2() {
		if(hour2 != null && minute2 != null) {
			Calendar cal = Calendar.getInstance();
			cal.set(0, 0, 0, hour2, minute2);
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
			this.untilTimeButton.setText(df.format(cal.getTime()));
		}
		else
			this.untilTimeButton.setText("-");
	}

	private void updateOptionSpinnerLayout(int selectedPosition) {
		boolean showFirst = false;
		boolean showSecond = false;
		boolean showThird = false;
		boolean showForth = false;
		boolean showFifth = false;
		switch(selectedPosition) {
		case 1 : showFirst = true; break;
		case 2 : showSecond = true; break;
		case 3 : showThird = true; break;
		case 4 : showForth = true; break;
		case 5 : showFifth = true; break;
		default : break;
		}
		if(showFirst)
			this.countLayout.setVisibility(View.VISIBLE);
		else
			this.countLayout.setVisibility(View.GONE);
		if(showSecond)
			this.untilLayout.setVisibility(View.VISIBLE);
		else
			this.untilLayout.setVisibility(View.GONE);
		if(showThird)
			this.weeklyLayout.setVisibility(View.VISIBLE);
		else
			this.weeklyLayout.setVisibility(View.GONE);
		if(showForth)
			this.monthly1Layout.setVisibility(View.VISIBLE);
		else
			this.monthly1Layout.setVisibility(View.GONE);
		if(showFifth)
			this.monthly2Layout.setVisibility(View.VISIBLE);
		else
			this.monthly2Layout.setVisibility(View.GONE);

	}

	protected void showUpdatedRecurrenceDialog() {
		Recurrence rec = null;
		RecurrenceOption ro = null;
		String rs = null;
		if(this.taskToBeEdited != null) {
			rec = this.taskToBeEdited.getRecurrence();
		}
		if(newRecurrenceOption != null && newRecurrenceString != null) {
			ro = newRecurrenceOption;
			rs = newRecurrenceString;
		}							
		else if(rec != null) {
			ro = rec.getOption();
			rs = rec.getOptionValue();
		}
		if(ro == null) {
			this.optionSpinner.setSelected(false);
		}
		else {
			switch(ro) {
			case COUNT :
				this.optionSpinner.setSelection(1);
				try {
					int count = Integer.parseInt(rs);
					if(count < 100) 
						countSpinner.setSelection(count);
				} catch(NumberFormatException nfe) {/* do nothing */}
				break;
			case UNTIL :
				this.optionSpinner.setSelection(2);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
				//sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				try {
					Date d = sdf.parse(rs);
					Calendar cal =  Calendar.getInstance();
					cal.setTime(d);
					this.year2 = cal.get(Calendar.YEAR);
					this.month2 = cal.get(Calendar.MONTH);
					this.day2 = cal.get(Calendar.DAY_OF_MONTH);
					this.hour2 = cal.get(Calendar.HOUR_OF_DAY);
					this.minute2 = cal.get(Calendar.MINUTE);
					this.untilDateButton.setText(SmartDateFormat.format(this.dateFormatStrings, cal.getTime()));
					this.updateTimeButton2();

				} catch(ParseException pe) {/* do nothing */}
				break;
			case BYDAY :
				if(this.isWeeklyOption(rs)) {//by day - weekly
					this.optionSpinner.setSelection(3);
					checkbox1.setChecked(false);
					checkbox2.setChecked(false);
					checkbox3.setChecked(false);
					checkbox4.setChecked(false);
					checkbox5.setChecked(false);
					checkbox6.setChecked(false);
					checkbox7.setChecked(false);
					String[] days = rs.split(",");
					for(String day : days) {
						if(day.equals("MO")) checkbox1.setChecked(true);
						else if(day.equals("TU")) checkbox2.setChecked(true);
						else if(day.equals("WE")) checkbox3.setChecked(true);
						else if(day.equals("TH")) checkbox4.setChecked(true);
						else if(day.equals("FR")) checkbox5.setChecked(true);
						else if(day.equals("SA")) checkbox6.setChecked(true);
						else if(day.equals("SU")) checkbox7.setChecked(true);
					}
				}
				else {//by day - monthly
					this.optionSpinner.setSelection(4);
					String ordinal = rs.substring(0, (rs.length() - 2));
					String weekday = rs.substring((rs.length() - 2));
					try {
						int ord = Integer.parseInt(ordinal);
						if(ord > 0) monthly1spinner1.setSelection(ord - 1);
						else if(ord < 0) monthly1spinner1.setSelection(4 - ord);
						if(weekday.equals("MO")) monthly1spinner2.setSelection(0);
						else if(weekday.equals("TU")) monthly1spinner2.setSelection(1);
						else if(weekday.equals("WE")) monthly1spinner2.setSelection(2);
						else if(weekday.equals("TH")) monthly1spinner2.setSelection(3);
						else if(weekday.equals("FR")) monthly1spinner2.setSelection(4);
						else if(weekday.equals("SA")) monthly1spinner2.setSelection(5);
						else if(weekday.equals("SU")) monthly1spinner2.setSelection(6);
					} catch(NumberFormatException nfe) {/* do nothing */}
				}
				break;
			case BYMONTHDAY : //by month day - monthly
				this.optionSpinner.setSelection(5);
				try {
					int day = Integer.parseInt(rs);
					monthly2spinner.setSelection(day - 1);
				} catch(NumberFormatException nfe) {/* do nothing */}
				break;
			}
		}
	}
	
	private boolean isWeeklyOption(String recurrenceString) {
		//distinguish between BYDAY options
		String firstChar = recurrenceString.substring(0,1);
		Pattern p = Pattern.compile("[\\d\\-]");
		Matcher m = p.matcher(firstChar);
		return !m.matches();
	}

	private void loadForms() {
		//recurrence option dialog
		LayoutInflater inflater
		= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		recurrenceDialogView = inflater.inflate(R.layout.recurrence, null);
		optionSpinner = (Spinner) recurrenceDialogView.findViewById(R.id.optionSpinner);
		countLayout = recurrenceDialogView.findViewById(R.id.countLayout);
		countSpinner = (Spinner) countLayout.findViewById(R.id.countSpinner);
		untilLayout = recurrenceDialogView.findViewById(R.id.untilLayout);
		untilDateButton = (Button) recurrenceDialogView.findViewById(R.id.untilDateButton);
		untilTimeButton = (Button) recurrenceDialogView.findViewById(R.id.untilTimeButton);
		weeklyLayout = recurrenceDialogView.findViewById(R.id.weekDayLayout);
		monthly1Layout = recurrenceDialogView.findViewById(R.id.monthDay1Layout);
		monthly1spinner1 = (Spinner) recurrenceDialogView.findViewById(R.id.monthDay1Spinner1);
		monthly1spinner2 = (Spinner) recurrenceDialogView.findViewById(R.id.monthDay1Spinner2);
		monthly2Layout = recurrenceDialogView.findViewById(R.id.monthDay2Layout);
		monthly2spinner = (Spinner) recurrenceDialogView.findViewById(R.id.monthDay2Spinner);
		checkbox1 = (CheckBox) recurrenceDialogView.findViewById(R.id.checkBox1);
		checkbox2 = (CheckBox) recurrenceDialogView.findViewById(R.id.checkBox2);
		checkbox3 = (CheckBox) recurrenceDialogView.findViewById(R.id.checkBox3);
		checkbox4 = (CheckBox) recurrenceDialogView.findViewById(R.id.checkBox4);
		checkbox5 = (CheckBox) recurrenceDialogView.findViewById(R.id.checkBox5);
		checkbox6 = (CheckBox) recurrenceDialogView.findViewById(R.id.checkBox6);
		checkbox7 = (CheckBox) recurrenceDialogView.findViewById(R.id.checkBox7);

		//form elements - NAME
		this.nameInput = (EditText) this.findViewById(R.id.nameInput);

		//form elements - DATE&TIME
		this.dateButton = (Button) this.findViewById(R.id.dateButton);
		this.dateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG);
			}
		});
		this.dateSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int month, int day) {
				//Toast.makeText(TaskAddActivity.this, "" + year + month + day, Toast.LENGTH_LONG).show();
				if(year == -1 || month == -1 || day == -1) {
					//-1 is returned by ImporvedDatePickerDialog if "Never" button is pressed
					TaskDetailActivity.this.year = null;
					TaskDetailActivity.this.month = null;
					TaskDetailActivity.this.day = null;
				}
				else {
					TaskDetailActivity.this.year = year;
					TaskDetailActivity.this.month = month;
					TaskDetailActivity.this.day = day;
				}
				TaskDetailActivity.this.updateDateButton();
			}
		};
		this.timeButton = (Button) this.findViewById(R.id.timeButton);
		this.timeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG);
			}
		});
		this.timeSetListener = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int hour, int minute) {
				if(hour == -1 || minute == -1) {
					TaskDetailActivity.this.hour = null;
					TaskDetailActivity.this.minute = null;
				}
				else {
					TaskDetailActivity.this.hour = hour;
					TaskDetailActivity.this.minute = minute;
				}
				TaskDetailActivity.this.updateTimeButton();
			}
		};

		//form elements - ESTIMATE
		this.estimateDayInput = (EditText) this.findViewById(R.id.estimateDayInput);
		this.estimateHourInput = (EditText) this.findViewById(R.id.estimateHourInput);
		this.estimateMinuteInput = (EditText) this.findViewById(R.id.estimateMinuteInput);

		//form elements - PRIORITY
		this.prioritySpinner = (Spinner) this.findViewById(R.id.priorityInput);
		this.priorityDrawable = (ImageView) this.findViewById(R.id.priorityDrawable);
		ArrayAdapter<String> priorityAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		String[] priorities = this.getResources().getStringArray(R.array.priorities);
		priorityAdapter.add(priorities[0]);
		priorityAdapter.add(priorities[1]);
		priorityAdapter.add(priorities[2]);
		priorityAdapter.add(priorities[3]);
		priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.priorityDrawable.setBackgroundResource(R.drawable.priority_background_none);
		this.prioritySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if(pos == 1)
					TaskDetailActivity.this.priorityDrawable.setBackgroundResource(R.drawable.priority_background_low);
				else if(pos == 2)
					TaskDetailActivity.this.priorityDrawable.setBackgroundResource(R.drawable.priority_background_medium);
				else if(pos == 3)
					TaskDetailActivity.this.priorityDrawable.setBackgroundResource(R.drawable.priority_background_high);
				else
					TaskDetailActivity.this.priorityDrawable.setBackgroundResource(R.drawable.priority_background_none);

			}
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		this.prioritySpinner.setAdapter(priorityAdapter);

		//form elements - TAGS
		this.tagInput = (MultiAutoCompleteTextView) this.findViewById(R.id.tagInput);
		this.labelAdapter = new LabelAdapter(this, new ArrayList<Label>(), R.layout.dropdown_tag);
		this.tagInput.setAdapter(this.labelAdapter);
		this.tagInput.setTokenizer(new SpaceTokenizer());

		//form elements - LISTS&LOCATIONS
		this.listSpinner = (Spinner) this.findViewById(R.id.listSpinner);
		this.tasklistAdapter = new TaskListAdapter(this);
		this.listSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				TaskList list = (TaskList) parent.getItemAtPosition(pos);
				TaskDetailActivity.this.selectedListId = list.getId();
			}
			public void onNothingSelected(AdapterView<?> parent) {
				TaskDetailActivity.this.selectedListId = null;
			}
		});
		this.listSpinner.setAdapter(tasklistAdapter);

		this.locationSpinner = (Spinner) this.findViewById(R.id.locationSpinner);
		this.locationAdapter = new LocationAdapter(this);	
		this.locationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				Location loc = (Location) parent.getItemAtPosition(pos);
				TaskDetailActivity.this.selectedLocationId = loc.getId();
			}
			public void onNothingSelected(AdapterView<?> parent) {
				TaskDetailActivity.this.selectedLocationId = null;
			}
		});
		this.locationSpinner.setAdapter(locationAdapter);

		this.repeatSpinner1 = (Spinner) this.findViewById(R.id.repeatSpinner1);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.repeatStrings1);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.repeatSpinner1.setAdapter(adapter1);
		this.repeatSpinner2 = (Spinner) this.findViewById(R.id.repeatSpinner2);
		Integer[] array2 = new Integer[100];
		for(int i = 0; i < 100; i++) array2[i] = i;
		ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, array2);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.repeatSpinner2.setAdapter(adapter2);
		this.repeatSpinner3 = (Spinner) this.findViewById(R.id.repeatSpinner3);
		this.repeatSpinner3.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> adapterView, View view,
					int pos, long id) {
				RecurrenceOption ro = null;
				String rs = null;
				if(TaskDetailActivity.this.newRecurrenceOption != null) {
					ro = TaskDetailActivity.this.newRecurrenceOption;
					rs = TaskDetailActivity.this.newRecurrenceString;
				}
				else {
					if(TaskDetailActivity.this.taskToBeEdited != null) {
						Recurrence rec = TaskDetailActivity.this.taskToBeEdited.getRecurrence();
						if(rec != null) {
							ro = rec.getOption();
							rs = rec.getOptionValue();
						}
					}
				}
				if(ro != null && rs != null) {
					if(ro.equals(RecurrenceOption.BYMONTHDAY)) {
						if(pos != 0 && pos != 3)
							Toast.makeText(TaskDetailActivity.this, R.string.monthlyWrong, Toast.LENGTH_LONG).show();
					}
					else if(ro.equals(RecurrenceOption.BYDAY)) {
						if(TaskDetailActivity.this.isWeeklyOption(rs)) {
							if(pos != 0 && pos != 2)
								Toast.makeText(TaskDetailActivity.this, R.string.weeklyWrong, Toast.LENGTH_LONG).show();
						}
						else {
							if(pos != 0 && pos != 3)
								Toast.makeText(TaskDetailActivity.this, R.string.monthlyWrong, Toast.LENGTH_LONG).show();
						}
					}
				}
			}

			@Override public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.repeatStrings2);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.repeatSpinner3.setAdapter(adapter3);
		this.repeatOptionInput = (Button) this.findViewById(R.id.repeatOptionInput);
		if(year2 == null || month2 == null || this.day2 == null)
			this.untilDateButton.setText("");
		else {
			Calendar cal = Calendar.getInstance();
			cal.set(this.year2, this.month2, this.day2, 0, 0, 0);
			this.untilDateButton.setText(SmartDateFormat.format(dateFormatStrings, cal.getTime()));
		}
		if(hour2 == null || this.minute2 == null)
			this.untilTimeButton.setText("");
		else {
			Calendar cal = Calendar.getInstance();
			cal.set(this.year2, this.month2, this.day2, this.hour2, this.minute2, 0);
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
			this.untilTimeButton.setText(df.format(cal.getTime()));
		}
		this.participantsEditText = (EditText) this.findViewById(R.id.participants);
		participantsEditText.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(TaskDetailActivity.this,R.string.coming_soon,Toast.LENGTH_SHORT).show();
			}
		});
		this.urlInput = (EditText) this.findViewById(R.id.urlInput);
	}
	
	protected class TaskListAdapter extends ImprovedSpinnerAdapter<TaskList> {
		
		public TaskListAdapter(Context context) {
			super(context);
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent){
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(android.R.layout.simple_spinner_item, null);
			}
			TextView tv = (TextView) convertView;
			TaskList list = (TaskList) this.getItem(position);
			tv.setText(list.getName());
			return convertView;
		}
		
		@Override public View getDropDownView(int position, View convertView, ViewGroup parent){
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.dropdown_spinner, null);
			}
			TextView tv = (TextView) convertView;
			TaskList list = (TaskList) this.getItem(position);
			tv.setText(list.getName());
			return convertView;
		}		
		
		
	}
	
	protected class LocationAdapter extends ImprovedSpinnerAdapter<Location> {
		
		private int ORANGE;
		private int BLACK;
		
		public LocationAdapter(Context context) {
			super(context);
			ORANGE = TaskDetailActivity.this.getResources().getColor(R.color.dark_orange);
			BLACK = TaskDetailActivity.this.getResources().getColor(R.color.black);
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent){
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(android.R.layout.simple_spinner_item, null);
			}
			TextView tv = (TextView) convertView;
			Location loc = (Location) this.getItem(position);
			tv.setText(loc.getName());
			if(position == 0)
				tv.setTextColor(BLACK);
			else
				tv.setTextColor(ORANGE);
			return convertView;
		}
		
		@Override public View getDropDownView(int position, View convertView, ViewGroup parent){
			if(convertView == null) {
				LayoutInflater inflater 
					= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.dropdown_spinner, null);
			}
			TextView tv = (TextView) convertView;
			Location loc = (Location) this.getItem(position);
			tv.setText(loc.getName());
			if(position == 0)
				tv.setTextColor(BLACK);
			else
				tv.setTextColor(ORANGE);
			return convertView;
		}
		
		
	}
	
	protected class DetailMessageReceiver extends DefaultMessageReceiver {
	
		public DetailMessageReceiver(SherlockActivity activity) {
			super(activity);
		}
		
		@Override protected void onTasklistsUpdated(Context context) {
			DBTaskListsGetter tlg = new DBTaskListsGetter(TaskDetailActivity.this) {
				@Override protected void onPostExecute(List<TaskList> tasklists) {
					Collections.sort(tasklists, new TaskListComparator());
					TaskDetailActivity.this.tasklistAdapter.reloadAndNotify(tasklists);
				}
			};
			tlg.execute();
		}
			
		@Override protected void onLocationsUpdated(Context context) {
			DBLocationsGetter lg = new DBLocationsGetter(TaskDetailActivity.this) {
				@Override protected void onPostExecute(List<Location> locations) {
					Collections.sort(locations, new LocationComparator());
					TaskDetailActivity.this.locationAdapter.reloadAndNotify(locations);
				}
			};
			lg.execute();
		}
		
		@Override protected void onFoldersUpdated(Context context) {
			TaskDetailActivity.this.reloadFolderLabels();
		}
			
		@Override protected void onTaskChanged(Context context, List<String> changedId) {
			TaskDetailActivity.this.reloadTagLabels();
		}
			
		@Override protected void onTaskAdded(Context context, List<ParcelableTask> addedTasks) {
			TaskDetailActivity.this.reloadTagLabels();
		}
			
	}
	
	private void reloadFolderLabels() {
		DBFolderGetter fg = new DBFolderGetter(TaskDetailActivity.this) {
			@Override protected void onPostExecute(List<Folder> folders) {
				TaskDetailActivity.this.folderLabels.clear();
				for(Folder folder : folders) {
					String rule = folder.getRule();
					for(Label tagLabel : TaskDetailActivity.this.tagLabels) {
						String tag = tagLabel.getUnruledTag();
						String unruledTag = tag.substring(rule.length());
						TaskDetailActivity.this.folderLabels.add(new Label(rule,unruledTag));
					}
				}
				TaskDetailActivity.this.labelAdapter.clear();
				TaskDetailActivity.this.labelAdapter.addAll(TaskDetailActivity.this.tagLabels);
				TaskDetailActivity.this.labelAdapter.addAll(TaskDetailActivity.this.folderLabels);
				TaskDetailActivity.this.labelAdapter.notifyDataSetChanged();
			}
		};
		fg.execute();
	}
	
	private void reloadTagLabels() {
		DBTagGetter tg = new DBTagGetter(TaskDetailActivity.this) {
			@Override protected void onPostExecute(Set<String> tags) {
				TaskDetailActivity.this.tagLabels.clear();
				for(String tag : tags)
					TaskDetailActivity.this.tagLabels.add(new Label("",tag));
				TaskDetailActivity.this.labelAdapter.clear();
				TaskDetailActivity.this.labelAdapter.addAll(TaskDetailActivity.this.tagLabels);
				TaskDetailActivity.this.labelAdapter.addAll(TaskDetailActivity.this.folderLabels);
				TaskDetailActivity.this.labelAdapter.notifyDataSetChanged();
			}
		};
		tg.execute();
	}
	
	



}
