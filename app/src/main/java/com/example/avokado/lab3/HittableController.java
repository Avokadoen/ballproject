package com.example.avokado.lab3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HittableController {
	static private int imgSizeX;
	static private int imgSizeY;
	private int frameWidth;
	private int frameHeight;
	static private int atomicPrecision = 10000;

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
		spikeRatio = ( sWidth / sHeight);

		float oWidth = oxygenImage.getWidth();
		float oHeight = oxygenImage.getHeight();
		oxygenRatio = ( oWidth / oHeight);

		this.oxygenSpawnInterval 	= new AtomicInteger();
		this.spikesSpawnInterval 	= new AtomicInteger();
		oxygenSpawnIntervalCounter 	= new AtomicInteger();
		spikesSpawnIntervalCounter 	= new AtomicInteger();


		this.oxygenSpawnInterval.addAndGet((int)(oxygenSpawnInterval*atomicPrecision));
		this.spikesSpawnInterval.addAndGet((int)(spikesSpawnInterval*atomicPrecision));
		this.oxygenImage 			= Bitmap.createScaledBitmap(oxygenImage, (int)(imgSizeX * oxygenRatio), imgSizeY, false);
		this.spikeImage				= Bitmap.createScaledBitmap(spikeImage,  (int)(imgSizeX * spikeRatio), imgSizeY, false);
		this.oxygenImage.setDensity(this.oxygenImage.getDensity()/2);
		this.spikeImage.setDensity(this.spikeImage.getDensity()/2);

		oxygen = new ConcurrentLinkedQueue<Hittable>();
		spikes = new ConcurrentLinkedQueue<Hittable>();
		scores = new ConcurrentLinkedQueue<ScoreHittable>();
	}
	/*
		@param: The object containing the player data and delta time
		@return: 1(or more) for oxygen (score), 0 for nothing, -1 for dead
	*/
	public int update(double deltaTime, CharacterSprite player){

		oxygenSpawnIntervalCounter.addAndGet((int)(deltaTime*atomicPrecision));
		if( oxygenSpawnIntervalCounter.intValue() >= oxygenSpawnInterval.intValue()){
			double speed = (rand.nextDouble() / 12) + 0.06;
			Hittable newOxygen = new Hittable(speed, frameWidth, frameHeight, rand);
			oxygen.add(newOxygen);
			oxygenSpawnIntervalCounter.addAndGet(-oxygenSpawnIntervalCounter.intValue());
		}

		spikesSpawnIntervalCounter.addAndGet((int)(deltaTime*atomicPrecision));
		if(spikesSpawnIntervalCounter.intValue() >= spikesSpawnInterval.intValue()){
			double speed = (rand.nextDouble() / 14) + 0.06;
			Hittable newOxygen = new Hittable(speed, frameWidth, frameHeight, rand);
			spikes.add(newOxygen);
			spikesSpawnIntervalCounter.addAndGet(-spikesSpawnIntervalCounter.intValue());
		}
		int index = -1;
		int status = 0;
		for (Object obj : oxygen) {
			index++;
			if (obj != null && obj instanceof Hittable) {
				if(((Hittable) obj).move(deltaTime)){
					oxygen.remove(obj);
				}
				Matrix matrix = new Matrix();
				matrix.setRotate(((Hittable) obj).getCanvasDrawAngle(), 0, 0);
				matrix.postTranslate(((Hittable) obj).currentY, ((Hittable) obj).currentX);
				RectF hitboxFloat = new RectF(0, 0,
						oxygenImage.getWidth(), oxygenImage.getHeight());
				matrix.mapRect(hitboxFloat);
				Rect hitbox = new Rect
						((int)hitboxFloat.left, (int)hitboxFloat.top, (int)hitboxFloat.right, (int)hitboxFloat.bottom);
				if(player.checkContact(hitbox)){
					oxygen.remove(obj);
					status += 1;

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
				if(((Hittable) obj).move(deltaTime)){
					spikes.remove(obj);
				}
				Matrix matrix = new Matrix();
				matrix.setRotate(((Hittable) obj).getCanvasDrawAngle(), 0, 0);
				matrix.postTranslate(((Hittable) obj).currentY, ((Hittable) obj).currentX);
				RectF hitboxFloat = new RectF(0, 0,
						spikeImage.getWidth(), spikeImage.getHeight());
				matrix.mapRect(hitboxFloat);
				Rect hitbox = new Rect
						((int)hitboxFloat.left, (int)hitboxFloat.top, (int)hitboxFloat.right, (int)hitboxFloat.bottom);
				if(player.checkContact(hitbox)){
					//spikes.remove(obj);
					return -1;
				}
			}
		}
		index = -1;
		for (Object obj : scores) {
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
