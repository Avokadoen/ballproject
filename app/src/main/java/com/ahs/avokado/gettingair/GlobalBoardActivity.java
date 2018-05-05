package com.ahs.avokado.gettingair;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

public class GlobalBoardActivity extends AppCompatActivity {

	private GoogleSignInClient client;
	private GoogleSignInAccount signedInAccount;
	private LeaderboardsClient scoreLeaderboard;
	private static final int RC_SIGN_IN = 100;
	private static final int RC_LEADERBOARD_UI = 9004;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_global_board);

		GoogleSignInOptions gso = new GoogleSignInOptions.
				Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();

		// Build a GoogleSignInClient with the options specified by gso.
		client = GoogleSignIn.getClient(this, gso);

		signIn();

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

			showLeaderboard();
		} catch (ApiException e) {
			// The ApiException status code indicates the detailed failure reason.
			// Please refer to the GoogleSignInStatusCodes class reference for more information.
			Log.w("debug", "signInResult:failed code=" + e.getStatusCode());
			//updateUI(null);

		}
	}

	private boolean isSignedIn() {
		return GoogleSignIn.getLastSignedInAccount(this) != null;
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
