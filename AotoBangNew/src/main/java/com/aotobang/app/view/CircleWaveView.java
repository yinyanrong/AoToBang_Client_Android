package com.aotobang.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleWaveView extends View implements Runnable {
	private int gradualWidth;
	public int getGradualWidth() {
		return gradualWidth;
	}

	public void setGradualWidth(int gradualWidth) {
		this.gradualWidth = gradualWidth;
	}

	private float mWidth;
	private float mHeight;
	
	private float centerX; //圆心X
	private float centerY; //圆心Y
	private float floatRadius; //变化的半径
	private float maxRadius = -1; //圆半径
	private volatile boolean started = false;
	//private Paint mLinePaint;
	private Paint mSolidPaint;
	private int waveColor = Color.argb(128, 0, 0, 0); //颜色
	private int waveInterval = 100; //圆环的宽度
	private boolean centerAlign = true;//居中
	private float bottomMargin = 0;//底部margin
	private boolean fillCircle = true;//是否填充成实心圆环
	
	public CircleWaveView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initView();
	}

	public CircleWaveView(Context context) {
		this(context, null, 0);
		initView();
	}
	
	public CircleWaveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		initView();
	}
	
	private void initView() {
		//mLinePaint = new Paint();
		mSolidPaint = new Paint();
	}

	private void init() {
		mWidth = getWidth();
		mHeight = getHeight();
		
	//	mLinePaint.setAntiAlias(true);
		//mLinePaint.setStrokeWidth(1.0F);
	//	mLinePaint.setStyle(Paint.Style.STROKE);
	//	mLinePaint.setColor(waveColor);
		
		if (fillCircle) {
			//画笔选择。控制效果
			mSolidPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mSolidPaint.setStrokeWidth(waveInterval);
			mSolidPaint.setColor(waveColor);
		}
		
		centerX = mWidth / 2.0F;
		if (centerAlign) {
			centerY = (mHeight / 2.0F);
		}
		else {
			centerY = mHeight - bottomMargin;
		}
		
		if (mWidth >= mHeight) {
			maxRadius = mHeight / 2.0F;
		}
		else {
			maxRadius = mWidth / 2.0F;
		}
		
		//floatRadius = (maxRadius % waveInterval);
		
		//start();
	}

	public void start() {
		if (!started) {
			started = true;
			new Thread(this).start();
		}
	}

	public void stop() {
		started = false;
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stop();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (maxRadius <= 0.0F) {
			return;
		}
		float radius = floatRadius % waveInterval;
		while (true) {
			
			//int alpha2=(int) (255.0F * (1.0F - (floatRadius- gradualWidth)/ maxRadius-gradualWidth));
			
			int	 alpha = (int) (255.0F * (1.0F - radius / maxRadius));
				
			if (alpha <= 0) {
				break;
			}
			
			if (fillCircle) {
				float realdius=radius-waveInterval/2;
				mSolidPaint.setAlpha(alpha >> 1);
				canvas.drawCircle(centerX, centerY, realdius, mSolidPaint);
			}
		//	mLinePaint.setAlpha(alpha);
		//	canvas.drawCircle(centerX, centerY, radius, mLinePaint);
			radius += waveInterval;
		}
	}

	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			init();
		}
		else {
			stop();
		}
	}

	public void run() {
		while (started) {
			floatRadius = 2.0F + floatRadius;
			if (floatRadius > maxRadius) {
				floatRadius = (maxRadius % waveInterval);
			}
			postInvalidate();
			try {
				Thread.sleep(20L);
			} catch (InterruptedException localInterruptedException) {
				localInterruptedException.printStackTrace();
			}
		}
	}

	public void setMaxRadius(float maxRadius) {
		this.maxRadius = maxRadius;
	}

	public void setWaveColor(int waveColor) {
		this.waveColor = waveColor;
	}

	public void setWaveInterval(int waveInterval) {
		this.waveInterval = waveInterval;
	}

	public void setCenterAlign(boolean centerAlign) {
		this.centerAlign = centerAlign;
	}
}
