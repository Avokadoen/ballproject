package com.ahs.avokado.gettingair;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

class CharacterSprite {

	private final Bitmap originalOriginalImage;
	private Bitmap originalImage;
	private Bitmap image;

	private float prevX, prevY;
	private float x, y;
	private float velX, velY;
	private float gravityX, gravityY;
	private final float fallVel;
	private final float maxSpeed;
	private final float ratio;
	private float characterScale;
	private final float maxCharacterScale;
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
		characterScale = 0.02f;
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

		velX = 0;
		velY = 0;
		gravityX = 0;
		gravityY = 0;
		fallVel = size * 0.6f;
		maxSpeed = fallVel * 30f;
		frameCollision = false;
		image = originalImage;

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
		if(gravityY <= 0.01  && gravityY >= -0.01 ) {
			if(gravityX > 0) newAngle = -90;
			else newAngle = 90;

		}
		else{
			newAngle = (float)Math.toDegrees(Math.atan((gravityX * deltaTime)/(gravityY * deltaTime))) * -1;
			if(gravityY < 0) newAngle += 180;
		}

		rotateBitmap(newAngle, deltaTime);

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
		if(Math.abs(x) < 0.2 && Math.abs(y) < 0.2){
			x = 0;
			y = 0;
		}
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

	public boolean checkContact(RectF target){

		RectF self = new RectF(0,0, image.getWidth(), image.getHeight());

		Matrix transform = new Matrix();
		transform.setScale(0.9f,0.9f);
		transform.postTranslate(x, y);
		transform.mapRect(self);

		// rtr true if param contains sprite, false otherwise
		return target.intersect(self);
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
		characterScale 	= 0.02f;
		score 			= 0;

		imgSizeX = (int)(screenWidth * characterScale);
		imgSizeY = (int)((screenWidth * characterScale) * ratio);
		originalImage = Bitmap.createScaledBitmap(originalOriginalImage, imgSizeY, imgSizeX, false);
	}

	int getScore(){
		return score;
	}
}