package com.lorentzos.flingswipe;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinosaurs might appear!
 */

public class SwipeFlingAdapterView extends BaseFlingAdapterView {
    private List<View> cards=new ArrayList<>();
    private  Context context;

    private final   String TAG="SwipeFlingAdapterView";

    private int MAX_VISIBLE = 4;
    private int MIN_ADAPTER_STACK = 6;
    private float ROTATION_DEGREES = 15.f;

    private Adapter mAdapter;
    private int LAST_OBJECT_IN_STACK = 0;
    private onFlingListener mFlingListener;
    private AdapterDataSetObserver mDataSetObserver;
    private boolean mInLayout = false;
    private View mActiveCard = null;
    private OnItemClickListener mOnItemClickListener;
    private FlingCardListener flingCardListener;
    private PointF mLastTouchPoint;
    public static boolean moving=false;
    private int leftMargin=0;
    private int topMargin=0;
    private boolean anim=false;
    public SwipeFlingAdapterView(Context context) {
        this(context, null);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SwipeFlingStyle);
    }

    public SwipeFlingAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeFlingAdapterView, defStyle, 0);
        MAX_VISIBLE = a.getInt(R.styleable.SwipeFlingAdapterView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(R.styleable.SwipeFlingAdapterView_min_adapter_stack, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(R.styleable.SwipeFlingAdapterView_rotation_degrees, ROTATION_DEGREES);
        a.recycle();
    }


    /**
     * A shortcut method to set both the listeners and the adapter.
     *
     * @param context The activity context which extends onFlingListener, OnItemClickListener or both
     * @param mAdapter The adapter you have to set.
     */
    public void init(final Context context, Adapter mAdapter) {
        if(context instanceof onFlingListener) {
            mFlingListener = (onFlingListener) context;
        }else{
            throw new RuntimeException("Activity does not implement SwipeFlingAdapterView.onFlingListener");
        }
        if(context instanceof OnItemClickListener){
            mOnItemClickListener = (OnItemClickListener) context;
        }
        setAdapter(mAdapter);
    }

 	@Override
    public View getSelectedView() {
        return mActiveCard;
    }


    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(moving) {
            Log.e(TAG,"onDraw---moving");
            return;

        }

        Log.e(TAG,"onDraw---");
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }
        if(flingCardListener!=null&&flingCardListener.isSlidingAnimRunnimg())
            return;
         if(flingCardListener!=null&&flingCardListener.isAnimationRunning())
             anim=true;
        else
             anim=false;




        mInLayout = true;
        final int adapterCount = mAdapter.getCount();

        if(adapterCount == 0) {
            removeAllViewsInLayout();
        }else {

            View topCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null && topCard!=null && topCard==mActiveCard) {
              //  if (this.flingCardListener.isTouching()) {

                    PointF lastPoint = this.flingCardListener.getLastPoint();
                 //   if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {

                        this.mLastTouchPoint = lastPoint;
                        removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                        layoutChildren(1, adapterCount);
                   // }
              //  }
            }else{
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                layoutChildren(0, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;
        
        if(adapterCount <= MIN_ADAPTER_STACK)
            mFlingListener.onAdapterAboutToEmpty(adapterCount);




    }


    private void layoutChildren(int startingIndex, int adapterCount){
    for(int i=startingIndex;i<Math.min(adapterCount, MAX_VISIBLE);i++){

        View  newUnderChild;
           if(cards.size()<MAX_VISIBLE) {
               if(cards.size()>startingIndex)
                   newUnderChild = cards.get(i);
               else {
                   newUnderChild = mAdapter.getView(i, null, this);
                   cards.add(newUnderChild);
               }
            }else {

                newUnderChild = cards.get(i);

            }

            if (newUnderChild.getVisibility() != GONE) {
                makeAndAddView(newUnderChild, i);
                LAST_OBJECT_IN_STACK = startingIndex;
            }

        startingIndex++;
    }

/*
        while (startingIndex < Math.min(adapterCount, MAX_VISIBLE) ) {
            View newUnderChild = mAdapter.getView(startingIndex, null, this);
            if (newUnderChild.getVisibility() != GONE) {
                makeAndAddView(newUnderChild,startingIndex);
                LAST_OBJECT_IN_STACK = startingIndex;
            }
            startingIndex++;


        }*/




    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void makeAndAddView(View child,int index) {


        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        if(lp==null){

            Log.e(TAG,"lp空");
            return;


        }

            if(!moving&&!anim) {
                //Log.e(TAG,"layoutChildren----"+index);
                int margin = index;
                if (index == 2) {
                    margin = 1;
                }
                lp.topMargin = (margin )
                        * getResources().getDimensionPixelSize(
                        R.dimen.card_item_margin);
                lp.leftMargin = margin
                        * getResources().getDimensionPixelSize(
                        R.dimen.card_item_margin_left_right);
                lp.rightMargin = margin
                        * getResources().getDimensionPixelSize(
                        R.dimen.card_item_margin_left_right);
                child.setLayoutParams(lp);
               /* Log.e(TAG,"topMargin"+ lp.topMargin);
                Log.e(TAG, "leftRightMargin--" + lp.leftMargin);*/

            }else{

                if(index==1) {
                    lp.topMargin = topMargin;
                    lp.leftMargin = leftMargin;
                    lp.rightMargin = leftMargin;
                    child.setLayoutParams(lp);
                }else{

                    lp.topMargin =
                             getResources().getDimensionPixelSize(
                            R.dimen.card_item_margin);
                    lp.leftMargin =
                            getResources().getDimensionPixelSize(
                            R.dimen.card_item_margin_left_right);
                    lp.rightMargin =
                            getResources().getDimensionPixelSize(
                            R.dimen.card_item_margin_left_right);
                    child.setLayoutParams(lp);
                }
            }



        addViewInLayout(child, 0, lp, true);
        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }


        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - w) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }




        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;

                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }

        child.layout(childLeft, childTop, childLeft + w, childTop + h);


    }





    /**
    *  Set the top view and add the fling listener
    */
    private void setTopView() {
        if(getChildCount()>0){

            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null) {

                flingCardListener = new FlingCardListener(mActiveCard, mAdapter.getItem(0),
                        ROTATION_DEGREES, new FlingCardListener.FlingListener() {

                            @Override
                            public void onCardExited() {
                                cards.remove(0);
                                mActiveCard = null;
                                mFlingListener.removeFirstObjectInAdapter();
                            }

                            @Override
                            public void leftExit(Object dataObject) {
                                mFlingListener.onLeftCardExit(dataObject);
                            }

                            @Override
                            public void rightExit(Object dataObject) {
                                mFlingListener.onRightCardExit(dataObject);
                            }

                            @Override
                            public void onClick(Object dataObject) {
                                if(mOnItemClickListener!=null)
                                    mOnItemClickListener.onItemClicked(0, dataObject);

                            }

                    @Override
                            public void onScroll(float scrollProgressPercent) {
                                mFlingListener.onScroll(scrollProgressPercent);
                            }

                    @Override
                    public void onMove(float positionOffset, int positionOffsetPixels) {

                        if (positionOffset == 0f) {
                            positionOffset = 1f;
                        }

                        if (Math.abs(positionOffsetPixels) != 0) {
                            double  offset=Math.abs(positionOffset)*3;
                            if(offset>1) {
                                offset = 1.0;
                                return;
                            }


                            float leftRightMargin =(float)(1 -offset)
                                    * getResources().getDimensionPixelSize(
                                    R.dimen.card_item_margin_left_right);
                            float mTopMargin=(int) ((1 -offset) * getResources()
                                    .getDimensionPixelSize(R.dimen.card_item_margin));
                            leftMargin=(int)leftRightMargin;
                            topMargin=(int)mTopMargin;
                         //   Log.e(TAG,"layout------");

                                    requestLayout();



                          /*  Log.e(TAG,"topMargin"+mTopMargin);
                            Log.e(TAG, "leftRightMargin--" + leftRightMargin);*/

                           /* for (int i = getChildCount() - 2; i >1; i--) {
                                int index;
                                CustomFrameLayout view = (CustomFrameLayout) getChildAt(i);
                                if (view != null) {
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                            view.getLayoutParams());
                                    if(i==2)
                                        index=1;
                                    else
                                        index=2;
                                    float leftRightMargin = (4 - i - Math.abs(positionOffset))
                                            * getResources().getDimensionPixelSize(
                                            R.dimen.card_item_margin_left_right);
                                    float mTopMargin=(int) ((3 - i - Math.abs(positionOffset)) * getResources()
                                            .getDimensionPixelSize(R.dimen.card_item_margin));
                                    leftMargin=(int)leftRightMargin/2;
                                    topMargin=(int)mTopMargin/2;
                                    requestLayout();
                                   *//* params.topMargin = (int) ((3 - i - Math.abs(positionOffset)) * getResources()
                                            .getDimensionPixelSize(R.dimen.card_item_margin));
                                    params.leftMargin = (int) leftRightMargin;
                                    params.rightMargin = (int) leftRightMargin;

                                    Log.e(TAG,"topMargin"+params.topMargin);
                                    Log.e(TAG, "leftRightMargin--" + leftRightMargin);
                                    view.setLayoutParams(params);*//*

                                }

                            }*/

                        }



                    }
                });

                mActiveCard.setOnTouchListener(flingCardListener);
            }
        }
    }

    public FlingCardListener getTopCardListener() throws NullPointerException{
        if(flingCardListener==null){
            throw new NullPointerException();
        }
        return flingCardListener;
    }

    public void setMaxVisible(int MAX_VISIBLE){
        this.MAX_VISIBLE = MAX_VISIBLE;
    }

    public void setMinStackInAdapter(int MIN_ADAPTER_STACK){
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;

        if (mAdapter != null  && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }

    }

    public void setFlingListener(onFlingListener onFlingListener) {
        this.mFlingListener = onFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }


    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            requestLayout();
        }

    }


    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public interface onFlingListener {
        void removeFirstObjectInAdapter();
        void onLeftCardExit(Object dataObject);
        void onRightCardExit(Object dataObject);
        void onAdapterAboutToEmpty(int itemsInAdapter);
        void onScroll(float scrollProgressPercent);
    }


}
