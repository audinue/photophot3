package edu.stts;

import android.content.Context;
import android.widget.ImageView;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader.TileMode;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class CircleImageView extends ImageView {

	public CircleImageView(Context context) {
		super(context);
	}

	protected void onDraw(Canvas c) {
		if (getDrawable() instanceof BitmapDrawable && getWidth() > 0 && getHeight() > 0) {
			Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
			if (bitmap != null) {
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				paint.setShader(new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP));
				c.drawOval(new RectF(0, 0, getWidth(), getHeight()), paint);
			}
		}
	}
}