package com.amusebouche.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Floating action button class.
 * Author: Noelia Sales <noelia.salesmontes@gmail.com
 *
 * Android view class that implements the material design FAB.
 */
public class FloatingActionButton extends View {

    final static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    final static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();

    private Paint mButtonPaint;
    private Paint mDrawablePaint;
    private Bitmap mBitmap;
    private boolean mHidden = false;

    public FloatingActionButton(Context context) {
        super(context);
        init(Color.WHITE);
    }

    public void setFloatingActionButtonColor(int FloatingActionButtonColor) {
        init(FloatingActionButtonColor);
    }

    public void setFloatingActionButtonDrawable(Drawable FloatingActionButtonDrawable) {
        mBitmap = ((BitmapDrawable) FloatingActionButtonDrawable).getBitmap();
        invalidate();
    }

    public void init(int FloatingActionButtonColor) {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(FloatingActionButtonColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setClickable(true);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6),
            mButtonPaint);
        canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2,
            (getHeight() - mBitmap.getHeight()) / 2, mDrawablePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setAlpha(1.0f);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setAlpha(0.6f);
        }
        return super.onTouchEvent(event);
    }

    public void hideFloatingActionButton() {
        if (!mHidden) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1, 0);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1, 0);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(accelerateInterpolator);
            animSetXY.setDuration(100);
            animSetXY.start();
            mHidden = true;
        }
    }

    public void showFloatingActionButton() {
        if (mHidden) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0, 1);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(overshootInterpolator);
            animSetXY.setDuration(200);
            animSetXY.start();
            mHidden = false;
        }
    }

    public boolean isHidden() {
        return mHidden;
    }

    /*
     * The calculation (value * scale + 0.5f) is a widely used to convert to dps to pixel units
     * based on density scale
     *
     * See developer.android.com (Supporting Multiple Screen Sizes)
     */
    public static int convertToPixels(int dp, float scale){
        return (int) (dp * scale + 0.5f) ;
    }
}