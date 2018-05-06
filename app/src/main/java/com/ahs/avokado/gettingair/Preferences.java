package com.ahs.avokado.gettingair;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Preferences extends AppCompatActivity {

	SharedPreferences.Editor editor;
	SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		sharedPref = getDefaultSharedPreferences(this.getApplicationContext());
		editor = sharedPref.edit();

		Switch shareScore = findViewById(R.id.pr_noShare_sw);

		boolean defaultValue = false;
		boolean dontShare = sharedPref.getBoolean(getResources().getString(R.string.shareGlobalState), defaultValue);
		shareScore.setChecked(dontShare);

		// Stop sharing with google
		shareScore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				editor.putBoolean(getString(R.string.shareGlobalState), b);
				editor.apply();
			}
		});

		// Play option - starts the game
		findViewById(R.id.pr_back_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
