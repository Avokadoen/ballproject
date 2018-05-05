package com.example.avokado.lab3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
	}
}
