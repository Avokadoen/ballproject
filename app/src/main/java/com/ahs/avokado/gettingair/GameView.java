package com.ahs.avokado.gettingair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.Context.POWER_SERVICE;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;

// todo: cosmetic: balloon pop frame on death
// class for creating frames
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	// our game thread that handles all of the app logic
	private final GameMainThread thread;

	// for holding ball bitmap and physics related data
	private CharacterSprite characterSprite;
	private int playerState;
	private boolean drawnDeathFrame;

	// for holding ball bitmap and physics related data
	private HittableController hittableController;

	// deals with all in-game gui
	private GUI gui;
	private Rect windowFrame;


	// variable to talk to hardware for feedback
	private final MediaPlayer plinger;
	private final Vibrator vibrator;
	private float effectInterval;
	private static boolean startEffects;

	private PowerManager.WakeLock wakeLock;

	// toggle thread
	public boolean running = false;

	public GameView(final Context context) {
		super(context);

		// Player related variables
		playerState 	= 0;
		drawnDeathFrame = false;

		// prepare parameter for thread
		getHolder().addCallback(this);
		thread = new GameMainThread(getHolder(), this);

		// Set view in focus
		setFocusable(true);

		// initialize hardware feedback
		startEffects = true;
		plinger = MediaPlayer.create(context, R.raw.hitmarker);
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		effectInterval = 0f;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		try{
			PowerManager powerManager = (PowerManager) getContext().getApplicationContext().getSystemService(POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"MyWakelockTag");

			// lock cpu for 10 min unless surface destroyed
			long timeOut = 600000;
			wakeLock.acquire(timeOut);
		}
		catch (NullPointerException e){
			Log.d("debug", "surfaceCreated: " + e.getMessage());
		}

		// get screen properties
		Rect frame = holder.getSurfaceFrame();

		gui = new GUI();
		gui.setView(this);

		// create the ball
		characterSprite =
				new CharacterSprite(
						BitmapFactory.decodeResource(getResources(), R.drawable.balloon),
						frame.width(), frame.centerX(), frame.centerY());

		// create hittable controller
		hittableController =
				new HittableController(
						frame.width(), frame.height(), 1f, 1f,
						BitmapFactory.decodeResource(getResources(), R.drawable.oxygen),
						BitmapFactory.decodeResource(getResources(), R.drawable.spike));

		// initialize the game thread
		running = true;
		thread.setRunning(running);
		thread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// attempt to abort thread
		boolean retry = true;
		while (retry) {
			try {
				running = false;
				thread.setRunning(running);
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retry = false;
		}

		wakeLock.release();
	}

	public void update(double deltaTime) {
		// update sprite
		if(playerState != -1){
			playerState = hittableController.update(deltaTime, characterSprite);
			if( playerState >= 1){
				characterSprite.receiveScore(playerState * 10);
				playerState = 0;
			}
			else if(playerState == -1){

				SharedPreferences sharedPref = getDefaultSharedPreferences(getContext().getApplicationContext());

				GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
				boolean doShare = !(sharedPref.getBoolean(getResources().getString(R.string.shareGlobalState), false));
				if(doShare){
					if(account != null){
						Games.getLeaderboardsClient(getContext(), account)
								.submitScore(getResources().getString(R.string.score_leader_id), characterSprite.getScore());
					}
				}
				if(account != null) {
					Games.getAchievementsClient(getContext(), account)
							.unlock(getResources().getString(R.string.ach_pilot_flight));
					Games.getAchievementsClient(getContext(), account)
							.increment(getResources().getString(R.string.ach_playten), 1);

					if(characterSprite.getScore() >= 50){
						Games.getAchievementsClient(getContext(), account)
								.unlock(getResources().getString(R.string.ach_way_to_go_rookie));
					}
				}

				gui.createDeadMenu(characterSprite.getScore(), windowFrame);

				//File dir = getContext().getFilesDir();
				//File file = new File(dir, filename);
				//boolean deleted = file.delete();
				ArrayList<String> scores = new ArrayList<>();

				for(int i = 0; i < 10; i++){		// This is done to ensure there are no NULL-values in the Score List
					scores.add(i, "0");
				}

				try{	// Try to read from leaderboard file, and put the values into the score list
					FileInputStream boardsFileContent = getContext().openFileInput(Globals.leaderBoardPath);
					BufferedReader reader = new BufferedReader(new InputStreamReader(boardsFileContent));
					String line;
					int index = 0;
					while ((line = reader.readLine()) != null){
						scores.add(index, line);
						index++;
					}
					boardsFileContent.close();
					reader.close();
				}
				catch(IOException e){
					Log.d("debug", "update: " + e.getCause());
				}

				boolean addedNewScore = false;
				String output = "";
				for (int i = 0; i < 10; i++) {
					//String scoreRaw = scores.get(i);
					//String value = scoreRaw.split("\r")[0];
					// If the player got a better score than what is already in the list
					if(!addedNewScore && characterSprite.getScore() >= Integer.valueOf(scores.get(i))){
						addedNewScore = true;
						scores.add(i, String.valueOf(characterSprite.getScore()));	// Adding score
					}
					// Preparing output for the leaderboard file
					output = output.concat(scores.get(i)).concat("\r");
					Log.d("debug", "update output: " + output);

				}
				try {

					FileOutputStream outputStream;
					outputStream = getContext().openFileOutput(Globals.leaderBoardPath, Context.MODE_PRIVATE);
					// Writing to the leaderboard file
					outputStream.write(output.getBytes());

					//File dir = getContext().getFilesDir();
					//File file = new File(dir, filename);
					//boolean deleted = file.delete();
					outputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			characterSprite.update(deltaTime);
			effectInterval += deltaTime;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		// create frame for ball
		windowFrame = canvas.getClipBounds();
		Rect bounds = canvas.getClipBounds();
		float widthInset = bounds.width() * 0.01f;
		float heightInset = bounds.height() * 0.01f;
		bounds.inset((int)(widthInset), (int)(heightInset));

		if(playerState != -1){
			// check if ball is leaving frame
			if(!characterSprite.checkContain(bounds)){

				// tell sprite it has collided
				characterSprite.collided();

				// collision effects
				if(startEffects){
					if(vibrator.hasVibrator()) {
						vibrator.vibrate(10);
					}
					plinger.start();
					startEffects = false;
				}
			}
			else if (effectInterval > 0.32){
				// allow new effect trigger
				startEffects = true;
				effectInterval = 0;
			}
		}

		// draw everything to screen
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(bounds, paint);
		hittableController.draw(canvas);
		characterSprite.draw(canvas);

		// Show Menu when dead
		if(playerState == -1){
			// Get player input from the gui and react accordingly
			int guiState = gui.drawDeadMenu(canvas);
			if(guiState == 2){ // Retry
				reset(windowFrame.centerX(), windowFrame.centerY());
			}
			else if(guiState == 3){ // Back to main menu
				Activity gameActivity = (Activity)getContext();
				gameActivity.finish();
			}
		}
	}

	public void updateGravity(float x, float y){
		characterSprite.updateGravity(x, y);
	}

	private void reset(int x, int y){
		hittableController.reset();
		characterSprite.reset(x, y);
		playerState = 0;
	}
}
