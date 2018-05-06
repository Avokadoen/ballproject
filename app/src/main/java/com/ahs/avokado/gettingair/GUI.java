package com.ahs.avokado.gettingair;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import android.view.MotionEvent;
import android.view.View;


class GUI {
	private int guiState; // Different states for the menu, that are also accessed in the GameView for input from the user
	private Bitmap deadMenu;

	private RectF retryRect;
	private RectF menuRect;

	private final View.OnTouchListener handleTouch = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.performClick();
			if(guiState == -1){
				int x = (int) event.getX();
				int y = (int) event.getY();

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
						// Retry
						if(retryRect.contains(x, y)){
							guiState = 2;
						}
						// Back to menu
						else if(menuRect.contains(x, y)){
							guiState = 3;
						}
						break;
				}


			}
			return true;
		}
	};

	GUI(){
		guiState = 0;
	}

	public void setView(View v){
		v.setOnTouchListener(handleTouch);
	}

	public int drawDeadMenu(Canvas canvas){
		if(guiState == -1){
			canvas.drawBitmap(deadMenu, 0, 0, null);
		}
		return guiState;
	}

	public void createDeadMenu(int score, Rect frame){

		Bitmap deadMessage;
		Bitmap menuOption;
		Bitmap retryOption;

		deadMessage 	= textAsBitmap("SCORE: " + String.valueOf(score), (frame.width()/2), frame.height()/10);
		menuOption 		= textAsBitmap("TO MENU", (frame.width()/4), frame.height()/14);
		retryOption 	= textAsBitmap("RETRY", (frame.width()/4), frame.height()/14);

		deadMenu = Bitmap.createBitmap(frame.width(), frame.height(), deadMessage.getConfig());
		Canvas canvas = new Canvas(deadMenu);
		Matrix transform = new Matrix();
		transform.setTranslate(deadMessage.getWidth()/2, frame.height()/6);

		// draw menu background
		canvas.drawARGB(140, 0, 0, 0);
		canvas.drawBitmap(deadMessage, transform, null);

		// Create the "to menu" option and its hitbox for click
		transform.setTranslate(deadMessage.getWidth() - menuOption.getWidth(), frame.height()/2);
		canvas.drawBitmap(menuOption, transform, null);
		menuRect = new RectF(0, 0, menuOption.getWidth(), menuOption.getHeight());
		transform.mapRect(menuRect);

		// Create the "retry" option and its hitbox for click
		transform.setTranslate(deadMessage.getWidth() , frame.height()/2);
		canvas.drawBitmap(retryOption, transform, null);
		retryRect = new RectF(0, 0, retryOption.getWidth(), retryOption.getHeight());
		transform.mapRect(retryRect);


		guiState = -1;
	}

	static public Bitmap textAsBitmap(String text, int textWidth, int textSize) {
		// Get text dimensions
		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.LINEAR_TEXT_FLAG);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setARGB(255,200, 200, 0);
		textPaint.setShadowLayer(textWidth/100, 0, 0, Color.BLACK);
		textPaint.setTextSize(textSize);
		StaticLayout mTextLayout = new StaticLayout(text, textPaint,
				textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

		// Create bitmap and canvas to draw to
		Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		// Draw background
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.LINEAR_TEXT_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setARGB(0,0,0,0);
		c.drawPaint(paint);

		// Draw text
		c.save();
		c.translate(0, 0);
		mTextLayout.draw(c);
		c.restore();

		return b;
	}
}
