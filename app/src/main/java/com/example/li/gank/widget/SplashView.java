package com.example.li.gank.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;

import com.example.li.gank.BuildConfig;
import com.example.li.gank.R;

/**
 * Created by Li on 2017/10/24.
 */

public class SplashView extends View {
    private static final String TAG="SplashView";
    public SplashView(Context context) {
        super(context);
        initialize();
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
        setupAttributes(attrs);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
        setupAttributes(attrs);
    }

    public static final int DEFAULT_HOLE_FILL_COLOR= Color.WHITE;
    public static final int DEFAULT_ICON_COLOR=Color.rgb(23,169,229);
    public static final int DEFAULT_DURATION=500;
    public static final boolean DEFAULT_REMOVE_FROM_PARENT_ON_END=true;

    private static final int PAINT_STROKE_WIDTH=2;

    private Drawable mIcon;
    private int mHoleFillColor=DEFAULT_HOLE_FILL_COLOR;
    private int mIconColor=DEFAULT_ICON_COLOR;
    private long mDuration=DEFAULT_DURATION;
    private boolean mRemoveFormParentOnEnd=true;
    private float mCurrentScale=1;

    private int mWidth,mHeight;
    private int mIconWidth,mIconHeight;
    private float mMaxScale=1;

    private Paint mPaint =new Paint();

    private void setupAttributes(AttributeSet attrs){
        Context context=getContext();

        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.SplashView);

        int numAttrs=a.getIndexCount();
        for(int i=0;i<numAttrs;++i){
            int attr=a.getIndex(i);
            switch(attr){
                case R.styleable.SplashView_splashIcon:
                    setIconDrawable(a.getDrawable(i));
                    break;
                case R.styleable.SplashView_iconColor:
                    setIconColor(a.getColor(i,DEFAULT_ICON_COLOR));
                    break;
                case R.styleable.SplashView_holeFillColor:
                    setHoleFillColor(a.getColor(i,DEFAULT_HOLE_FILL_COLOR));
                    break;
                case R.styleable.SplashView_duration:
                    setDuration(a.getInt(i,DEFAULT_DURATION));
                    break;
                case R.styleable.SplashView_removeFromParentOnEnd:
                    setRemoveFromParentOnEnd(a.getBoolean(i,DEFAULT_REMOVE_FROM_PARENT_ON_END));
                    break;
            }
        }
        a.recycle();

    }

    private void initialize(){
        setBackgroundColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(PAINT_STROKE_WIDTH);
    }
    private void setHoleFillColor(int bgColor) {
        mHoleFillColor=bgColor;
    }

    private void setIconColor(int iconColor) {
        mIconColor=iconColor;
    }

    private void setIconDrawable(Drawable icon) {
        mIcon=icon;
        if(mIcon!=null){
            mIconWidth=mIcon.getIntrinsicWidth();
            mIconHeight=mIcon.getIntrinsicHeight();
            Rect iconBounds=new Rect();
            iconBounds.left=0;
            iconBounds.top=0;
            iconBounds.right=mIconWidth;
            iconBounds.bottom=mIconHeight;
            mIcon.setBounds(iconBounds);
        }else{
            mIconWidth=0;
            mIconHeight=0;
        }
        setMaxScale();
    }

    private void setMaxScale() {
        if (mIconWidth<1||mIconHeight<1){
            mMaxScale=1;
            return;
        }
        mMaxScale=2*Math.max((float)mWidth/mIconWidth,(float)mHeight/mIconHeight);
        if (mMaxScale<1){
            mMaxScale=1;
        }
    }

    private void setDuration(long duration) {
        if(duration<0){
            throw new IllegalArgumentException("duration cannot be less than 0");

        }
        mDuration=duration;
    }

    private void setRemoveFromParentOnEnd(boolean shouldRemove) {
        mRemoveFormParentOnEnd=shouldRemove;
    }
    public static interface ISplashListener{
        void onStart();
        void onUpdate(float completionFraction);
        void onEnd();
    }


    public void splashAndDisappear(final ISplashListener listener){
        final ValueAnimator animator=ValueAnimator.ofFloat(1,mMaxScale);
        animator.setDuration(mDuration);
        animator.setInterpolator(new OvershootInterpolator(1F));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentScale=1+mMaxScale-(Float)animator.getAnimatedValue();
                invalidate();
                if(listener!=null){
                    listener.onUpdate((float)animator.getCurrentPlayTime()/mDuration);
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null) {
                    listener.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mRemoveFormParentOnEnd){
                    ViewParent parent=getParent();
                    if (parent!=null&&parent instanceof ViewManager){
                        ViewManager viewManager=(ViewManager)parent;
                        viewManager.removeView(SplashView.this);

                    }else if (BuildConfig.DEBUG){
                        Log.w(TAG,"splash view not removed after animation ended because no ViewManager parent was found");

                    }
            }
            if (listener!=null){
                listener.onEnd();
            }
        }
        });
        post(new Runnable() {
            @Override
            public void run() {
                animator.reverse();
            }
        });
    }
    protected void onSizeChanged(int w,int h,int oldw,int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        mWidth=w;
        mHeight=h;
        setMaxScale();
    }
    protected void onDraw(Canvas canvas){
        float iconWidth=mIconWidth*mCurrentScale;
        float iconHeight=mIconHeight*mCurrentScale;

        float mIconLeft=(mWidth-iconWidth)/2;
        float mIconRight=mIconLeft+iconWidth;
        float mIconTop=(mHeight-iconHeight)/2;
        float mIconBottom=mIconTop+iconHeight;

        if (mCurrentScale<2){
            mPaint.setColor(mHoleFillColor);
            canvas.drawRect(mIconLeft,mIconTop,mIconRight,mIconBottom,mPaint);

        }
        mPaint.setColor(mIconColor);
        canvas.drawRect(0,0,mIconLeft,mHeight,mPaint);
        canvas.drawRect(mIconLeft,0,mIconRight,mIconTop,mPaint);
        canvas.drawRect(mIconLeft,mIconBottom,mIconRight,mHeight,mPaint);
        canvas.drawRect(mIconRight,0,mWidth,mHeight,mPaint);

        if (mIcon!=null){
            canvas.save();
            canvas.translate(mIconLeft,mIconTop);
            canvas.scale(mCurrentScale,mCurrentScale);
            mIcon.draw(canvas);
            canvas.restore();
        }else if (BuildConfig.DEBUG){
            Log.w(TAG,"icon is not set when the view needs to be drawn");
        }

    }

}
