package com.bestspa.spa.client.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

import com.bestspa.spa.client.R;

public class DotsProgressBar extends View {
    private int heightSize;
    private int mDotCount = 3;
    private Handler mHandler = new Handler();
    private int mIndex = 0;
    private Paint mPaint = new Paint(1);
    private Paint mPaintFill = new Paint(1);
    private float mRadius;
    private Runnable mRunnable = new C13431();
    private int margin = 4;
    private int step = 1;
    private int widthSize;

    /* renamed from: com.spa.easyspa.model.DotsProgressBar$1 */
    class C13431 implements Runnable {
        C13431() {
        }

        public void run() {
            DotsProgressBar.this.mIndex = DotsProgressBar.this.mIndex + DotsProgressBar.this.step;
            if (DotsProgressBar.this.mIndex < 0) {
                DotsProgressBar.this.mIndex = 1;
                DotsProgressBar.this.step = 1;
            } else if (DotsProgressBar.this.mIndex > DotsProgressBar.this.mDotCount - 1) {
                if (DotsProgressBar.this.mDotCount - 2 >= 0) {
                    DotsProgressBar.this.mIndex = DotsProgressBar.this.mDotCount - 2;
                    DotsProgressBar.this.step = -1;
                } else {
                    DotsProgressBar.this.mIndex = 0;
                    DotsProgressBar.this.step = 1;
                }
            }
            DotsProgressBar.this.invalidate();
            DotsProgressBar.this.mHandler.postDelayed(DotsProgressBar.this.mRunnable, 300);
        }
    }

    public DotsProgressBar(Context context) {
        super(context);
        init(context);
    }

    public DotsProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DotsProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mRadius = context.getResources().getDimension(R.dimen._5sdp);
        this.mPaintFill.setStyle(Style.FILL);
        this.mPaintFill.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(855638016);
        start();
    }

    public void setDotsCount(int count) {
        this.mDotCount = count;
    }

    public void start() {
        this.mIndex = -1;
        this.mHandler.removeCallbacks(this.mRunnable);
        this.mHandler.post(this.mRunnable);
    }

    public void stop() {
        this.mHandler.removeCallbacks(this.mRunnable);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.widthSize = MeasureSpec.getSize(widthMeasureSpec);
        this.heightSize = ((((int) this.mRadius) * 2) + getPaddingBottom()) + getPaddingTop();
        setMeasuredDimension(this.widthSize, this.heightSize);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float dX = ((((float) this.widthSize) - ((((float) this.mDotCount) * this.mRadius) * 2.0f)) - ((float) ((this.mDotCount - 1) * this.margin))) / 2.0f;
        float dY = (float) (this.heightSize / 2);
        for (int i = 0; i < this.mDotCount; i++) {
            if (i == this.mIndex) {
                canvas.drawCircle(dX, dY, this.mRadius, this.mPaintFill);
            } else {
                canvas.drawCircle(dX, dY, this.mRadius, this.mPaint);
            }
            dX += (this.mRadius * 2.0f) + ((float) this.margin);
        }
    }
}

