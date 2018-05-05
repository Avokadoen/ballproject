package com.example.avokado.lab3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.Games;

public class GlobalBoardActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_global_board);


	}

	private void lastSignIn() {
		Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
				.submitScore(getString(R.string.score_leader_id), 420);
	}
	private void signIn() {
		GoogleSignInOptions signInOpt;
		signInOpt = new GoogleSignInOptions();

	}
}
