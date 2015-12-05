package com.aotobang.app.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class ListenClickRelativeLayout extends RelativeLayout {
private OnRlTouchListener onRlTouchlistener ;
private boolean touchOut=false;


public void setOnRlTouchlistener(OnRlTouchListener onRlTouchlistener) {
	this.onRlTouchlistener = onRlTouchlistener;
}

	public ListenClickRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ListenClickRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ListenClickRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(onRlTouchlistener!=null)
				onRlTouchlistener.OnRlDown();
			break;
		case MotionEvent.ACTION_MOVE:
			if(onRlTouchlistener!=null){
				if(isContains(ListenClickRelativeLayout.this,(int)event.getRawX(),(int)event.getRawY())&&!touchOut)
					onRlTouchlistener.OnRlMove(true);
				else
					onRlTouchlistener.OnRlMove(false);
				
			}
			break;
		case MotionEvent.ACTION_UP:
			if(onRlTouchlistener!=null)
				onRlTouchlistener.OnRlUp();
			touchOut=false;
			break;
		
		
		
		
		}
		return super.onTouchEvent(event);
	}
	public  interface OnRlTouchListener{
		
		public void OnRlDown();
		public void OnRlMove(boolean focused);
		public void OnRlUp();
		
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
