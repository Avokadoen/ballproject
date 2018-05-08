package com.ahs.avokado.gettingair;

import android.graphics.Bitmap;
import android.graphics.Paint;

/*
	sources:
		-textAsBitmap: http://snipplr.com/view/73714/android-draw-text-to-dynamically-sized-bitmap/
*/

class ScoreHittable extends Hittable {
	private float imageAlpha;
	private float lerpColorPos;

	private final Bitmap scoreBitmap;

	ScoreHittable(int startX, int startY, int score, int textSize){
		this.startX = startX;
		this.startY = startY;
		currentX 	= startX;
		currentY 	= startY;
		endX 		= startX;
		endY 		= (int)(startY * 0.85f);

		imageAlpha 	= 255.0f;
		lerpColorPos = 0;

		scoreBitmap = GUI.textAsBitmap(String.valueOf(score), textSize * 4,  textSize);

	}

	public boolean moveAndLerpColor(double deltaTime){
		// lerp alpha on floating score text
		lerpColorPos += deltaTime * speed;
		imageAlpha = Globals.lerp(255, 0, lerpColorPos);
		return move(deltaTime);
	}

	public Paint getAlphaPaint(){
		Paint alphaPaint = new Paint();
		alphaPaint.setARGB((int)imageAlpha, 255, 255, 255);
		return alphaPaint;
	}

	public Bitmap getBitmap(){
		return scoreBitmap;
	}


}
