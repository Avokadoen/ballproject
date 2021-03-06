package com.ahs.avokado.gettingair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


// Local Leaderboard activity
public class LocalBoardActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_board);

		ArrayList<String> scores = new ArrayList<>();

		for(int i = 0; i < 10; i++){			// This is done to ensure there are no NULL-values in the Score List
			scores.add(i, "0");
		}

		try{	// Try to read from leaderboard file, and put the values into the score list
			FileInputStream boardsFileContent = this.openFileInput(Globals.leaderBoardPath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(boardsFileContent));
			String line;
			int index = 0;
			while ((line = reader.readLine()) != null){
				scores.add(index, line);
				Log.d("debug", "line: " + line);
				index++;
			}

			boardsFileContent.close();
			reader.close();
		}
		catch(IOException e){
			Log.d("debug", "update: " + e.getCause());
		}


		// Fill leaderboard with previous scores
		for(int i = 1; i <= 10; i++){
			TextView score = findViewById(getResources().getIdentifier("lb_score" + i + "_tv", "id", getPackageName()));
			if(Integer.valueOf(scores.get(i-1)) > 0 ){
				score.setText(String.valueOf(i).concat(". \t\t\t").concat(scores.get(i-1)));
			}
			else score.setText("");
		}

		// Play option - starts the game
		findViewById(R.id.lb_back_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
