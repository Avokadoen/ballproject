package com.ahs.avokado.gettingair;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Array;

import static java.lang.Math.abs;

/*
	Sources:
	verifyCollision(): https://www.codeproject.com/Articles/524105/Per-pixel-collision-detection-on-Android-devices
*/

class CharacterSprite {

	private final Bitmap originalOriginalImage;
	private Bitmap originalImage;
	private Bitmap image;

	private float currentRotation;
	private float prevX, prevY;
	private float x, y;
	private float velX, velY;
	private float gravityX, gravityY;
	private float characterScale;
	private final float fallVel;
	private final float maxSpeed;
	private final float ratio;
	private final float maxCharacterScale;
	private static float startScale = 0.05f;
	private int score;

	private int imgSizeX;
	private int imgSizeY;

	private boolean frameCollision;
	private final int screenWidth;


	CharacterSprite(Bitmap bmp, int size, int X, int Y) {

		screenWidth = size;

		float width = bmp.getWidth();
		float height = bmp.getHeight();
		ratio = ( width / height);
		maxCharacterScale = 0.02f * 10;
		characterScale = startScale;
		originalOriginalImage = bmp;
		originalOriginalImage.setDensity(originalOriginalImage.getDensity()/2);

		// because of landscape mode these are flipped
		imgSizeX = (int)(size * characterScale);
		imgSizeY = (int)((size * characterScale) * ratio);
		originalImage = Bitmap.createScaledBitmap(bmp, imgSizeY, imgSizeX, false);

		// init pos and physics vars
		x = X;
		y = Y;

		fallVel 			= size * 0.6f;
		maxSpeed 			= fallVel * 30f;
		frameCollision 		= false;
		image 				= originalImage;

	}

	public void draw(Canvas canvas) {

		// draw sprite bitmap at x y
		canvas.drawBitmap(image, x, y, null);
	}

	public void receiveScore(int score){
		if(characterScale <= maxCharacterScale){

			double growthRateModifier = 0.0001 * this.score;
			if(growthRateModifier > 0.04) growthRateModifier = 0.04;

			characterScale *= 1.05 - growthRateModifier;
			imgSizeX = (int)(screenWidth * characterScale);
			imgSizeY = (int)((screenWidth * characterScale) * ratio);
			originalImage = Bitmap.createScaledBitmap(originalOriginalImage, imgSizeY, imgSizeX, false);
		}
		this.score += score;
	}

	public void update(double deltaTime){
		// save previous positions for collisions
		prevX = x;
		prevY = y;

		// if sprite collided in this frame
		if(frameCollision){
			frameCollision = false;
		}
		else{
			// calculate the max speed allowed for this frame
			float frameMaxSpeed = (float)(maxSpeed * deltaTime);

			// calculate the velocity of the sprite
			velX -= (gravityX * fallVel * deltaTime)*0.6;
			velY -= (gravityY * fallVel * deltaTime)*0.6;

			// check if max has been reached
			if(velX > frameMaxSpeed){
				velX = frameMaxSpeed;
			}
			else if(velX < -frameMaxSpeed){
				velX = -frameMaxSpeed;
			}
			if(velY > frameMaxSpeed){
				velY = frameMaxSpeed;
			}
			else if(velY < -frameMaxSpeed){
				velY = -frameMaxSpeed;
			}
		}

		// Rotating bitmap to match proper input from the user
		float newAngle;

		newAngle = (float)Math.toDegrees(Math.atan2(gravityX, gravityY)) * -1;

		currentRotation = newAngle;
		rotateBitmap(currentRotation, deltaTime);

		// move sprite
		y += velY * deltaTime;
		x += velX * deltaTime;

		// apply drag
		velY -= velY * deltaTime * 2;
		velX -= velX * deltaTime * 2;

	}

	public void updateGravity(float x, float y){
		// save data in sprite
		gravityX = x;
		gravityY = y;
	}

	public void collided(){
		// get screen inset
		float inset = screenWidth * 0.01f;

		// if ball has left frame with applied inset
		if(x + imgSizeX > screenWidth - inset){ // if ball hit right wall
			velX = -velX * 0.9f;
		}
		else if (x < inset){ 					// if ball hit left wall
			velX = -velX * 0.9f;
		}
		else { 									// if ball hit top or bottom
			velY = -velY * 0.9f;
		}

		// move sprite back to previous position to fix any issued with collision
		x = prevX;
		y = prevY;
		frameCollision = true;
	}

	public boolean checkContain(Rect target){
		Rect self = new Rect
				((int)(x),(int)(y), (int)(x + image.getWidth()), (int)(y + image.getHeight()));

		// rtr true if param contains sprite, false otherwise
		return target.contains(self);
	}

	// rtr true if param contains sprite, false otherwise
	public boolean checkContact(RectF targetHitbox, @Nullable Bitmap targetAsset, @Nullable Matrix targetMatrix){

		RectF self = new RectF(0,0, image.getWidth(), image.getHeight());

		Matrix selfTransform = new Matrix();
		selfTransform.setScale(0.9f,0.9f);
		selfTransform.setRotate(currentRotation);
		selfTransform.setTranslate(x, y);
		selfTransform.mapRect(self);

		RectF overlap = new RectF();
		if(overlap.setIntersect(targetHitbox, self)){
			if(targetAsset != null && targetMatrix != null){
				float[] offsetOverlap = new float[4];
				offsetOverlap[0] = self.centerX();
				offsetOverlap[1] = self.centerY();
				offsetOverlap[2] = targetHitbox.centerX();
				offsetOverlap[3] = targetHitbox.centerX();
				return verifyContact(overlap, targetAsset, targetMatrix, selfTransform, offsetOverlap);
			} else return true;
		}
		return false;
	}

	public boolean verifyContact(RectF overlap, Bitmap targetAsset, Matrix targetTransform, Matrix selfTransform, float[] overlapOffset){
		// offset twice to avoid floating point error (which cause illegal argument)
		overlap.offset(-overlap.centerX(), -overlap.centerY());
		overlap.offset(-overlap.centerX(), -overlap.centerY());

        overlap.offset(overlap.width()/2, overlap.height()/2);

        RectF selfOverlap = new RectF(overlap);
        selfTransform.mapRect(selfOverlap);
        selfOverlap.offset(overlapOffset[0], overlapOffset[1]);

        RectF targetOverlap = new RectF(overlap);
        targetTransform.mapRect(targetOverlap);
		targetOverlap.offset(overlapOffset[2], overlapOffset[3]);


		for (int y = 0; y < overlap.height(); y++){
			for (int x = 0; x < overlap.width(); x++){

				// if we are within both images pixel range, check pixel values
				if	(	selfOverlap.left + x < image.getWidth() && selfOverlap.top + y < image.getHeight())// &&
						//(overlap.left + x) < targetAsset.getWidth() && (overlap.top + y) < targetAsset.getHeight())
				{
					int charColor 	= image.getPixel((int)overlap.left + x, (int)overlap.top + y);
					int targetColor = targetAsset.getPixel((int)overlap.left + x, (int)overlap.top + y);

					Log.d("debug", "char alpha: " + Color.alpha(charColor));
					Log.d("debug", "target alpha: " + Color.alpha(targetColor));

					if ((Color.alpha(charColor) > 100) || (Color.alpha(targetColor) > 100)){
						return true;  //there are non-transparent pixels which overlap
					}

				}


			}
		}
		return false;
	}

	private void rotateBitmap (float newAngle, double deltaTime)
	{
		// makes sure balloon isn't stuck
		x += x/9 * -1 * deltaTime;
		y += y/9 * -1 * deltaTime;

		Matrix matrix = new Matrix();
		matrix.postRotate(newAngle);
		image = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, false);
	}

	void reset(int x, int y){
		prevX 			= x;
		prevY 			= y;
		this.x 			= x;
		this.y 			= y;
		velX 			= 0;
		velY 			= 0;
		score 			= 0;
		characterScale 	= startScale;

		imgSizeX = (int)(screenWidth * characterScale);
		imgSizeY = (int)((screenWidth * characterScale) * ratio);
		originalImage = Bitmap.createScaledBitmap(originalOriginalImage, imgSizeY, imgSizeX, false);
	}

	int getScore(){
		return score;
	}

	private int calculateDegreeChange(float currentRotation, float newAngle){
		int degreeChange = 0;
		if(currentRotation >= -90 && currentRotation <= 0){
			if(newAngle <= 90 && newAngle >= 0){
				degreeChange = (int)(abs(currentRotation) + newAngle);
			}
			else if(newAngle <= 180 && newAngle >= 90){
				degreeChange = (int)(abs(currentRotation + newAngle));
			}
			else if(newAngle >= -90 && newAngle <= 0){
				if(currentRotation >= newAngle) degreeChange = (int)(abs(currentRotation) - newAngle);
				else degreeChange = (int)(newAngle + abs(currentRotation));
				degreeChange = (int)(currentRotation - newAngle);
			}
			else{
				degreeChange = (int)(abs(currentRotation) - newAngle);
			}
		}
		else if(currentRotation >= -180 && currentRotation <= -90){
			if(newAngle >= 180 && newAngle <= 90){
				if(currentRotation >= newAngle) degreeChange = (int)(currentRotation - newAngle);
				else degreeChange = (int)(newAngle - currentRotation);
			}
			else{
				degreeChange = (int)(currentRotation - newAngle);
			}
		}
		else if(currentRotation >= 90 && currentRotation <= 180){
			if(newAngle >= 0 && newAngle <= 270){
				if(currentRotation >= newAngle) degreeChange = (int)(currentRotation - newAngle);
				else degreeChange = (int)(newAngle - currentRotation);
			}
			else{
				degreeChange = (int)(newAngle - currentRotation);
			}
		}
		else {
			if(newAngle <= 90){
				if(currentRotation >= newAngle) degreeChange = (int)(currentRotation - newAngle);
				else degreeChange = (int)(newAngle - currentRotation);
			}
			else if(newAngle <= 180){
				degreeChange = (int)(newAngle - currentRotation);
			}
			else if(newAngle <= 270){
				degreeChange = (int)(newAngle - currentRotation);
			}
			else{
				degreeChange = (int)(newAngle - 360 - currentRotation);
			}
		}
		// degreeChange = (int)(360 - currentRotation + newAngle);
		return degreeChange;
	}

	private int easyCalculateDegreeChange(float currentRotation, float newAngle){

		//if(currentRotation > 180) currentRotation = currentRotation - 360;
		//else if(currentRotation < -180) currentRotation = currentRotation + 360;


		int degreeChange = 0;
		if(currentRotation == newAngle){
			degreeChange = 0;
		}
		else if(currentRotation > newAngle){
			if(currentRotation <= 0) 						degreeChange = (int)(abs(currentRotation) - abs(newAngle));
			else if(currentRotation > 0 && newAngle > 0) 	degreeChange = (int)(currentRotation - newAngle);
			else if(currentRotation > 0 && newAngle < 0) 	degreeChange = (int)-(currentRotation + abs(newAngle));
			else if(currentRotation > 0 && newAngle == 0)	degreeChange = (int)-(currentRotation);
		}
		else if(currentRotation < newAngle){
			if(newAngle <= 0) 								degreeChange = (int)(abs(currentRotation) - abs(newAngle));
			else if(newAngle > 0 && currentRotation > 0) 	degreeChange = (int)(newAngle - currentRotation);
			else if(newAngle > 0 && currentRotation < 0) 	degreeChange = (int)(newAngle + abs(currentRotation));
			else if(newAngle > 0 && currentRotation == 0) 	degreeChange = (int)(newAngle);
		}
		if(degreeChange > 180) degreeChange = degreeChange - 360;
		else if(degreeChange < -180) degreeChange = degreeChange + 360;


		if(abs(degreeChange) > 45) Log.d("debug", "newAngle is: " + newAngle + "currentRotation is: " + currentRotation + "degreeChange is: " + degreeChange);
		return degreeChange;
	}
}