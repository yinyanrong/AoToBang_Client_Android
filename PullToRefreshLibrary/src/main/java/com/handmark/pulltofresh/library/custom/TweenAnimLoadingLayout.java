package com.handmark.pulltofresh.library.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class TweenAnimLoadingLayout extends LoadingLayout {
	private final Animation mRotateAnimation, mResetRotateAnimation;
	static final int FLIP_ANIMATION_DURATION = 150;
	 private AnimationDrawable animationDrawable; 
	 private int imageHeigh;
	 private int rawHeigh;
	 private int headerHeigh;
	
	 
	private int headerTextHeigh;
	public TweenAnimLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		
		final int rotateAngle = mode == Mode.PULL_FROM_START ? -180 : 180;
		mRotateAnimation = new RotateAnimation(0, rotateAngle, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
		mRotateAnimation.setFillAfter(true);

		mResetRotateAnimation = new RotateAnimation(rotateAngle, 0, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mResetRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mResetRotateAnimation.setDuration(FLIP_ANIMATION_DURATION);
		mResetRotateAnimation.setFillAfter(true);

		 mHeaderImage.setImageResource(R.drawable.xlist_view_loading);
		 mHeaderImage.setScaleType(ScaleType.FIT_XY);
		 mHeaderImage.measure(0, 0);
		 mHeaderText.measure(0, 0);
		 imageHeigh=mHeaderImage.getMeasuredHeight();
		 headerTextHeigh=mHeaderText.getMeasuredHeight();
		 //System.out.println(imageHeigh+"--imageHeigh" +headerTextHeigh+"--headerTextHeigh");
		 rawHeigh=mHeaderImage.getMeasuredHeight();
		 headerHeigh=getContentSize();
	    
	}

	@Override
	protected int getDefaultDrawableResId() {

		return R.drawable.xlist_view_loading;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
	}

	@Override
	protected void onPullImpl(float scaleOfLayout,int newScrollValue) {
	/*	System.out.println(scaleOfLayout+"-------------scaleOfLayout");
		System.out.println(newScrollValue+"-------------newScrollValue");
		System.out.println(imageHeigh+headerTextHeigh+"-------------imageHeigh+headerTextHeigh");
		System.out.println(getContentSize()+"-------------getContentSize");
		System.out.println(layoutPanding+"-------------layoutPanding");*/
		
		if(newScrollValue>imageHeigh+headerTextHeigh+extraHeigh+layoutPanding&&!isReleaseToRefresh){
	//float angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
		android.view.ViewGroup.LayoutParams params= mHeaderImage.getLayoutParams();
		params.height=imageHeigh+extraHeigh;
	     mHeaderImage.setLayoutParams(params);
	     
		}else if(newScrollValue>imageHeigh+headerTextHeigh+layoutPanding&&newScrollValue<imageHeigh+headerTextHeigh+extraHeigh+layoutPanding&&!isReleaseToRefresh){
			android.view.ViewGroup.LayoutParams params= mHeaderImage.getLayoutParams();
			params.height=newScrollValue-headerTextHeigh-layoutPanding;
		     mHeaderImage.setLayoutParams(params);
			
			
		}
	}

	@Override
	protected void pullToRefreshImpl() {
		if (mRotateAnimation == mHeaderImage.getAnimation()) {
			mHeaderImage.startAnimation(mResetRotateAnimation);
		}
	}

	@Override
	protected void refreshingImpl() {
		//mHeaderImage.clearAnimation();
		  if(rawHeigh!=0){
		        android.view.ViewGroup.LayoutParams params=mHeaderImage.getLayoutParams();
		        System.out.println(rawHeigh+"--------------");
		        params.height=rawHeigh+10;
		        mHeaderImage.setLayoutParams(params);
		        imageHeigh=rawHeigh;
		        
		        }
		//mHeaderImage.setImageResource(R.anim.loding_anim);
		animationDrawable = (AnimationDrawable) mHeaderImage.getDrawable(); 
		animationDrawable.start();
	}

	@Override
	protected void releaseToRefreshImpl() {
		mHeaderImage.startAnimation(mRotateAnimation);
	}

	@Override
	protected void resetImpl() {
		
			mHeaderImage.setVisibility(View.VISIBLE);  
	        mHeaderImage.clearAnimation();  
	        mHeaderImage.setImageResource(R.drawable.xlist_view_loading);
	        if(rawHeigh!=0){
	        android.view.ViewGroup.LayoutParams params=mHeaderImage.getLayoutParams();
	        System.out.println(rawHeigh+"--------------");
	        params.height=rawHeigh;
	        mHeaderImage.setLayoutParams(params);
	        imageHeigh=rawHeigh;
	        
	        }
	        
	}

}
