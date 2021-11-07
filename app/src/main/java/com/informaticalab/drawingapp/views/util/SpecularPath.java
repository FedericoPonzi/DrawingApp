package com.informaticalab.drawingapp.views.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

/**
 * Created by FedericoPonzi on 13/06/2016.
 */
public class SpecularPath extends PathWithPaint
{
    private static String LOG_TAG = SpecularPath.class.getSimpleName();

    Path mirrorHorizontalDrawPath;
    Path mirrorVerticalDrawPath;
    boolean isMirrorVertical = false;
    boolean isMirrorHorizontal = false;
    long halfHorizontal = 0;
    long halfVertical = 0;
    public SpecularPath(Paint p)
    {
        super(p);

    }

    public SpecularPath(Paint p, boolean isMirrorVertical, boolean isMirrorHorizontal, long halfHorizontal, long halfVertical)
    {
        super(p);
        Log.d(LOG_TAG, "Half: " + halfHorizontal);
        this.halfHorizontal = halfHorizontal;
        this.halfVertical = halfVertical;

        this.isMirrorVertical = isMirrorVertical;
        this.isMirrorHorizontal = isMirrorHorizontal;
        if (isMirrorHorizontal)
        {
            this.mirrorHorizontalDrawPath = new PathWithPaint(p);
        }
        if (isMirrorVertical)
        {
            this.mirrorVerticalDrawPath = new PathWithPaint(p);
        }
    }

    @Override
    public void lineTo(float x, float y)
    {
        super.lineTo(x, y);
        if (isMirrorVertical)
        {
            float mVy = 0;
            if (y < halfVertical)
            {
                mVy = halfVertical + Math.abs(halfVertical - y);
            }
            else
            {
                mVy = halfVertical - Math.abs(halfVertical - y);
            }
            mirrorVerticalDrawPath.lineTo(x, mVy);
        }
        if (isMirrorHorizontal)
        {
            float mHx = 0;
            if (x < halfHorizontal)
            {
                mHx = halfHorizontal + Math.abs(halfHorizontal - x);
            }
            else
            {
                mHx = halfHorizontal - Math.abs(halfHorizontal - x);
            }
            mirrorHorizontalDrawPath.lineTo(mHx, y);
        }
    }

    @Override
    public void moveTo(float x, float y)
    {
        super.moveTo(x, y);
        if (isMirrorHorizontal)
        {
            if (x < halfHorizontal)
            {
                mirrorHorizontalDrawPath.moveTo(halfHorizontal + Math.abs(halfHorizontal - x),
                                                y);
            }
            else
            {
                mirrorHorizontalDrawPath.moveTo(halfHorizontal - Math.abs(halfHorizontal - x),
                                                y);
            }
        }
        if (isMirrorVertical)
        {
            if (y < halfVertical)
            {
                mirrorVerticalDrawPath.moveTo(x, halfVertical + Math.abs(halfVertical - y));
            }
            else
            {
                mirrorVerticalDrawPath.moveTo(x, halfVertical - Math.abs(halfVertical - y));
            }

        }
    }

    @Override
    public void quadTo(float x1, float y1, float x2, float y2)
    {
        //mX, mY, (x + mX) / 2, (y + mY) / 2
        super.quadTo(x1, y1, x2, y2);
        if (isMirrorHorizontal)
        {
            float mHx1;
            float mHx2;

            if (x1 < halfHorizontal)
            {
                mHx1 = halfHorizontal + Math.abs(halfHorizontal - x1);
            }
            else
            {
                mHx1 = halfHorizontal - Math.abs(halfHorizontal - x1);
            }

            if (x2 < halfHorizontal)
            {
                mHx2 = halfHorizontal + Math.abs(halfHorizontal - x2);
            }
            else
            {
                mHx2 = halfHorizontal - Math.abs(halfHorizontal - x2);
            }

            mirrorHorizontalDrawPath.quadTo(mHx1,
                                            y1, mHx2,
                                            y2);
        }
        if (isMirrorVertical)
        {
            float mVy1;
            float mVy2;

            if (y1 < halfVertical)
            {
                mVy1 = halfVertical + Math.abs(halfVertical - y1);
            }
            else
            {
                mVy1 = halfVertical - Math.abs(halfVertical - y1);
            }

            if (y2 < halfVertical)
            {
                mVy2 = halfVertical + Math.abs(halfVertical - y2);
            }
            else
            {
                mVy2 = halfVertical - Math.abs(halfVertical - y2);
            }

            mirrorVerticalDrawPath.quadTo(x1,
                                          mVy1, x2,
                                          mVy2);

        }
    }

    public void drawPath(Canvas canvas)
    {
        canvas.drawPath(this, this.p);
        if (isMirrorHorizontal)
        {
            canvas.drawPath(this.mirrorHorizontalDrawPath, this.p);
        }
        if (isMirrorVertical)
        {
            canvas.drawPath(this.mirrorVerticalDrawPath, this.p);
        }
    }
}
