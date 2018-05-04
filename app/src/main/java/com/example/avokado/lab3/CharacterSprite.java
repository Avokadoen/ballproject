package com.example.avokado.lab3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

import static android.content.ContentValues.TAG;


public class CharacterSprite {

	private Bitmap originalOriginalImage;
	private Bitmap originalImage;
	private Bitmap image;

	private float prevx, prevy;
	private float x, y;
	private float velx, vely;
	private float gravx, gravy;
	private float fallvel;
	private float maxSpeed;
	private float ratio;
	private float characterScale;
	private float maxCharacterScale;
	private int score;

	private int imgSizeX;
	private int imgSizeY;

	private boolean frameCollision;
	private int screenWidth;


	public CharacterSprite(Bitmap bmp, int size, int X, int Y) {

		screenWidth = size;
		score = 0;
		float width = bmp.getWidth();
		float height = bmp.getHeight();
		ratio = ( width / height);
		maxCharacterScale = 0.02f * 10;
		characterScale = 0.02f;
		originalOriginalImage = bmp;
		originalOriginalImage.setDensity(originalOriginalImage.getDensity()/2);
		imgSizeX = (int)(size * characterScale);
		imgSizeY = (int)((size * characterScale) * ratio);
		originalImage = Bitmap.createScaledBitmap(bmp, imgSizeY, imgSizeX, false);
		Matrix matrix = new Matrix();
		matrix.postRotate(0);
		originalImage = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, false);

		// init pos and physics vars
		x = X;
		y = Y;

		velx = 0;
		vely = 0;
		gravx = 0;
		gravy = 0;
		fallvel = size * 0.6f;
		maxSpeed = fallvel * 30f;
		frameCollision = false;
		image = originalImage;
		//rotateBitmap(angle);
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
		prevx = x;
		prevy = y;

		// if sprite collided in this frame
		if(frameCollision){
			frameCollision = false;
		}
		else{
			// calculate the max speed allowed for this frame
			float frameMaxSpeed = (float)(maxSpeed * deltaTime);

			// calculate the velocity of the sprite
			velx -= (gravx * fallvel * deltaTime)*0.6;
			vely -= (gravy * fallvel * deltaTime)*0.6;

			// check if max has been reached
			if(velx > frameMaxSpeed){
				velx = frameMaxSpeed;
			}
			else if(velx < -frameMaxSpeed){
				velx = -frameMaxSpeed;
			}
			if(vely > frameMaxSpeed){
				vely = frameMaxSpeed;
			}
			else if(vely < -frameMaxSpeed){
				vely = -frameMaxSpeed;
			}
		}

		float newAngle = 0;
		if(gravy <= 0.01  && gravy >= -0.01 ) {
			if(gravx > 0) newAngle = -90;
			else newAngle = 90;

		}
		else{
			newAngle = (float)Math.toDegrees(Math.atan((gravx * deltaTime)/(gravy * deltaTime))) * -1;
			if(gravy < 0) newAngle += 180;
		}

		rotateBitmap(newAngle, deltaTime);

		// move sprite
		y += vely * deltaTime;
		x += velx * deltaTime;

		// apply drag
		vely -= vely * deltaTime * 2;
		velx -= velx * deltaTime * 2;

	}

	public void updateGravity(float x, float y){
		// smooth out decrease of speed
		// also an interpretation of users goal
		if(Math.abs(x) < 0.2 && Math.abs(y) < 0.2){
			x = 0;
			y = 0;
		}
		// feed accelerometer data to sprite
		gravx = x;
		gravy = y;
	}

	public void collided(){
		// get screen inset
		float inset = screenWidth * 0.01f;

		// if ball has left frame with applied inset
		if(x + imgSizeX > screenWidth - inset){ // if ball hit right wall
			velx = -velx * 0.9f;
		}
		else if (x < inset){ 					// if ball hit left wall
			velx = -velx * 0.9f;
		}
		else { 									// if ball hit top or bottom
			vely = -vely * 0.9f;
		}

		// move sprite back to previous position to fix any issued with collision
		x = prevx;
		y = prevy;
		frameCollision = true;
	}

	public boolean checkContain(Rect target){
		Rect self = new Rect
				((int)(x),(int)(y), (int)(x + image.getWidth()), (int)(y + image.getHeight()));

		// rtr true if param contains sprite, false otherwise
		return target.contains(self);
	}

	public boolean checkContact(Rect target){
		Rect self = new Rect
				((int)(x),(int)(y), (int)(x + image.getWidth()), (int)(y + image.getHeight()));


		// rtr true if param contains sprite, false otherwise
		return target.intersect(self);
	}

	public void rotateBitmap (float newAngle, double deltaTime)
	{
		// makes sure balloon isn't stuck
		x += x/9 * -1 * deltaTime;
		y += y/9 * -1 * deltaTime;

		Matrix matrix = new Matrix();
		matrix.postRotate(newAngle);
		image = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, false);
	}

	public void reset(int x, int y){
		prevx 			= x;
		prevy 			= y;
		this.x 			= x;
		this.y 			= y;
		velx 			= 0;
		vely 			= 0;
		characterScale 	= 0.02f;
		score 			= 0;
	}

	public int getScore(){
		return score;
	}
}