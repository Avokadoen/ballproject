package com.ahs.avokado.gettingair;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


class GameMainThread extends Thread {
	private boolean running;
	private final SurfaceHolder surfaceHolder;
	private final GameView gameView;

	public GameMainThread(SurfaceHolder surfaceHolder, GameView gameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gameView = gameView;
	}

	@Override
	public void run() {
		double lastFrameTime = System.currentTimeMillis();
		double deltaTime;

		while (running) {
			Canvas canvas = null;
			// get time since last frame
			deltaTime = (System.currentTimeMillis() - lastFrameTime) / 1000;
			lastFrameTime = System.currentTimeMillis();
			try {
				// start of frame
				canvas = this.surfaceHolder.lockCanvas();

				// lock update and draw
				synchronized(surfaceHolder) {
					this.gameView.update(deltaTime);
					this.gameView.draw(canvas);
				}
			} catch (Exception e) {
				Log.d("debug", "run: " + e.getMessage());
			} finally {
				if (canvas != null) {
					try {
						// separate canvas from surface
						surfaceHolder.unlockCanvasAndPost(canvas);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void setRunning(boolean isRunning) {		running = isRunning;	}

}
