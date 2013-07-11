package it.bova.bioniccow;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {

	public void appendToHeaderText(String text) {
		HeaderFragment headerFragment = (HeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.headerFragment);
		if(headerFragment != null)
			headerFragment.append(text);
	}

	public void setHeaderText(String text) {
		HeaderFragment headerFragment = (HeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.headerFragment);
		if(headerFragment != null)
			headerFragment.setText(text);
	}

	public void showKeyboard() {
		HeaderFragment headerFragment = (HeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.headerFragment);
		if(headerFragment != null)
			headerFragment.showKeyboard();
	}

	public void hideKeyboard() {
		HeaderFragment headerFragment = (HeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.headerFragment);
		if(headerFragment != null)
			headerFragment.hideKeyboard();
	}







}
