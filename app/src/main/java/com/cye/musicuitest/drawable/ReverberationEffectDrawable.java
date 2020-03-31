package com.cye.musicuitest.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import com.cye.musicuitest.helpes.LineDrawHelper;
import com.cye.musicuitest.utils.SystemUtil;


public class ReverberationEffectDrawable extends BaseEffectDrawable {

    private PointF[] points;
    private float mDrawWidth = 10;

    private LineDrawHelper mLineDrawHelper;

    private int mLineRectWidth = 4;

    RectF mRectFS = new RectF();

    public ReverberationEffectDrawable(Context context) {
        super(context);
        init();
    }

    private void init() {
        mLineRectWidth = SystemUtil.dip2px(getContext(), mLineRectWidth);
        mDrawWidth = SystemUtil.dip2px(getContext(), mDrawWidth);

        mLineDrawHelper = new LineDrawHelper(mCount / 4);

        points = new PointF[mCount / 4];
        for (int i = 0; i < mCount / 4; i++) {
            points[i] = new PointF();
        }
    }


    @Override
    public void draw(Canvas canvas) {
        mIsDrawing = true;
        drawWare(canvas);
        mIsDrawing = false;
    }


    private void drawWare(Canvas canvas) {

        for (int i = 0; i < points.length; i++) {
            float value = 0;
            if (mData != null) {
                value = (int) mData[i];
            }
            //debug
//            value = (int) (mRandom.nextFloat()*127);
            if (value > 0) {
                value = SystemUtil.dip2px(getContext(), value * 0.5f + 5);
            }

            float x = i * mDrawWidth;
            float y = value;
            mLineDrawHelper.setPoint(points[i], x, y);
        }

        mLineDrawHelper.setPoints(points, mDrawWidth);

        mLineDrawHelper.calculate(0.8);
        mLineDrawHelper.calculateHeight();

        for (int i = 0; i < mLineDrawHelper.getLineCount(); i++) {
            int index = i * 360 / mLineDrawHelper.getLineCount();

            float l = canvas.getWidth() / 2 + mRadius;
            float t = canvas.getHeight() / 2;

            float r = l + mLineRectWidth + mLineDrawHelper.getHeights()[i];
            float b = t + mLineRectWidth;
            mRectFS.set(l, t, r, b);

            canvas.save();
            canvas.rotate(index, canvas.getWidth() / 2, canvas.getHeight() / 2);
            mPaint.setColor(mPaintColor);
            canvas.drawRoundRect(mRectFS, mLineRectWidth, mLineRectWidth, mPaint);
            canvas.restore();
        }
    }

}
