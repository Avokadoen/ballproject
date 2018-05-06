package com.ahs.avokado.gettingair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

public class MainMenu extends AppCompatActivity {

	private GoogleSignInClient client;
	private GoogleSignInAccount signedInAccount;
	private LeaderboardsClient scoreLeaderboard;
	private static final int RC_SIGN_IN = 100;
	private static final int RC_LEADERBOARD_UI = 9004;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		signedInAccount = null;

		GoogleSignInOptions gso = new GoogleSignInOptions.
				Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();

		// Build a GoogleSignInClient with the options specified by gso.
		client = GoogleSignIn.getClient(this, gso);

		signIn();

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
				if(isSignedIn()){
					SharedPreferences sharedPref = getDefaultSharedPreferences(getApplicationContext());
					boolean defaultValue = false;
					boolean dontShare = sharedPref.getBoolean(getResources().getString(R.string.shareGlobalState), defaultValue);
					if(!dontShare) {
						try {
							FileInputStream boardsFileContent = openFileInput(Globals.leaderBoardPath);
							BufferedReader reader = new BufferedReader(new InputStreamReader(boardsFileContent));
							String line;
							line = reader.readLine();

							Games.getLeaderboardsClient(getApplicationContext(), signedInAccount)
									.submitScore(getResources().getString(R.string.score_leader_id), Integer.valueOf(line));


						} catch (FileNotFoundException e) {
							Log.d("debug", "onClick: " + e.getMessage());
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
		// Check for existing Google Sign In account, if the user is already signed in
		// the GoogleSignInAccount will be non-null.
		signedInAccount = GoogleSignIn.getLastSignedInAccount(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		signInSilently();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			// The Task returned from this call is always completed, no need to attach
			// a listener.
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void signIn() {
		Intent signInIntent = client.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	private void showLeaderboard() {
		Games.getLeaderboardsClient(this, signedInAccount)
				.getLeaderboardIntent(getString(R.string.score_leader_id))
				.addOnSuccessListener(new OnSuccessListener<Intent>() {
					@Override
					public void onSuccess(Intent intent) {
						startActivityForResult(intent, RC_LEADERBOARD_UI);
					}
				});
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			signedInAccount = completedTask.getResult(ApiException.class);

		} catch (ApiException e) {
			// The ApiException status code indicates the detailed failure reason.
			// Please refer to the GoogleSignInStatusCodes class reference for more information.
			Log.w("debug", "signInResult:failed code=" + e.getStatusCode());
			//updateUI(null);

		}
	}

	private boolean isSignedIn() {
		return signedInAccount != null;
	}

	private void signInSilently() {
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
						}
					}
				});
	}
}


