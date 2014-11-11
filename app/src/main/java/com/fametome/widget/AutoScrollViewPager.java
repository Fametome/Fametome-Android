package com.fametome.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.fametome.listener.AutoScrollListener;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class AutoScrollViewPager extends ViewPager{

    private AutoScrollListener autoScrollListener = null;

    private Timer autoScrollTimer;
    private int currentPage = 0;

    private int FLASH_DURATION = 2000;
    private int TRANSITION_ANIMATION_DURATION = 1000;

    public AutoScrollViewPager(Context context) {
        super(context);
        init();
    }

    public AutoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){



        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(this.getContext());
            mScroller.set(this, scroller);
        } catch (Exception e) {
            Log.e("AutoScrollViewPager", "error of change scroller : " + e);
        }


        autoScrollTimer = new Timer(); // At this line a new Thread will be created
         autoScrollTimer.scheduleAtFixedRate(new RemindTask(), FLASH_DURATION, FLASH_DURATION); // delay

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public void cancelAutoScroll(){
        autoScrollTimer.cancel();
    }

    private class RemindTask extends TimerTask {

        @Override
        public void run() {
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                public void run() {
                    currentPage++;
                    if (currentPage < getAdapter().getCount()) {
                        setCurrentItem(currentPage);
                    } else if (currentPage == getAdapter().getCount()){
                        autoScrollTimer.cancel();

                        if(autoScrollListener != null){
                            autoScrollListener.onScrollFinished();
                        }
                    }
                }
            });

        }
    }

    public class ViewPagerScroller extends Scroller {

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, TRANSITION_ANIMATION_DURATION);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, TRANSITION_ANIMATION_DURATION);
        }

    }

    public void setAutoScrollListener(AutoScrollListener autoScrollListener){
        this.autoScrollListener = autoScrollListener;
    }
}
