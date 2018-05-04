package com.example.avokado.lab3;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

// todo: cosmetic: balloon pop frame on death
// todo: reset functions for retry option
// class for creating frames
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	// our game thread that handles all of the app logic
	private GameMainThread thread;

	// for holding ball bitmap and physics related data
	private CharacterSprite characterSprite;
	private int playerState;

	// for holding ball bitmap and physics related data
	private HittableController hittableController;

	// deals with all in-game gui
	public GUI gui;
	public Rect windowFrame;


	// variable to talk to hardware for feedback
	private final MediaPlayer plinger;
	private Vibrator vibrator;
	private float effectInterval;
	public static boolean startEffects;

	// toggle thread
	public boolean running = false;

	public GameView(Context context) {
		super(context);
		playerState = 0;
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
		// get screen properties
		Rect frame = holder.getSurfaceFrame();

		gui = new GUI();
		gui.setView(this);

		// create the ball
		characterSprite =
				new CharacterSprite(
						BitmapFactory.decodeResource(getResources(),R.drawable.balloon),
						frame.width(), frame.centerX(), frame.centerY());

		// create hittable controller
		hittableController =
				new HittableController(
						frame.width(), frame.height(), 1f, 1f,
						BitmapFactory.decodeResource(getResources(),R.drawable.oxygen),
						BitmapFactory.decodeResource(getResources(),R.drawable.spike), gui);

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
				Log.d("debug", "surfaceDestroyed: ");
				e.printStackTrace();
			}
			retry = false;
		}
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
				gui.createDeadMenu(characterSprite.getScore(), windowFrame);

				String filename = "localLeaderboard";
				//File dir = getContext().getFilesDir();
				//File file = new File(dir, filename);
				//boolean deleted = file.delete();
				ArrayList<String> scores = new ArrayList<>();

				for(int i = 0; i < 10; i++){
					scores.add(i, "0\n\r");
				}

				try{
					FileInputStream boardsFileContent = getContext().openFileInput(filename);
					BufferedReader reader = new BufferedReader(new InputStreamReader(boardsFileContent));
					String line;
					while ((line = reader.readLine()) != null){
						scores.add(0, line);
						Log.d("debug", "line: " + line);
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
				boolean addedNewScore = false;
				String output = "";
				for (int i = 0; i < 10; i++) {

					if(!addedNewScore && characterSprite.getScore() >= Integer.valueOf(scores.get(i))){
						addedNewScore = true;
						scores.add(i, String.valueOf(characterSprite.getScore()) + "\n\r");
					}
					output = output.concat(scores.get(i));
					Log.d("debug", "update output: " + output);

				}
				try {

					if(scores.isEmpty()){
						scores.add(String.valueOf(characterSprite.getScore()) + "\n");
						output = output.concat(scores.get(0));
						Log.d("debug", "empty output: " + output);
					}

					FileOutputStream outputStream;
					outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);

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


		if(playerState == -1){
			int guiState = gui.drawDeadMenu(canvas);
			if(guiState == 2){
				reset(windowFrame.centerX(), windowFrame.centerY());
			}
		}
	}

	public void updateGravity(float x, float y){
		characterSprite.updateGravity(x, y);
	}

	public void reset(int x, int y){
		hittableController.reset();
		characterSprite.reset(x, y);
		playerState = 0;
	}
}
