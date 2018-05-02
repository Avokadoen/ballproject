package com.example.avokado.lab3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by Avokado on 02-May-18.
 */

public class GUI {
	GUI(){

	}
	void deadLoop(){
		while(true){

		}
	}

	public Bitmap textAsBitmap(String text, int textWidth, int textSize) {
		// Get text dimensions
		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.LINEAR_TEXT_FLAG);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setARGB(255,200, 200, 0);
		textPaint.setShadowLayer(textWidth/100, 0, 0, Color.BLACK);
		textPaint.setTextSize(textSize);
		StaticLayout mTextLayout = new StaticLayout(text, textPaint,
				textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

		// Create bitmap and canvas to draw to
		Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		// Draw background
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.LINEAR_TEXT_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setARGB(0,0,0,0);
		c.drawPaint(paint);

		// Draw text
		c.save();
		c.translate(0, 0);
		mTextLayout.draw(c);
		c.restore();

		return b;
	}
}
