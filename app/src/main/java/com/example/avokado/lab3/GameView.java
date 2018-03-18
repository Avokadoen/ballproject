package com.example.avokado.lab3;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


// class for creating frames
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	// our game thread that handles all of the app logic
	private GameMainThread thread;

	// for holding ball bitmap and physics related data
	private CharacterSprite characterSprite;

	// variable to talk to hardware for feedback
	private final MediaPlayer plinger;
	private Vibrator vibrator;
	private float effectInterval;
	public static boolean startEffects;

	// toggle thread
	public boolean running = false;

	public GameView(Context context) {
		super(context);

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

		// create the ball
		characterSprite =
				new CharacterSprite(
						BitmapFactory.decodeResource(getResources(),R.drawable.beachball),
						frame.width(), frame.centerX(), frame.centerY());

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
	}

	public void update(double deltaTime) {
		// update sprite
		characterSprite.update(deltaTime);
		effectInterval += deltaTime;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		// create frame for ball
		Rect bounds = canvas.getClipBounds();
		float widthInset = bounds.width() * 0.01f;
		float heightInset = bounds.height() * 0.01f;
		bounds.inset((int)(widthInset), (int)(heightInset));

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

		// draw everything to screen
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawRect(bounds, paint);
		characterSprite.draw(canvas);
	}

	public void updateGravity(float x, float y){
		characterSprite.updateGravity(x, y);
	}
}
