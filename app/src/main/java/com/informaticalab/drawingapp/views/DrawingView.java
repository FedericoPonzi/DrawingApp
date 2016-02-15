package com.informaticalab.drawingapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.informaticalab.drawingapp.R;

import java.util.ArrayList;

/**
 * Created by FedericoPonzi on 12/02/2016.
 */
public class DrawingView extends View
{

    private static String LOG_TAG = DrawingView.class.getSimpleName();

    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();

    //drawing path
    private Path drawPath;

    //defines what to draw
    private Paint canvasPaint;

    //defines how to draw
    private Paint drawPaint;

    //initial color
    private int paintColor = 0xFF660000;

    //canvas - holding pen, holds your drawings
    //and transfers them to the view
    private Canvas drawCanvas;

    //canvas bitmap
    private Bitmap canvasBitmap;

    //brush size
    private float currentBrushSize, lastBrushSize;

    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    private void init(){
        currentBrushSize = getResources().getInteger(R.integer.small_size);
        lastBrushSize = currentBrushSize;

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);

        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(LOG_TAG, "Ricevuto touch down!");
                //touch_start(touchX, touchY);
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                //touch_move(touchX, touchY);
                Log.i(LOG_TAG, "Ricevuto touch move!");
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:

                Log.i(LOG_TAG, "Ricevuto touch up!");
                //touch_up();
                //invalidate();
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPoint(touchX, touchY, drawPaint); //Aggiunto per punti singoli

                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;
    }
    /** Start new Drawing */
    public void eraseAll() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
