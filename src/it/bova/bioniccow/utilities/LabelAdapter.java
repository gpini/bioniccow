package it.bova.bioniccow.utilities;

import it.bova.bioniccow.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LabelAdapter extends ImprovedArrayAdapter<Label> {
	
	private int dropdownResId;

	public LabelAdapter(Context context, List<Label> labels, int dropdownResId) {
		super(context, 0, labels);
		this.dropdownResId = dropdownResId;
	}
	
	private class LabelViewHolder {
		TextView rule;
		TextView unlabeledTag;
	}
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		Label label = this.getItem(position);
		if(convertView == null) {
			LayoutInflater inflater 
				= (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(dropdownResId, null); 
			LabelViewHolder labelHolder = new LabelViewHolder();
			labelHolder.rule = (TextView) convertView.findViewById(R.id.rule);
			labelHolder.unlabeledTag = (TextView) convertView.findViewById(R.id.unlabeledTag);
			convertView.setTag(labelHolder);
		}
		LabelViewHolder holder = (LabelViewHolder) convertView.getTag();
		holder.rule.setText(label.getRule());
		holder.unlabeledTag.setText(label.getUnruledTag());
		return convertView;
	}


}
