package com.ahs.avokado.gettingair;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HittableController {
	static private int imgSizeX;
	static private int imgSizeY;
	private int frameWidth;
	private int frameHeight;
	static private int atomicPrecision = 10000; // Variable used to make AtomicInteger checks more precise

	private float oxygenRatio;
	private float spikeRatio;

	static Random rand = new Random();

	public ConcurrentLinkedQueue oxygen;
	public ConcurrentLinkedQueue spikes;
	public ConcurrentLinkedQueue scores;

	private AtomicInteger oxygenSpawnInterval;
	private AtomicInteger spikesSpawnInterval;
	private AtomicInteger oxygenSpawnIntervalCounter;
	private AtomicInteger spikesSpawnIntervalCounter;


	private Bitmap oxygenImage;
	private Bitmap spikeImage;

	public GUI gui;

	public HittableController(int frameWidth, int frameHeight, float oxygenSpawnInterval, float spikesSpawnInterval,
							  Bitmap oxygenImage, Bitmap spikeImage, GUI gui){
		this.gui = gui;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		imgSizeX = (int)(frameWidth * 0.05);
		imgSizeY = (int)(frameWidth * 0.05);

		float sWidth = spikeImage.getWidth();
		float sHeight = spikeImage.getHeight();
		spikeRatio = ( sWidth / sHeight);	// Ratio for proper difference in width/height for assets

		float oWidth = oxygenImage.getWidth();
		float oHeight = oxygenImage.getHeight();
		oxygenRatio = ( oWidth / oHeight);	// Ratio for proper difference in width/height for assets

		// Initialize values for spawn interval
		this.oxygenSpawnInterval 	= new AtomicInteger();
		this.spikesSpawnInterval 	= new AtomicInteger();
		oxygenSpawnIntervalCounter 	= new AtomicInteger();
		spikesSpawnIntervalCounter 	= new AtomicInteger();


		this.oxygenSpawnInterval.addAndGet((int)(oxygenSpawnInterval*atomicPrecision));
		this.spikesSpawnInterval.addAndGet((int)(spikesSpawnInterval*atomicPrecision));
		this.oxygenImage 			= Bitmap.createScaledBitmap(oxygenImage, (int)(imgSizeX * oxygenRatio), imgSizeY, false);
		this.spikeImage				= Bitmap.createScaledBitmap(spikeImage,  (int)(imgSizeX * spikeRatio), imgSizeY, false);
		this.oxygenImage.setDensity(this.oxygenImage.getDensity()/2);	// Setting the pixel density lower for bitmaps to increase performance
		this.spikeImage.setDensity(this.spikeImage.getDensity()/2);		// Setting the pixel density lower for bitmaps to increase performance

		oxygen = new ConcurrentLinkedQueue<Hittable>();
		spikes = new ConcurrentLinkedQueue<Hittable>();
		scores = new ConcurrentLinkedQueue<ScoreHittable>();
	}
	/*
		@param: The object containing the player data and delta time
		@return: 1(or more) for oxygen (score), 0 for nothing, -1 for dead
	*/
	public int update(double deltaTime, CharacterSprite player){

		// Increase counter for each loop with deltaTime
		oxygenSpawnIntervalCounter.addAndGet((int)(deltaTime*atomicPrecision));
		if( oxygenSpawnIntervalCounter.intValue() >= oxygenSpawnInterval.intValue()){		// When counter hits or exceeds spawn-rate variable
			double speed = (rand.nextDouble() / 12) + 0.06;									// Init random speed, with a default min speed of 0.06+
			Hittable newOxygen = new Hittable(speed, frameWidth, frameHeight, rand);		// Create object
			oxygen.add(newOxygen);															// Adding object to the ConcurrentLinkedQueue
			oxygenSpawnIntervalCounter.addAndGet(-oxygenSpawnIntervalCounter.intValue());	// Decrease IntervalCounter to maintain the spawn-rate
		}

		// Increase counter for each loop with deltaTime
		spikesSpawnIntervalCounter.addAndGet((int)(deltaTime*atomicPrecision));
		if(spikesSpawnIntervalCounter.intValue() >= spikesSpawnInterval.intValue()){		// When counter hits or exceeds spawn-rate variable
			double speed = (rand.nextDouble() / 14) + 0.06;									// Init random speed, with a default min speed of 0.06+
			Hittable newOxygen = new Hittable(speed, frameWidth, frameHeight, rand);		// Create Object
			spikes.add(newOxygen);															// Adding object to the ConcurrentLinkedQueue
			spikesSpawnIntervalCounter.addAndGet(-spikesSpawnIntervalCounter.intValue());	// Decrease IntervalCounter to maintain the spawn-rate
		}
		int index = -1;
		int status = 0;
		for (Object obj : oxygen) {
			index++;
			if (obj != null && obj instanceof Hittable) {
				if(((Hittable) obj).move(deltaTime)){	// Moves the object, and checks if it's lerp-movement is done
					oxygen.remove(obj);					// Remove the object if it's movement is done (off-screen)
				}
				Matrix matrix = new Matrix();
				// Retrieving and setting the proper rotation
				matrix.setRotate(((Hittable) obj).getCanvasDrawAngle(), 0, 0);
				matrix.postTranslate(((Hittable) obj).currentY, ((Hittable) obj).currentX);
				// Constructing hitbox
				RectF hitbox = new RectF(0, 0,
						oxygenImage.getWidth(), oxygenImage.getHeight());
				matrix.mapRect(hitbox);
				if(player.checkContact(hitbox)){		// Check for collision with player
					oxygen.remove(obj);					// Remove object that collided with player
					status += 1;						// Counting a status for "score", if the player would collide with more than one object in one frame

					// Creating a floating score text to display score to the player
					ScoreHittable newScore = new ScoreHittable
							(((Hittable) obj).currentY, ((Hittable) obj).currentX,
									player.getScore() + 10, frameWidth / 25);

					scores.add(newScore);
				}
			}
		}
		index = -1;
		for (Object obj : spikes) {
			index++;
			if (obj != null && obj instanceof Hittable) {
				if(((Hittable) obj).move(deltaTime)){	// Moves the object, and checks if it's lerp-movement is done
					spikes.remove(obj);					// Remove the object if it's movement is done (off-screen)
				}
				Matrix matrix = new Matrix();
				// Retrieving and setting the proper rotation
				matrix.setRotate(((Hittable) obj).getCanvasDrawAngle(), 0, 0);
				matrix.postScale(0.8f, 0.8f);
				matrix.postTranslate(((Hittable) obj).currentY, ((Hittable) obj).currentX);
				//matrix.postScale(0.9f, 0f;
				// Constructing hitbox
				RectF hitbox = new RectF(0, 0,
						spikeImage.getWidth(), spikeImage.getHeight());
				matrix.mapRect(hitbox);

				if(player.checkContact(hitbox)){		// Check for collision with player
					//spikes.remove(obj);
					return -1;							// Returns -1 as playerStatus (dead)
				}
			}
		}
		index = -1;
		for (Object obj : scores) {			// Updating the displaying scores to the player
			index++;
			if (obj != null && obj instanceof ScoreHittable) {
				if (((ScoreHittable) obj).moveAndLerpColor(deltaTime)) {
					scores.remove(obj);
				}
			}
		}
		return status;
	}

	public void draw(Canvas canvas){
		for (Object obj : scores) {
			if (obj != null && obj instanceof ScoreHittable) {
				canvas.drawBitmap(((ScoreHittable) obj).getBitmap(), ((ScoreHittable) obj).currentX, ((ScoreHittable) obj).currentY, ((ScoreHittable) obj).getAlphaPaint());
			}
		}
		for (Object obj : oxygen) {
			if (obj != null && obj instanceof Hittable) {
				Matrix matrix = new Matrix();
				matrix.setRotate(((Hittable) obj).getCanvasDrawAngle(), 0, 0);
				matrix.postTranslate(((Hittable) obj).currentY, ((Hittable) obj).currentX);
				canvas.drawBitmap(oxygenImage, matrix, null);
			}
		}
		for (Object obj : spikes) {
			if (obj != null && obj instanceof Hittable) {
				Matrix matrix = new Matrix();
				matrix.setRotate(((Hittable) obj).getCanvasDrawAngle(), 0, 0);
				matrix.postTranslate(((Hittable) obj).currentY, ((Hittable) obj).currentX);
				canvas.drawBitmap(spikeImage, matrix, null);
			}
		}
	}

	public void reset(){
		oxygen.clear();
		spikes.clear();
		scores.clear();

		oxygenSpawnIntervalCounter.set(0);
		spikesSpawnIntervalCounter.set(0);
	}
}
