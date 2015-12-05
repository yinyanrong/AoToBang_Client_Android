package com.aotobang.app.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.aotobang.app.R;


public class AlphaView extends ImageView {
	private Bitmap slideBitmap;
	private Drawable alphaDrawable;
	private boolean showBkg; // 是否显示背景
	
	private int choose; // 当前选中首字母的位置
	private String[] ALPHAS;
	private OnAlphaChangedListener listener;
	private OnActionUpedListener actionUpListener;
	private float y;
	private int c;

	public AlphaView(Context context) {
		super(context);
		initAlphaView();
		
	}

	public AlphaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAlphaView();
	}

	public AlphaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAlphaView();
	}

	private void initAlphaView() {
		slideBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.ic_hit_point);
		
		showBkg = false;
		choose = -1;
		setImageResource(R.drawable.alpha_normal);
		alphaDrawable = getDrawable();
		
		ALPHAS = new String[28];
		ALPHAS[0] = "HOT"; // " "代表搜索
		ALPHAS[27] = "`";
		for (int i = 0; i < 26; i++) {
			ALPHAS[i + 1] = String.valueOf((char) (65 + i));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (showBkg) {
			int height=(int)y-slideBitmap.getHeight()/2;
			int with=(getWidth()-slideBitmap.getWidth())/2;
			canvas.drawBitmap(slideBitmap,with, height, null);
			setImageResource(R.drawable.alpha_pressed);
			alphaDrawable = getDrawable();

			alphaDrawable.setBounds(0, 0, getWidth(), getHeight());
		} else {
			setImageResource(R.drawable.alpha_normal);
			alphaDrawable = getDrawable();
			
			alphaDrawable.setBounds(0, 0, getWidth(), getHeight());
		}

		canvas.save();
		alphaDrawable.draw(canvas);
		canvas.restore();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		y = event.getY();
		final int oldChoose = choose;
		c = (int) (y / getHeight() * 28);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < ALPHAS.length) {
					listener.onAlphaChanged(ALPHAS[c], c);
					choose = c;
				}
			}
			invalidate();
			break;

		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < ALPHAS.length) {
					listener.onAlphaChanged(ALPHAS[c], c);
					choose = c;
				}
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			if(actionUpListener!=null)
				actionUpListener.onActionUped();
			break;
		}
		return true;
	}

	// 设置事件
	public void setOnAlphaChangedListener(OnAlphaChangedListener listener) {
		this.listener = listener;
	}

	// 事件接口
	public interface OnAlphaChangedListener {
		public void onAlphaChanged(String s, int index);
	}
	public interface OnActionUpedListener{
		public void onActionUped();
	}
	public void setOnActionUpedListener(OnActionUpedListener listener) {
		this.actionUpListener = listener;
	}

}
