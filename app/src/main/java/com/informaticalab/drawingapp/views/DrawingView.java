package com.informaticalab.drawingapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.informaticalab.drawingapp.R;
import com.informaticalab.drawingapp.views.util.SpecularPath;

import java.util.ArrayList;

/**
 * Created by FedericoPonzi on 12/02/2016.
 */
public class DrawingView extends View {

    private static String LOG_TAG = DrawingView.class.getSimpleName();

    private static final float TOUCH_TOLERANCE = 0;

    private ArrayList<SpecularPath> paths = new ArrayList<SpecularPath>();
    private ArrayList<SpecularPath> undonePaths = new ArrayList<SpecularPath>();
    private boolean erase = false;
<<<<<<< HEAD

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

=======
>>>>>>> 56d1cf409c37fda46ffe7ff569b7c7ff6e2f9d39
    private Bitmap photoBM;
    private Paint photoPaint;

    private long halfVertical = 0;
    private long halfHorizontal = 0;
    private boolean mirrorHorizontal = false;
    private boolean mirrorVertical = false;
    private int bitmapWidth;
    private int bitmapHeight;

    //drawing path
    private SpecularPath drawPath;

    //defines how to draw
    private Paint drawPaint;

    //initial color
    private int paintColor = ContextCompat.getColor(getContext(), R.color.primary);

    //canvas - holding pen, holds your drawings
    //and transfers them to the view
    private Canvas drawCanvas;

    //canvas bitmap
    private Bitmap canvasBitmap;

    private String photoPath = "";

    //brush size
    private float brushSize;
    private float mX;
    private float mY;
    private boolean isGridVisible = false;

    private ArrayList<SpecularPath> gridPaths = new ArrayList<>();

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public boolean isEdited() {
        return paths.size() > 0;
    }

    public void setColor(int color) {

        this.paintColor = color;

        //Is is erase, save the selected color but not set it now.
        if (erase) {
            return;
        }

        drawPaint = new Paint();
        drawPath.setPaint(drawPaint);
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private Paint gridPaint;

    private void init() {
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        /*
            Setup for the Grid:
         */
        gridPaint = new Paint();
        gridPaint.setColor(ContextCompat.getColor(getContext(), R.color.half_black));
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(5);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setXfermode(null);

        /*
         * base brush size
         */
        brushSize = 20;

        drawPaint = new Paint();

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPath = new SpecularPath(drawPaint);
        photoPaint = new Paint();
        photoPaint.setAntiAlias(true);
        photoPaint.setFilterBitmap(true);
        photoPaint.setDither(true);

    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void addImage(String path) {
        photoPath = path;
    }

    /**
     * Resize the photo before showing it on the canvas.
     */
    private void resizePhoto() {
        if (photoPath.length() == 0) {
            return;
        }
        Log.d(LOG_TAG, "Path dell' immagine: " + photoPath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        photoBM = BitmapFactory.decodeFile(photoPath, options);
        Log.d(LOG_TAG, "Width:" + bitmapWidth + " height:" + bitmapHeight);

        options.inSampleSize = calculateInSampleSize(options, bitmapWidth, bitmapHeight);

        options.inJustDecodeBounds = false;
        //halfHorizontal = bitmapWidth / 2;
        photoBM = BitmapFactory.decodeFile(photoPath, options);

    }

    /**
     * Main draw method.
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        canvas.drawColor(Color.WHITE);
        if (photoBM != null) {
            canvas.drawBitmap(photoBM, 0, 0, photoPaint);
        }

        for (SpecularPath p : paths) {
            p.drawPath(canvas);

            //canvas.drawPath(p, p.getPaint());
        }
        if (isGridVisible) {
            for (SpecularPath p : gridPaths) {
                p.drawPath(canvas);
            }
        }

        drawPath.drawPath(canvas);
        canvas.restore();
        //canvas.drawPath(drawPath, drawPath.getPaint());
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.v(LOG_TAG, "On Size Changed called");
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);
        bitmapHeight = h;
        bitmapWidth = w;
        halfHorizontal = (long) (((float) w) / 2f);
        halfVertical = (long) (((float) h) / 2f);

        resizePhoto();
        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        canvasBitmap.eraseColor(Color.TRANSPARENT);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);

        gridPaths = new ArrayList<>();
        Log.v(LOG_TAG, "Size e width: " + bitmapWidth + " h:" + bitmapHeight);
        for (int i = 0; i < bitmapWidth; i += 25) {
            Log.v(LOG_TAG, "Creo griglia:" + i);
            SpecularPath p = new SpecularPath(gridPaint, false, false, halfHorizontal,
                    halfVertical);
            p.moveTo(i, 0);
            p.quadTo(i, 0, i, bitmapHeight);
            p.lineTo(i, bitmapHeight);
            gridPaths.add(p);
        }
        for (int i = 0; i < bitmapHeight; i += 25) {
            Log.v(LOG_TAG, "Creo griglia:" + i);
            SpecularPath p = new SpecularPath(gridPaint, false, false, halfHorizontal,
                    halfVertical);
            p.moveTo(0, i);
            p.quadTo(0, i, bitmapWidth, i);
            p.lineTo(bitmapWidth, i);
            gridPaths.add(p);
        }
        //drawCanvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Start new Drawing
     */
    public void eraseAll() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        paths = new ArrayList<SpecularPath>();
        undonePaths = new ArrayList<SpecularPath>();
        drawPath = new SpecularPath(drawPaint);
        invalidate();
    }

    private void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath = new SpecularPath(drawPaint, mirrorVertical, mirrorHorizontal, halfHorizontal,
                halfVertical);
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            drawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }

    }

    private void touch_up() {
        drawPath.quadTo(mX, mY, mX + 0.1f, mY + 0.1f);
        drawPath.lineTo(mX, mY);
        drawCanvas.drawPath(drawPath, drawPath.getPaint());
        paths.add(drawPath);
        drawPath = new SpecularPath(drawPaint);
    }

    /**
     * Set the new size of the brush
     *
     * @param newSize
     */
    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = newSize;
        Log.i(LOG_TAG, "Brush size: " + newSize + " pizel Amount:" + pixelAmount);
        drawPaint = new Paint();
        drawPath.setPaint(drawPaint);
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

    }


    public void onClickUndo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }

    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        }
    }


    public void toggleErase() {
        if (erase) {
            erase = false;
            Log.i(LOG_TAG, "non erase mode");
            drawPaint = new Paint();

            drawPaint.setColor(paintColor);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(brushSize);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
            drawPaint.setXfermode(null);
        } else {
            erase = true;
            Log.i(LOG_TAG, "in erase mode");
            drawPaint = new Paint();
            drawPaint.setColor(Color.WHITE);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(brushSize);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }


    public float getBrushSize() {
        return brushSize;
    }

    public Bitmap getBitmap() {
        return canvasBitmap;
    }

    public void setVerticalFlip(boolean verticalFlip) {
        Log.v(LOG_TAG, "VerticalFlip: " + verticalFlip);
        this.mirrorVertical = verticalFlip;
    }

    public void setHorizontalFlip(boolean horizontalFlip) {
        Log.v(LOG_TAG, "HorizontalFlip: " + horizontalFlip);
        this.mirrorHorizontal = horizontalFlip;
    }

    public void toggleGrid() {
        isGridVisible = !isGridVisible;
        invalidate();
        Log.i(LOG_TAG, "togglegrid:" + isGridVisible);

    }
}
