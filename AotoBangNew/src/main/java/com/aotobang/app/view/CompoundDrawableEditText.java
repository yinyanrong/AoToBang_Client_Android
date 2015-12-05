package com.aotobang.app.view;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class CompoundDrawableEditText extends EditText {
	 private Drawable              mLeftDrawable;
	    private Drawable              mTopDrawable;
	    private Drawable              mRightDrawable;
	    private Drawable              mBottomDrawable;

	    private boolean               mIsLeftTouched;
	    private boolean               mIsTopTouched;
	    private boolean               mIsRightTouched;
	    private boolean               mIsBottomTouched;

	    private boolean               mIsAllDrawableTouchedResponse = true;
	    private boolean               mIsAlwaysClick                = true;
	    private int                   mLazyX                        = 0;
	    private int                   mLazyY                        = 0;

	    private DrawableClickListener mDrawableClickListener;

	 //   private OnClickListener       mOnClickListener;

	    public CompoundDrawableEditText(Context context, AttributeSet attrs, int defStyle){
	        super(context, attrs, defStyle);
	        init();
	    }

	    public CompoundDrawableEditText(Context context, AttributeSet attrs){
	        super(context, attrs);
	        init();
	    }

	    public CompoundDrawableEditText(Context context){
	        super(context);
	        init();
	    }

	    private void init() {
	       // super.setOnClickListener(this);
	    }

	    @Override
	    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
	    	 mLeftDrawable = left;
	         mTopDrawable = top;
	         mRightDrawable = right;
	         mBottomDrawable = bottom;
	    	super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
	    }
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {

	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	            resetTouchStatus();
	            if (mDrawableClickListener != null) {
	            	mIsBottomTouched = touchBottomDrawable(event);
	                mIsLeftTouched = touchLeftDrawable(event);
	                mIsTopTouched = touchTopDrawable(event);
	            	 mIsRightTouched = touchRightDrawable(event);
	            	if(mIsRightTouched){
	            		mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.RIGHT);
	            		return   true;
	            	}else if(mIsBottomTouched){
	            		mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.BOTTOM);
	            		return   true;
	            		
	            	}else if(mIsLeftTouched){
	            		mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.LEFT);
	            		return   true;
	            		
	            	}else if(mIsTopTouched){
	            		mDrawableClickListener.onClick(DrawableClickListener.DrawablePosition.TOP);
	            		return   true;
	            		
	            		
	            		
	            	}else{
	            		  return super.onTouchEvent(event);
	            		
	            	}
	             
	                
	                
	            }
	        }else if(event.getAction() == MotionEvent.ACTION_UP&&mIsRightTouched||mIsBottomTouched||mIsLeftTouched||mIsTopTouched){
	        	
	        		return true;
	        	
	        }else if(event.getAction() == MotionEvent.ACTION_MOVE)
	        	return true;

	        return super.onTouchEvent(event);
	    }


	 /*   @Override
	    public void setOnClickListener(OnClickListener l) {
	        mOnClickListener = l;
	    }*/

	    @Override
	    protected void finalize() throws Throwable {
	        mRightDrawable = null;
	        mBottomDrawable = null;
	        mLeftDrawable = null;
	        mTopDrawable = null;
	        super.finalize();
	    }

	    private void resetTouchStatus() {
	        mIsLeftTouched = false;
	        mIsTopTouched = false;
	        mIsRightTouched = false;
	        mIsBottomTouched = false;
	    }

	    private boolean touchLeftDrawable(MotionEvent event) {
	        if (mLeftDrawable == null) {
	            return false;
	        }

	        int drawHeight = mLeftDrawable.getIntrinsicHeight();
	        int drawWidth = mLeftDrawable.getIntrinsicWidth();
	        int topBottomDis = (mTopDrawable == null ? 0 : mTopDrawable.getIntrinsicHeight())
	                           - (mBottomDrawable == null ? 0 : mBottomDrawable.getIntrinsicHeight());
	        double imageCenterY = 0.5 * (this.getHeight() + topBottomDis);
	        Rect imageBounds = new Rect(this.getCompoundDrawablePadding() - mLazyX,
	                                    (int)(imageCenterY - 0.5 * drawHeight - mLazyY), this.getCompoundDrawablePadding()
	                                                                                     + drawWidth + mLazyX,
	                                    (int)(imageCenterY + 0.5 * drawHeight + mLazyY));
	        return imageBounds.contains((int)event.getX(), (int)event.getY());
	    }

	    private boolean touchTopDrawable(MotionEvent event) {
	        if (mTopDrawable == null) {
	            return false;
	        }

	        int drawHeight = mTopDrawable.getIntrinsicHeight();
	        int drawWidth = mTopDrawable.getIntrinsicWidth();
	        int leftRightDis = (mLeftDrawable == null ? 0 : mLeftDrawable.getIntrinsicWidth())
	                           - (mRightDrawable == null ? 0 : mRightDrawable.getIntrinsicWidth());
	        double imageCenterX = 0.5 * (this.getWidth() + leftRightDis);
	        Rect imageBounds = new Rect((int)(imageCenterX - 0.5 * drawWidth - mLazyX), this.getCompoundDrawablePadding()
	                                                                                    - mLazyY,
	                                    (int)(imageCenterX + 0.5 * drawWidth + mLazyX), this.getCompoundDrawablePadding()
	                                                                                    + drawHeight + mLazyY);
	        return imageBounds.contains((int)event.getX(), (int)event.getY());
	    }

	    private boolean touchRightDrawable(MotionEvent event) {
	        if (mRightDrawable == null) {
	            return false;
	        }

	        int drawHeight = mRightDrawable.getIntrinsicHeight();
	        int drawWidth = mRightDrawable.getIntrinsicWidth();
	        int topBottomDis = (mTopDrawable == null ? 0 : mTopDrawable.getIntrinsicHeight())
	                           - (mBottomDrawable == null ? 0 : mBottomDrawable.getIntrinsicHeight());
	        double imageCenterY = 0.5 * (this.getHeight() + topBottomDis);
	        Rect imageBounds = new Rect(this.getWidth() - this.getCompoundDrawablePadding() - drawWidth - mLazyX,
	                                    (int)(imageCenterY - 0.5 * drawHeight - mLazyY),
	                                    this.getWidth() - this.getCompoundDrawablePadding() + mLazyX,
	                                    (int)(imageCenterY + 0.5 * drawHeight + mLazyY));
	        return imageBounds.contains((int)event.getX(), (int)event.getY());
	    }

	    private boolean touchBottomDrawable(MotionEvent event) {
	        if (mBottomDrawable == null) {
	            return false;
	        }

	        int drawHeight = mBottomDrawable.getIntrinsicHeight();
	        int drawWidth = mBottomDrawable.getIntrinsicWidth();
	        int leftRightDis = (mLeftDrawable == null ? 0 : mLeftDrawable.getIntrinsicWidth())
	                           - (mRightDrawable == null ? 0 : mRightDrawable.getIntrinsicWidth());
	        double imageCenterX = 0.5 * (this.getWidth() + leftRightDis);
	        Rect imageBounds = new Rect((int)(imageCenterX - 0.5 * drawWidth - mLazyX), this.getHeight()
	                                                                                    - this.getCompoundDrawablePadding()
	                                                                                    - drawHeight - mLazyY,
	                                    (int)(imageCenterX + 0.5 * drawWidth + mLazyX), this.getHeight()
	                                                                                    - this.getCompoundDrawablePadding()
	                                                                                    + mLazyY);
	        return imageBounds.contains((int)event.getX(), (int)event.getY());
	    }

	    public interface DrawableClickListener {

	        public static enum DrawablePosition {
	            LEFT,
	            TOP,
	            RIGHT,
	            BOTTOM
	        };

	        public void onClick(DrawablePosition position);
	    }

	    public boolean isAllDrawableTouchedResponse() {
	        return mIsAllDrawableTouchedResponse;
	    }

	    public void setAllDrawableTouchedResponse(boolean isAllDrawableTouchedResponse) {
	        this.mIsAllDrawableTouchedResponse = isAllDrawableTouchedResponse;
	    }

	    public boolean isAlwaysClick() {
	        return mIsAlwaysClick;
	    }

	    public void setAlwaysClick(boolean isAlwaysClick) {
	        this.mIsAlwaysClick = isAlwaysClick;
	    }

	    public int getLazyX() {
	        return mLazyX;
	    }

	    public void setLazyX(int lazyX) {
	        this.mLazyX = lazyX;
	    }

	    public int getLazyY() {
	        return mLazyY;
	    }

	    public void setLazyY(int lazyY) {
	        this.mLazyY = lazyY;
	    }

	    public void setLazy(int lazyX, int lazyY) {
	        this.mLazyX = lazyX;
	        this.mLazyY = lazyY;
	    }

	    public void setDrawableClickListener(DrawableClickListener listener) {
	        this.mDrawableClickListener = listener;
	    }
}
