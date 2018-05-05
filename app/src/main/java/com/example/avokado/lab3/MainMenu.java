package com.example.avokado.lab3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

public class MainMenu extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		// Play option - starts the game
		findViewById(R.id.mm_play_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent gameIntent = new Intent(MainMenu.this, GameActivity.class);
				startActivity(gameIntent);
			}
		});

		// Leaderboard Option - Brings up a local leaderboard up
		findViewById(R.id.mm_loclead_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent gameIntent = new Intent(MainMenu.this, LocalBoardActivity.class);
				startActivity(gameIntent);
			}
		});

		// Global leaderboard Option - Brings up a global leaderboard up
		findViewById(R.id.mm_glolead_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent gameIntent = new Intent(MainMenu.this, GlobalBoardActivity.class);
				startActivity(gameIntent);
			}
		});
	}
}
