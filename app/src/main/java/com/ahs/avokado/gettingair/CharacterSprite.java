package com.ahs.avokado.gettingair;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import static java.lang.Math.abs;

class CharacterSprite {

	private final Bitmap originalOriginalImage;
	private Bitmap originalImage;
	private Bitmap image;

	private float rotationPos;
	private float currentRotation, startAngle, endAngle;
	private float prevX, prevY;
	private float x, y;
	private float velX, velY;
	private float gravityX, gravityY;
	private float prevXGravity, prevYGravity;
	private float characterScale;
	private final float fallVel;
	private final float maxSpeed;
	private final float ratio;
	private final float maxCharacterScale;
	private static float startScale = 0.02f;
	private int score;

	private int imgSizeX;
	private int imgSizeY;

	private boolean frameCollision;
	private final int screenWidth;


	CharacterSprite(Bitmap bmp, int size, int X, int Y) {

		screenWidth = size;
		score = 0;
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
		Matrix matrix = new Matrix();
		matrix.postRotate(0);
		originalImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, false);

		// init pos and physics vars
		x = X;
		y = Y;

		currentRotation		= 0;
		startAngle 			= 0;
		endAngle 			= 0;
		velX 				= 0;
		velY 				= 0;
		gravityX 			= 0;
		gravityY 			= 0;
		prevXGravity		= 0;
		prevYGravity		= 0;
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

		float checkXNoise = abs(prevXGravity - gravityX);
		float checkYNoise = abs(prevYGravity - gravityY);

		//if(checkXNoise > 0.1 && checkYNoise > 0.1)
		newAngle = (float)Math.toDegrees(Math.atan2(gravityX, gravityY)) * -1;
		//else newAngle = currentRotation;

		currentRotation = newAngle;
		rotateBitmap(currentRotation, deltaTime);

		prevXGravity = gravityX;
		prevYGravity = gravityY;
		/*
		if(gravityX <= 0.03 && gravityX >= -0.03) newAngle = currentRotation;
		else newAngle = (float)Math.toDegrees(Math.atan2(gravityX, gravityY)) * -1;

		float lastRotation = currentRotation;

		int degreeChange = easyCalculateDegreeChange(currentRotation, newAngle);

		if(gravityX <= 0.08 && gravityX >= -0.08 && gravityY <= 0.08 && gravityY >= -0.08) currentRotation = lastRotation;
		else{
			if(abs(degreeChange) < 15) currentRotation = lastRotation;
			else if(degreeChange >= 15) currentRotation += 5;
			else if(degreeChange <= -15) currentRotation -= 5;
		}
		*/

		//if(currentRotation > 180) currentRotation = currentRotation - 360;
		//else if(currentRotation < -180) currentRotation = currentRotation + 360;

		//currentRotation = newAngle;
		//rotateBitmap(currentRotation, deltaTime);


		// move sprite
		y += velY * deltaTime;
		x += velX * deltaTime;

		// apply drag
		velY -= velY * deltaTime * 2;
		velX -= velX * deltaTime * 2;

	}

	public void updateGravity(float x, float y){
		// smooth out decrease of speed
		// also an interpretation of users goal
		/*if(abs(x) < 0.2 && abs(y) < 0.2){
			x = 0;
			y = 0;
		}*/
		// feed accelerometer data to sprite
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
	public boolean checkContact(RectF target){

		RectF self = new RectF(0,0, image.getWidth(), image.getHeight());

		Matrix transform = new Matrix();
		transform.setScale(0.9f,0.9f);
		transform.setRotate(currentRotation);
		transform.setTranslate(x, y);

		transform.mapRect(self);


		//if(target.intersect(self)){
		//	return verifyContact(target);
		//}
		return target.intersect(self);
	}

	public boolean verifyContact(RectF target){

		int imgW = image.getWidth();
		int imgH = image.getHeight();

		RectF self 			= new RectF(0,0, image.getWidth(), image.getHeight());
		RectF selfMiddle 	= new RectF();
		RectF selfRight 	= new RectF();
		RectF selfLeft 		= new RectF();
		RectF selfBottom 	= new RectF();


		Matrix coreTransform = new Matrix();
		coreTransform.setScale(0.5f,0.8f);
		coreTransform.setRotate(currentRotation);
		coreTransform.setTranslate(x, y);
		coreTransform.mapRect(selfMiddle, self);
		if(target.intersect(selfMiddle)){
			return true;
		}

		Matrix leftSideTransform = new Matrix();
		leftSideTransform.setScale(0.2f,0.4f);
		leftSideTransform.setRotate(currentRotation);
		leftSideTransform.setTranslate(x - imgW * 0.45f, y + imgH * 0.3f);
		leftSideTransform.mapRect(selfRight, self);
		if(target.intersect(selfRight)){
			return true;
		}

		Matrix rightSideTransform = new Matrix();
		rightSideTransform.setScale(0.2f,0.4f);
		rightSideTransform.setRotate(currentRotation);
		rightSideTransform.setTranslate(x - imgW * 0.45f, y + imgH * 0.3f);
		rightSideTransform.mapRect(selfLeft, self);
		if(target.intersect(selfLeft)){
			return true;
		}

		Matrix bottomTransform = new Matrix();
		bottomTransform.setScale(0.2f,0.1f);
		bottomTransform.setRotate(currentRotation);
		bottomTransform.setTranslate(x, y - imgH * 0.347f);
		bottomTransform.mapRect(selfBottom, self);
		if(target.intersect(selfBottom)){
			return true;
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