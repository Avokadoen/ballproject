package com.ahs.avokado.gettingair;

import java.util.Random;

/*
	source:
		lerp: https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support
*/

class Hittable {

	int startX;
	int startY;
	int endX;
	int endY;
	public int currentX, currentY;
	private float width;
	private float height;
	private float canvasDrawAngle;

	final double speed;
	private double lerpPos;

	Hittable(){
		speed = 0.25;
		lerpPos = 0;
	}

	public Hittable(double speed, int windowX, int windowY, Random rand){ //  float width, float height,
		this.speed 	= speed;

		// Calculating a random start and end position for game objects
		startX  	= (rand.nextInt((int)(windowX * 0.2)) + windowX);
		startY		= rand.nextInt(windowY*2);
		endY		= rand.nextInt(windowY*2);
		endX 		= -(rand.nextInt((int)(windowX * 0.2)) + windowX);

		currentX = startX;
		currentY = startY;

		lerpPos = 0;
		// Randomizing if start and end pos should be flipped to make objects come from both sides of the screen
		boolean down = rand.nextBoolean();
		if(down) {
			startX -= windowX;
			startX *= -1;
			endX *= -1;
		}


		// Calculating the proper rotation for the game objects to match it's movement
		float vectorX = endX - startX;
		float vectorY = endY - startY;
		canvasDrawAngle = 0;
		if(vectorY <= 0.0  && vectorY >= -0.0 ) {
			if(vectorX > 0) canvasDrawAngle = 90;
			else canvasDrawAngle = -90;

		}
		else{
			canvasDrawAngle = (float)Math.toDegrees(Math.atan((vectorX)/(vectorY)));
			if(vectorY < 0) canvasDrawAngle += 180;
		}
		canvasDrawAngle += 90;
	}

	public boolean move(double deltaTime){
		lerpPos += deltaTime * speed;

		currentX = (int)lerp(startX, endX, (float)lerpPos);
		currentY = (int)lerp(startY, endY, (float)lerpPos);

		return lerpPos >= 1 || currentX == endX && currentY == endY;
	}

	public float getCanvasDrawAngle(){
		return canvasDrawAngle;
	}

	float lerp(float v0, float v1, float t) {	return (1 - t) * v0 + t * v1;	}

}
