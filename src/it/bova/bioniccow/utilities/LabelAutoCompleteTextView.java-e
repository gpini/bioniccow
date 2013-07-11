package it.bova.bioniccow.utilities;

import it.bova.bioniccow.utilities.LabelAutoCompleteTextView.OnTextChangedListener;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class LabelAutoCompleteTextView extends MultiAutoCompleteTextView {

	private OnTextChangedListener listener;

	public LabelAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setInputType(InputType.TYPE_CLASS_TEXT);
	}

	@Override protected CharSequence convertSelectionToString(Object selectedItem) {
		if(selectedItem instanceof Label){
			Label label = (Label) selectedItem;
			return label.getRule() + label.getUnruledTag();
		} else {
			return super.convertSelectionToString(selectedItem);
		}
	}
	
	@Override protected void onTextChanged(CharSequence text, int start, int before, int after) {
		if(this.listener != null)
			this.listener.onTextChange(text, start, before, after);
	}
	
	public void setOnTextChangedListener(OnTextChangedListener listener) {
		this.listener = listener;
	}
	
	public static class OnTextChangedListener {
		protected void onTextChange(CharSequence text, int start, int before, int after) {}
	}

}
