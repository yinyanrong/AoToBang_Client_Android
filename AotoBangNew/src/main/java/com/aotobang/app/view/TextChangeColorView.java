package com.aotobang.app.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TextChangeColorView extends TextView {
	private TextClickListener listener;
	private boolean touchOut=false;


	public TextChangeColorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TextChangeColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TextChangeColorView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setTextClickListener(TextClickListener listener) {
		this.listener = listener;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
		
		
		case MotionEvent.ACTION_DOWN:
			if(listener!=null)
				listener.onDown();
			break;
		case MotionEvent.ACTION_MOVE:
			if(listener!=null){
				if(isContains(TextChangeColorView.this,(int)event.getRawX(),(int)event.getRawY())&&!touchOut)
					listener.onMove(true);
				else
					listener.onMove(false);
				
			}
			break;
		case MotionEvent.ACTION_UP:
			if(listener!=null)
				listener.onUp();
			touchOut=false;
			break;

		}
			
		return super.onTouchEvent(event);
		

	}

	public interface TextClickListener {
		public void onDown();
		public void onMove(boolean focused);

		public void onUp();

	}
	private Rect rect = null;
    private boolean isContains(View view, int x, int y) {
    	if (null == rect) {
    		rect = new Rect();
        }
    	view.getDrawingRect(rect);
    	  int[] location = new int[2];
    	  view.getLocationOnScreen(location);
    	  rect.left = location[0];
    	  rect.top = location[1];
    	  rect.right= rect.right+location[0];
    	  rect.bottom= rect.bottom+location[1];
       if(rect.contains(x, y))
    	   return true;
       
       else{
    	touchOut=true;
        return false;
       
       }
    }

}
