package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.Task;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


public class CheckableTask extends ParcelableTask {

	private boolean isChecked;

	public CheckableTask(Task task, boolean isChecked) {
		super(task);
		this.isChecked = isChecked;
	}

	private CheckableTask(Parcel in) {
		super(in);
		this.isChecked = in.readByte() == 1 ? true : false;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public static final Parcelable.Creator<CheckableTask> CREATOR
	= new Parcelable.Creator<CheckableTask>() {

		public CheckableTask[] newArray(int size) {
			return new CheckableTask[size];
		}

		@Override
		public CheckableTask createFromParcel(Parcel in) {
			return new CheckableTask(in);
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeByte((byte) (this.isChecked == true ? 1 : 0));
	}


}
