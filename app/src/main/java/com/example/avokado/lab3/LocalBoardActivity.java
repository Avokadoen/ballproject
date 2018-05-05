package com.example.avokado.lab3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LocalBoardActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_board);

		ArrayList<String> scores = new ArrayList<>();

		for(int i = 0; i < 10; i++){
			scores.add(i, "0");
		}

		try{
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
		catch(FileNotFoundException e){
			Log.d("debug", "update: " + e.getCause());
		}
		catch (IOException e){
			Log.d("debug", "update: " + e.getCause());
		}

		// fill leaderboard with previous scores
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
