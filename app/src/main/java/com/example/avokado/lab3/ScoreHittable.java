package com.example.avokado.lab3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/*
	sources:
		-textAsBitmap: http://snipplr.com/view/73714/android-draw-text-to-dynamically-sized-bitmap/
*/

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class ScoreHittable extends Hittable {
	private float imageAlpha;
	private float lerpColorPos;

	private Bitmap scoreBitmap;

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
		// lerp color
		lerpColorPos += deltaTime * speed;
		imageAlpha = lerp(255, 0, lerpColorPos);
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
