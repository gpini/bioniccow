package it.bova.bioniccow.utilities.rtmobjects;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import it.bova.rtmapi.Contact;
import it.bova.rtmapi.Frequency;
import it.bova.rtmapi.Note;
import it.bova.rtmapi.Priority;
import it.bova.rtmapi.Recurrence;
import it.bova.rtmapi.Recurrence.RecurrenceOption;
import it.bova.rtmapi.Task;

public class ParcelableTask extends Task implements Parcelable {
	
	public ParcelableTask(Task task) {
		super(task.getId(), task.getName(), task.getAdded(), task.getCompleted(),
				task.getDeleted(), task.getDue(), task.getEstimate(), task.getHasDueTime(),
				task.getPostponed(), task.getPriority(), task.getTaskserieId(),
				task.getLocationId(), task.getListId(),
				task.getCreated(), task.getModified(), task.getNotes(), task.getRecurrence(),
				task.getParticipants(), task.getSource(), task.getTags(), task.getUrl());
	}
     
	protected ParcelableTask(Parcel in) {
		super("", "", null, null,
				null, null, "", false,
				0, Priority.NONE, "", null, "",
				null, null, new Note[0], null,
				new Contact[0], null, new String[0], null);
		this.setTaskFromParcel(in);
	}
	
	protected void setTaskFromParcel(Parcel in) {
		this.setId(in.readString());
		this.setName(in.readString());
		long add = in.readLong();
		if(add == 0)
			this.setAdded(null);
		else
			this.setAdded(new Date(add));
		long compl = in.readLong();
		if(compl == 0)
			this.setCompleted(null);
		else
			this.setCompleted(new Date(compl));
		long del = in.readLong();
		if(del == 0)
			this.setDeleted(null);
		else
			this.setDeleted(new Date(del));
		long due = in.readLong();
		if(due == 0)
			this.setDue(null);
		else
			this.setDue(new Date(due));
		this.setEstimate(in.readString());
		this.setHasDueTime(in.readByte() == 1 ? true : false);
		this.setPostponed(in.readInt());
		this.setPriority(Priority.values()[in.readInt()]);
		this.setTaskserieId(in.readString());
		this.setLocationId(in.readString());
		this.setListId(in.readString());
		long cr = in.readLong();
		if(cr == 0)
			this.setCreated(null);
		else
			this.setCreated(new Date(cr));
		long mod = in.readLong();
		if(mod == 0)
			this.setModified(null);
		else
			this.setModified(new Date(mod));
		int noteSize = in.readInt();
		String[] noteIds = new String[noteSize];
		in.readStringArray(noteIds);
		String[] titles = new String[noteSize];
		in.readStringArray(titles);
		String[] texts = new String[noteSize];
		in.readStringArray(texts);
		long[] creationDates = new long[noteSize];
		in.readLongArray(creationDates);
		long[] modificationDates = new long[noteSize];
		in.readLongArray(modificationDates);
		Note[] notes = new Note[noteSize];
		for(int i = 0; i < noteSize; i++) {
			Date created = null;
			if(creationDates[i] != 0)
				created = new Date(creationDates[i]);
			Date modified = null;
			if(modificationDates[i] != 0)
				modified = new Date(modificationDates[i]);
			notes[i] =
					new Note(noteIds[i], titles[i], texts[i], created, modified);
		}
		this.setNotes(notes);
		boolean hasRecurrence = in.readByte() == 1 ? true : false;
		if(hasRecurrence) {
			boolean isEvery = in.readByte() == 1 ? true : false;
			int interval =  in.readInt();
			Frequency freq = Frequency.values()[in.readInt()];
			int recOptOrdinal = in.readInt();
			RecurrenceOption recOpt = null;
			if(recOptOrdinal >= 0)
				recOpt = RecurrenceOption.values()[recOptOrdinal];
			String recString = in.readString();
			this.setRecurrence(new Recurrence(isEvery, interval, freq, recOpt, recString));
		}
		else this.setRecurrence(null);
		int contactSize = in.readInt();
		String[] contactIds = new String[contactSize];
		in.readStringArray(contactIds);
		String[] fullnames = new String[contactSize];
		in.readStringArray(fullnames);
		String[] usernames = new String[contactSize];
		in.readStringArray(usernames);
		Contact[] contacts = new Contact[contactSize];
		for(int i = 0; i < contactSize; i++) {
			contacts[i] = 
					new Contact(contactIds[i], fullnames[i], usernames[i]);
		}
		this.setParticipants(contacts);
		this.setSource(in.readString());
		int tagSize = in.readInt();
		String[] tags = new String[tagSize];
		in.readStringArray(tags);
		this.setTags(tags);
		this.setUrl(in.readString());
	}


	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ParcelableTask> CREATOR
			= new Parcelable.Creator<ParcelableTask>() {

		public ParcelableTask[] newArray(int size) {
			return new ParcelableTask[size];
		}

		@Override
		public ParcelableTask createFromParcel(Parcel in) {
			return new ParcelableTask(in);
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.getId());
		out.writeString(this.getName());
		if(this.getAdded() == null)
			out.writeLong(0);
		else
			out.writeLong(this.getAdded().getTime());
		if(this.getCompleted() == null)
			out.writeLong(0);
		else
			out.writeLong(this.getCompleted().getTime());
		if(this.getDeleted() == null)
			out.writeLong(0);
		else
			out.writeLong(this.getDeleted().getTime());
		if(this.getDue() == null)
			out.writeLong(0);
		else
			out.writeLong(this.getDue().getTime());
		out.writeString(this.getEstimate());
		out.writeByte((byte) (this.getHasDueTime() == true ? 1 : 0));
		out.writeInt(this.getPostponed());
		out.writeInt(this.getPriority().ordinal());
		out.writeString(this.getTaskserieId());
		out.writeString(this.getLocationId());
		out.writeString(this.getListId());
		if(this.getCreated() == null)
			out.writeLong(0);
		else
			out.writeLong(this.getCreated().getTime());
		if(this.getModified() == null)
			out.writeLong(0);
		else
			out.writeLong(this.getModified().getTime());
		Note[] notes = this.getNotes();
		int noteSize = notes.length;
		out.writeInt(noteSize);
		String[] noteIds = new String[noteSize];
		String[] titles = new String[noteSize];
		String[] texts = new String[noteSize];
		long[] creationDates = new long[noteSize];
		long[] modificationDates = new long[noteSize];
		for(int i = 0; i < noteSize; i++) {
			Note note = notes[i];
			noteIds[i] = note.getId();
			titles[i] = note.getTitle();
			texts[i] = note.getText();
			if(this.getCreated() == null)
				creationDates[i] = 0;
			else
				creationDates[i] = this.getCreated().getTime();
			if(this.getModified() == null)
				creationDates[i] = 0;
			else
				creationDates[i] = this.getModified().getTime();
		}
		out.writeStringArray(noteIds);
		out.writeStringArray(titles);
		out.writeStringArray(texts);
		out.writeLongArray(creationDates);
		out.writeLongArray(modificationDates);
		if(this.getRecurrence() != null) {
			Recurrence rec = this.getRecurrence();
			out.writeByte((byte) 1);
			out.writeByte((byte) (rec.isEvery() == true ? 1 : 0));
			out.writeInt(rec.getInterval());
			out.writeInt(rec.getFrequency().ordinal());
			if(rec.getOption() == null)
				out.writeInt(-1);
			else
				out.writeInt(rec.getOption().ordinal());
			out.writeString(rec.getOptionValue());
		}
		else
			out.writeByte((byte) 0);
		Contact[] contacts = this.getParticipants();
		int contactSize = contacts.length;
		out.writeInt(contactSize);
		String[] contactIds = new String[contactSize];
		String[] fullnames = new String[contactSize];
		String[] usernames = new String[contactSize];
		for(int i = 0; i < contactSize; i++) {
			Contact contact = contacts[i];
			contactIds[i] = contact.getId();
			fullnames[i] = contact.getFullname();
			usernames[i] = contact.getUsername();
		}
		out.writeStringArray(contactIds);
		out.writeStringArray(fullnames);
		out.writeStringArray(usernames);
		out.writeString(this.getSource());
		out.writeInt(this.getTags().length);
		out.writeStringArray(this.getTags());
		out.writeString(this.getUrl());

	}
	


}
