package com.example.avokado.lab3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;


public class CharacterSprite {
	private Bitmap image;
	private float prevx, prevy;
	private float x, y;
	private float velx, vely;
	private float gravx, gravy;
	private float fallvel;
	private float maxSpeed;
	private int imgSize;

	private boolean framecollision;
	private int screenWidth;


	public CharacterSprite(Bitmap bmp, int size, int X, int Y) {
		// init bitmap
		screenWidth = size;
		imgSize = (int)(size * 0.05);
		image = Bitmap.createScaledBitmap(bmp, imgSize, imgSize, false);

		// init pos and physics vars
		x = X;
		y = Y;
		velx = 0;
		vely = 0;
		gravx = 0;
		gravy = 0;
		fallvel = size * 0.8f;
		maxSpeed = fallvel * 65f;
		framecollision = false;
	}

	public void draw(Canvas canvas) {
		// draw sprite bitmap at x y
		canvas.drawBitmap(image, x, y, null);
	}

	public void update(double deltaTime){
		// save previous positions for collisions
		prevx = x;
		prevy = y;

		// if sprite collided in this frame
		if(framecollision){
			framecollision = false;
		}
		else{
			// calculate the max speed allowed for this frame
			float frameMaxSpeed = (float)(maxSpeed * deltaTime);

			// calculate the velocity of the sprite
			velx += (gravx * fallvel * deltaTime)*0.6;
			vely += (gravy * fallvel * deltaTime)*0.6;

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
		if(x + imgSize > screenWidth - inset){ // if ball hit right wall
			velx = -velx * 0.9f;
		}
		else if (x < inset){ // if ball hit left wall
			velx = -velx * 0.9f;
		}
		else { // if ball hit top or bottom
			vely = -vely * 0.9f;
		}

		// move sprite back to previous position to fix any issued with collision
		x = prevx;
		y = prevy;
		framecollision = true;
	}

	public boolean checkContain(Rect target){
		Rect self = new Rect
				((int)(x),(int)(y), (int)(x + imgSize), (int)(y + imgSize));

		// rtr true if param contains sprite false otherwise
		return target.contains(self);
	}
}