package com.ahs.avokado.gettingair;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainMenu extends AppCompatActivity {

	private GoogleSignInAccount signedInAccount;

	private static final int RC_SIGN_IN = 100;
	private static final int RC_LEADERBOARD_UI = 9004;
	private static final int RC_ACHIEVEMENT_UI = 9003;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		signedInAccount = GoogleSignIn.getLastSignedInAccount(this);

		// Play option - starts the game
		findViewById(R.id.mm_play_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent gameIntent = new Intent(MainMenu.this, GameActivity.class);
				startActivity(gameIntent);
			}
		});

		// Leaderboard Option - Brings up a local leaderboard up
		findViewById(R.id.mm_locLead_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent gameIntent = new Intent(MainMenu.this, LocalBoardActivity.class);
				startActivity(gameIntent);
			}
		});

		// Global leaderboard Option - Brings up a global leaderboard up
		findViewById(R.id.mm_gloLead_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(isSignedIn()){
					SharedPreferences sharedPref = getDefaultSharedPreferences(getApplicationContext());

					boolean dontShare = sharedPref.getBoolean(getResources().getString(R.string.shareGlobalState), false);
					if(!dontShare) {
						try {
							FileInputStream boardsFileContent = openFileInput(Globals.leaderBoardPath);
							BufferedReader reader = new BufferedReader(new InputStreamReader(boardsFileContent));
							String line;
							line = reader.readLine();

							Games.getLeaderboardsClient(getApplicationContext(), signedInAccount)
									.submitScore(getResources().getString(R.string.score_leader_id), Integer.valueOf(line));



						} catch (IOException e) {
							Log.d("debug", "onClick: " + e.getMessage());
						}
					}
					showLeaderboard();
				}
				else{
					CharSequence text = "you are not signed in";
					int duration = Toast.LENGTH_LONG;

					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					signIn();
				}
			}
		});

		// Preference Option - Brings up a local Preference up
		findViewById(R.id.mm_pref_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent prefIntent = new Intent(MainMenu.this, Preferences.class);
				startActivity(prefIntent);
			}
		});

		// Preference Option - Brings up a local Preference up
		findViewById(R.id.mm_achiev_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(isSignedIn()){
					showAchievements();
				}
				else{
					CharSequence text = "you are not signed in";
					int duration = Toast.LENGTH_LONG;

					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					signIn();
				}
			}
		});

		// Play option - starts the game
		findViewById(R.id.mm_exit_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	@Override
	protected  void onStart(){
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!isSignedIn()){
			signInSilently();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//signOut();
	}

	@Override
	protected void onPause(){
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			if (result.isSuccess()) {
				// The signed in account is stored in the result.
				signedInAccount = result.getSignInAccount();
			} else {
				String message = result.getStatus().getStatusMessage();
				if (message == null || message.isEmpty()) {
					message = getString(R.string.signInGenericError);
				}
				new AlertDialog.Builder(this).setMessage(message)
						.setNeutralButton(android.R.string.ok, null).show();
			}
		}
	}

	private void signIn() {
		GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
				GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
		Intent intent = signInClient.getSignInIntent();
		startActivityForResult(intent, RC_SIGN_IN);
	}

	private void showLeaderboard() {
		if(isSignedIn()){
			Games.getLeaderboardsClient(this,  signedInAccount)
					.getLeaderboardIntent(getString(R.string.score_leader_id))
					.addOnSuccessListener(new OnSuccessListener<Intent>() {
						@Override
						public void onSuccess(Intent intent) {
							startActivityForResult(intent, RC_LEADERBOARD_UI);
						}
					});

		}

	}

	private boolean isSignedIn() {
		return signedInAccount != null;
	}

	private void signInSilently() {
		if(!isSignedIn()){
			GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
					GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
			signInClient.silentSignIn().addOnCompleteListener(this,
					new OnCompleteListener<GoogleSignInAccount>() {
						@Override
						public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
							if (task.isSuccessful()) {
								// The signed in account is stored in the task's result.
								signedInAccount = task.getResult();
							} else {
								// Player will need to sign-in explicitly using via UI
								signIn();
							}
						}
					});
		}
	}

	private void signOut() {
		final GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
				GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
		signInClient.signOut().addOnCompleteListener(this,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						// at this point, the user is signed out.
						signedInAccount = null;
					}
				});
	}

	private void showAchievements() {
		Games.getAchievementsClient(this, signedInAccount)
				.getAchievementsIntent()
				.addOnSuccessListener(new OnSuccessListener<Intent>() {
					@Override
					public void onSuccess(Intent intent) {
						startActivityForResult(intent, RC_ACHIEVEMENT_UI);
					}
				});
	}
}


