package com.informaticalab.drawingapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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

    private static final float TOUCH_TOLERANCE = 0;

    private ArrayList<PathWithPaint> paths = new ArrayList<PathWithPaint>();
    private ArrayList<PathWithPaint> undonePaths = new ArrayList<PathWithPaint>();
    private boolean erase = false;
    private int colorBeforeErase;
    private Bitmap photoBM;
    private Paint photoPaint;


    private class PathWithPaint extends Path
    {

        Paint p;
        public void setPaint(Paint p)
        {
            this.p = p;
        }

        public Paint getPaint()
        {
            return p;
        }

        public PathWithPaint(Paint p)
        {
            this.p = p;
        }
    }

    //drawing path
    private PathWithPaint drawPath;

    //defines how to draw
    private Paint drawPaint;

    //initial color
    private int paintColor = ContextCompat.getColor(getContext(), R.color.sapienza);

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

    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        brushSize = getResources().getInteger(R.integer.small_size);

        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        drawPath = new PathWithPaint(drawPaint);
        photoPaint =  new Paint();
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
    public void addImage(String path)
    {
        photoPath = path;
    }

    /**
     * TODO: verificare che la path sia presente  priam di provare a fare decode.
     */
    private void resizePhoto(){
        if(photoPath.length() == 0) return;
        Log.d(LOG_TAG, "Path dell' immagine: " + photoPath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        photoBM = BitmapFactory.decodeFile(photoPath, options);
        Log.d(LOG_TAG, "Width:" + bitmapWidth+ " height:" +bitmapHeight);

        options.inSampleSize = calculateInSampleSize(options, bitmapWidth, bitmapHeight);

        options.inJustDecodeBounds = false;
        photoBM = BitmapFactory.decodeFile(photoPath, options);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);
        if(photoBM != null)
        {
            canvas.drawBitmap(photoBM, 0,0,photoPaint);
        }
        for (PathWithPaint p : paths)
        {
            canvas.drawPath(p, p.getPaint());
        }
        canvas.drawPath(drawPath, drawPath.getPaint());
    }
    private int bitmapWidth;
    private int bitmapHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);
        bitmapHeight = h;
        bitmapWidth = w;
        resizePhoto();
        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        canvasBitmap.eraseColor(Color.TRANSPARENT);

        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);

        //drawCanvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction())
        {
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
    public void eraseAll()
    {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        paths =  new ArrayList<PathWithPaint>();
        undonePaths = new ArrayList<PathWithPaint>();

        invalidate();
    }

    private void touch_start(float x, float y)
    {
        undonePaths.clear();
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            drawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up()
    {
        drawPath.quadTo(mX, mY, mX + 0.1f, mY + 0.1f);
        drawPath.lineTo(mX, mY);
        drawCanvas.drawPath(drawPath, drawPath.getPaint());
        paths.add(drawPath);

        drawPath = new PathWithPaint(drawPaint);

    }

    /**
     * Set the new size of the brush
     *
     * @param newSize
     */
    public void setBrushSize(float newSize)
    {
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

    public float getBrushSize()
    {
        return brushSize;
    }


    public void onClickUndo()
    {
        if (paths.size() > 0)
        {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        }

    }

    public void onClickRedo()
    {
        if (undonePaths.size() > 0)
        {
            paths.add(undonePaths.remove(undonePaths.size() - 1));
            invalidate();
        }
    }


    public void toggleErase()
    {
        if (erase)
        {
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

            drawPath = new PathWithPaint(drawPaint);
        }
        else
        {
            erase = true;
            Log.i(LOG_TAG, "in erase mode");
            drawPaint = new Paint();
            colorBeforeErase = drawPaint.getColor();
            drawPaint.setColor(Color.WHITE);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(brushSize);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);

            drawPath = new PathWithPaint(drawPaint);
        }
    }


    public Bitmap getBitmap()
    {
        return canvasBitmap;
    }
}
