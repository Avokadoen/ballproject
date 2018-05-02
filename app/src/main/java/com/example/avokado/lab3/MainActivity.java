package com.example.avokado.lab3;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;



/* Sources:
https://www.androidauthority.com/android-game-java-785331/
*/

// This activity should be main menu

public class MainActivity extends AppCompatActivity implements SensorEventListener{

	// accelerometer
	private SensorManager mSensorManager;
	private Sensor mGravity;

	// the renderer
	private GameView theGame;

	static private SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mPrefs = getPreferences(MODE_PRIVATE);

		theGame = new GameView(this);

		super.onCreate(savedInstanceState);
		setContentView(theGame);

		// retrieve hardware interface for accelerometer
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		try {
			mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		} catch (NullPointerException ne) {
			Log.d("SensorActivity()", "SensorActivity: null pointer exception");
			System.exit(-1);
		}

		// force fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);


	}

	@Override
	protected void onResume() {
		super.onResume();
		// create interface for accelerometer
		mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_GAME);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// remove interface for accelerometer
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// if player gives input
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			if(theGame.running) {
				theGame.updateGravity(event.values[1], event.values[0]);
			}
		}
	}

}

